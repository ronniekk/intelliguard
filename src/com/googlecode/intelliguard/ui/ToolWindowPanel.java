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

package com.googlecode.intelliguard.ui;

import com.googlecode.intelliguard.runner.ProgressInfoReceiver;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-10
 * Time: 19:30:30
 */
public class ToolWindowPanel implements ProgressInfoReceiver
{
    private static final String NL = System.getProperty("line.separator", "\n");
    private JPanel panel;
    private JTextArea textArea;

    public ToolWindowPanel()
    {
        clear();
    }

    public JPanel getPanel()
    {
        return panel;
    }

    public void info(String info)
    {
        textArea.append(info + NL);
    }

    public void clear()
    {
        textArea.setText("");
    }
}
