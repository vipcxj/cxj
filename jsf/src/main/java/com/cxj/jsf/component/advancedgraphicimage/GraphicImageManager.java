/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cxj.jsf.component.advancedgraphicimage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 */
@SessionScoped
@ManagedBean(name = "GraphicImageManager")
public class GraphicImageManager implements HttpSessionBindingListener, Serializable {

    private static final long serialVersionUID = 1733126216188797072L;
    private static final Logger LOGGER = LogManager.getLogger(GraphicImageManager.class);

    private final Map<String, Meta> storedContent = new HashMap<>();

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        // No action required
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        for (Meta meta : storedContent.values()) {
            File f = new File(meta.getPath());
            if (!f.delete()) {
                LOGGER.error("Unable to delete the tmp image file: " + meta.getPath());
            }
        }
    }

    public void registerImage(StreamedContent content, String uniqueId) {
        try {
            File tempFile = File.createTempFile(uniqueId, "primefaces");

            Meta meta = new Meta(tempFile.getAbsolutePath(), content.getContentType());
            storedContent.put(uniqueId, meta);

            InputStream input = content.getStream();
            if (input.markSupported()) {
                input.reset();
            }
            OutputStream output = new FileOutputStream(tempFile);
            ReadableByteChannel inputChannel = Channels.newChannel(input);
            try ( // get a channel from the stream
                    WritableByteChannel outputChannel = Channels.newChannel(output);) {
                // copy the channels
                fastChannelCopy(inputChannel, outputChannel);
                if (input.markSupported()) {
                    input.reset();
                }
                // closing the channels
            }
        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    public StreamedContent retrieveImage(String uniqueId) {
        StreamedContent result = null;
        Meta meta = storedContent.get(uniqueId);
        if (meta != null) {
            String tempFile = meta.getPath();
            File f = new File(tempFile);
            try {
                result = new DefaultStreamedContent(new FileInputStream(f), meta.getContentType());
            } catch (FileNotFoundException e) {
                LOGGER.error("Unable to find the file: " + tempFile);
            }
        }
        return result;
    }
    
    public Meta retrieveMeta(String uid) {
        return storedContent.get(uid);
    }

    private static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            // prepare the buffer to be drained
            buffer.flip();
            // write to the channel, may block
            dest.write(buffer);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buffer.compact();
        }
        // EOF will leave buffer in fill state
        buffer.flip();
        // make sure the buffer is fully drained.
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    public static class Meta implements Serializable {

        private static final long serialVersionUID = -677101768594497744L;

        private String path;
        private String contentType;

        public Meta() {
        }

        public Meta(String path, String contentType) {
            this.path = path;
            this.contentType = contentType;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

    }
}
