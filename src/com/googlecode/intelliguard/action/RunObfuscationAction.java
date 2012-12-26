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
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.facet.ui.ValidationResult;
import com.googlecode.intelliguard.facet.GuardFacet;
import com.googlecode.intelliguard.facet.GuardFacetConfiguration;
import com.googlecode.intelliguard.ui.FormDialogWrapper;
import com.googlecode.intelliguard.ui.JarOptionsForm;
import com.googlecode.intelliguard.util.UiUtils;
import com.googlecode.intelliguard.runner.JarTask;
import com.googlecode.intelliguard.runner.ObfuscateTask;
import com.googlecode.intelliguard.runner.RunProgress;
import com.googlecode.intelliguard.util.ModuleUtils;
import com.googlecode.intelliguard.util.ObfuscatorUtils;
import com.googlecode.intelliguard.GuardProjectComponent;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-okt-29
 * Time: 17:04:58
 */
public class RunObfuscationAction extends AbstractGuardAction
{
    @Override
    public void update(AnActionEvent e)
    {
        final Presentation presentation = e.getPresentation();

        final Module module = e.getData(DataKeys.MODULE);
        if (module == null)
        {
            presentation.setEnabled(false);
            return;
        }

        presentation.setText("Obfuscate module '" + module.getName() + "'");

        final GuardFacet guardFacet = GuardFacet.getInstance(module);
        presentation.setEnabled(guardFacet != null);
    }

    public void actionPerformed(AnActionEvent e)
    {
        final Module module = e.getData(DataKeys.MODULE);
        if (module == null)
        {
            System.out.println("RunObfuscationAction.actionPerformed: no Module");
            return;
        }
        final GuardFacet guardFacet = GuardFacet.getInstance(module);
        if (guardFacet == null)
        {
            System.out.println("RunObfuscationAction.actionPerformed: no GuardFacet");
            return;
        }
        VirtualFile baseDir = module.getProject().getBaseDir();
        if (baseDir == null)
        {
            System.out.println("RunObfuscationAction.actionPerformed: no baseDir");
            return;
        }

        if (guardFacet.getConfiguration().yGuardJar == null)
        {
            Messages.showErrorDialog(module.getProject(), "Missing yGuard archive\n\nPlease check Obfuscation facet settings.", "Obfuscation error");
            return;
        }
        
        final ValidationResult yGuardValidationResult = ObfuscatorUtils.checkYGuard(guardFacet.getConfiguration().yGuardJar);
        if (yGuardValidationResult != ValidationResult.OK)
        {
            Messages.showErrorDialog(module.getProject(), "Invalid yGuard archive: " + yGuardValidationResult.getErrorMessage() + "\n\nPlease check Obfuscation facet settings.", "Obfuscation error");
            return;
        }

        File outputDir = ModuleUtils.getModuleOutputDir(module);
        if (outputDir != null)
        {
            guardFacet.getConfiguration().jarConfig.addEntry(outputDir.getAbsolutePath());
        }

        JarOptionsForm jarOptionsForm = FormDialogWrapper.showJarOptionsForm(guardFacet);

        if (jarOptionsForm != null)
        {
            GuardFacetConfiguration configuration = guardFacet.getConfiguration();
            configuration.jarConfig.setLinkLibraries(jarOptionsForm.getLibrariesManifestPath());
            configuration.mainclass = jarOptionsForm.getMainClass();
            configuration.inFile = jarOptionsForm.getJarPath();
            configuration.outFile = jarOptionsForm.getObfuscatedJarPath();

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
                Messages.showErrorDialog(module.getProject(), errorMessage, "Obfuscation error");
                return;
            }

            final File inJar = new File(configuration.inFile);
            final File outJar = new File(configuration.outFile);

            final RunProgress runProgress = new RunProgress(module.getProject().getComponent(GuardProjectComponent.class).createProgressInfoReceiver());
            final Runnable jarTask = new JarTask(runProgress, module, configuration.jarConfig, configuration.mainclass, inJar);
            final Runnable obfuscateTask = new ObfuscateTask(runProgress, guardFacet);

            if (jarOptionsForm.getExecuteMake())
            {
                CompilerManager compilerManager = CompilerManager.getInstance(module.getProject());
                compilerManager.make(module.getProject(), new Module[] { module }, new CompileStatusNotification()
                {
                    public void finished(boolean aborted, int errors, int warnings, CompileContext compileContext)
                    {
                        if (errors == 0 && !aborted)
                        {
                            ProgressManager.getInstance().runProcessWithProgressSynchronously(jarTask, "Building jar " + inJar.getName(), true, module.getProject());
                            if (runProgress.lookingGood())
                            {
                                ProgressManager.getInstance().runProcessWithProgressSynchronously(obfuscateTask, "Obfuscating jar " + inJar.getName(), true, module.getProject());

                                if (runProgress.lookingGood())
                                {
                                    UiUtils.showInfoBallon(module.getProject(), "Obfuscated jar: " + outJar.getAbsolutePath());
                                }
                                else
                                {
                                    UiUtils.showErrorBallon(module.getProject(), "Error obfuscating jar " + outJar.getAbsolutePath());
                                }
                            }
                            else
                            {
                                UiUtils.showErrorBallon(module.getProject(), "Error building jar " + inJar.getAbsolutePath());
                            }
                        }
                        else
                        {
                            runProgress.markError("Obfuscation aborted. Compilation errors: " + errors);
                            UiUtils.showErrorBallon(module.getProject(), "Obfuscation aborted. Compilation errors: " + errors);
                        }
                    }
                });
            }
            else
            {
                ProgressManager.getInstance().runProcessWithProgressSynchronously(jarTask, "Building jar " + inJar.getName(), true, module.getProject());
                if (runProgress.lookingGood())
                {
                    ProgressManager.getInstance().runProcessWithProgressSynchronously(obfuscateTask, "Obfuscating jar " + inJar.getName(), true, module.getProject());

                    if (runProgress.lookingGood())
                    {
                        UiUtils.showInfoBallon(module.getProject(), "Obfuscated jar: " + outJar.getAbsolutePath());
                    }
                    else
                    {
                        UiUtils.showErrorBallon(module.getProject(), "Error obfuscating jar " + outJar.getAbsolutePath());
                    }
                }
                else
                {
                    UiUtils.showErrorBallon(module.getProject(), "Error building jar " + inJar.getAbsolutePath());
                }
            }
        }
    }
}
