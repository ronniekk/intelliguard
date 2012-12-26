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

import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * User: ronnie
 * Date: 2009-nov-09
 * Time: 12:51:43
 */
public class GuardGutterRenderer extends GutterIconRenderer
{
    private Icon icon;
    @Nullable
    private String tooltip;
    private TextRange range;

    public GuardGutterRenderer(@NotNull Icon icon, @Nullable String tooltip, @NotNull TextRange range)
    {
        this.icon = icon;
        this.tooltip = tooltip;
        this.range = range;
    }

    @Override
    public String getTooltipText()
    {
        return tooltip != null ? tooltip : super.getTooltipText();
    }

    @NotNull
    public Icon getIcon()
    {
        return icon;
    }

    public RangeHighlighter addLineHighlighter(@NotNull MarkupModel markupModel)
    {
        return markupModel.addRangeHighlighter(range.getStartOffset(), range.getEndOffset(), HighlighterLayer.LAST, null, HighlighterTargetArea.LINES_IN_RANGE);
    }
}
