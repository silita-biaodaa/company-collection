package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.MyMapper;

public interface TbCompanyMapper extends MyMapper<TbCompany> {

    /**
     * 添加公司基本信息
     * @param tbCompany
     */
    void insertCompany(TbCompany tbCompany);

    /**
     * 根据工商营业执照或公司名称判断公司是否存在
     * @param tbCompany
     * @return
     */
    Integer getCompanyTotalByOrgCodeOrCompanyName(TbCompany tbCompany);

    /**
     * 根据工商营业执照或公司名称获取公司ID
     * @param tbCompany
     * @return
     */
    Integer getCompanyIdByOrgCodeOrCompanyName(TbCompany tbCompany);

    /**
     * 添加企业资质到企业基本信息表（方便业务查询）
     * @param tbCompany
     */
    void updateCompanyRangeByComId(TbCompany tbCompany);

    /**
     * 根据公司id更新公司基本信息
     * @param tbCompany
     */
    void updateCompany(TbCompany tbCompany);

}