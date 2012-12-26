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

import com.googlecode.intelliguard.facet.GuardFacet;
import com.googlecode.intelliguard.generator.YGuardGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-10
 * Time: 20:48:09
 */
public class ExportYGuard extends AbstractExportAction
{
    private static final String FILE_EXTENSION = "xml";

    protected String generateConfiguration(@NotNull GuardFacet guardFacet)
    {
        return YGuardGenerator.generateBuildXml(guardFacet);
    }

    @Override
    protected String getConfigFileExtension()
    {
        return FILE_EXTENSION;
    }
}
