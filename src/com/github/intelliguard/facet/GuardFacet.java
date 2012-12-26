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

package com.github.intelliguard.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-okt-21
 * Time: 19:03:44
 */
public class GuardFacet extends Facet<GuardFacetConfiguration>
{
    public static final FacetTypeId<GuardFacet> ID = new FacetTypeId<GuardFacet>("IntelliGuard");

    public GuardFacet(@NotNull Module module) {
      this(FacetTypeRegistry.getInstance().findFacetType(ID), module, "IntelliGuard", new GuardFacetConfiguration(), null);
    }

    public GuardFacet(@NotNull FacetType facetType, @NotNull Module module, String name, @NotNull GuardFacetConfiguration configuration, Facet underlyingFacet)
    {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @Nullable
    public static GuardFacet getInstance(@NotNull Module module)
    {
        return FacetManager.getInstance(module).getFacetByType(ID);
    }
}
