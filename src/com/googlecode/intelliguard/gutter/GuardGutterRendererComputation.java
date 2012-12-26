/*
 * Copyright 2009 Ronnie Kolehmainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.intelliguard.gutter;

import com.googlecode.intelliguard.facet.GuardFacet;
import com.googlecode.intelliguard.model.Keeper;
import com.googlecode.intelliguard.ui.Icons;
import com.googlecode.intelliguard.util.InspectionUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * User: ronnie
 * Date: 2009-nov-09
 * Time: 12:55:44
 */
public class GuardGutterRendererComputation implements Computable<List<GuardGutterRenderer>>
{
    private PsiFile psiFile;

    public GuardGutterRendererComputation(@NotNull PsiFile psiFile)
    {
        this.psiFile = psiFile;
    }

    public List<GuardGutterRenderer> compute()
    {
        final List<GuardGutterRenderer> guardGutterRenderers = new ArrayList<GuardGutterRenderer>();
        final Document document = psiFile.getViewProvider().getDocument();
        if (document == null)
        {
            return Collections.emptyList();
        }
        final Module module = ModuleUtil.findModuleForPsiElement(psiFile);
        if (module == null)
        {
            return Collections.emptyList();
        }
        final GuardFacet guardFacet = GuardFacet.getInstance(module);
        if (guardFacet == null)
        {
            return Collections.emptyList();
        }

        psiFile.accept(new JavaRecursiveElementVisitor()
        {
            @Override
            public void visitClass(PsiClass psiClass)
            {
                checkElement(psiClass, "class");
                super.visitClass(psiClass);
            }

            @Override
            public void visitField(PsiField psiField)
            {
                checkElement(psiField, "field");
                super.visitField(psiField);
            }

            @Override
            public void visitMethod(PsiMethod psiMethod)
            {
                if (!InspectionUtils.isDefinedInLibrary(psiMethod))
                {
                    checkElement(psiMethod, "method");
                }
                super.visitMethod(psiMethod);
            }

            private void checkElement(PsiElement element, String type)
            {
                if (guardFacet.getConfiguration().findConfiguredGuardKeepers(element).length != 0)
                {
                    return;
                }
                if (guardFacet.getConfiguration().isKeptByMainClass(element))
                {
                    return;
                }
                // no keeper
                final PsiElement nameIdentifierElement = InspectionUtils.getNameIdentifierElement(element);
                GuardGutterRenderer gradeGutterRenderer = new GuardGutterRenderer(Icons.OBFUSCATION_NODE_ICON, "Obfuscated " + type, nameIdentifierElement.getTextRange());
                guardGutterRenderers.add(gradeGutterRenderer);
            }
        });

        return guardGutterRenderers;
    }
}
