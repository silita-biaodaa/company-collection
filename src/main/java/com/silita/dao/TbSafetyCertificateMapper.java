package com.silita.dao;

import com.silita.model.TbSafetyCertificate;
import com.silita.utils.MyMapper;

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
    void insertSafetyCertificate(TbSafetyCertificate tbSafetyCertificate);

    /**
     *
     * @param tbSafetyCertificate
     * @return
     */
    Integer getTotalByCertNoAndCompanyName(TbSafetyCertificate tbSafetyCertificate);

    /**
     *
     * @param tbSafetyCertificate
     */
    void updateSafetyCertificate(TbSafetyCertificate tbSafetyCertificate);

    /**
     *
     * @param tbSafetyCertificate
     * @return
     */
    TbSafetyCertificate getSafetyCertificateByCertNoAndCompanyName(TbSafetyCertificate tbSafetyCertificate);
}