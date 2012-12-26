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

package com.github.intelliguard.runner;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.compiler.make.ManifestBuilder;
import com.intellij.openapi.roots.ProjectRootsTraversing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathsList;
import com.github.intelliguard.model.JarConfig;
import com.github.intelliguard.util.ModuleUtils;

import java.io.*;
import java.util.jar.Manifest;
import java.util.jar.JarOutputStream;
import java.util.jar.JarEntry;
import java.util.List;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-02
 * Time: 18:34:27
 */
public class JarTask implements Runnable
{
    private RunProgress runProgress;
    private Module module;
    private JarConfig jarConfig;
    private String mainClass;
    private File outFile;

    public JarTask(@NotNull final RunProgress runProgress, @NotNull final Module module, @NotNull final JarConfig jarConfig, @Nullable final String mainClass, @NotNull final File outFile)
    {
        this.runProgress = runProgress;
        this.module = module;
        this.jarConfig = jarConfig;
        this.mainClass = mainClass;
        this.outFile = outFile;
    }

    public void run()
    {
        Manifest manifest = new Manifest();
        manifest = createManifest(manifest, jarConfig, module);
        if (mainClass != null && mainClass.length() != 0)
        {
            manifest.getMainAttributes().putValue("Main-Class", mainClass);
            runProgress.markMessage("Setting Main-Class: " + mainClass);
        }

        final File classesDir = ModuleUtils.getModuleOutputDir(module);

        JarOutputStream jos = null;

        try
        {
            outFile.getParentFile().mkdirs();
            jos = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)), manifest);

            List<String> jarEntries = jarConfig.getJarEntries();
            for (String jarEntry : jarEntries)
            {
                File entryFile = new File(jarEntry);
                if (entryFile.isFile())
                {
                    jarFile(jos, entryFile.getAbsolutePath(), entryFile.getParentFile().getAbsolutePath());
                }
                else if (entryFile.isDirectory())
                {
                    jarDirectory(jos, entryFile.getAbsolutePath(), entryFile.equals(classesDir) ? entryFile.getAbsolutePath() : entryFile.getParentFile().getAbsolutePath());
                }
            }
        }
        catch (IOException e)
        {
            runProgress.markError(e.getMessage());
        }
        finally
        {
            if (jos != null)
            {
                try
                {
                    jos.close();
                }
                catch (IOException e)
                {
                    runProgress.markError(e.getMessage());
                }
            }
        }
    }
    
    private void jarDirectory(@NotNull JarOutputStream jos, @NotNull String directoryName, @NotNull String baseDir)
    {
        File dirobject = new File(directoryName);
        if (dirobject.exists())
        {
            if (dirobject.isDirectory())
            {
                File[] fileList = dirobject.listFiles();
                for (File aFileList : fileList)
                {
                    if (aFileList.isDirectory())
                    {
                        jarDirectory(jos, aFileList.getAbsolutePath(), baseDir);
                    }
                    else if (aFileList.isFile())
                    {
                        jarFile(jos, aFileList.getAbsolutePath(), baseDir);
                    }
                }
            }
        }
    }

    private void jarFile(@NotNull JarOutputStream jos, @NotNull String filePath, @NotNull String baseDir)
    {
        try
        {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            String entryName = filePath.substring(baseDir.length()).replace('\\', '/');
            while (entryName.startsWith("/")) entryName = entryName.substring(1);

            runProgress.markMessage("Adding " + entryName);
            JarEntry fileEntry = new JarEntry(entryName);
            jos.putNextEntry(fileEntry);
            byte[] data = new byte[1024];
            int byteCount;
            while ((byteCount = bis.read(data, 0, 1024)) > -1)
            {
                jos.write(data, 0, byteCount);
            }
        }
        catch (IOException e)
        {
            runProgress.markError(e.getMessage());
        }
    }

    @NotNull
    private Manifest createManifest(@Nullable Manifest manifest, @NotNull JarConfig jarConfig, @NotNull Module module)
    {
        if (manifest == null)
        {
            manifest = new Manifest();
        }
        ManifestBuilder.setGlobalAttributes(manifest.getMainAttributes());
        String libsPrefix = jarConfig.getLinkLibraries();
        if (libsPrefix != null)
        {
            PathsList dependenciesList = ProjectRootsTraversing.collectRoots(module, ProjectRootsTraversing.PROJECT_LIBRARIES);
            List<VirtualFile> virtualFileList = dependenciesList.getVirtualFiles();
            if (!virtualFileList.isEmpty())
            {
                List<String> dependencyFileNames = new ArrayList<String>();
                for (VirtualFile dependencyJar : virtualFileList)
                {
                    final String dependencyName = dependencyJar.getName();
                    runProgress.markMessage("Adding dependency " + dependencyName);
                    dependencyFileNames.add(dependencyName);
                }
                StringBuilder sb = new StringBuilder();
                libsPrefix = libsPrefix.replace('\\', '/').trim();
                while (libsPrefix.startsWith("/")) libsPrefix = libsPrefix.substring(1);
                while (libsPrefix.endsWith("/")) libsPrefix = libsPrefix.substring(0, libsPrefix.length() - 1);
                for (String dependencyFileName : dependencyFileNames)
                {
                    sb.append(" ");
                    sb.append(libsPrefix);
                    if (libsPrefix.length() > 0) sb.append("/");
                    sb.append(dependencyFileName);
                }
                String classpath = sb.toString().trim();
                manifest.getMainAttributes().putValue("Class-Path", classpath);
            }
        }
        return manifest;
    }
}
