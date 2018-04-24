package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonQualification;
import com.silita.biaodaa.utils.MyMapper;

import java.util.Map;

public interface TbPersonQualificationMapper extends MyMapper<TbPersonQualification> {

    /**
     * 添加人员职业证书
     * @param tbPersonQualification
     */
    void insertPersonQualification(TbPersonQualification tbPersonQualification);

    /**
     * 根据人员证书编号人员主键名称编号判断是否还要抓取
     * @param tbPersonQualification
     * @return
     */
    Integer getTotalByCertNoAndComId(TbPersonQualification tbPersonQualification);

    /**
     * 根据人员证书url判断是否已经抓取
     * 一个公司有多个资质证书、多个证书对应一个公司的人员证书 所以抓一个公司的一个资质证书就可以了
     * 人员的证书会发生变化，所以加上公司id防止证书换公司不抓问题
     * @param params
     * @return
     */
    Integer getTolalByPersonQualificationUrlAndComId(Map<String, Object> params);


}