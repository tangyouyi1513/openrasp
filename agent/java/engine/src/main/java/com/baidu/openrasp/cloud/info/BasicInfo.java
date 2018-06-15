/*
 * Copyright 2017-2018 Baidu Inc.
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

package com.baidu.openrasp.cloud.info;

import com.baidu.openrasp.ModuleLoader;
import com.baidu.openrasp.config.Config;
import com.baidu.openrasp.hook.server.ServerStartHook;

import java.util.UUID;

/**
 * Rasp 基本信息，只需上传一次
 * Created by tyy on 18-6-12.
 */
public class BasicInfo {

    public String serverName;
    public String serverVersion;
    public String raspHome;
    public String javaVersion;
    public String raspType = "java";
    public String raspVersion;
    public String osName;
    public String raspID;

    public static BasicInfo build() {
        BasicInfo basicInfo = new BasicInfo();
        basicInfo.serverName = ServerStartHook.serverInfo.getName();
        basicInfo.serverVersion = ServerStartHook.serverInfo.getVersion();
        basicInfo.raspHome = Config.getConfig().getBaseDirectory();
        basicInfo.javaVersion = System.getProperty("java.version");
        basicInfo.raspVersion = ModuleLoader.getAgentEngineVersion();
        basicInfo.osName = System.getProperty("os.name");
        basicInfo.osName = System.getProperty("os.version");
        basicInfo.raspID = UUID.randomUUID().toString().replace("-", "");
        return basicInfo;
    }

}
