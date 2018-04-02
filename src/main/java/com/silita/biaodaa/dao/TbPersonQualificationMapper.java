package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonQualification;
import com.silita.biaodaa.utils.MyMapper;

public interface TbPersonQualificationMapper extends MyMapper<TbPersonQualification> {

    /**
     * 添加人员职业证书
     * @param tbPersonQualification
     */
    void insertPersonQualification(TbPersonQualification tbPersonQualification);

    /**
     * 根据人员证书编号判断是否还要抓取
     * @param certNo
     * @return
     */
    Integer getTotalByCertNo(String certNo);


}