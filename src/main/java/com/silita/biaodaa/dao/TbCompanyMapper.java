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
}