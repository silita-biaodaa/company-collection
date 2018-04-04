package com.silita.biaodaa.service;

import com.silita.biaodaa.model.*;

import java.util.List;

/**
 * Created by 91567 on 2018/4/2.
 */
public interface ICompanyService {
    /**
     * 批量添加企业资质证书列表页数据
     *
     * @param companyQualifications
     */
    void batchInsertCompanyQualification(List<TbCompanyQualification> companyQualifications);

    /**
     * 添加企业资质证书列表页数据
     *
     * @param companyQualification
     */
    void InsertCompanyQualification(TbCompanyQualification companyQualification);

    /**
     * 根据证书编号判断是否已抓取企业资质证书
     *
     * @param certNo
     * @return
     */
    boolean checkCompanyQualificationByCertNo(String certNo);

    /**
     * 取得全部资质证书详情页面url
     *
     * @return
     */
    List<String> getAllCompanyQualificationUrl();

    /**
     * 更新企业资质证书详情信息
     * 注意他与企业基本信息的关联关系
     *
     * @param companyQualification
     */
    void updateCompanyQualificationUrlByCorpid(TbCompanyQualification companyQualification);

    /**
     * 添加企业基本信息
     * 根据组织机构代码、工商营业执照号判断是否添加
     *
     * @param tbCompany
     */
    int insertCompanyInfo(TbCompany tbCompany);

    /**
     * 添加人员基本信息
     * 根据人员名字、身份证、性别判断是否添加
     *
     * @param tbPerson
     * @return
     */
    int insertPersionInfo(TbPerson tbPerson);

    /**
     * 添加人员执业证书信息
     * 根据证件号码判断是否需要抓取
     *
     * @param tbPersonQualification
     */
    void insertPersonQualification(TbPersonQualification tbPersonQualification);

    /**
     * 根据项目编号判断是否添加项目基本信息
     *
     * @param tbProject
     */
    int insertProjectInfo(TbProject tbProject);

    /**
     * 添加施工合段信息
     * 根据施工许可证号判断是否添加
     *
     * @param tbProjectBuild
     */
    void insertProjectBuild(TbProjectBuild tbProjectBuild);

}
