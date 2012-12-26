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

package com.googlecode.intelliguard.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.googlecode.intelliguard.facet.GuardFacet;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-10
 * Time: 21:22:59
 */
public class ExportGroup extends DefaultActionGroup
{
    @Nullable
    protected Module getModule(AnActionEvent e)
    {
        return DataKeys.MODULE.getData(e.getDataContext());
    }

    @Nullable
    protected GuardFacet getGuardFacet(@Nullable Module module)
    {
        return module == null ? null : GuardFacet.getInstance(module);
    }

    @Override
    public void update(AnActionEvent e)
    {
        final Presentation presentation = e.getPresentation();
        final Module module = getModule(e);
        if (module != null)
        {
            final GuardFacet guardFacet = getGuardFacet(module);
            if (guardFacet != null)
            {
                presentation.setEnabled(true);
                return;
            }
        }
        presentation.setEnabled(false);
    }
}
