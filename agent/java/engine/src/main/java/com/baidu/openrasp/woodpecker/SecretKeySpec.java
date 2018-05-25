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
package com.baidu.openrasp.woodpecker;

/**
　　* @Description: AES加密所需key
　　* @author anyang
　　* @date 2018/5/24 19:14
　　*/
public enum SecretKeySpec {

    KEY1("is_bywxbyjb!rand(zz)*eoyyb33_ca*", 0),
    KEY2("is_amazing_!rand(vv)*lsttc34_cb?", 1),
    KEY3("is_zdnkxjh_!rand(v5)*tianq35_cc$", 2),
    KEY4("is_ylwjx_z_!rand(v6)*caiho36_cd%", 3),
    KEY5("is_ddheyyh_!rand(7v)*xiaoh37_cr^", 4),
    KEY6("is_tdbyzzq_!rand(9v)*Ctoss38_cz&", 5),
    KEY7("is_xtbcshc_!rand(0v)*teesq39_cx*", 6),
    KEY8("is_gxnrshd_!rand(v1)*caiaa30_ci@", 7),
    KEY9("is_qgxg_hw_!rand(xv)*ktkfc31_co!", 8),
    KEY10("is_dshgmml_!rand(#v)*ktmdl32_cp:",9);

    private String key;

    private int index;

    SecretKeySpec(String key, int index) {
        this.key = key;
        this.index = index;
    }

    public static String getName(int index) {
        for (SecretKeySpec key  : SecretKeySpec.values()) {
            if (key.getIndex() == index) {
                return key.getKey();
            }
        }
        return null;
    }
    public static int getcode( String name){

        for (SecretKeySpec key  : SecretKeySpec.values()) {
            if (key.getKey().equals(name)) {
                return key.getIndex();
            }
        }
        return -1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
