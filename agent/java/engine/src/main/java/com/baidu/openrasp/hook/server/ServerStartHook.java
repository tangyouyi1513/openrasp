package com.baidu.openrasp.hook.server;

import com.baidu.openrasp.hook.AbstractClassHook;
import com.baidu.openrasp.tool.model.ServerInfo;

/**
 * 服务器
 * Created by tyy on 18-6-12.
 */
public abstract class ServerStartHook extends AbstractClassHook {

    public static ServerInfo serverInfo;

    @Override
    public String getType() {
        return "server_start";
    }

    public static void serverTypeHandle(String serverType){
        if(serverType.equals("catalina")){

        }
        serverInfo = new ServerInfo(serverType,"");

    }
}
