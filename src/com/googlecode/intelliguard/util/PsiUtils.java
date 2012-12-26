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

package com.googlecode.intelliguard.util;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-02
 * Time: 20:44:14
 */
public class PsiUtils
{
    private static final String VOID_MAIN_JAVA_LANG_STRING = "void main(java.lang.String[])";

    @NotNull
    public static String getSignatureString(@NotNull PsiMethod method)
    {
        StringBuilder sb = new StringBuilder();

        PsiType returnType = method.getReturnType();
        if (returnType != null)
        {
            final String text = returnType.getCanonicalText();
            sb.append(simplify(text)).append(" ");
        }

        sb.append(method.getName());
        sb.append("(");

        String prepend = "";
        for (PsiParameter psiParameter : method.getParameterList().getParameters())
        {
            sb.append(prepend);
            prepend = ", ";
            final String text = psiParameter.getType().getCanonicalText();
            sb.append(simplify(text));
        }

        sb.append(")");

        return sb.toString();
    }

    private static String simplify(@NotNull String text)
    {
        while (text.contains(">"))
        {
            int eix = text.indexOf('>');
            String before = text.substring(0, eix);
            int six = before.lastIndexOf('<');
            if (six == -1)
            {
                // unmatched pair - abort...
                return text;
            }
            text = text.substring(0, six) + text.substring(eix + 1);
        }
        return text;
    }

    @Nullable
    public static String getKeeperName(@Nullable PsiElement element)
    {
        if (element == null)
        {
            return null;
        }

        if (element instanceof PsiClass)
        {
            final PsiClass psiClass = (PsiClass) element;
            final PsiClass containingClass = psiClass.getContainingClass();
            if (containingClass != null)
            {
                return getKeeperName(containingClass) + "$" + psiClass.getName();
            }
            return psiClass.getQualifiedName();
        }
        else if (element instanceof PsiMethod)
        {
            return getSignatureString((PsiMethod) element);
        }
        else if (element instanceof PsiField)
        {
            return ((PsiField) element).getName();
        }
        else if (element instanceof PsiPackage)
        {
            return ((PsiPackage) element).getQualifiedName();
        }

        return null;
    }

    @Nullable
    public static String getClazzName(@Nullable PsiElement element)
    {
        if (element == null)
        {
            return null;
        }

        if (element instanceof PsiMethod)
        {
            final PsiClass psiClass = ((PsiMethod) element).getContainingClass();
            return psiClass == null ? null : PsiUtils.getKeeperName(psiClass);
        }
        else if (element instanceof PsiField)
        {
            final PsiClass psiClass = ((PsiField) element).getContainingClass();
            return psiClass == null ? null : PsiUtils.getKeeperName(psiClass);
        }

        return null;
    }

    public static boolean isPublicStaticVoidMain(@NotNull PsiMethod method)
    {
        final PsiModifierList modifierList = method.getModifierList();
        return modifierList.hasModifierProperty("public")
                && modifierList.hasModifierProperty("static")
                && VOID_MAIN_JAVA_LANG_STRING.equals(getSignatureString(method));
    }

    public static void main(String[] args)
    {
        String in = "java.util.Collection<java.lang.String> foobar(java.util.List<java.util.List<java.lang.String>>)";
        System.out.println("in = " + in);
        String out = simplify(in);
        System.out.println("out = " + out);
    }
}
