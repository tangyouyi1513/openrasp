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

import com.baidu.openrasp.plugin.checker.CheckerManager;
import com.baidu.openrasp.plugin.event.CheckEventListener;
import com.baidu.openrasp.plugin.info.EventInfo;

/**
 * Created by tyy on 18-6-13.
 *
 * 统计 hook 检测的信息
 */
public class SumEventListener implements CheckEventListener {

    @Override
    public void onCheckUpdate(EventInfo info) {
        if (info.isBlock()) {
            CheckerManager.sumHandler.incrementBlock(info.getParameter().getType().toString());
        } else {
            CheckerManager.sumHandler.incrementInfo(info.getParameter().getType().toString());
        }
    }
}

