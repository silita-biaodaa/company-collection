package com.silita.biaodaa.service;

import com.silita.biaodaa.model.*;

import java.util.List;
import java.util.Map;

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
     * 根据tab类型取得全部资质证书详情页面url
     *
     * @return
     */
    List<String> getAllCompanyQualificationUrlByTab(String tableName);

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
    int insertProjectBuild(TbProjectBuild tbProjectBuild);

    /**
     * 添加施工人员信息或监理人员信息
     * 根据人员姓名、证书编号、安全证书编号判断是否添加
     * @param tbPersonProject
     */
    void insertPersonProject(TbPersonProject tbPersonProject);

    /**
     * 添加施工图审查信息(设计)
     * 根据施工图审查合格书编号、设计单位判断是否添加
     * @param tbProjectDesign
     */
    int insertProjectDesign(TbProjectDesign tbProjectDesign);

    /**
     * 添加勘察设计人员名单
     * 根据人员姓名、公司名称、角色判断是否添加
     * @param tbPersonDesign
     */
    void insertPersonDesign(TbPersonDesign tbPersonDesign);

    /**
     * 添加施工图审查信息(勘察)
     * 根据施工图审查合格书编号、勘察单位判断是否添加
     */
    int insertProjectDesignTwo(TbProjectDesign tbProjectDesign);

    /**
     * 添加监理合同段信息
     * 根据监理内部id判断是否添加
     * @return
     */
    int insertProjectSupervisor(TbProjectSupervision tbProjectSupervision);


    /**
     *
     * @param tableName
     * @return
     */
    int getTotalCompanyQualificationByTabName(String tableName);

    /**
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getCompanyQualification(Map<String, Object> params);

    /**
     *
     * @param name
     * @return
     */
    AllZh getAllZhByName(String name);

    /**
     *
     * @param majorUuid
     * @return
     */
    String getMajorNameBymajorUuid(String majorUuid);

    /**
     *
     * @param tbCompanyAptitudes
     */
    void batchInsertCompanyAptitude(List<TbCompanyAptitude> tbCompanyAptitudes);

//    List<TbCompanyAptitude>

}
