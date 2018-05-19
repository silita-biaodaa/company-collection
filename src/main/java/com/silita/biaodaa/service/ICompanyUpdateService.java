package com.silita.biaodaa.service;

import com.silita.biaodaa.model.*;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/24.
 */
public interface ICompanyUpdateService {

    /**
     * 根据公司名称和类别获取证书url
     * @param params
     * @return
     */
    List<String> getAllCompanyQualificationUrlByTabAndCompanyName(Map<String, Object> params);

    /**
     * 插入异常信息
     * @param tbExceptionUrl
     */
    void insertException(TbExceptionUrl tbExceptionUrl);

    /**
     * 根据证书url获取公司id
     * @param url
     * @return
     */
    TbCompanyQualification getComIdByUrl(String url);

    /**
     * 更新企业基本信息
     * @param tbCompany
     */
    void updateCompany(TbCompany tbCompany);

    /**
     * 根据证书url更新企业证书信息
     * @param tbCompanyQualification
     */
    void updateCompanyQualificationByUrl(TbCompanyQualification tbCompanyQualification);

    /**
     * 根据证书编号、注册类别、公司id判断证书是否抓取
     * @param tbPersonQualification
     * @return
     */
    boolean checkPersonQualificationIsExist(TbPersonQualification tbPersonQualification);

    /**
     * 根据公司id删除公司下面的全部人员证书
     * @param comId
     */
    void deletePersonQualByComId(Integer comId);

    /**
     * 添加人员基本信息
     * @param tbPerson
     * @return
     */
    int insertPersionInfo(TbPerson tbPerson);

    /**
     * 根据证书编号、注册类别、公司id判断是否抓取
     * @param tbPersonQualification
     */
    void insertPersonQualification(TbPersonQualification tbPersonQualification);

    /**
     * 添加人员变更信息
     * @param tbPersonChange
     */
    void insertPeopleChange(TbPersonChange tbPersonChange);

    /**
     * 查询全部企业资质证书名称、和类别
     * @return
     */
    List<Map<String, Object>> listComNameAndTab();


    //----------------------------------------------
    //----------拆分单个公司资质--------------------
    //----------------------------------------------

    void deleteCcompanyAptitudeByComId(Integer companyId);

    /**
     * 根据企业id获取企业资质证书
     * @param companyId
     * @return
     */
    List<TbCompanyQualification> getCompanyQualificationByComId(Integer companyId);

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
     * 按批次取拆分后的企业证书资质
     */
    List<TbCompanyAptitude> listCompanyAptitude(Integer companyId);

    /**
     * 更新企业资质
     * @param tbCompany
     */
    void updateCompanyRangeByComId(TbCompany tbCompany);


    /**
     * 据证书编号、注册类别、公司id判断证书是否存在
     * @param tbPersonHunan
     * @return
     */
    boolean checkPersonHunanIsExist(TbPersonHunan tbPersonHunan);

    /**
     * 添加人员资质证书信息
     * @param tbPersonHunan
     */
    void insertPersonHunan(TbPersonHunan tbPersonHunan);

    /**
     * 根据企业id删除企业下面的资质证书
     * @param companyId
     */
    void deletePersonHunanByCompanyId(Integer companyId);

}
