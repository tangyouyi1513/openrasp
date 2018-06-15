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

import com.baidu.openrasp.config.Config;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;

/**
 * 心跳线程入口
 * Created by tyy on 18-6-12.
 */
public class AppBeat implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(CloudControlManager.class.getName());

    private DatagramSocket udpClient;
    private boolean isAlive = true;
    private boolean isLogin = false;
    private int udpPort;

    public AppBeat(int udpPort) throws IOException {
        this.udpPort = udpPort;
        initUdp();
    }

    @Override
    public void run() {
        login();
        while (isAlive) {
            try {
                if (!isLogin) {
                    login();
                } else {

                }
                Thread.sleep(Config.getConfig().getHeartbeatDelay());
            } catch (Exception e) {
                LOGGER.error("heartbeat error", e);
            }
        }
    }

    private void login() {

    }

    /**
     * 开启定时心跳
     */
    public void start() {

        new Thread(this).start();
    }

    /**
     * 开启 udp 通信
     */
    private void initUdp() throws IOException {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        if (udpPort < 0) {
            LOGGER.error("must set the agent.port property with positive integer");
        } else {
            udpClient = new DatagramSocket();
            udpClient.connect(address, udpPort);
        }
    }

    /**
     * 心跳
     */
    private void beat() {

    }

    /**
     * 停止心跳
     */
    public void stop() {
        isAlive = false;
        udpClient.close();
    }

}
