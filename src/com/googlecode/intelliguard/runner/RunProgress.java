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

package com.googlecode.intelliguard.runner;

import com.intellij.openapi.progress.ProgressManager;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-02
 * Time: 18:40:18
 */
public class RunProgress
{
    private ProgressInfoReceiver infoReceiver;
    private int errors;

    public RunProgress(@Nullable ProgressInfoReceiver infoReceiver)
    {
        this.infoReceiver = infoReceiver;
    }

    public void markError(@Nullable String errorMessage)
    {
        errors++;
        if (errorMessage != null)
        {
            markMessage("[ERROR] " + errorMessage);
        }
    }

    public boolean lookingGood()
    {
        return errors == 0;
    }

    public void markMessage(@Nullable String text)
    {
        if (text == null)
        {
            return;
        }
        if (infoReceiver != null)
        {
            infoReceiver.info(text);
        }
        ProgressManager.getInstance().getProgressIndicator().setText2(text);
    }
}
