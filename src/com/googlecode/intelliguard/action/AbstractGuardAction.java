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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.googlecode.intelliguard.facet.GuardFacet;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-10
 * Time: 21:09:18
 */
public abstract class AbstractGuardAction extends AnAction
{
    // just a bunch of helpers
    
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

    @Nullable
    protected GuardFacet getGuardFacet(AnActionEvent e)
    {
        final Module module = getModule(e);
        return module == null ? null : GuardFacet.getInstance(module);
    }

    @Nullable
    protected Project getProject(AnActionEvent e)
    {
        return DataKeys.PROJECT.getData(e.getDataContext());
    }

    @Nullable
    protected Document getDocument(AnActionEvent e)
    {
        final Editor editor = getEditor(e);
        return editor == null ? null : editor.getDocument();
    }

    @Nullable
    private Editor getEditor(AnActionEvent e)
    {
        return DataKeys.EDITOR.getData(e.getDataContext());
    }
}
