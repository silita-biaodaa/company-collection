package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.MyMapper;

public interface TbCompanyMapper extends MyMapper<TbCompany> {

    /**
     *
     * @param tbCompany
     */
    void insertCompany(TbCompany tbCompany);

    /**
     *
     * @param tbCompany
     * @return
     */
    Integer getCompanyTotalForOrgCodeAndBusinessNum(TbCompany tbCompany);

    /**
     *
     * @param tbCompany
     * @return
     */
    Integer getCompanyIdByOrgCodeAndBusinessNum(TbCompany tbCompany);

    /**
     * 添加企业资质到企业基本信息表（方便业务查询）
     * @param tbCompany
     */
    void updateCompanyRangeByComId(TbCompany tbCompany);
}