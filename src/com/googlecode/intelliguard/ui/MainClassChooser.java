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

package com.googlecode.intelliguard.ui;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.psi.PsiClass;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.googlecode.intelliguard.util.PsiUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-dec-03
 * Time: 19:58:54
 */
public class MainClassChooser implements ActionListener
{
    private final Module module;
    private final TextFieldWithBrowseButton mainClass;

    public MainClassChooser(Module module, TextFieldWithBrowseButton mainClass)
    {
        this.module = module;
        this.mainClass = mainClass;
    }

    public void actionPerformed(ActionEvent e)
    {
        TreeClassChooser classChooser = TreeClassChooserFactory.getInstance(module.getProject()).createProjectScopeChooser("Choose Main-Class");
        classChooser.showDialog();
        PsiClass psiClass = classChooser.getSelectedClass();
        if (psiClass != null)
        {
            String className = PsiUtils.getKeeperName(psiClass);
            // state.mainclass = className;
            mainClass.getTextField().setText(className);
        }
    }
}
