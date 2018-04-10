package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbCompanyQualificationMapper extends MyMapper<TbCompanyQualification> {

    /**
     * 批量添加企业资质证书
     * @param companyQualifications
     */
    void batchInsertCompanyQualification(List<TbCompanyQualification> companyQualifications);

    /**
     * 添加企业资质证书
     * @param companyQualification
     */
    void InsertCompanyQualification(TbCompanyQualification companyQualification);

    /**
     * 根据资质证书号获取资质证书数量
     * @param certNo
     * @return
     */
    Integer getTotalByCertNo(String certNo);

    /**
     * 取得全部资质证书详情页面url
     * @return
     */
    List<String> getAllCompanyQualificationUrlByTabName(String tableName);

    /**
     * 根据企业资质证书内部id更新企业资质证书内部
     * 注意记得添加企业id
     * @param companyQualification
     */
    void updateCompanyQualificationUrlByCorpid(TbCompanyQualification companyQualification);
}