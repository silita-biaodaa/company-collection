/*
package com.silita.biaodaa.service;

import com.silita.biaodaa.task.HuNanAllCompanyUpdateTask;
import com.silita.biaodaa.task.HuNanCompanyUpdateTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

*/
/**
 * Created by 91567 on 2018/4/24.
 *//*

@ContextConfiguration(locations = {"classpath:config/spring/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskUpdateTest {

    @Autowired
    private HuNanCompanyUpdateTask huNanBuilderCompanyUpdateTask;
    @Autowired
    private ICompanyService companyService;

    @Autowired
    HuNanAllCompanyUpdateTask huNanAllCompanyUpdateTask;



    */
/**
     * 按企业名称单个更新
     *//*

    @Test
    public void taskHuNanCompanyUpdate() {
        Map<String, Object> params = new HashMap();
//        params.put("tableName", "建筑业企业");
        params.put("comName", "绿茵美环境建设有限公司");
        huNanBuilderCompanyUpdateTask.taskBuilderCompany(params);
    }

    */
/**
     * 更新全部
     *//*

    @Test
    public void taskHuNanAllCompanyUpdate() {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> maps = companyService.listComNameAndTab();
        Map<String, Object> params;
        for (Map<String, Object> map : maps) {
            params = new HashMap<>();
            params.put("tableName", map.get("tab"));
            params.put("comName", map.get("com_name"));
            huNanBuilderCompanyUpdateTask.taskBuilderCompany(params);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("更新持续时间： " + (endTime - startTime) * 60 * 60 + "小时！");
    }

    */
/**
     * 建筑业企业
     *//*

    @Test
    public void taskHuNanBuilderCompanyUpdate() {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> maps = companyService.listComNameAndTabByTab("建筑业企业");
        Map<String, Object> params;
        for (Map<String, Object> map : maps) {
            params = new HashMap<>();
            params.put("tableName", map.get("tab"));
            params.put("comName", map.get("com_name"));
            huNanBuilderCompanyUpdateTask.taskBuilderCompany(params);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("更新持续时间： " + (endTime - startTime) * 60 * 60 + "小时！");
    }

    */
/**
     * 工程设计企业
     *//*

    @Test
    public void taskHuNanDesignCompanyUpdate() {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> maps = companyService.listComNameAndTabByTab("工程设计企业");
        Map<String, Object> params;
        for (Map<String, Object> map : maps) {
            params = new HashMap<>();
            params.put("tableName", map.get("tab"));
            params.put("comName", map.get("com_name"));
            huNanBuilderCompanyUpdateTask.taskBuilderCompany(params);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("更新持续时间： " + (endTime - startTime) * 60 * 60 + "小时！");
    }

    */
/**
     * 工程勘察企业
     *//*

    @Test
    public void taskHuNanSurveyCompanyUpdate() {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> maps = companyService.listComNameAndTabByTab("工程勘察企业");
        Map<String, Object> params;
        for (Map<String, Object> map : maps) {
            params = new HashMap<>();
            params.put("tableName", map.get("tab"));
            params.put("comName", map.get("com_name"));
            huNanBuilderCompanyUpdateTask.taskBuilderCompany(params);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("更新持续时间： " + (endTime - startTime) * 60 * 60 + "小时！");
    }

    */
/**
     * 工程监理企业
     *//*

    @Test
    public void taskHuNanSupervisionCompanyUpdate() {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> maps = companyService.listComNameAndTabByTab("工程监理企业");
        Map<String, Object> params;
        for (Map<String, Object> map : maps) {
            params = new HashMap<>();
            params.put("tableName", map.get("tab"));
            params.put("comName", map.get("com_name"));
            huNanBuilderCompanyUpdateTask.taskBuilderCompany(params);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("更新持续时间： " + (endTime - startTime) * 60 * 60 + "小时！");
    }

    @Test
    public void test() {
        huNanAllCompanyUpdateTask.allCompanyUpdateTask();
    }

}
*/
