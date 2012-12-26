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

package com.github.intelliguard;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.codeInspection.InspectionToolProvider;
import com.github.intelliguard.facet.GuardFacetType;
import com.github.intelliguard.inspection.GuardInspection;
import com.github.intelliguard.inspection.SerializationProblemsInspection;
import com.github.intelliguard.inspection.PluginProblemsInspection;
import com.github.intelliguard.inspection.ReflectionProblemsInspection;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-okt-21
 * Time: 19:27:37
 */
public class GuardComponent implements ApplicationComponent, InspectionToolProvider
{
    public GuardComponent()
    {
    }

    public void initComponent()
    {
        FacetTypeRegistry.getInstance().registerFacetType(GuardFacetType.getInstance());
    }

    public void disposeComponent()
    {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName()
    {
        return "GuardComponent";
    }

    public Class[] getInspectionClasses()
    {
        return new Class[] {
                GuardInspection.class,
                SerializationProblemsInspection.class,
                PluginProblemsInspection.class,
                ReflectionProblemsInspection.class
        };
    }
}
