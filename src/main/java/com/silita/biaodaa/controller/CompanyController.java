package com.silita.biaodaa.controller;

import com.silita.biaodaa.task.HuNanBuilderCompanyDetailTask;
import com.silita.biaodaa.task.HuNanCompanyQualificationListTask;
import com.silita.biaodaa.task.HuNanDesignCompanyDetailTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by 91567 on 2018/4/2.
 */
@Controller
@RequestMapping("/companytask")
public class CompanyController {

    @Autowired
    private HuNanCompanyQualificationListTask huNanCompanyQualificationListTask;
    @Autowired
    private HuNanBuilderCompanyDetailTask huNanBuilderCompanyDetailTask;
    @Autowired
    private HuNanDesignCompanyDetailTask huNanDesignCompanyDetailTask;

    /**
     * 列表
     * @throws Exception
     */
    @RequestMapping("/taskCompanyQualificationList")
    public void taskCompanyQualificationList() throws Exception {
        huNanCompanyQualificationListTask.getCompanyList();
    }

    /**
     * 建筑
     * @throws Exception
     */
    @RequestMapping("/taskhuNanBuilderCompanyDetail")
    public void huNanBuilderCompanyDetailTask() throws Exception {
        huNanBuilderCompanyDetailTask.taskBuilderCompany();
    }

    /**
     * 设计
     * @throws Exception
     */
    @RequestMapping("/taskhuNanDesignCompanyDetail")
    public void huNanDesignCompanyDetailTask() throws Exception {
        huNanDesignCompanyDetailTask.taskDesignCompany();
    }


}
