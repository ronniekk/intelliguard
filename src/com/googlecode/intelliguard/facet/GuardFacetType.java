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

package com.googlecode.intelliguard.facet;

import com.intellij.facet.FacetType;
import com.intellij.facet.Facet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.googlecode.intelliguard.ui.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-okt-21
 * Time: 19:06:57
 */
public class GuardFacetType extends FacetType<GuardFacet, GuardFacetConfiguration>
{
    private static final GuardFacetType instance = new GuardFacetType();
    private static final String JAVA_MODULE = "JAVA_MODULE";
    private static final String PLUGIN_MODULE = "PLUGIN_MODULE";
    private static final String J2ME_MODULE = "J2ME_MODULE";
    private static final String ANDROID_MODULE = "ANDROID_MODULE";

    private GuardFacetType()
    {
        super(GuardFacet.ID, "IntelliGuard", "Obfuscation");
    }

    public static GuardFacetType getInstance()
    {
        return instance;
    }

    @Override
    public Icon getIcon()
    {
        return Icons.OBFUSCATION_NODE_ICON;
    }

    public GuardFacetConfiguration createDefaultConfiguration()
    {
        return new GuardFacetConfiguration();
    }

    public GuardFacet createFacet(@NotNull Module module, String name, @NotNull GuardFacetConfiguration configuration, @Nullable Facet underlyingFacet)
    {
        return new GuardFacet(getInstance(), module, name, configuration, underlyingFacet);
    }

    public boolean isSuitableModuleType(ModuleType moduleType)
    {
        if (moduleType == null)
        {
            return false;
        }
        final String moduleId = moduleType.getId();
        return J2ME_MODULE.equals(moduleId) || JAVA_MODULE.equals(moduleId) || PLUGIN_MODULE.equals(moduleId) || ANDROID_MODULE.equals(moduleId);
    }
}
