/*
 * Copyright (c) 2008 VMware, Inc.
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
package pivot.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import pivot.collections.Map;
import pivot.collections.adapter.MapAdapter;

/**
 * Serializes data to and from Java the properties file format.
 *
 * @author smartini
 * @author gbrown
 */
public class PropertiesSerializer implements Serializer {
    public static final String MIME_TYPE = "text/plain";

    /**
     * Returns a {@link pivot.collections.Map} containing the entries in the
     * properties file. Both keys and values are strings.
     */
    @SuppressWarnings("unchecked")
    public Object readObject(InputStream inputStream) throws IOException,
        SerializationException {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream is null.");
        }

        Properties properties = new Properties();
        properties.load(inputStream);

        return new MapAdapter<Object, Object>(properties);
    }

    /**
     * Writes the entries from a {@link pivot.collections.Map} to a properties
     * file. Keys must be strings, and values will be converted to strings.
     */
    @SuppressWarnings("unchecked")
    public void writeObject(Object object, OutputStream outputStream) throws IOException,
        SerializationException {
        if (!(object instanceof Map<?, ?>)) {
            throw new IllegalArgumentException("object must be an instance of "
                + Map.class.getName());
        }

        if (outputStream == null) {
            throw new IllegalArgumentException("outputStream is null.");
        }

        Map<String, Object> map = (Map<String, Object>) object;
        Properties properties = new Properties();

        for (String key : map) {
            Object value = map.get(key);
            if (value != null) {
                value = value.toString();
            }

            properties.put(key, value);
        }

        properties.store(outputStream, null);
    }

    public String getMIMEType() {
        return MIME_TYPE;
    }
}
