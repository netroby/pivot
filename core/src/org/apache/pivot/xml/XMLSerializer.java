/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.serialization.Serializer;

/**
 * Reads and writes XML data.
 */
public class XMLSerializer implements Serializer<Element> {
    private Charset charset = null;
    private XMLInputFactory xmlInputFactory;

    private Element element = null;

    public static final String XMLNS_ATTRIBUTE_PREFIX = "xmlns";

    public static final String DEFAULT_CHARSET_NAME = "UTF-8";
    public static final String MIME_TYPE = "text/xml";
    public static final int BUFFER_SIZE = 2048;

    public XMLSerializer() {
        this(DEFAULT_CHARSET_NAME);
    }

    public XMLSerializer(String charsetName) {
        this(charsetName == null ? Charset.defaultCharset() : Charset.forName(charsetName));
    }

    public XMLSerializer(Charset charset) {
        if (charset == null) {
            throw new IllegalArgumentException("charset is null.");
        }

        this.charset = charset;

        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);
    }

    public Charset getCharset() {
        return charset;
    }

    @Override
    public Element readObject(InputStream inputStream) throws IOException, SerializationException {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream is null.");
        }

        Reader reader = new BufferedReader(new InputStreamReader(inputStream, charset), BUFFER_SIZE);
        Element element = readObject(reader);

        return element;
    }

    public Element readObject(Reader reader) throws SerializationException {
        if (reader == null) {
            throw new IllegalArgumentException("reader is null.");
        }

        // Parse the XML stream
        element = null;

        try {
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(reader);

            while (xmlStreamReader.hasNext()) {
                int event = xmlStreamReader.next();

                switch (event) {
                    case XMLStreamConstants.NAMESPACE: {
                        // TODO
                        break;
                    }

                    case XMLStreamConstants.CHARACTERS: {
                        String text = xmlStreamReader.getText();
                        text = text.trim();
                        if (text.length() > 0) {
                            element.add(new TextNode(text));
                        }

                        break;
                    }

                    case XMLStreamConstants.START_ELEMENT: {
                        // Create the element
                        String prefix = xmlStreamReader.getPrefix();
                        if (prefix.length() == 0) {
                            prefix = null;
                        }

                        String localName = xmlStreamReader.getLocalName();

                        Element element = new Element(prefix, localName);

                        for (int i = 0, n = xmlStreamReader.getAttributeCount(); i < n; i++) {
                            String attributePrefix = xmlStreamReader.getAttributePrefix(i);
                            String attributeLocalName = xmlStreamReader.getAttributeLocalName(i);
                            String attributeValue = xmlStreamReader.getAttributeValue(i);

                            String attribute;
                            if (attributePrefix.length() == 0) {
                                attribute = attributeLocalName;
                            } else {
                                attribute = attributePrefix + ":" + attributeLocalName;
                            }

                            element.put(attribute, attributeValue);
                        }

                        if (this.element != null) {
                            this.element.add(element);
                        }

                        this.element = element;

                        break;
                    }

                    case XMLStreamConstants.END_ELEMENT: {
                        // Move up the stack
                        Element parent = element.getParent();
                        if (parent != null) {
                            element = parent;
                        }

                        break;
                    }
                }
            }

            xmlStreamReader.close();
        } catch (XMLStreamException exception) {
            throw new SerializationException(exception);
        }

        return element;
    }

    @Override
    public void writeObject(Element element, OutputStream outputStream)
        throws IOException, SerializationException {
        if (outputStream == null) {
            throw new IllegalArgumentException("outputStream is null.");
        }

        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, charset),
            BUFFER_SIZE);
        writeObject(element, writer);
    }

    public void writeObject(Element element, Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer is null.");
        }

        if (element == null) {
            throw new IllegalArgumentException("element is null.");
        }

        // TODO (note that we'll need to check for valid namespace prefixes here,
        // since we don't do it in Element)

        throw new UnsupportedOperationException();
    }

    @Override
    public String getMIMEType(Element object) {
        return MIME_TYPE;
    }
}
