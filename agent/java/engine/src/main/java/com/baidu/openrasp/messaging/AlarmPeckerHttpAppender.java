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
package com.baidu.openrasp.messaging;

import com.baidu.openrasp.HookHandler;
import com.baidu.openrasp.woodpecker.AESUtils;
import com.baidu.openrasp.woodpecker.GrayboxMaster;
import com.baidu.openrasp.woodpecker.RequestHeader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 　　* @Description: 啄木鸟漏洞信息上报Appender
 * 　　* @author anyang
 * 　　* @date 2018/5/24 19:05
 */
public class AlarmPeckerHttpAppender extends AppenderSkeleton {

    public static final Logger LOGGER = Logger.getLogger(AlarmPeckerHttpAppender.class.getName());
    /**
     * 啄木鸟流量标识
     */
    public static final String WOODPECKER_TAG_BEGIN = "BD-rain inf-ssl-duty-scan";
    public static final String WOODPECKER_TAG_END = "GrAyBoX";

    private static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    private final HttpClient httpClient;

    private String url;
    private int connectionTimeout = -1;
    private int readTimeout = -1;
    /**
     * AES加密秘钥序号
     */
    public static int encryptCode = 0;

    public AlarmPeckerHttpAppender() {

        httpClient = new AsyncHttpClient();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void activateOptions() {
        connectionTimeout = connectionTimeout < DEFAULT_CONNECTION_TIMEOUT ? DEFAULT_CONNECTION_TIMEOUT : connectionTimeout;
        readTimeout = readTimeout < DEFAULT_READ_TIMEOUT ? DEFAULT_READ_TIMEOUT : readTimeout;
    }

    public boolean checkEntryConditions() {
        if (httpClient == null) {
            LogLog.warn("HttpClient need to be initialized.");
            return false;

        } else if (this.closed) {
            LogLog.warn("Not allowed to write to a closed appender.");
            return false;

        } else if ((url == null) || url.trim().isEmpty()) {
            LogLog.warn("url need to be initialized.");
            return false;
        }
        return true;
    }


    @Override
    protected void append(LoggingEvent loggingEvent) {
        if (checkEntryConditions()) {
            try {
                String msg = loggingEvent.getMessage().toString();
                JsonObject jsonObject = new JsonParser().parse(msg).getAsJsonObject();
                String userAgent = jsonObject.get("user_agent").getAsString();
                //判断是否是啄木鸟流量
                if (userAgent.contains(WOODPECKER_TAG_BEGIN) && userAgent.contains(WOODPECKER_TAG_END)) {
                    ByteString encryped = ByteString.copyFrom(getEncrypBytes(msg));
                    RequestHeader.pb_header.Builder builder = RequestHeader.pb_header.newBuilder();
                    builder.setData(encryped);
                    builder.setVersion(2);
                    builder.setEncryptCode(encryptCode);
                    builder.setCompressMethod(0);
                    httpClient.request(url, builder.build().toByteArray(), connectionTimeout, readTimeout);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("AlarmPeckerHttpAppender log print failed", e);
            }
        }

    }


    public byte[] getEncrypBytes(String s) throws Exception {

        /**
         　　* @Description: 实例化protobuf类，并加密
         　　* @param s 待上报的漏洞日志消息
         　　* @return 返回加密后的字节数组
         　　* @author anyang
         　　* @date 2018/5/24 19:09
         　　*/
        JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
        byte[] res = null;
        GrayboxMaster.pb_vul_data.Builder vulDataBuilder = GrayboxMaster.pb_vul_data.newBuilder();
        GrayboxMaster.pb_vul_info.Builder vulInfoBuilder = GrayboxMaster.pb_vul_info.newBuilder();
        GrayboxMaster.pb_graybox_vul.Builder grayboxVulBuilder = GrayboxMaster.pb_graybox_vul.newBuilder();

        vulDataBuilder.addArgs(jsonObject.get("attack_params").getAsString());
        vulDataBuilder.addTraceStack(jsonObject.get("stack_trace").getAsString());
        String separator = System.getProperty("file.separator");
        String basePath = jsonObject.get("base_path").getAsString();
        String filePath = basePath + StringUtils.join(jsonObject.get("path").getAsString().split("/"), separator);
        vulDataBuilder.setPhpFilePath(filePath);
        String className = getClassName(jsonObject.get("stack_trace").getAsString());
        vulDataBuilder.setClassFuncName(className);
        vulDataBuilder.setVulLine(0);

        vulInfoBuilder.setRequestMethod(jsonObject.get("request_method").getAsString());
        vulInfoBuilder.setVulName(jsonObject.get("attack_type").getAsString());
        vulInfoBuilder.setUrl(jsonObject.get("referer").getAsString());
        long eventTime = convertTimeToLong(jsonObject.get("event_time").getAsString());
        vulInfoBuilder.setTimeStamp(eventTime);
        //根据user_agent获取任务id、子任务id和proc_name
        String userAgent = jsonObject.get("user_agent").getAsString();
        String[] arr = userAgent.substring(userAgent.lastIndexOf("(") + 1, userAgent.lastIndexOf(")")).split(",");
        vulInfoBuilder.setPocName(arr[2]);
        vulInfoBuilder.setTaskId(Integer.valueOf(arr[0]));
        vulInfoBuilder.setSubtaskId(Integer.valueOf(arr[1]));
//        vulInfoBuilder.setPocName("common_sql_timebase_1_all");
//        vulInfoBuilder.setTaskId(641805010);
//        vulInfoBuilder.setSubtaskId(64180);
        if (jsonObject.get("body") != null) {

            ByteString bytes = ByteString.copyFrom(jsonObject.get("body").getAsString().getBytes());
            vulInfoBuilder.setPostBody(bytes);
        }
        vulInfoBuilder.setVulData(vulDataBuilder.build());
        grayboxVulBuilder.setTimeStamp(System.currentTimeMillis());
        grayboxVulBuilder.setHostIp(jsonObject.get("server_ip").getAsString());
        grayboxVulBuilder.addVulInfo(vulInfoBuilder.build());
        grayboxVulBuilder.setHostName(jsonObject.get("host_name").getAsString());
        byte[] encrypBytes = grayboxVulBuilder.build().toByteArray();
        String key = AESUtils.getKey();
        encryptCode = AESUtils.getIndex(key);
        res = AESUtils.encrypt(encrypBytes, key.substring(0, 16));
        return res;
    }


    public static Long convertTimeToLong(String time) {

        /**
         　　* @Description: 转换固定格式的时间转换成时间戳
         　　* @param 固定格式时间字符串
         　　* @return 时间戳
         　　* @author anyang
         　　* @date 2018/5/24 19:20
         　　*/

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            date = sdf.parse(time);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String getClassName(String stackTrace) {

        /**
         　　* @Description: 获取漏洞的hook类
         　　* @param  调用栈
         　　* @return hook类名
         　　* @author anyang
         　　* @date 2018/5/24 19:16
         　　*/

        String className = null;
        String[] arr = stackTrace.split("\\n");

        for (String s : arr) {
            if (s.contains("reflect") || s.contains("invoke")) {
                continue;
            }
            String[] clazz = s.substring(s.indexOf("(") + 1, s.indexOf(")")).split(":");
            if (clazz.length <= 1) {
                className = clazz[0];
                break;
            } else {

                if (Integer.valueOf(clazz[1]) <= 0) {
                    className = clazz[0];
                    break;
                }
            }
        }
        return className;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }


}
