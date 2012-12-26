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

import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.options.ConfigurationException;
import com.github.intelliguard.ui.YFacetConfigurationForm;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-okt-21
 * Time: 19:08:51
 */
public class GuardFacetEditorTab extends FacetEditorTab
{
    private GuardFacetConfiguration originalState;
    private GuardFacetConfiguration currentState;
    private YFacetConfigurationForm yFacetConfigurationForm;

    public GuardFacetEditorTab(GuardFacetConfiguration originalState, FacetEditorContext editorContext, FacetValidatorsManager validatorsManager)
    {
        this.originalState = originalState;
        this.currentState = new GuardFacetConfiguration();
        this.currentState.loadState(originalState);
        yFacetConfigurationForm = new YFacetConfigurationForm(editorContext, validatorsManager, currentState);
    }

    @Nls
    public String getDisplayName()
    {
        return "IntelliGuard Configuration";
    }

    public JComponent createComponent()
    {
        return yFacetConfigurationForm.getPanel();
    }

    public boolean isModified()
    {
        return !currentState.equalsGlobalSettings(originalState);
    }

    public void apply() throws ConfigurationException
    {
        originalState.applyGlobalSettings(currentState);
    }

    public void reset()
    {
    }

    public void disposeUIResources()
    {
    }
}
