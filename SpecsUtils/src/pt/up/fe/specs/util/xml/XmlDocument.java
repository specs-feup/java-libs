/**
 * Copyright 2020 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.util.xml;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import pt.up.fe.specs.util.SpecsXml;

public class XmlDocument implements XmlNode {
    /**
     * The document this element is part of.
     */
    private final Document document;

    public XmlDocument(Document document) {
        this.document = document;
    }

    @Override
    public Node getNode() {
        return document;
    }

    public static XmlDocument newInstance(File file) {
        return new XmlDocument(SpecsXml.getXmlRoot(file));
    }

    public static XmlDocument newInstance(String contents) {
        return new XmlDocument(SpecsXml.getXmlRoot(contents));
    }

    public static XmlDocument newInstance(InputStream inputStream) {
        return new XmlDocument(SpecsXml.getXmlRoot(inputStream));
    }

    public static XmlDocument newInstanceFromUri(String uri) {
        return new XmlDocument(SpecsXml.getXmlRootFromUri(uri));
    }

    public void write(File outputFile) {
        try {
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer;

            transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(outputFile);

            transformer.transform(source, result);

        } catch (Exception e) {
            throw new RuntimeException("Could not write XML file from document", e);
        }
    }

}
