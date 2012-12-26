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

package com.github.intelliguard.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.github.intelliguard.facet.GuardFacetConfiguration;
import com.github.intelliguard.model.Keeper;
import com.github.intelliguard.inspection.GuardInspectionBase;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-02
 * Time: 21:26:45
 */
public class RemoveKeepFix implements LocalQuickFix
{
    private final GuardFacetConfiguration configuration;
    private final Keeper keeper;
    private final PsiElement element;

    public RemoveKeepFix(@NotNull final GuardFacetConfiguration configuration, @NotNull final Keeper keeper, @NotNull final PsiElement element)
    {
        this.configuration = configuration;
        this.keeper = keeper;
        this.element = element;
    }

    @NotNull
    public String getName()
    {
        return "Remove " + keeper.getType().getName() + " keeper" + (keeper.getType() == Keeper.Type.CLASS ? "" : (keeper.getClazz() == null ? " in all classes" : " in class " + keeper.getClazz()));
    }

    @NotNull
    public String getFamilyName()
    {
        return "Remove guard family";
    }

    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor)
    {
        configuration.keepers.remove(keeper);
        GuardInspectionBase.alertGuardMarkers(element);
    }
}
