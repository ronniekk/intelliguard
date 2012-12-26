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

import com.github.intelliguard.facet.GuardFacet;
import com.intellij.openapi.ui.DialogBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-okt-30
 * Time: 11:56:51
 */
public class FormDialogWrapper
{
    @Nullable
    public static JarOptionsForm showJarOptionsForm(@NotNull GuardFacet guardFacet)
    {
        DialogBuilder builder = new DialogBuilder(guardFacet.getModule().getProject());
        JarOptionsForm jarOptionsForm = new JarOptionsForm(guardFacet);
        builder.setCenterPanel(jarOptionsForm.getContentPane());
        builder.setTitle("Obfuscate Jar");
        builder.addOkAction().setText("Build");
        builder.addCancelAction().setText("Cancel");
        
        int res = builder.show();

        return res == 0 ? jarOptionsForm : null;
    }

    @Nullable
    public static ExportOptionsForm showExportOptionsForm(@NotNull GuardFacet guardFacet)
    {
        DialogBuilder builder = new DialogBuilder(guardFacet.getModule().getProject());
        ExportOptionsForm exportOptionsForm = new ExportOptionsForm(guardFacet);
        builder.setCenterPanel(exportOptionsForm.getContentPane());
        builder.setTitle("Export Configuration");
        builder.addOkAction().setText("Export");
        builder.addCancelAction().setText("Cancel");

        int res = builder.show();

        return res == 0 ? exportOptionsForm : null;
    }
}
