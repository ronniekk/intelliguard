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

package com.googlecode.intelliguard.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginBean;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.JDOMXIncluder;
import com.googlecode.intelliguard.facet.GuardFacetConfiguration;
import com.googlecode.intelliguard.facet.GuardFacet;
import com.googlecode.intelliguard.gutter.GuardMarker;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-02
 * Time: 21:58:10
 */
public abstract class GuardInspectionBase extends LocalInspectionTool
{
    private GuardFacetConfiguration localConfiguration;
    private PluginBean localPluginDescriptor;

    @Nls
    @NotNull
    public String getGroupDisplayName()
    {
        return "Obfuscation";
    }

    @Override
    public boolean isEnabledByDefault()
    {
        return true;
    }

    @Override
    public void inspectionStarted(LocalInspectionToolSession session)
    {
        this.localConfiguration = getConfiguration(session.getFile());

        this.localPluginDescriptor = getPluginDescriptor(session.getFile());

        super.inspectionStarted(session);
    }

    @Override
    public void inspectionFinished(LocalInspectionToolSession session)
    {
        this.localConfiguration = null;

        this.localPluginDescriptor = null;

        super.inspectionFinished(session);
    }

    @Nullable
    public static PluginBean getPluginDescriptor(@NotNull PsiElement element)
    {
        final Module module = ModuleUtil.findModuleForPsiElement(element);
        if (module == null)
        {
            return null;
        }
        if (!"PLUGIN_MODULE".equals(module.getModuleType().getId()))
        {
            return null;
        }
        final VirtualFile moduleFile = module.getModuleFile();
        if (moduleFile == null)
        {
            return null;
        }
        final VirtualFile moduleDir = moduleFile.getParent();
        if (moduleDir == null)
        {
            return null;
        }
        final File moduleIoDir = VfsUtil.virtualToIoFile(moduleDir);

        try
        {
            // Extensions are private in IdeaPluginDescriptorImpl so we need to parse the xml
            final URL url = new File(new File(moduleIoDir, PluginManager.META_INF), PluginManager.PLUGIN_XML).toURI().toURL();
            Document document = JDOMUtil.loadDocument(url);
            document = JDOMXIncluder.resolve(document, url.toExternalForm());
            final Element rootElement = document.getRootElement();
            return XmlSerializer.deserialize(rootElement, PluginBean.class);
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static GuardFacetConfiguration getConfiguration(@NotNull PsiElement element)
    {
        Module module = ModuleUtil.findModuleForPsiElement(element);
        if (module == null)
        {
            // ModuleUtil does not find module for a package so we search classes in package until we find a module for one
            if (element instanceof PsiPackage)
            {
                final PsiClass[] classesInProjectPackage = ((PsiPackage) element).getClasses(GlobalSearchScope.allScope(element.getProject()));
                for (PsiClass psiClass : classesInProjectPackage)
                {
                    module = ModuleUtil.findModuleForPsiElement(psiClass);
                    if (module != null)
                    {
                        break;
                    }
                }
            }
        }
        if (module == null)
        {
            return null;
        }

        GuardFacet guardFacet = GuardFacet.getInstance(module);
        return guardFacet != null ? guardFacet.getConfiguration() : null;
    }

    @Nullable
    protected GuardFacetConfiguration getLocalConfiguration()
    {
        return localConfiguration;
    }

    @Nullable
    protected PluginBean getLocalPluginDescriptor()
    {
        return localPluginDescriptor;
    }

    public static void alertGuardMarkers(@NotNull PsiElement element)
    {
        final PsiFile psiFile = element.getContainingFile();
        final GuardMarker marker = GuardMarker.getGuardMarker(psiFile);
        if (marker != null)
        {
            marker.refresh();
        }
    }
}
