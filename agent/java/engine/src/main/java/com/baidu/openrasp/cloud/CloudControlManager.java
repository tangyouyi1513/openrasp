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

package com.baidu.openrasp.cloud;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 云控模块
 * Created by tyy on 18-6-12.
 */
public class CloudControlManager {

    private static final Logger LOGGER = Logger.getLogger(CloudControlManager.class.getName());
    public static final int MAX_LENGTH = 2048;
    public static final int BEAT_DELAY = 60;
    private static DatagramSocket udpClient;
    private static AppBeat appBeat;

    /**
     * 开启云控
     */
    public static void init(int udpPort) {
        try {
            appBeat = new AppBeat(udpPort);
            appBeat.start();
        } catch (IOException e) {
            LOGGER.error("failed to start cloud control: ", e);
        }
    }

    public static void release() {
        if (appBeat != null) {
            appBeat.stop();
        }
    }

}
