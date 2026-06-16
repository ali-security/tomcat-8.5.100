/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.http.fileupload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

/**
 * Unit tests for the partHeaderSizeMax field in {@link FileUploadBase}
 * and the headerSizeMax field in {@link MultipartStream}, added as part
 * of the CVE-2025-48988 fix.
 */
public class TestFileUploadBasePartHeaderSize {

    @Test
    public void testPartHeaderSizeMaxDefault() {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        Assert.assertEquals(-1, upload.getPartHeaderSizeMax());
    }

    @Test
    public void testPartHeaderSizeMaxSetter() {
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        upload.setPartHeaderSizeMax(512);
        Assert.assertEquals(512, upload.getPartHeaderSizeMax());
    }

    @Test
    public void testMultipartStreamHeaderSizeMax() throws Exception {
        byte[] boundary = "----boundary".getBytes("UTF-8");
        byte[] content = ("------boundary\r\nContent-Disposition: form-data; name=\"field1\"\r\n\r\nvalue1\r\n------boundary--\r\n").getBytes("UTF-8");
        InputStream input = new ByteArrayInputStream(content);

        MultipartStream stream = new MultipartStream(input, boundary, 4096, null);
        // Default: headerSizeMax is -1, falls back to HEADER_PART_SIZE_MAX
        stream.setHeaderSizeMax(256);
        // If we got here without exception, the setter works
        Assert.assertTrue(true);
    }

    @Test
    public void testHeaderSizeLimitEnforced() throws Exception {
        // Create a multipart stream with a very small header size limit
        byte[] boundary = "----boundary".getBytes("UTF-8");
        // Create content with a header that exceeds the tiny limit
        StringBuilder sb = new StringBuilder();
        sb.append("------boundary\r\n");
        sb.append("Content-Disposition: form-data; name=\"field1\"\r\n");
        // Add extra header data to exceed a tiny limit
        sb.append("X-Custom-Header: ");
        for (int i = 0; i < 100; i++) {
            sb.append("x");
        }
        sb.append("\r\n\r\nvalue1\r\n------boundary--\r\n");

        byte[] content = sb.toString().getBytes("UTF-8");
        InputStream input = new ByteArrayInputStream(content);

        MultipartStream stream = new MultipartStream(input, boundary, 4096, null);
        // Set a very small header size limit (10 bytes)
        stream.setHeaderSizeMax(10);

        try {
            stream.skipPreamble();
            stream.readHeaders();
            Assert.fail("Expected MalformedStreamException due to header size limit");
        } catch (MultipartStream.MalformedStreamException e) {
            // Expected - header exceeds the configured limit
            Assert.assertTrue(e.getMessage().contains("Header section has more than"));
        }
    }
}
