/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.apache.eagle.security.crawler.audit;

import org.apache.eagle.security.hdfs.HDFSAuditLogParser;
import org.apache.eagle.security.hdfs.HDFSAuditLogObject;
import org.junit.Assert;
import org.junit.Test;

public class TestHDFSAuditLogParser {
//    @Test
//    public void testParser() throws Exception{
//        HDFSAuditLogParser parser = new HDFSAuditLogParser();
//        HDFSAuditLogObject entity =
//                parser.parse("2015-09-21 21:36:52,172 INFO FSNamesystem.audit: allowed=true   ugi=user1@APD.xyz.com (auth:SIMPLE)     ip=/10.0.2.15   cmd=getfileinfo src=/tmp/hive   dst=null        perm=null       proto=rpc\n");
//        Assert.assertEquals("user1", entity.user);
//        Assert.assertEquals(new Boolean(true), entity.allowed);
//
//        entity = parser.parse("2015-09-21 21:36:52,172 INFO FSNamesystem.audit: allowed=true ugi=hadoop/host123.xyz.com@APD.xyz.com (auth:SIMPLE)     ip=/10.0.2.15   cmd=getfileinfo src=/tmp/hive   dst=null        perm=null       proto=rpc\n");
//        Assert.assertEquals("hadoop", entity.user);
//        Assert.assertEquals(new Boolean(true), entity.allowed);
//    }

    public static void main(String[] args) throws Exception {
        HDFSAuditLogParser parser = new HDFSAuditLogParser();
        //2019-07-11T17:42:39.605+08:00 INFO IPC Server handler 6 on 8020 : allowed=true    ugi=dd_edw (auth:SIMPLE)    erp="dd_edw"    jobId="job_1562051348994_745878"    ip=/10.198.173.4    cmd=getfileinfo
        String log = "[2019-07-11T17:42:39.605+08:00] [INFO] [IPC Server handler 6 on 8020] : allowed=true    ugi=dd_edw (auth:SIMPLE)    erp=\"dd_edw\"    jobId=\"job_1562051348994_745878\"    ip=/10.198.173.4    cmd=getfileinfo ";
        String log1 = "2015-09-21 21:36:52,172 INFO IPC Server handler 6 on 8020 : allowed=true    ugi=dd_edw (auth:SIMPLE)    erp=\"dd_edw\"    jobId=\"job_1562051348994_745878\"    ip=/10.198.173.4    cmd=getfileinfo  " +
                "src=/user/dd_edw/gdm.db/gdm_m03_item_sku_da_arc/dm=2017-11/dt=2017-11-30/004272_0   dst=null    perm=null   proto=rpc   callerContext=mr_attempt_1562826810007_19568_m_002694_0 appID=application_1562826810007_19568   cost=47322";

         HDFSAuditLogObject entity = parser.parse(log1);
              //  parser.parse("2015-09-21 21:36:52,172 INFO FSNamesystem.audit: allowed=true   ugi=user1@APD.xyz.com (auth:SIMPLE)     ip=/10.0.2.15   cmd=getfileinfo src=/tmp/hive   dst=null        perm=null       proto=rpc\n");

        int index0 = log.indexOf(" ");
//        System.out.println(index0);
//        index0 = log.indexOf(" ", index0 + 1);
//        System.out.println(index0);
//        String data = log.substring(0, index0).trim();
//        System.out.println(data);
        System.out.println(entity.user);
    }
}
