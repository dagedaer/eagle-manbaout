/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.common.utils;

import java.io.Serializable;

public class Tuple2<F1, F2> implements Serializable {
    private final F1 f1;
    private final F2 f2;

    public Tuple2(F1 f1, F2 f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    public F2 f1() {
        return f2;
    }

    public F1 f0() {
        return f1;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", this.f1, this.f2);
    }
}
