package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

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
     * @param companyQualification
     * @return
     */
    Integer getTotalByCertNoAndTab(TbCompanyQualification companyQualification);

    /**
     * 根据tabname取得全部资质证书详情页面url
     * @return
     */
    List<String> getAllCompanyQualificationUrlByTabName(String tableName);

    /**
     * 根据企业资质证书内部id和证书编号更新企业资质证书内部
     * 注意记得添加企业id
     * @param companyQualification
     */
    void updateCompanyQualificationUrlByCorpidAndCertId(TbCompanyQualification companyQualification);

    /**
     *  根据tabname取得资质证书个数
     * @param tableName
     * @return
     */
    Integer getCompanyQualificationTotalByTabName(String tableName);

    /**
     * 按批次取得企业资质证书
     * @param params
     * @return
     */
    List<TbCompanyQualification> listCompanyQualification(Map<String,Object> params);


    /**
     * 根据公司名称和类别查询证书url
     * @param params
     * @return
     */
    List<String> getAllCompanyQualificationUrlByTabAndCompanyName(Map<String, Object> params);

    /**
     * 根据证书url获取公司id
     * @param url
     * @return
     */
    TbCompanyQualification getComIdByUrl(String url);

    /**
     * 根据证书url更新企业证书信息
     */
    void updateTbCompanyQualificationByUrl(TbCompanyQualification companyQualification);

    /**
     * 查询全部企业资质证书名称、和类别
     * @return
     */
    List<Map<String, Object>> listComNameAndTab();

    /**
     * 根据tab名称查询全部企业资质证书名称、和类别
     * @param tab
     * @return
     */
    List<Map<String, Object>> listComNameAndTabByTab(String tab);


    //##################################################北京

    /**
     *
     * @param
     * @return
     */
    Integer getBeiJinCompanyQualificationTotalByTabName();

    /**
     * 按批次取得企业资质证书
     * @param params
     * @return
     */
    List<TbCompanyQualification> listBeiJinCompanyQualification(Map<String,Object> params);


    //######################################################

    /**
     * 根据企业id获取企业资质证书
     * @param companyId
     * @return
     */
    List<TbCompanyQualification> getCompanyQualificationByComId(Integer companyId);
}