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

package com.github.intelliguard.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiClass;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-09
 * Time: 18:44:16
 */
public class InspectionUtils
{
    @NotNull
    public static PsiElement getNameIdentifierElement(@NotNull PsiElement owner)
    {
        if (owner instanceof PsiNameIdentifierOwner)
        {
            PsiElement identifierElement = ((PsiNameIdentifierOwner) owner).getNameIdentifier();
            if (identifierElement != null)
            {
                return identifierElement;
            }
        }
        return owner;
    }

    public static boolean isDefinedInLibrary(@NotNull PsiMethod method)
    {
        final PsiMethod[] superMethods = method.findDeepestSuperMethods();
        if (superMethods.length != 0)
        {
            for (PsiMethod superMethod : superMethods)
            {
                final Module superModule = ModuleUtil.findModuleForPsiElement(superMethod);
                if (superModule == null || !superModule.equals(ModuleUtil.findModuleForPsiElement(method)))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Find every superclass until (but not inclusive) <tt>java.lang.Object</tt>.
     * @param aClass starting class
     * @return a collection of classes not including neither
     * the starting class nor the ending class <tt>java.jang.Object</tt>
     */
    @NotNull
    public static Collection<PsiClass> resolveAllSuperClasses(@NotNull PsiClass aClass)
    {
        Collection<PsiClass> superClasses = new ArrayList<PsiClass>();
        collectSuperClasses(superClasses, aClass);
        return superClasses;
    }

    private static void collectSuperClasses(Collection<PsiClass> collector, PsiClass aClass)
    {
        final PsiClass superClass = aClass.getSuperClass();
        if (superClass != null)
        {
            final String name = superClass.getQualifiedName();
            if ("java.lang.Object".equals(name))
            {
                return;
            }
            collector.add(superClass);
            collectSuperClasses(collector, superClass);
        }
    }

    /**
     * Find every interface implemented by a given class and it's superclasses (if given).
     * @param aClass starting class
     * @param superClasses the superclasses to include in search - may be <tt>null</tt> or empty
     * @return a collection containing every interface implemented
     */
    @NotNull
    public static Collection<PsiClass> resolveInterfaces(@NotNull PsiClass aClass, @Nullable Collection<PsiClass> superClasses)
    {
        Collection<PsiClass> collector = new ArrayList<PsiClass>();
        for (PsiClass iface : aClass.getInterfaces())
        {
            collectInterfaces(collector, iface);
        }

        if (superClasses != null)
        {
            for (PsiClass superClass : superClasses)
            {
                collector.addAll(resolveInterfaces(superClass, null));
            }
        }

        return collector;
    }

    private static void collectInterfaces(@NotNull Collection<PsiClass> collector, @NotNull PsiClass anInterface)
    {
        collector.add(anInterface);
        PsiClass[] interfaces = anInterface.getInterfaces();
        if (interfaces != null)
        {
            for (PsiClass iface : interfaces)
            {
                collectInterfaces(collector, iface);
            }
        }
    }

}
