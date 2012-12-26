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

package com.github.intelliguard.util;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-09
 * Time: 22:11:49
 */
public class ObfuscatorUtils
{
    public static ValidationResult checkYGuard(@NotNull final String jarPath)
    {
        final VirtualFile jarFile = findJarFile(jarPath);
        if (jarFile == null)
        {
          return new ValidationResult("File " + jarPath + " does not exist");
        }

        String yGuardClassName = "com.yworks.yguard.YGuardTask";
        final VirtualFile yGuardClassFile = jarFile.findFileByRelativePath(yGuardClassName.replace('.', '/') + ".class");
        if (yGuardClassFile == null)
        {
            return new ValidationResult(jarPath + " does not contain class " + yGuardClassName);
        }
        return ValidationResult.OK;
    }

    @Nullable
    private static VirtualFile findJarFile(@NotNull final String jarPath)
    {
        return JarFileSystem.getInstance().refreshAndFindFileByPath(FileUtil.toSystemIndependentName(jarPath) + JarFileSystem.JAR_SEPARATOR);
    }
}
