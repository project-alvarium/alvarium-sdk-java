/*******************************************************************************
 * Copyright 2023 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package com.alvarium.annotators.dependencies;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MavenHandler implements PackageFileHandler {
    final private File file;

    protected MavenHandler(String dir) {
        this.file = new File(dir + "/" + "pom.xml");
    }

    @Override
    public String getFileName() {
        return this.file.getName();
    }

    @Override
    public Map<String, String> getPackages() throws DependenciesException {
        final Map<String, String> packages = new HashMap<String, String>();

        final Document pom = this.getDocument();
        NodeList deps = pom.getElementsByTagName("dependency");
        for (int i = 0; i < deps.getLength(); i++) {
            Node node = deps.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element dep = (Element) node;
                String groupId = dep.getElementsByTagName("groupId").item(0).getTextContent();
                String artifactId = dep.getElementsByTagName("artifactId").item(0).getTextContent();
                String version = dep.getElementsByTagName("version").item(0).getTextContent();
                packages.put(
                    String.format("%s:%s", groupId, artifactId), 
                    version
                );
            }
        }
        return packages;
    }

    @Override
    public boolean exists() {
        return this.file.exists();
    }

    private Document getDocument() throws DependenciesException {
        try {
            final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = documentBuilder.parse(this.file);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException e) {
            throw new DependenciesException(
                "Failed to configure XML reader, could not read package info from pom.xml",
                e
            );
        } catch (IOException e) {
            throw new DependenciesException(
                "Failed to read file, could not read package info from pom.xml",
                e
            );
        } catch (SAXException e) {
            throw new DependenciesException(
                "Bad XML file, could not read package info from pom.xml",
                e
            );
        } catch (Exception e) {
            throw new DependenciesException(
                "Could not read package info from pom.xml",
                e
            );
        }
    }
    
}
