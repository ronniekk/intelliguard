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

package com.googlecode.intelliguard.inspection;

import com.googlecode.intelliguard.facet.GuardFacetConfiguration;
import com.googlecode.intelliguard.model.Keeper;
import com.googlecode.intelliguard.util.InspectionUtils;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-21
 * Time: 14:58:13
 */
public class ReflectionProblemsInspection extends GuardInspectionBase
{
    private static final String STATIC_DESCRIPTION = "This inspection detects classes which " +
            "under usual conditions need to have their class names kept for reflection purposes, e.g. servlets, " +
            "applets, etcetera.";

    private static final Map<String, String> EXTENDS = new HashMap<String, String>();
    private static final Map<String, String> IMPLEMENTS = new HashMap<String, String>();
    static
    {
        EXTENDS.put("javax.servlet.http.HttpServlet", "HTTP Servlet");
        EXTENDS.put("java.applet.Applet", "Applet");
        EXTENDS.put("javax.microedition.midlet.MIDlet", "MIDlet");
        EXTENDS.put("android.app.Activity", "Android Activity");
        EXTENDS.put("android.app.Service", "Android Service");
        EXTENDS.put("android.content.BroadcastReceiver", "Android Broadcast Receiver");
        EXTENDS.put("android.content.ContentProvider", "Android Content Provider");
        EXTENDS.put("org.apache.struts.action.Action", "Struts Action");
        IMPLEMENTS.put("javacard.framework.Applet", "Java Card");
        IMPLEMENTS.put("javax.tv.xlet.Xlet", "Xlet");
        IMPLEMENTS.put("java.sql.Driver", "SQL Driver");
        IMPLEMENTS.put("com.opensymphony.xwork2.Action", "Struts 2 Action");
    }

    @Nls
    @NotNull
    public String getDisplayName()
    {
        return "Reflection problems";
    }

    @NotNull
    public String getShortName()
    {
        return "ReflectionProblems";
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
            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression)
            {
            }

            @Override
            public void visitClass(PsiClass aClass)
            {
                final GuardFacetConfiguration facetConfiguration = getLocalConfiguration();
                if (facetConfiguration != null)
                {
                    if (aClass != null && !aClass.isInterface() && !aClass.hasModifierProperty("abstract"))
                    {
                        final Keeper[] obfuscationKeepers = facetConfiguration.findConfiguredGuardKeepers(aClass);
                        if (obfuscationKeepers.length == 0)
                        {
                            checkClass(holder, aClass);
                        }
                    }
                }

                super.visitClass(aClass);
            }
        };
    }

    private void checkClass(@NotNull final ProblemsHolder holder, @NotNull PsiClass aClass)
    {
        final Collection<PsiClass> supers = InspectionUtils.resolveAllSuperClasses(aClass);
        for (PsiClass superClass : supers)
        {
            final String description = EXTENDS.get(superClass.getQualifiedName());
            if (description != null)
            {
                holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass), description + " name should probably not be obfuscated");
            }
        }

        final Collection<PsiClass> interfaces = InspectionUtils.resolveInterfaces(aClass, supers);
        for (PsiClass iface : interfaces)
        {
            final String description = IMPLEMENTS.get(iface.getQualifiedName());
            if (description != null)
            {
                holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass), description + " name should probably not be obfuscated");
            }
        }

    }

}
