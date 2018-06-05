package com.silita.biaodaa.common.xxl;

import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyXxlLogger {

    private static Logger logger = LoggerFactory.getLogger("MyXxlLogger");

    public static void debug(String appendLog) {
        XxlJobLogger.log(appendLog);
        logger.debug(appendLog);
    }

    public static void info(String appendLog) {
        XxlJobLogger.log(appendLog);
        logger.info(appendLog);
    }

    public static void warn(String appendLog) {
        XxlJobLogger.log("$warn$:"+appendLog);
        logger.warn(appendLog);
    }

    public static void error(String appendLog,Throwable e) {
        logger.error(appendLog,e);
        XxlJobLogger.log("$error$:"+e.getMessage());
        StackTraceElement[] se = e.getStackTrace();
        for(int i=0;i<se.length;i++){
            XxlJobLogger.log(se[i].toString());
        }
    }

    public static void error(Throwable e) {
        error(e.getMessage(),e);
    }

}
