package com.silita.biaodaa.service;

import com.silita.biaodaa.task.HuNanCompanyUpdateTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/24.
 */
@ContextConfiguration(locations = {"classpath:config/spring/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskUpdateTest {

    @Autowired
    private HuNanCompanyUpdateTask huNanBuilderCompanyUpdateTask;
    @Autowired
    private ICompanyUpdateService companyUpdateService;


    /**
     * 按企业名称单个更新
     */
    @Test
    public void taskHuNanBuilderCompanyUpdate() {
        Map<String, Object> params = new HashMap();
        params.put("tableName", "工程设计企业");
        params.put("comName", "湖南智谋规划工程设计咨询有限责任公司");
        huNanBuilderCompanyUpdateTask.taskBuilderCompany(params);
    }

    /**
     * 逐个更新企业
     */
    @Test
    public void taskHuNanCompanyUpdate() {
        long startTime = System.currentTimeMillis();   //获取开始时间
        List<Map<String, Object>> maps = companyUpdateService.listComNameAndTab();
        Map<String, Object> params;
        for (Map<String, Object> map : maps) {
            params = new HashMap<>();
            params.put("tableName", map.get("tab"));
            params.put("comName", map.get("com_name"));
            huNanBuilderCompanyUpdateTask.taskBuilderCompany(params);
        }
        long endTime = System.currentTimeMillis();   //获取开始时间
        System.out.println("程序运行时间： " + (endTime - startTime) * 60 * 60 + "小时！");
    }

}
