package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.TbSafetyCertificateMapper;
import com.silita.biaodaa.model.TbSafetyCertificate;
import com.silita.biaodaa.service.ISafetyCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 91567 on 2018/4/9.
 */
@Service("safetyCertificateService")
public class SafetyCertificateServiceImpl implements ISafetyCertificateService {

    @Autowired
    private TbSafetyCertificateMapper tbSafetyCertificateMapper;

    @Override
    public void batchInsertSafetyCertificate(List<TbSafetyCertificate> safetyCertificates) {
        tbSafetyCertificateMapper.batchInsertSafetyCertificate(safetyCertificates);
    }

    public void insertSafetyCertificate(TbSafetyCertificate safetyCertificates) {
        boolean flag = tbSafetyCertificateMapper.getTotalByCertNo(safetyCertificates.getCertNo()) > 0;
        if(!flag) {
            tbSafetyCertificateMapper.InsertSafetyCertificate(safetyCertificates);
        }
    }
}
