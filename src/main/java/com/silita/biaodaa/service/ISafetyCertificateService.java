package com.silita.biaodaa.service;

import com.silita.biaodaa.model.TbSafetyCertificate;

import java.util.List;

/**
 * Created by 91567 on 2018/4/9.
 */
public interface ISafetyCertificateService {

    /**
     * 批量添加企业安全证书
     *
     * @param safetyCertificates
     */
    void batchInsertSafetyCertificate(List<TbSafetyCertificate> safetyCertificates);

    /**
     *
     * @param safetyCertificates
     */
    void insertSafetyCertificate(TbSafetyCertificate safetyCertificates);
}
