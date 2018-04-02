package com.silita.biaodaa.controller;

import com.silita.biaodaa.task.HuNanCompanyDetailTask;
import com.silita.biaodaa.task.HuNanCompanyQualificationListTask;
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
    private HuNanCompanyDetailTask huNanCompanyDetailTask;

    @RequestMapping("/taskCompanyQualificationList")
    public void taskCompanyQualificationList() throws Exception {
        huNanCompanyQualificationListTask.getCompanyList();
    }

    @RequestMapping("/huNanCompanyDetailTask")
    public void huNanCompanyDetailTask() throws Exception {
        huNanCompanyDetailTask.task();
    }

}
