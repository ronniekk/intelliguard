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

package com.googlecode.intelliguard.gutter;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * User: ronnie
 * Date: 2009-nov-09
 * Time: 13:11:58
 */
public class GuardMarker
{
    public static final Key<GuardMarker> KEY = Key.create("com.googlecode.intelliguard.gutter.GuardMarker");

    private final PsiFile psiFile;

    public GuardMarker(@NotNull PsiFile psiFile)
    {
        this.psiFile = psiFile;
    }

    public void refresh()
    {
        createMarkers(psiFile);
    }

    public static void clearMarkers(@Nullable final PsiFile psiFile)
    {
        final MarkupModel markupModel = getMarkupModel(psiFile);
        if (markupModel == null)
        {
            return;
        }

        final List<GuardGutterRenderer> guardGutterRenderers = Collections.emptyList();

        ApplicationManager.getApplication().invokeLater(new Runnable()
        {
            public void run()
            {
                applyRenderers(markupModel, guardGutterRenderers);
                final GuardMarker marker = markupModel.getDocument().getUserData(KEY);
                if (marker != null)
                {
                    markupModel.getDocument().putUserData(KEY, null);
                }
            }
        }, ModalityState.NON_MODAL);
    }

    public static void createMarkers(@Nullable final PsiFile psiFile)
    {
        final MarkupModel markupModel = getMarkupModel(psiFile);
        if (markupModel == null)
        {
            return;
        }

        final List<GuardGutterRenderer> guardGutterRenderers = ApplicationManager.getApplication().runReadAction(new GuardGutterRendererComputation(psiFile));

        ApplicationManager.getApplication().invokeLater(new Runnable()
        {
            public void run()
            {
                applyRenderers(markupModel, guardGutterRenderers);
                final GuardMarker marker = new GuardMarker(psiFile);
                markupModel.getDocument().putUserData(KEY, marker);
            }
        }, ModalityState.NON_MODAL);
    }

    @Nullable
    private static MarkupModel getMarkupModel(@Nullable final PsiFile psiFile)
    {
        if (psiFile == null) return null;

        final Document document = psiFile.getViewProvider().getDocument();
        final Project project = psiFile.getProject();
        if (document != null)
        {
            return document.getMarkupModel(project);
        }
        return null;
    }

    @Nullable
    public static GuardMarker getGuardMarker(@Nullable PsiFile psiFile)
    {
        final MarkupModel markupModel = GuardMarker.getMarkupModel(psiFile);
        return markupModel == null ? null : markupModel.getDocument().getUserData(KEY);
    }

    private static void applyRenderers(MarkupModel markupModel, List<GuardGutterRenderer> guardGutterRenderers)
    {
        RangeHighlighter[] allHighlighters = markupModel.getAllHighlighters();

        for (RangeHighlighter highlighter : allHighlighters)
        {
            GutterIconRenderer gutterIconRenderer = highlighter.getGutterIconRenderer();
            if (gutterIconRenderer instanceof GuardGutterRenderer)
            {
                markupModel.removeHighlighter(highlighter);
            }
        }

        for (GuardGutterRenderer guardGutterRenderer : guardGutterRenderers)
        {
            RangeHighlighter rangeHighlighter = guardGutterRenderer.addLineHighlighter(markupModel);
            rangeHighlighter.setGutterIconRenderer(guardGutterRenderer);
        }
    }
}
