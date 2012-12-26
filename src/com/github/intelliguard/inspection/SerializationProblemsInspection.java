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

import com.github.intelliguard.facet.GuardFacetConfiguration;
import com.github.intelliguard.model.Keeper;
import com.github.intelliguard.util.InspectionUtils;
import com.github.intelliguard.util.PsiUtils;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-08
 * Time: 19:56:13
 */
public class SerializationProblemsInspection extends GuardInspectionBase
{
    private static final String STATIC_DESCRIPTION = "This inspection detects fields and methods in serializable " +
            "classes which are used for serialization and must have their exact signature kept.";
    private static final String JAVA_IO_SERIALIZABLE = "java.io.Serializable";
    private static final String SERIAL_VERSION_UID = "serialVersionUID";
    private static final String SERIAL_PERSISTENT_FIELDS = "serialPersistentFields";
    private static final String METHOD_WRITE_OBJECT = "void writeObject(java.io.ObjectOutputStream)";
    private static final String METHOD_READ_OBJECT = "void readObject(java.io.ObjectInputStream)";
    private static final String METHOD_WRITE_REPLACE = "java.lang.Object writeReplace()";
    private static final String METHOD_READ_RESOLVE = "java.lang.Object readResolve()";

    @Nls
    @NotNull
    public String getDisplayName()
    {
        return "Serialization problems";
    }

    @NotNull
    public String getShortName()
    {
        return "SerializationProblems";
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
                super.visitClass(aClass);
            }

            @Override
            public void visitField(PsiField field)
            {
                GuardFacetConfiguration configuration = getLocalConfiguration();
                if (configuration != null)
                {
                    if (implementsSerializable(field.getContainingClass()))
                    {
                        final Keeper[] configuredGuardKeepers = configuration.findConfiguredGuardKeepers(field);
                        if (configuredGuardKeepers.length == 0)
                        {
                            final String name = PsiUtils.getKeeperName(field);
                            if (SERIAL_VERSION_UID.equals(name) || SERIAL_PERSISTENT_FIELDS.equals(name))
                            {
                                holder.registerProblem(InspectionUtils.getNameIdentifierElement(field),
                                        MessageFormat.format("Class implements {0} but field should not be obfuscated in order for serialization to work.", JAVA_IO_SERIALIZABLE));
                            }
                        }
                    }
                }

                super.visitField(field);
            }

            @Override
            public void visitMethod(PsiMethod method)
            {
                GuardFacetConfiguration configuration = getLocalConfiguration();
                if (configuration != null)
                {
                    if (implementsSerializable(method.getContainingClass()))
                    {
                        final Keeper[] configuredGuardKeepers = configuration.findConfiguredGuardKeepers(method);
                        if (configuredGuardKeepers.length == 0)
                        {
                            final String name = PsiUtils.getKeeperName(method);
                            if (METHOD_READ_OBJECT.equals(name)
                                    || METHOD_WRITE_OBJECT.equals(name)
                                    || METHOD_READ_RESOLVE.equals(name)
                                    || METHOD_WRITE_REPLACE.equals(name))
                            {
                                holder.registerProblem(InspectionUtils.getNameIdentifierElement(method),
                                        MessageFormat.format("Class implements {0} but method should not be obfuscated in order for serialization to work.", JAVA_IO_SERIALIZABLE));
                            }
                        }
                    }
                }

                super.visitMethod(method);
            }
        };
    }

    private boolean implementsSerializable(PsiClass psiClass)
    {
        final PsiClass[] interfaces = psiClass.getInterfaces();
        for (PsiClass anInterface : interfaces)
        {
            if (JAVA_IO_SERIALIZABLE.equals(anInterface.getQualifiedName()))
            {
                return true;
            }
        }
        return false;
    }
}
