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

package com.googlecode.intelliguard.generator;

import com.googlecode.intelliguard.facet.GuardFacet;
import com.googlecode.intelliguard.facet.GuardFacetConfiguration;
import com.googlecode.intelliguard.model.Keeper;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.roots.ProjectRootsTraversing;
import com.intellij.util.PathsList;

import java.text.MessageFormat;
import java.io.File;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-08
 * Time: 12:09:04
 */
public class YGuardGenerator
{
    public static final String YGUARD_TARGET_NAME = "yguard";

    public static String generateBuildXml(@NotNull GuardFacet facet)
    {
        final GuardFacetConfiguration configuration = facet.getConfiguration();
        final String inFile = configuration.inFile != null ? new File(configuration.inFile).getAbsolutePath() : "injar.jar";
        final File file = configuration.outFile != null ? new File(configuration.outFile) : null;
        final String outFile = file != null ? file.getAbsolutePath() : "outjar.jar";
        final String yguardFile = new File(configuration.yGuardJar).getAbsolutePath();
        final String logFile = file != null ? new File(file.getParent(), file.getName() + "-yguard.xml").getAbsolutePath() : "logfile.xml";

        final StringBuilder sb = new StringBuilder();

        sb.append(MessageFormat.format(OPEN_PROJECT, YGUARD_TARGET_NAME));
        sb.append(MessageFormat.format(OPEN_TARGET, YGUARD_TARGET_NAME));
        sb.append(MessageFormat.format(TASK_DEF, yguardFile));
        sb.append(OPEN_YGUARD);
        sb.append(MessageFormat.format(IN_OUT_PAIR, inFile, outFile));

        final PathsList dependenciesList = ProjectRootsTraversing.collectRoots(facet.getModule(), ProjectRootsTraversing.LIBRARIES_AND_JDK);
        final List<VirtualFile> externalDependencies = dependenciesList.getVirtualFiles();
        if (!externalDependencies.isEmpty())
        {
            sb.append(OPEN_EXTERNAL_CLASSES);
            for (VirtualFile dependencyJar : externalDependencies)
            {
                final String path = VfsUtil.virtualToIoFile(dependencyJar).getAbsolutePath();
                sb.append(MessageFormat.format(EXTERNAL_PATH_ELEMENT, path));
            }
            sb.append(CLOSE_EXTERNAL_CLASSES);
        }

        /* TODO:
                    <property name="language-conformity" value="illegal"/>
                    <property name="naming-scheme" value="mix"/>

         */

        if (configuration.mainclass.length() != 0)
        {
            sb.append(MessageFormat.format(OPEN_RENAME_WITH_MAIN_CLASS, configuration.mainclass, logFile, configuration.conservemanifest, configuration.replaceClassNameStrings));
        }
        else
        {
            sb.append(MessageFormat.format(OPEN_RENAME, logFile, configuration.conservemanifest, configuration.replaceClassNameStrings));
        }

        if (configuration.errorChecking)
        {
            sb.append(PEDANTIC_ERROR_CHECKING);
        }

        if (!configuration.keepers.isEmpty())
        {
            final String keep = MessageFormat.format(OPEN_KEEP,
                    configuration.sourcefile,
                    configuration.linenumbertable,
                    configuration.localvariabletable,
                    configuration.localvariabletypetable,
                    configuration.runtimevisibleannotations,
                    configuration.runtimevisibleparameterannotations,
                    configuration.runtimeinvisibleannotations,
                    configuration.runtimeinvisibleparameterannotations);
            sb.append(keep);
            for (Keeper keeper : configuration.keepers)
            {
                sb.append("                    ");
                sb.append(keeper.toAntElement());
                sb.append("\n");
            }
            sb.append(CLOSE_KEEP);
        }

        sb.append(CLOSE_RENAME);
        sb.append(CLOSE_YGUARD);
        sb.append(CLOSE_TARGET);
        sb.append(CLOSE_PROJECT);

        return sb.toString();
    }

    private static final String OPEN_PROJECT = "<project default=\"{0}\" name=\"yguard\" basedir=\".\">\n";
    private static final String OPEN_TARGET = "    <target name=\"{0}\">\n";
    private static final String OPEN_YGUARD = "        <yguard>\n";
    private static final String OPEN_RENAME = "            <rename logfile=\"{0}\" conservemanifest=\"{1}\" replaceClassNameStrings=\"{2}\">\n";
    private static final String OPEN_RENAME_WITH_MAIN_CLASS = "            <rename mainclass=\"{0}\" logfile=\"{1}\" conservemanifest=\"{2}\" replaceClassNameStrings=\"{3}\">\n";
    private static final String OPEN_KEEP = "                <keep sourcefile=\"{0}\" linenumbertable=\"{1}\" localvariabletable=\"{2}\" localvariabletypetable=\"{3}\" runtimevisibleannotations=\"{4}\" runtimevisibleparameterannotations=\"{5}\" runtimeinvisibleannotations=\"{6}\" runtimeinvisibleparameterannotations=\"{7}\">\n";
    private static final String CLOSE_KEEP = "                </keep>\n";
    private static final String OPEN_EXTERNAL_CLASSES = "            <externalclasses>\n";
    private static final String EXTERNAL_PATH_ELEMENT = "                <pathelement location=\"{0}\"/>\n";
    private static final String CLOSE_EXTERNAL_CLASSES = "            </externalclasses>\n";
    private static final String IN_OUT_PAIR = "            <inoutpair in=\"{0}\" out=\"{1}\"/>\n";
    private static final String TASK_DEF = "        <taskdef name=\"yguard\" classname=\"com.yworks.yguard.YGuardTask\" classpath=\"{0}\"/>\n";
    private static final String PEDANTIC_ERROR_CHECKING = "                <property name=\"error-checking\" value=\"pedantic\"/>\n";

    private static final String CLOSE_RENAME = "            </rename>\n";
    private static final String CLOSE_YGUARD = "        </yguard>\n";
    private static final String CLOSE_TARGET = "    </target>\n";
    private static final String CLOSE_PROJECT = "</project>\n";

}
