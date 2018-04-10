package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbSafetyCertificate;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbSafetyCertificateMapper extends MyMapper<TbSafetyCertificate> {

    /**
     *
     * @param safetyCertificates
     */
    void batchInsertSafetyCertificate(List<TbSafetyCertificate> safetyCertificates);

    /**
     *
     * @param tbSafetyCertificate
     */
    void InsertSafetyCertificate(TbSafetyCertificate tbSafetyCertificate);

    /**
     *
     * @param certNo
     * @return
     */
    Integer getTotalByCertNo(String certNo);

}