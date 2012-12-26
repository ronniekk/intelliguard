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

package com.github.intelliguard.refactor;

import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.psi.*;
import com.github.intelliguard.inspection.GuardInspectionBase;
import com.github.intelliguard.facet.GuardFacetConfiguration;
import com.github.intelliguard.model.Keeper;
import com.github.intelliguard.util.PsiUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-03
 * Time: 19:31:35
 */
public class RenameListenerProvider implements RefactoringElementListenerProvider
{
    public RefactoringElementListener getListener(final PsiElement element)
    {
        if (element instanceof PsiPackage || element instanceof PsiClass || element instanceof PsiMethod || element instanceof PsiField)
        {
            final String oldName = PsiUtils.getKeeperName(element);
            if (oldName == null)
            {
                return null;
            }

            final GuardFacetConfiguration configuration = GuardInspectionBase.getConfiguration(element);
            if (configuration == null)
            {
                return null;
            }

            return new RefactoringElementListener()
            {
                public void elementMoved(@NotNull PsiElement newElement)
                {
                    elementRenamed(newElement);
                }

                public void elementRenamed(@NotNull PsiElement newElement)
                {
                    final String newName = PsiUtils.getKeeperName(newElement);
                    if (newName == null)
                    {
                        return;
                    }

                    // renamed package
                    if (newElement instanceof PsiPackage)
                    {
                        for (Keeper keeper : configuration.keepers)
                        {
                            if (keeper.getType() == Keeper.Type.CLASS)
                            {
                                final String clazz = keeper.getName();
                                String oldPackage = getPackageName(clazz);
                                if (oldPackage.equals(oldName))
                                {
                                    keeper.setName(newName + "." + getSimpleName(clazz));
                                }
                            }
                            else
                            {
                                final String clazz = keeper.getClazz();
                                if (clazz != null)
                                {
                                    String oldPackage = getPackageName(clazz);
                                    if (oldPackage.equals(oldName))
                                    {
                                        keeper.setClazz(newName + "." + getSimpleName(clazz));
                                    }
                                }
                            }
                        }
                        return;
                    }

                    // renamed Main-Class
                    if (newElement instanceof PsiClass && oldName.equals(configuration.mainclass))
                    {
                        configuration.mainclass = newName;
                    }

                    for (Keeper keeper : configuration.keepers)
                    {
                        if (newElement instanceof PsiClass)
                        {
                            if (keeper.getType() == Keeper.Type.CLASS)
                            {
                                if (oldName.equals(keeper.getName()))
                                {
                                    keeper.setName(newName);
                                }
                            }
                            else
                            {
                                if (oldName.equals(keeper.getClazz()))
                                {
                                    keeper.setClazz(newName);
                                }
                            }
                        }
                        else
                        {
                            if (oldName.equals(keeper.getName()))
                            {
                                keeper.setName(newName);
                            }
                        }
                    }
                }
            };
        }
        return null;
    }

    private String getSimpleName(String clazz)
    {
        return clazz.substring(clazz.lastIndexOf('.') + 1);
    }

    private String getPackageName(String clazz)
    {
        return clazz.indexOf('.') != -1 ? clazz.substring(0, clazz.lastIndexOf('.')) : clazz;
    }
}
