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

package com.github.intelliguard.ant;

import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;

/**
 * A slightly modified version of {@link org.apache.tools.ant.helper.ProjectHelper2} in the way
 * that it only deals with in-memory build files.
 */
public class YProjectHelper extends ProjectHelper2
{
    /**
     * Only parses java.io.InputStream.
     * @param project the project
     * @param source the input stream
     * @param handler the handler
     * @throws BuildException if <tt>source</tt> is not of type java.io.InputStream
     */
    @Override
    public void parse(Project project, Object source, RootHandler handler) throws BuildException
    {
        if (!(source instanceof InputStream))
        {
            throw new BuildException("Only InpuStream source supported");
        }

        InputStream inputStream = (InputStream) source;
        InputSource inputSource = null;


        try {
            /**
             * SAX 2 style parser used to parse the given file.
             */
            XMLReader parser = JAXPUtils.getNamespaceXMLReader();

            inputSource = new InputSource(inputStream);
            project.log("parsing inputstream", Project.MSG_VERBOSE);

            DefaultHandler hb = handler;

            parser.setContentHandler(hb);
            parser.setEntityResolver(hb);
            parser.setErrorHandler(hb);
            parser.setDTDHandler(hb);
            parser.parse(inputSource);
        } catch (SAXParseException exc) {
            Location location = new Location(exc.getSystemId(),
                exc.getLineNumber(), exc.getColumnNumber());

            Throwable t = exc.getException();
            if (t instanceof BuildException) {
                BuildException be = (BuildException) t;
                if (be.getLocation() == Location.UNKNOWN_LOCATION) {
                    be.setLocation(location);
                }
                throw be;
            }

            throw new BuildException(exc.getMessage(), t, location);
        } catch (SAXException exc) {
            Throwable t = exc.getException();
            if (t instanceof BuildException) {
                throw (BuildException) t;
            }
            throw new BuildException(exc.getMessage(), t);
        } catch (FileNotFoundException exc) {
            throw new BuildException(exc);
        } catch (UnsupportedEncodingException exc) {
              throw new BuildException("Encoding of project file  is invalid.",
                                       exc);
        } catch (IOException exc) {
            throw new BuildException("Error reading project file: " + exc.getMessage(),
                                     exc);
        } finally {
                try {
                    inputStream.close();
                } catch (IOException ioe) {
                    // ignore this
                }
        }
    }
}
