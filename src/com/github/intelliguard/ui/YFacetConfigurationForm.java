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

package com.github.intelliguard.ui;

import com.github.intelliguard.facet.GuardFacetConfiguration;
import com.github.intelliguard.util.PsiUtils;
import com.github.intelliguard.util.ObfuscatorUtils;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorValidator;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.ValidationResult;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiClass;
import com.intellij.ui.DocumentAdapter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-okt-21
 * Time: 22:55:01
 */
public class YFacetConfigurationForm
{
    private JCheckBox sourceFileCheckBox;
    private JCheckBox linenumberTableCheckBox;
    private JCheckBox localVariableTableCheckBox;
    private JCheckBox localVariableTypeTableCheckBox;
    private JCheckBox runtimeVisibleAnnotationsCheckBox;
    private JCheckBox runtimeVisibleParameterAnnotationsCheckBox;
    private JCheckBox runtimeInvisibleAnnotationsCheckBox;
    private JCheckBox runtimeInvisibleParameterAnnotationsCheckBox;
    private JCheckBox conserveManifestCheckBox;
    private JCheckBox replaceClassNameStringsCheckBox;
    private JPanel panel;
    private JCheckBox pedanticErrorCheckingCheckBox;
    private TextFieldWithBrowseButton mainClass;
    private JButton downloadYGuardButton;
    private TextFieldWithBrowseButton yJarPath;
    private static final String YGUARD_DOWNLOAD_URL = "http://www.yworks.com/en/products_yguard_about.html";

    public YFacetConfigurationForm(@NotNull final FacetEditorContext editorContext, @NotNull final FacetValidatorsManager validatorsManager, @NotNull final GuardFacetConfiguration state)
    {
        sourceFileCheckBox.setSelected(state.sourcefile);
        linenumberTableCheckBox.setSelected(state.linenumbertable);
        localVariableTableCheckBox.setSelected(state.localvariabletable);
        localVariableTypeTableCheckBox.setSelected(state.localvariabletypetable);
        runtimeVisibleAnnotationsCheckBox.setSelected(state.runtimevisibleannotations);
        runtimeVisibleParameterAnnotationsCheckBox.setSelected(state.runtimevisibleparameterannotations);
        runtimeInvisibleAnnotationsCheckBox.setSelected(state.runtimeinvisibleannotations);
        runtimeInvisibleParameterAnnotationsCheckBox.setSelected(state.runtimeinvisibleparameterannotations);
        conserveManifestCheckBox.setSelected(state.conservemanifest);
        replaceClassNameStringsCheckBox.setSelected(state.replaceClassNameStrings);
        pedanticErrorCheckingCheckBox.setSelected(state.errorChecking);
        mainClass.getTextField().setText(state.mainclass != null ? state.mainclass : "");
        yJarPath.getTextField().setText(state.yGuardJar != null ? state.yGuardJar : "");

        sourceFileCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.sourcefile = sourceFileCheckBox.isSelected();
            }
        });
        linenumberTableCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.linenumbertable = linenumberTableCheckBox.isSelected();
            }
        });
        localVariableTableCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.localvariabletable = localVariableTableCheckBox.isSelected();
            }
        });
        localVariableTypeTableCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.localvariabletypetable = localVariableTypeTableCheckBox.isSelected();
            }
        });
        runtimeVisibleAnnotationsCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.runtimevisibleannotations = runtimeVisibleAnnotationsCheckBox.isSelected();
            }
        });
        runtimeVisibleParameterAnnotationsCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.runtimevisibleparameterannotations = runtimeVisibleParameterAnnotationsCheckBox.isSelected();
            }
        });
        runtimeInvisibleAnnotationsCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.runtimeinvisibleannotations = runtimeInvisibleAnnotationsCheckBox.isSelected();
            }
        });
        runtimeInvisibleParameterAnnotationsCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.runtimeinvisibleparameterannotations = runtimeInvisibleParameterAnnotationsCheckBox.isSelected();
            }
        });
        conserveManifestCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.conservemanifest = conserveManifestCheckBox.isSelected();
            }
        });
        replaceClassNameStringsCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.replaceClassNameStrings = replaceClassNameStringsCheckBox.isSelected();
            }
        });
        pedanticErrorCheckingCheckBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                state.errorChecking = pedanticErrorCheckingCheckBox.isSelected();
            }
        });

        mainClass.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                TreeClassChooser classChooser = TreeClassChooserFactory.getInstance(editorContext.getProject()).createProjectScopeChooser("Choose Main-Class");
                classChooser.showDialog();
                PsiClass psiClass = classChooser.getSelected();
                if (psiClass != null)
                {
                    String className = PsiUtils.getKeeperName(psiClass);
                    // state.mainclass = className;
                    mainClass.getTextField().setText(className);
                }
            }
        });
        mainClass.getTextField().getDocument().addDocumentListener(new DocumentAdapter()
        {
            @Override
            protected void textChanged(DocumentEvent e)
            {
                state.mainclass = mainClass.getText();
            }
        });

        yJarPath.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser chooser = FileChooserFactory.createFindJarChooser(yJarPath.getText(), yJarPath.getText());
                int res = chooser.showSaveDialog(getPanel());
                if (res == JFileChooser.APPROVE_OPTION && chooser.getFileFilter().accept(chooser.getSelectedFile()))
                {
                    yJarPath.getTextField().setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        yJarPath.getTextField().getDocument().addDocumentListener(new DocumentAdapter()
        {
            @Override
            protected void textChanged(DocumentEvent e)
            {
                state.yGuardJar = yJarPath.getText();
            }
        });

        downloadYGuardButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                BrowserUtil.launchBrowser(YGUARD_DOWNLOAD_URL);
            }
        });
        validatorsManager.registerValidator(new FacetEditorValidator()
        {
            @Override
            public ValidationResult check()
            {
                return ObfuscatorUtils.checkYGuard(yJarPath.getText());
            }
        }, yJarPath);
    }

    public JPanel getPanel()
    {
        return panel;
    }
}
