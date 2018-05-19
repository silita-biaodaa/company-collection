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
     * 根据人员名字、身份证6位、性别判断是否添加
     *
     * @param tbPerson
     * @return
     */
    int insertPersionInfo(TbPerson tbPerson);

    /**
     * 添加人员执业证书信息
     * 根据证书编号、注册类别、公司id判断证书是否抓取
     *
     * @param tbPersonQualification
     */
    void insertPersonQualification(TbPersonQualification tbPersonQualification);

    /**
     * 根据项目编号、内部id判断是否添加项目基本信息
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
     * 根据证书编号施工项目id、判断是否添加
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
     * 根据人员证书url判断是否已经抓取
     * 一个公司有多个资质证书、多个证书对应一个公司的人员证书 所以抓一个公司的一个资质证书就可以了
     * 人员的证书会变换公司，所以加上公司id防止证书换公司漏抓问题
     * @param tbPersonQualification
     * @return true存在
     */
    boolean checkPersonQualificationIsExist(TbPersonQualification tbPersonQualification);

    /**
     * 根据施工项目内部id判断该施工项目是否已抓取
     * 一个施工类企业有多个施工类资质证书、多个施工类资质证书对应一个公司的施工项目
     * @param params
     * @return
     */
    boolean checkProjectBuildExist(Map<String, Object> params);

    /**
     * 设计、勘察
     * @param
     * @return
     */
    boolean checkProjectDesignExist(Map<String, Object> params);

    /**
     * 监理
     * @param params
     * @return
     */
    boolean checkProjectSupervisionExist(Map<String, Object> params);

    /**
     * 批量插入人员变更信息
     * @param tbPersonChanges
     */
    void batchInsertPeopleChange(List<TbPersonChange> tbPersonChanges);

    /**
     * 插入人员变更信息
     * 根据人员id变更时间判断是否抓取
     * @param tbPersonChange
     */
    void insertPeopleChange(TbPersonChange tbPersonChange);

    /**
     * 插入异常信息
     * @param tbExceptionUrl
     */
    void insertException(TbExceptionUrl tbExceptionUrl);


    //####################以下为拆分资质相关业务########################

    /**
     *
     * @param tableName
     * @return
     */
    int getCompanyQualificationTotalByTabName(String tableName);

    /**
     *
     * @param params
     * @return
     */
    List<TbCompanyQualification> getCompanyQualifications(Map<String, Object> params);

    /**
     * 根据资质别名取得资质
     * @param name
     * @return
     */
    AllZh getAllZhByName(String name);

    /**
     * 根据资质别名id取得资质标准名称
     * @param majorUuid
     * @return
     */
    String getMajorNameBymajorUuid(String majorUuid);

    /**
     * 批量插入拆分后的企业证书资质
     * @param tbCompanyAptitudes
     */
    void batchInsertCompanyAptitude(List<TbCompanyAptitude> tbCompanyAptitudes);

    /**
     * 取得拆分后的企业证书资质
     * @return
     */
    Integer getCompanyAptitudeTotal();

    /**
     * 按批次取拆分后的企业证书资质
     */
    List<TbCompanyAptitude> listCompanyAptitude(Map<String, Object> params);

    /**
     * 添加企业资质到企业基本信息表（方便业务查询）
     * @param tbCompany
     */
    void updateCompanyRangeByComId(TbCompany tbCompany);

}
