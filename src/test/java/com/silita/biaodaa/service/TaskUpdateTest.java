package com.silita.biaodaa.service;

import com.silita.biaodaa.task.HuNanCompanyUpdateTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/24.
 */
@ContextConfiguration(locations = {"classpath:config/spring/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskUpdateTest {

    @Autowired
    private HuNanCompanyUpdateTask huNanBuilderCompanyUpdateTask;

    @Test
    public void taskhuNanBuilderCompanyUpdate() {
        Map<String, Object> params = new HashMap();
        params.put("tableName", "工程设计企业");
        params.put("comName", "湖南智谋规划工程设计咨询有限责任公司");
        huNanBuilderCompanyUpdateTask.taskBuilderCompany(params);
    }
}
