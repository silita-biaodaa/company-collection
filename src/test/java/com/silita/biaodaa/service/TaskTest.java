package com.silita.biaodaa.service;

import com.silita.biaodaa.task.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by 91567 on 2018/4/10.
 */
@ContextConfiguration(locations = {"classpath:config/spring/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskTest {

    @Autowired
    private HuNanCompanyQualificationListTask huNanCompanyQualificationListTask;
    @Autowired
    private HuNanBuilderCompanyDetailTask huNanBuilderCompanyDetailTask;
    @Autowired
    private HuNanDesignCompanyDetailTask huNanDesignCompanyDetailTask;
    @Autowired
    private HuNanSurveyCompanyDetailTask huNanSurveyCompanyDetailTask;
    @Autowired
    private HuNanSupervisorCompanyDetailTask huNanSupervisorCompanyDetailTask;
    @Autowired
    private CompanyQualificationsRangeTask companyQualificationsRangeTask;

    /**
     * 列表（包含列表、外省入湘、企业安许）
     * @throws Exception
     */
   @Test
    public void taskCompanyQualificationList() throws Exception {
        huNanCompanyQualificationListTask.getCompanyList();
    }

    /**
     * 建筑
     * @throws Exception
     */
    @Test
    public void huNanBuilderCompanyDetailTask() throws Exception {
        huNanBuilderCompanyDetailTask.taskBuilderCompany();
    }

    /**
     * 设计
     * @throws Exception
     */
    @Test
    public void huNanDesignCompanyDetailTask() throws Exception {
        huNanDesignCompanyDetailTask.taskDesignCompany();
    }

    /**
     * 勘察
     * @throws Exception
     */
    @Test
    public void huNanSurveyCompanyDetailTask() throws Exception {
        huNanSurveyCompanyDetailTask.taskSurveyCompany();
    }

    /**
     * 监理
     * @throws Exception
     */
    @Test
    public void HuNanSupervisorCompanyDetailTask() throws Exception {
        huNanSupervisorCompanyDetailTask.taskSupervisorCompany();
    }

    /**
     * 业务更新企业资质（最后执行）
     * @throws Exception
     */
    @Test
    public void CompanyQualificationsRangeTask() throws Exception {
        companyQualificationsRangeTask.updateCompanyAptitudeRange();
    }


}
