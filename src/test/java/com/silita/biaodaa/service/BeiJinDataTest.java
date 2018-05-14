package com.silita.biaodaa.service;

import com.silita.biaodaa.task.CompanyQualificationsRangeTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by 91567 on 2018/5/9.
 */

@ContextConfiguration(locations = {"classpath:config/spring/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class BeiJinDataTest {

    @Autowired
    private CompanyQualificationsRangeTask companyQualificationsRangeTask;

    @Test
    public void CompanyQualificationsRangeTask() throws Exception {

        companyQualificationsRangeTask.splitCompanyQualifications();
        companyQualificationsRangeTask.updateCompanyAptitudeRange();
    }
}
