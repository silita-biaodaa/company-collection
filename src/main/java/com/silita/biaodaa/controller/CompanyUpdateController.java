package com.silita.biaodaa.controller;

import com.silita.biaodaa.task.HuNanCompanyUpdateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by 91567 on 2018/4/24.
 */
@Controller
@RequestMapping("/companyUpdateTask")
public class CompanyUpdateController {

    @Autowired
    private HuNanCompanyUpdateTask huNanCompanyUpdateTask;

    @RequestMapping("/taskCompanyUpdate")
    public void taskCompanyQualificationList(@RequestBody Map<String, Object> params) throws Exception {
        huNanCompanyUpdateTask.taskBuilderCompany(params);
    }
}
