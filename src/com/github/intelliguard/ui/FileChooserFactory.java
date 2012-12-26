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

import com.intellij.openapi.module.Module;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-01
 * Time: 11:22:50
 */
public class FileChooserFactory
{
    /**
     * Creates a filechooser for saving jar files.
     * @param preferredDirectory the preferred 'current' directory/directories, in order. The first
     * usable directory will be used as current directory.
     * @return a filechooser instance
     */
    public static JFileChooser createSaveJarChooser(String... preferredDirectory)
    {
        JFileChooser jFileChooser = new JFileChooser();
        FileFilter fileFilter = new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.isDirectory() || f.getName().endsWith(".jar");
            }

            @Override
            public String getDescription()
            {
                return "Archive files";
            }
        };
        jFileChooser.setFileFilter(fileFilter);

        setPreferedDirectory(jFileChooser, preferredDirectory);

        jFileChooser.setDialogTitle("Save Jar File");

        return jFileChooser;
    }

    public static JFileChooser createPreferredDirectoryFileChooser(String dialogTitle, String... preferredDirectory)
    {
        JFileChooser jFileChooser = new JFileChooser();

        setPreferedDirectory(jFileChooser, preferredDirectory);

        jFileChooser.setDialogTitle(dialogTitle);

        return jFileChooser;
    }

    private static void setPreferedDirectory(JFileChooser jFileChooser, String... preferredDirectory)
    {
        for (String f : preferredDirectory)
        {
            File file = new File(f);
            if (file.exists())
            {
                jFileChooser.setCurrentDirectory(file);
                return;
            }
        }
    }

    public static JFileChooser createFindJarChooser(String... preferredDirectory)
    {
        final JFileChooser jFileChooser = createSaveJarChooser(preferredDirectory);
        jFileChooser.setDialogTitle("Open Jar File");
        return jFileChooser;
    }

    /**
     * Creates a filechooser for files and directories within the <i>module</i>.<br>
     * The filechooser created prohibits ascending to a directory above the module directory.<br>
     * @param module the module
     * @return a filechooser instance
     */
    public static JFileChooser createModuleFileChooser(@NotNull final Module module)
    {
        String moduleFilePath = module.getModuleFilePath();
        final File moduleDirectory = new File(moduleFilePath).getParentFile();
        JFileChooser jFileChooser = new JFileChooser()
        {
            @Override
            public void changeToParentDirectory()
            {
                if (getCurrentDirectory().equals(moduleDirectory))
                {
                    return;
                }
                super.changeToParentDirectory();
            }
        };

        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.setDialogTitle("Choose File or Directory");
        jFileChooser.setCurrentDirectory(moduleDirectory);

        return jFileChooser;
    }

}
