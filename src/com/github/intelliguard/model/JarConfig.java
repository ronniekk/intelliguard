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

package com.github.intelliguard.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ronnie
 * Date: 2009-nov-01
 * Time: 09:21:17
 */
public class JarConfig
{
    private String linkLibraries;

    private List<String> jarEntries = new ArrayList<String>();

    public String getLinkLibraries()
    {
        return linkLibraries;
    }

    public void setLinkLibraries(String linkLibraries)
    {
        this.linkLibraries = linkLibraries;
    }

    public List<String> getJarEntries()
    {
        return jarEntries;
    }

    public void setJarEntries(List<String> jarEntries)
    {
        this.jarEntries = jarEntries;
    }

    public void addEntry(String entry)
    {
        if (!jarEntries.contains(entry))
        {
            jarEntries.add(entry);
        }
    }

    public void removeEntry(String entry)
    {
        jarEntries.remove(entry);
    }
}
