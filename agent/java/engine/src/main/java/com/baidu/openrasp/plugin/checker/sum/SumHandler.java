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

package com.baidu.openrasp.plugin.checker.sum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tyy on 18-6-13.
 * 处理检测的统计信息
 */
public class SumHandler {

    private ConcurrentHashMap<String, AtomicLong> infoCheckResultSum;
    private ConcurrentHashMap<String, AtomicLong> blockCheckResultSum;

    public ConcurrentHashMap<String, AtomicLong> getInfoCheckResultSum() {
        return infoCheckResultSum;
    }

    public void setInfoCheckResultSum(ConcurrentHashMap<String, AtomicLong> infoCheckResultSum) {
        this.infoCheckResultSum = infoCheckResultSum;
    }

    public ConcurrentHashMap<String, AtomicLong> getBlockCheckResultSum() {
        return blockCheckResultSum;
    }

    public void setBlockCheckResultSum(ConcurrentHashMap<String, AtomicLong> blockCheckResultSum) {
        this.blockCheckResultSum = blockCheckResultSum;
    }

    public void incrementBlock(String type) {
        AtomicLong count = blockCheckResultSum.putIfAbsent(type, new AtomicLong(1));
        if (count != null) {
            count.incrementAndGet();
        }
    }

    public void incrementInfo(String type) {
        AtomicLong count = infoCheckResultSum.putIfAbsent(type, new AtomicLong(1));
        if (count != null) {
            count.incrementAndGet();
        }
    }
}
