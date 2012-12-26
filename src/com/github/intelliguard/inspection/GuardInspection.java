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

package com.github.intelliguard.inspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.*;
import com.github.intelliguard.facet.GuardFacetConfiguration;
import com.github.intelliguard.model.Keeper;
import com.github.intelliguard.util.PsiUtils;
import com.github.intelliguard.util.InspectionUtils;
import com.github.intelliguard.fix.RemoveKeepFix;
import com.github.intelliguard.fix.AddKeepFix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nls;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-02
 * Time: 19:04:46
 */
public class GuardInspection extends GuardInspectionBase
{
    private static final String STATIC_DESCRIPTION = "This inspection detects if a class, method or field will be " +
            "obfuscated according to the current configuration, and offers quick-fixes to either keep from obfuscation " +
            "or remove an existing keeper.";

    @Nls
    @NotNull
    public String getDisplayName()
    {
        return "Obfuscation options";
    }

    @NotNull
    public String getShortName()
    {
        return "GuardInspection";
    }

    @Override
    public String getStaticDescription()
    {
        return STATIC_DESCRIPTION;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly)
    {
        return new JavaElementVisitor()
        {
            public void visitReferenceExpression(PsiReferenceExpression expression)
            {
            }

            @Override
            public void visitClass(PsiClass aClass)
            {
                if (!isOnTheFly)
                {
                    return;
                }
                
                GuardFacetConfiguration configuration = getLocalConfiguration();
                if (configuration != null)
                {
                    if (configuration.isKeptByMainClass(aClass))
                    {
                        holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass), "Class is not obfuscated due to Main-Class", ProblemHighlightType.INFORMATION);
                    }
                    else
                    {
                        final Keeper[] configuredGuardKeepers = configuration.findConfiguredGuardKeepers(aClass);
                        if (configuredGuardKeepers.length != 0)
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass), "Class is not obfuscated", ProblemHighlightType.INFORMATION, createRemoveKeeperFixes(configuration, configuredGuardKeepers, aClass));
                        }
                        else
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass), "Class is obfuscated", ProblemHighlightType.INFORMATION, createAddClassKeeperFixes(configuration, aClass));
                        }
                    }
                }

                super.visitClass(aClass);
            }

            @Override
            public void visitField(PsiField field)
            {
                if (!isOnTheFly)
                {
                    return;
                }

                GuardFacetConfiguration configuration = getLocalConfiguration();
                if (configuration != null)
                {
                    final Keeper[] configuredGuardKeepers = configuration.findConfiguredGuardKeepers(field);
                    if (configuredGuardKeepers.length != 0)
                    {
                        holder.registerProblem(InspectionUtils.getNameIdentifierElement(field), "Field is not obfuscated", ProblemHighlightType.INFORMATION, createRemoveKeeperFixes(configuration, configuredGuardKeepers, field));
                    }
                    else
                    {
                        holder.registerProblem(InspectionUtils.getNameIdentifierElement(field), "Field is obfuscated", ProblemHighlightType.INFORMATION, createAddFieldKeeperFixes(configuration, field));
                    }
                }

                super.visitField(field);
            }

            @Override
            public void visitMethod(PsiMethod method)
            {
                if (!isOnTheFly)
                {
                    return;
                }

                GuardFacetConfiguration configuration = getLocalConfiguration();
                if (configuration != null && !InspectionUtils.isDefinedInLibrary(method))
                {
                    if (configuration.isKeptByMainClass(method))
                    {
                        holder.registerProblem(InspectionUtils.getNameIdentifierElement(method), "Method is not obfuscated due to Main-Class", ProblemHighlightType.INFORMATION);
                    }
                    else
                    {
                        final Keeper[] configuredGuardKeepers = configuration.findConfiguredGuardKeepers(method);
                        if (configuredGuardKeepers.length != 0)
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(method), "Method is not obfuscated", ProblemHighlightType.INFORMATION, createRemoveKeeperFixes(configuration, configuredGuardKeepers, method));
                        }
                        else
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(method), "Method is obfuscated", ProblemHighlightType.INFORMATION, createAddMethodKeeperFixes(configuration, method));
                        }
                    }
                }

                super.visitMethod(method);
            }
        };
    }

    private LocalQuickFix[] createAddClassKeeperFixes(final GuardFacetConfiguration configuration, final PsiClass aClass)
    {
        Collection<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();

        Keeper keeper = new Keeper();
        keeper.setType(Keeper.Type.CLASS);
        keeper.setName(PsiUtils.getKeeperName(aClass));

        fixes.add(new AddKeepFix(configuration, keeper, aClass));

        return fixes.toArray(new LocalQuickFix[fixes.size()]);
    }

    private LocalQuickFix[] createAddFieldKeeperFixes(final GuardFacetConfiguration configuration, final PsiField field)
    {
        Collection<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
        String name = field.getName();

        Keeper keeper = new Keeper();
        keeper.setType(Keeper.Type.FIELD);
        keeper.setName(name);
        PsiClass containingClass = field.getContainingClass();
        if (containingClass != null)
        {
            keeper.setClazz(PsiUtils.getKeeperName(containingClass));
            fixes.add(new AddKeepFix(configuration, keeper, field));
        }

        keeper = new Keeper();
        keeper.setType(Keeper.Type.FIELD);
        keeper.setName(name);
        fixes.add(new AddKeepFix(configuration, keeper, field));

        return fixes.toArray(new LocalQuickFix[fixes.size()]);
    }

    private LocalQuickFix[] createAddMethodKeeperFixes(final GuardFacetConfiguration configuration, final PsiMethod method)
    {
        if (method.isConstructor())
        {
            return new LocalQuickFix[0];
        }
        final PsiMethod[] superMethods = method.findDeepestSuperMethods();
        if (superMethods.length != 0)
        {
            return new LocalQuickFix[0];
        }

        Collection<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
        String signature = PsiUtils.getSignatureString(method);

        Keeper keeper = new Keeper();
        keeper.setType(Keeper.Type.METHOD);
        keeper.setName(signature);
        PsiClass containingClass = method.getContainingClass();
        if (containingClass != null)
        {
            keeper.setClazz(PsiUtils.getKeeperName(containingClass));
            fixes.add(new AddKeepFix(configuration, keeper, method));
        }

        keeper = new Keeper();
        keeper.setType(Keeper.Type.METHOD);
        keeper.setName(signature);
        fixes.add(new AddKeepFix(configuration, keeper, method));

        return fixes.toArray(new LocalQuickFix[fixes.size()]);
    }

    private LocalQuickFix[] createRemoveKeeperFixes(final GuardFacetConfiguration configuration, final Keeper[] keepers, final PsiElement element)
    {
        if (element instanceof PsiMethod)
        {
            PsiMethod psiMethod = (PsiMethod) element;
            if (psiMethod.isConstructor())
            {
                return new LocalQuickFix[0];
            }
        }
        Collection<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
        for (Keeper keeper : keepers)
        {
            fixes.add(new RemoveKeepFix(configuration, keeper, element));
        }
        return fixes.toArray(new LocalQuickFix[fixes.size()]);
    }
}
