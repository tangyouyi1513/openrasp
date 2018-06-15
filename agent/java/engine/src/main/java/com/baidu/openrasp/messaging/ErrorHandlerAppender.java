package com.baidu.openrasp.messaging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by tyy on 18-6-15.
 *
 * 错误日志收集
 */
public class ErrorHandlerAppender extends AppenderSkeleton {

    @Override
    protected void append(LoggingEvent event) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
