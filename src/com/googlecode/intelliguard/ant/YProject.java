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

package com.googlecode.intelliguard.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildEvent;
import org.jetbrains.annotations.NotNull;
import com.googlecode.intelliguard.runner.RunProgress;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-08
 * Time: 12:06:48
 */
public class YProject extends Project
{
    public YProject(@NotNull final RunProgress runProgress)
    {

        addBuildListener(new BuildListener()
        {
            public void buildStarted(BuildEvent buildEvent)
            {
            }

            public void buildFinished(BuildEvent buildEvent)
            {
            }

            public void targetStarted(BuildEvent buildEvent)
            {
            }

            public void targetFinished(BuildEvent buildEvent)
            {
            }

            public void taskStarted(BuildEvent buildEvent)
            {
            }

            public void taskFinished(BuildEvent buildEvent)
            {
            }

            public void messageLogged(BuildEvent buildEvent)
            {
                final String message = buildEvent.getMessage();
                if (message != null)
                {
                    runProgress.markMessage(message);
                }
            }
        });
    }
}

