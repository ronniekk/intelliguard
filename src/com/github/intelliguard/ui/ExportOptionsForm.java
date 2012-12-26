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

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.module.Module;
import com.github.intelliguard.facet.GuardFacetConfiguration;
import com.github.intelliguard.facet.GuardFacet;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-dec-03
 * Time: 19:49:59
 */
public class ExportOptionsForm
{
    private JPanel contentPane;
    private TextFieldWithBrowseButton mainClass;
    private TextFieldWithBrowseButton obfuscatedJarPath;
    private TextFieldWithBrowseButton jarPath;

    public ExportOptionsForm(@NotNull GuardFacet guardFacet)
    {
        final Module module = guardFacet.getModule();
        final GuardFacetConfiguration facetConfiguration = guardFacet.getConfiguration();

        mainClass.getTextField().setText(facetConfiguration.mainclass == null ? "" : facetConfiguration.mainclass);
        mainClass.addActionListener(new MainClassChooser(module, mainClass));

        jarPath.getTextField().setText(facetConfiguration.inFile == null ? "" : facetConfiguration.inFile);
        jarPath.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser chooser = FileChooserFactory.createSaveJarChooser(jarPath.getText(), module.getModuleFilePath());
                int res = chooser.showSaveDialog(contentPane);
                if (res == JFileChooser.APPROVE_OPTION && chooser.getFileFilter().accept(chooser.getSelectedFile()))
                {
                    jarPath.getTextField().setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        obfuscatedJarPath.getTextField().setText(facetConfiguration.outFile == null ? "" : facetConfiguration.outFile);
        obfuscatedJarPath.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser chooser = FileChooserFactory.createSaveJarChooser(obfuscatedJarPath.getText(), jarPath.getText(), module.getModuleFilePath());
                int res = chooser.showSaveDialog(contentPane);
                if (res == JFileChooser.APPROVE_OPTION && chooser.getFileFilter().accept(chooser.getSelectedFile()))
                {
                    obfuscatedJarPath.getTextField().setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
    }

    public JPanel getContentPane()
    {
        return contentPane;
    }

    public String getJarPath()
    {
        return jarPath.getText();
    }

    public String getObfuscatedJarPath()
    {
        return obfuscatedJarPath.getText();
    }

    public String getMainClass()
    {
        return mainClass.getText();
    }
}
