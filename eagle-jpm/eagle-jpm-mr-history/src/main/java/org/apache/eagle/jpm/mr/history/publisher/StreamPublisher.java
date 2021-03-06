/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.eagle.jpm.mr.history.publisher;

import org.apache.eagle.jpm.mr.history.crawler.EagleOutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public abstract class StreamPublisher<T> implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(StreamPublisher.class);

    protected String stormStreamId;
    protected EagleOutputCollector collector;

    public StreamPublisher(String stormStreamId) {
        this.stormStreamId = stormStreamId;
    }

    public void setCollector(EagleOutputCollector collector) {
        this.collector = collector;
    }

    public String stormStreamId() {
        return stormStreamId;
    }

    public abstract Class<?> type();

    public abstract void flush(T entity);
}
