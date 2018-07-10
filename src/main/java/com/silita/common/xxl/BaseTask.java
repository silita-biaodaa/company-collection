package com.silita.common.xxl;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;


public abstract class BaseTask extends IJobHandler {
    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        MyXxlLogger.info(this.getClass()+"任务执行开始...");
        Long startTime  = System.currentTimeMillis();
        try {
            JSONObject jsonObject = null;
            if(params!=null && params.length>0){
                String argStr = params[0].trim();
                jsonObject= JSONObject.parseObject(argStr);
            }
            runTask(jsonObject);
            MyXxlLogger.info(this.getClass()+"任务执行成功!");
            return ReturnT.SUCCESS;
        }catch (InterruptedException ie){
            MyXxlLogger.warn(this.getClass()+"调度中心任务手动停止。");
            throw ie;
        }catch (Exception e){
            MyXxlLogger.error(this.getClass()+"任务执行失败："+e.getMessage(),e);
            return ReturnT.FAIL;
        }finally {
            clear();//子类实现资源释放
            System.gc();
            MyXxlLogger.info(this.getClass()+"任务执行结束，耗时：" + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    /**
     * 调度中心配置的运行参数对象
     * 获取方式：jsonObject.get(key)
     * @param jsonObject
     * @throws Exception
     */
    public abstract void runTask(JSONObject jsonObject) throws Exception;


    /**
     * 清理资源
     * 可子类实现
     */
    protected void clear(){}

}
