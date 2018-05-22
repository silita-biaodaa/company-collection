package com.silita.biaodaa.service;

import com.silita.biaodaa.model.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/19.
 */
public interface ICompanyService {

    /**
     * 添加外省入湘企业
     *
     * @param tbCompanyInto
     */
    void insertCompanyInto(TbCompanyInto tbCompanyInto);

    /**
     * 批量添加企业安全证书
     *
     * @param safetyCertificates
     */
    void batchInsertSafetyCertificate(List<TbSafetyCertificate> safetyCertificates);

    /**
     * 添加企业安全证书
     *
     * @param safetyCertificates
     */
    void insertSafetyCertificate(TbSafetyCertificate safetyCertificates);

    /**
     * 添加企业资质证书列表页数据
     * @param companyQualification
     */
    void insertCompanyQualification(TbCompanyQualification companyQualification);

    /**
     * 批量添加企业资质证书列表页数据
     *
     * @param companyQualifications
     */
    void batchInsertCompanyQualification(List<TbCompanyQualification> companyQualifications);

    /**
     * 根据tab类型取得全部资质证书详情页面url
     *
     * @return
     */
    List<String> listCompanyQualificationUrlByTab(String tableName);

    /**
     * 添加企业基本信息
     * 根据组织机构代码、工商营业执照号判断是否添加
     *
     * @return 返回插入的公司主键
     */
    int insertCompanyInfo(TbCompany tbCompany);

    /**
     * 更新企业资质证书详情信息
     * 注意他与企业基本信息的关联关系
     *
     * @param companyQualification
     */
    void updateCompanyQualificationUrlByCorpid(TbCompanyQualification companyQualification);


    //###############数据更新相关###############

    /**
     * 查询全部企业资质证书名称、和类别
     * @return
     */
    List<Map<String, Object>> listComNameAndTab();

    /**
     * 根据tab名称查询全部企业资质证书名称、和类别
     * @return
     */
    List<Map<String, Object>> listComNameAndTabByTab(String tab);

    /**
     * 根据公司名称和类别获取证书url
     * @param params
     * @return
     */
    List<String> getAllCompanyQualificationUrlByTabAndCompanyName(Map<String, Object> params);

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
     * 根据证书url获取公司id
     * @param url
     * @return
     */
    TbCompanyQualification getComIdByUrl(String url);




    /**
     * 插入异常信息
     * @param tbExceptionUrl
     */
    void insertException(TbExceptionUrl tbExceptionUrl);



}

