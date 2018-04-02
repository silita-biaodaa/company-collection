package com.silita.biaodaa.service;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.model.TbPerson;
import com.silita.biaodaa.model.TbPersonQualification;

import java.util.List;

/**
 * Created by 91567 on 2018/4/2.
 */
public interface ICompanyService {
    /**
     * 批量添加企业资质证书列表页数据
     * @param companyQualifications
     */
    void batchInsertCompanyQualification(List<TbCompanyQualification> companyQualifications);

    /**
     * 添加企业资质证书列表页数据
     * @param companyQualification
     */
    void InsertCompanyQualification(TbCompanyQualification companyQualification);

    /**
     * 根据证书编号判断是否已抓取企业资质证书
     * @param certNo
     * @return
     */
    boolean checkCompanyQualificationByCertNo(String certNo);

    /**
     * 取得全部资质证书详情页面url
     * @return
     */
    List<String> getAllCompanyQualificationUrl();

    /**
     * 更新企业资质证书详情信息
     * 注意他与企业基本信息的关联关系
     * @param companyQualification
     */
    void updateCompanyQualificationUrlByCorpid(TbCompanyQualification companyQualification);

    /**
     * 添加企业基本信息
     * @param tbCompany
     */
    int insertCompanyInfo(TbCompany tbCompany);

    /**
     * 添加人员基本信息
     * @param tbPerson
     * @return
     */
    int insertPersionInfo(TbPerson tbPerson);

    /**
     * 添加人员执业证书信息
     * 根据证件号码判断是否需要抓取
     * @param tbPersonQualification
     */
    void insertPersonQualification(TbPersonQualification tbPersonQualification);

}
