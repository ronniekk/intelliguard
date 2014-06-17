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

package com.github.intelliguard.generator;

import com.github.intelliguard.facet.GuardFacet;
import com.github.intelliguard.facet.GuardFacetConfiguration;
import com.github.intelliguard.model.Keeper;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.util.PathsList;
//import com.intellij.openapi.roots.ProjectRootsTraversing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtil;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * User: ronnie
 * Date: 2009-nov-09
 * Time: 08:08:06
 */
public class ProGuardGenerator
{
    public static String generatePro(@NotNull GuardFacet facet)
    {
        final GuardFacetConfiguration configuration = facet.getConfiguration();
        final String inFile = configuration.inFile != null ? new File(configuration.inFile).getAbsolutePath() : "injar.jar";
        final String outFile = configuration.outFile != null ? new File(configuration.outFile).getAbsolutePath() : "outjar.jar";

        final StringBuilder sb = new StringBuilder();

        sb.append(MessageFormat.format(IN_JARS, escape(inFile)));
        sb.append(MessageFormat.format(OUT_JARS, escape(outFile)));

        final PathsList dependenciesList = OrderEnumerator.orderEntries(facet.getModule()).withoutDepModules().withoutModuleSourceEntries().getPathsList();
        //ProjectRootsTraversing.collectRoots(facet.getModule(), ProjectRootsTraversing.LIBRARIES_AND_JDK);
        final List<VirtualFile> externalDependencies = dependenciesList.getVirtualFiles();
        for (VirtualFile dependencyJar : externalDependencies)
        {
            final String path = VfsUtil.virtualToIoFile(dependencyJar).getAbsolutePath();
            sb.append(MessageFormat.format(LIBRARY_JARS, escape(path)));
        }

        sb.append(DONT_SHRINK);
        sb.append(DONT_OPTIMIZE);

        if (configuration.sourcefile)
        {
            sb.append(MessageFormat.format(KEEP_ATTRS, "SourceFile"));
        }
        if (configuration.linenumbertable)
        {
            sb.append(MessageFormat.format(KEEP_ATTRS, "LineNumberTable"));
        }
        if (configuration.localvariabletable)
        {
            sb.append(MessageFormat.format(KEEP_ATTRS, "LocalVariableTable"));
        }
        if (configuration.localvariabletypetable)
        {
            sb.append(MessageFormat.format(KEEP_ATTRS, "LocalVariableTypeTable"));
        }
        if (configuration.runtimevisibleannotations)
        {
            sb.append(MessageFormat.format(KEEP_ATTRS, "RuntimeVisibleAnnotations"));
        }
        if (configuration.runtimevisibleparameterannotations)
        {
            sb.append(MessageFormat.format(KEEP_ATTRS, "RuntimeVisibleParameterAnnotations"));
        }
        if (configuration.runtimeinvisibleannotations)
        {
            sb.append(MessageFormat.format(KEEP_ATTRS, "RuntimeInvisibleAnnotations"));
        }
        if (configuration.runtimeinvisibleparameterannotations)
        {
            sb.append(MessageFormat.format(KEEP_ATTRS, "RuntimeInvisibleParameterAnnotations"));
        }

        if (configuration.mainclass.length() != 0)
        {
            sb.append(MessageFormat.format(KEEP_MAIN_CLASS, configuration.mainclass));
        }

        for (Keeper keeper : configuration.keepers)
        {
            switch (keeper.getType())
            {
                case CLASS:
                    sb.append(MessageFormat.format(KEEP_CLASS, keeper.getName()));
                    break;
                default:
                    sb.append(MessageFormat.format(OPEN_KEEP_CLASS_MEMBERS, keeper.getClazz() == null ? "*" : keeper.getClazz()));
                    sb.append(MessageFormat.format(KEEP_CLASS_MEMBER, keeper.getName()));
                    sb.append(CLOSE_KEEP_CLASS_MEMBERS);
                    break;
            }
        }

        return sb.toString();
    }

    private static String escape(String in)
    {
        /*
        if (in.contains(" "))
        {
            return "\"" + in + "\"";
        }
        */
        return in;
    }

    private static final String IN_JARS = "-injars {0}\n";
    private static final String OUT_JARS = "-outjars {0}\n";
    private static final String LIBRARY_JARS = "-libraryjars {0}\n";
    private static final String DONT_SHRINK = "-dontshrink\n";
    private static final String DONT_OPTIMIZE = "-dontoptimize\n";
    private static final String KEEP_ATTRS = "-keepattributes {0}\n";
    private static final String KEEP_CLASS = "-keep class {0}\n";
    private static final String OPEN_KEEP_CLASS_MEMBERS = "-keepclassmembernames class {0} '{'\n";
    private static final String KEEP_CLASS_MEMBER = "    *** {0};\n";
    private static final String CLOSE_KEEP_CLASS_MEMBERS = "}\n";
    private static final String KEEP_MAIN_CLASS = "-keepclasseswithmembers public class {0} '{'\n    public static void main(java.lang.String[]);\n'}'\n";
}
