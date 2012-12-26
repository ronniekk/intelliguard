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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.googlecode.intelliguard.facet.GuardFacet;
import com.googlecode.intelliguard.facet.GuardFacetConfiguration;
import com.googlecode.intelliguard.GuardProjectComponent;
import com.googlecode.intelliguard.ui.FileChooserFactory;
import com.googlecode.intelliguard.ui.FormDialogWrapper;
import com.googlecode.intelliguard.ui.ExportOptionsForm;
import com.googlecode.intelliguard.ui.Icons;
import com.googlecode.intelliguard.util.UiUtils;
import com.googlecode.intelliguard.runner.ProgressInfoReceiver;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-10
 * Time: 20:55:33
 */
public abstract class AbstractExportAction extends AbstractGuardAction
{
    private static final String NL = "\n";
    private static final String LINE_SEP = System.getProperty("line.separator");

    public void actionPerformed(AnActionEvent e)
    {
        final Module module = getModule(e);
        if (module == null)
        {
            return;
        }
        final GuardFacet guardFacet = getGuardFacet(module);
        if (guardFacet == null)
        {
            return;
        }

        final ExportOptionsForm exportOptionsForm = FormDialogWrapper.showExportOptionsForm(guardFacet);
        if (exportOptionsForm == null)
        {
            // user aborted
            return;
        }

        GuardFacetConfiguration configuration = guardFacet.getConfiguration();
        configuration.mainclass = exportOptionsForm.getMainClass();
        configuration.inFile = exportOptionsForm.getJarPath();
        configuration.outFile = exportOptionsForm.getObfuscatedJarPath();

        String errorMessage = null;
        if (configuration.inFile.length() == 0)
        {
            errorMessage = "Output jar path not specified";
        }
        else if (configuration.outFile.length() == 0)
        {
            errorMessage = "Obfuscation jar path not specified";
        }
        else if (configuration.inFile.equals(configuration.outFile))
        {
            errorMessage = "Output jar path and obfuscated jar path can not be the same";
        }
        if (errorMessage != null)
        {
            Messages.showErrorDialog(module.getProject(), errorMessage, "Export error");
            return;
        }

        // output configuration to toolwindow
        final String config = generateConfiguration(guardFacet);
        final ProgressInfoReceiver receiver = module.getProject().getComponent(GuardProjectComponent.class).createProgressInfoReceiver();
        receiver.info(config);

        // ask for saving to file
        final int answer = Messages.showYesNoCancelDialog(module.getProject(), "Would you like to export configuration to a file?", "Export configuration", Icons.OBFUSCATION_NODE_ICON);
        if (answer == 0)
        {
            // show file chooser
            final Component component = DataKeys.CONTEXT_COMPONENT.getData(e.getDataContext());
            final JFileChooser jFileChooser = FileChooserFactory.createPreferredDirectoryFileChooser("Save '" + module.getName() + "' obfuscation settings",
                    module.getModuleFilePath());
            // suggest a suitable name for the output file
            jFileChooser.setSelectedFile(new File(jFileChooser.getCurrentDirectory(), module.getName() + "-obfuscation." + getConfigFileExtension()));
            int res = jFileChooser.showSaveDialog(component);
            if (res == JFileChooser.APPROVE_OPTION)
            {
                final File selectedFile = jFileChooser.getSelectedFile();
                if (!selectedFile.exists() || selectedFile.canWrite())
                {
                    dumpFile(config, selectedFile);
                }
            }
        }

        UiUtils.showInfoBallon(module.getProject(), "Generated obfuscation settings");
    }

    private void dumpFile(@NotNull String content, @NotNull File file)
    {
        if (!NL.equals(LINE_SEP))
        {
            content = content.replace(NL, LINE_SEP);
        }
        OutputStream os = null;
        try
        {
            os = new BufferedOutputStream(new FileOutputStream(file));
            os.write(content.getBytes("utf-8"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (os != null)
        {
            try
            {
                os.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected abstract String generateConfiguration(@NotNull GuardFacet guardFacet);

    protected abstract String getConfigFileExtension();
}
