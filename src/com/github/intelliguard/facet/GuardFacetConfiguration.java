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

package com.github.intelliguard.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiClass;
import com.github.intelliguard.model.Keeper;
import com.github.intelliguard.model.JarConfig;
import com.github.intelliguard.util.PsiUtils;
import org.jdom.Element;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-okt-21
 * Time: 19:02:21
 */
public class GuardFacetConfiguration implements FacetConfiguration, PersistentStateComponent<GuardFacetConfiguration>
{
    public String yGuardJar;

    public String inFile;
    public String outFile;
    
    // rename attributes
    public String mainclass = ""; // mainclass
    public boolean conservemanifest; // conservemanifest
    public boolean replaceClassNameStrings = true; // replaceClassNameStrings

    // rename property: <property name="error-checking" value="pedantic"/>
    public boolean errorChecking = true;

    // keep attributes
    public boolean sourcefile; // sourcefile
    public boolean linenumbertable; // linenumbertable
    public boolean localvariabletable; // localvariabletable
    public boolean localvariabletypetable; // localvariabletypetable
    public boolean runtimevisibleannotations = true; // runtimevisibleannotations
    public boolean runtimevisibleparameterannotations = true; // runtimevisibleparameterannotations
    public boolean runtimeinvisibleannotations = true; // runtimeinvisibleannotations
    public boolean runtimeinvisibleparameterannotations = true; // runtimeinvisibleparameterannotations

    public Collection<Keeper> keepers = new ArrayList<Keeper>();

    public JarConfig jarConfig = new JarConfig();

    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager)
    {
        return new FacetEditorTab[] {new GuardFacetEditorTab(this, editorContext, validatorsManager)};  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void applyGlobalSettings(GuardFacetConfiguration state)
    {
        loadState(state);
    }

    public void readExternal(Element element) throws InvalidDataException
    {
    }

    public void writeExternal(Element element) throws WriteExternalException
    {
    }

    public GuardFacetConfiguration getState()
    {
        return this;
    }

    public void loadState(GuardFacetConfiguration state)
    {
        this.yGuardJar = state.yGuardJar;
        this.inFile = state.inFile;
        this.outFile = state.outFile;
        this.conservemanifest = state.conservemanifest;
        this.errorChecking = state.errorChecking;
        this.linenumbertable = state.linenumbertable;
        this.localvariabletable = state.localvariabletable;
        this.localvariabletypetable = state.localvariabletypetable;
        this.mainclass = state.mainclass;
        this.replaceClassNameStrings = state.replaceClassNameStrings;
        this.runtimeinvisibleannotations = state.runtimeinvisibleannotations;
        this.runtimeinvisibleparameterannotations = state.runtimeinvisibleparameterannotations;
        this.runtimevisibleannotations = state.runtimevisibleannotations;
        this.runtimevisibleparameterannotations = state.runtimevisibleparameterannotations;
        this.sourcefile = state.sourcefile;
        this.keepers = state.keepers;
        this.jarConfig= state.jarConfig;
    }

    public boolean equalsGlobalSettings(GuardFacetConfiguration that)
    {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        if (conservemanifest != that.conservemanifest) return false;
        if (errorChecking != that.errorChecking) return false;
        if (linenumbertable != that.linenumbertable) return false;
        if (localvariabletable != that.localvariabletable) return false;
        if (localvariabletypetable != that.localvariabletypetable) return false;
        if (replaceClassNameStrings != that.replaceClassNameStrings) return false;
        if (runtimeinvisibleannotations != that.runtimeinvisibleannotations) return false;
        if (runtimeinvisibleparameterannotations != that.runtimeinvisibleparameterannotations) return false;
        if (runtimevisibleannotations != that.runtimevisibleannotations) return false;
        if (runtimevisibleparameterannotations != that.runtimevisibleparameterannotations) return false;
        if (sourcefile != that.sourcefile) return false;
        if (mainclass != null ? !mainclass.equals(that.mainclass) : that.mainclass != null) return false;
        if (yGuardJar != null ? !yGuardJar.equals(that.yGuardJar) : that.yGuardJar != null) return false;

        return true;
    }

    public boolean isKeptByMainClass(PsiElement element)
    {
        if (mainclass.length() == 0)
        {
            return false;
        }
        if (element instanceof PsiClass)
        {
            return mainclass.equals(PsiUtils.getKeeperName(element));
        }
        if (element instanceof PsiMethod)
        {
            PsiMethod psiMethod = (PsiMethod) element;
            return mainclass.equals(PsiUtils.getKeeperName(psiMethod.getContainingClass()))
                    && PsiUtils.isPublicStaticVoidMain(psiMethod);
        }
        return false;
    }

    public Keeper[] findConfiguredGuardKeepers(PsiElement psiElement)
    {
        // special treatment for constructors
        PsiMethod constructor = null;
        if (psiElement instanceof PsiMethod)
        {
            PsiMethod psiMethod = (PsiMethod) psiElement;
            if (psiMethod.isConstructor())
            {
                constructor = psiMethod;
            }
        }

        Collection<Keeper> found = new ArrayList<Keeper>();
        for (Keeper keeper : keepers)
        {
            if (constructor != null)
            {
                if (keeper.getType() == Keeper.Type.CLASS && keeper.getName() != null)
                {
                    if (keeper.getName().equals(PsiUtils.getKeeperName(constructor.getContainingClass())))
                    {
                        found.add(keeper);
                    }
                }
            }
            else if (keeper.satisfies(psiElement))
            {
                found.add(keeper);
            }
        }
        return found.toArray(new Keeper[found.size()]);
    }
}
