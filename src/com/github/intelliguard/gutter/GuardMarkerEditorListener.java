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

package com.github.intelliguard.gutter;

import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiFile;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-09
 * Time: 20:30:20
 */
public class GuardMarkerEditorListener implements FileEditorManagerListener
{
    private final Project project;

    public GuardMarkerEditorListener(Project project)
    {
        this.project = project;
    }

    public void fileOpened(FileEditorManager source, VirtualFile file)
    {
    }

    public void fileClosed(FileEditorManager source, VirtualFile file)
    {
    }

    public void selectionChanged(FileEditorManagerEvent event)
    {
        // Refresh the GuardMarker gutter
        final VirtualFile virtualFile = event.getNewFile();
        if (virtualFile == null)
        {
            return;
        }
        final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        final GuardMarker marker = GuardMarker.getGuardMarker(psiFile);
        if (marker != null)
        {
            marker.refresh();
        }
    }
}
