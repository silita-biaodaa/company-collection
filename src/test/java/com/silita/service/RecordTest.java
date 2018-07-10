package com.silita.service;

import com.silita.task.ChangShaGGZYRecordTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:config/spring/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RecordTest {

    @Autowired
    private ChangShaGGZYRecordTask changShaGGZYRecordTask;

    @Test
    public void taskCompanyQualificationList() throws Exception {
        changShaGGZYRecordTask.task();
    }
}
