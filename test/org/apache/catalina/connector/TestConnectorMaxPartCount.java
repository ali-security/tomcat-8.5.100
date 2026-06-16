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
package org.apache.catalina.connector;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the maxPartCount and maxPartHeaderSize fields in
 * {@link Connector}, added as part of the CVE-2025-48988 fix.
 */
public class TestConnectorMaxPartCount {

    @Test
    public void testMaxPartCountDefault() {
        Connector connector = new Connector();
        Assert.assertEquals(10, connector.getMaxPartCount());
    }

    @Test
    public void testMaxPartCountSetter() {
        Connector connector = new Connector();
        connector.setMaxPartCount(50);
        Assert.assertEquals(50, connector.getMaxPartCount());
    }

    @Test
    public void testMaxPartCountUnlimited() {
        Connector connector = new Connector();
        connector.setMaxPartCount(-1);
        Assert.assertEquals(-1, connector.getMaxPartCount());
    }

    @Test
    public void testMaxPartHeaderSizeDefault() {
        Connector connector = new Connector();
        Assert.assertEquals(512, connector.getMaxPartHeaderSize());
    }

    @Test
    public void testMaxPartHeaderSizeSetter() {
        Connector connector = new Connector();
        connector.setMaxPartHeaderSize(1024);
        Assert.assertEquals(1024, connector.getMaxPartHeaderSize());
    }

    @Test
    public void testMaxPartHeaderSizeUnlimited() {
        Connector connector = new Connector();
        connector.setMaxPartHeaderSize(-1);
        Assert.assertEquals(-1, connector.getMaxPartHeaderSize());
    }
}
