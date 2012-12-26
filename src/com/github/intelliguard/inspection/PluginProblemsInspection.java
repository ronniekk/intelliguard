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

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jdom.Element;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiClass;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.ide.plugins.PluginBean;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ComponentConfig;
import com.github.intelliguard.util.PsiUtils;
import com.github.intelliguard.util.InspectionUtils;
import com.github.intelliguard.facet.GuardFacetConfiguration;
import com.github.intelliguard.model.Keeper;

import java.util.*;
import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-14
 * Time: 23:34:12
 */
public class PluginProblemsInspection extends GuardInspectionBase
{
    private static final String STATIC_DESCRIPTION = "This inspection aids plugin developers and detects classes which " +
            "are declared in plugin.xml. These classes can be components, actions or extensions, and their names should " +
            "not be obfuscated in order for the plugin to load correctly.";

    @Nls
    @NotNull
    public String getDisplayName()
    {
        return "Plugin descriptor problems";
    }

    @NotNull
    public String getShortName()
    {
        return "PluginProblems";
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
                final PluginBean pluginDescriptor = getLocalPluginDescriptor();
                if (facetConfiguration != null && pluginDescriptor != null)
                {
                    final Keeper[] obfuscationKeepers = facetConfiguration.findConfiguredGuardKeepers(aClass);
                    if (obfuscationKeepers.length == 0)
                    {
                        final String className = PsiUtils.getKeeperName(aClass);

                        final Set<String> applicationComponentClasses = new HashSet<String>();
                        extractComponentClassNames(applicationComponentClasses, pluginDescriptor.applicationComponents);

                        final Set<String> projectComponentClasses = new HashSet<String>();
                        extractComponentClassNames(projectComponentClasses, pluginDescriptor.projectComponents);

                        final Set<String> moduleComponentClasses = new HashSet<String>();
                        extractComponentClassNames(moduleComponentClasses, pluginDescriptor.moduleComponents);

                        final Set<String> actionClasses = new HashSet<String>();
                        extractElementClassNames(actionClasses, pluginDescriptor.actions);

                        final Set<String> extensionClasses = new HashSet<String>();
                        extractElementClassNames(extensionClasses, pluginDescriptor.extensions);

                        final Set<String> extensionPoints = new HashSet<String>();
                        extractElementClassNames(extensionPoints, pluginDescriptor.extensionPoints);

                        /*
                        System.out.println("ApplicationComponentClasses = " + applicationComponentClasses);
                        System.out.println("ProjectComponentClasses = " + projectComponentClasses);
                        System.out.println("ModuleComponentClasses = " + moduleComponentClasses);
                        System.out.println("ActionClasses = " + actionClasses);
                        System.out.println("ExtensionClasses = " + extensionClasses);
                        System.out.println("ExtensionPoints = " + extensionPoints);
                        */

                        if (applicationComponentClasses.contains(className))
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass),
                                    MessageFormat.format("Class {0} is listed as an ApplicationComponent in {1} and should not be obfuscated", className, PluginManager.PLUGIN_XML));
                        }
                        if (projectComponentClasses.contains(className))
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass),
                                    MessageFormat.format("Class {0} is listed as a ProjectComponent in {1} and should not be obfuscated", className, PluginManager.PLUGIN_XML));
                        }
                        if (moduleComponentClasses.contains(className))
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass),
                                    MessageFormat.format("Class {0} is listed as a ModuleComponent in {1} and should not be obfuscated", className, PluginManager.PLUGIN_XML));
                        }
                        if (actionClasses.contains(className))
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass),
                                    MessageFormat.format("Class {0} is listed as an Action in {1} and should not be obfuscated", className, PluginManager.PLUGIN_XML));
                        }
                        if (extensionClasses.contains(className))
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass),
                                    MessageFormat.format("Class {0} is listed as an Extension in {1} and should not be obfuscated", className, PluginManager.PLUGIN_XML));
                        }
                        if (extensionPoints.contains(className))
                        {
                            holder.registerProblem(InspectionUtils.getNameIdentifierElement(aClass),
                                    MessageFormat.format("Class {0} is listed as an ExtensionPoint in {1} and should not be obfuscated", className, PluginManager.PLUGIN_XML));
                        }
                    }
                }

                super.visitClass(aClass);
            }
        };
    }

    private void extractElementClassNames(@NotNull Collection<String> classNames, @Nullable Element[] elements)
    {
        if (elements != null)
        {
            for (Element element : elements)
            {
                final List children = element.getChildren();
                if (children != null)
                {
                    for (Object child : children)
                    {
                        if (child instanceof Element)
                        {
                            final Element childElement = (Element) child;
                            addIfNotNullAttribute(classNames, childElement, "class");
                            addIfNotNullAttribute(classNames, childElement, "interface");
                            addIfNotNullAttribute(classNames, childElement, "implementation");
                            addIfNotNullAttribute(classNames, childElement, "serviceInterface");
                            addIfNotNullAttribute(classNames, childElement, "serviceImplementation");
                        }
                    }
                }
            }
        }
    }

    private void addIfNotNullAttribute(@NotNull Collection<String> classNames, @NotNull Element element, String attributeName)
    {
        addIfNotNull(classNames, element.getAttributeValue(attributeName));
    }

    private void extractComponentClassNames(@NotNull Collection<String> classes, @Nullable ComponentConfig[] components)
    {
        if (components != null)
        {
            for (ComponentConfig component : components)
            {
                addIfNotNull(classes, component.interfaceClass);
                addIfNotNull(classes, component.implementationClass);
                addIfNotNull(classes, component.headlessImplementationClass);
            }
        }
    }

    private void addIfNotNull(@NotNull Collection<String> names, @Nullable String name)
    {
        if (name != null)
        {
            names.add(name);
        }
    }
}
