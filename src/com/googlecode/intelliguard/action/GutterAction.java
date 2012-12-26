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
import com.googlecode.intelliguard.gutter.GuardMarker;
import com.googlecode.intelliguard.ui.Icons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

/**
 * User: ronnie
 * Date: 2009-nov-09
 * Time: 16:28:07
 */
public class GutterAction extends AbstractGuardAction
{
    @Override
    public void update(AnActionEvent e)
    {
        final Presentation presentation = e.getPresentation();
        final GuardFacet guardFacet = getGuardFacet(e);
        if (guardFacet == null)
        {
            presentation.setVisible(false);
        }
        else
        {
            final Document document = getDocument(e);
            if (document == null)
            {
                presentation.setVisible(false);
            }
            else
            {
                presentation.setVisible(true);
                presentation.setIcon(Icons.OBFUSCATION_NODE_ICON);
                final GuardMarker marker = document.getUserData(GuardMarker.KEY);
                if (marker == null)
                {
                    presentation.setText("Show Obfuscated symbols");
                }
                else
                {
                    presentation.setText("Hide Obfuscated symbols");
                }
            }
        }
    }

    public void actionPerformed(AnActionEvent e)
    {
        final Document document = getDocument(e);
        if (document == null)
        {
            return;
        }
        final Project project = getProject(e);
        if (project == null)
        {
            return;
        }
        final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        final GuardMarker marker = document.getUserData(GuardMarker.KEY);
        if (marker == null)
        {
            GuardMarker.createMarkers(psiFile);
        }
        else
        {
            GuardMarker.clearMarkers(psiFile);
        }
    }
}
