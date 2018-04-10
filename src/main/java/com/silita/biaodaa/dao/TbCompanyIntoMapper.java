package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyInto;
import com.silita.biaodaa.utils.MyMapper;

public interface TbCompanyIntoMapper extends MyMapper<TbCompanyInto> {

    /**
     *
     * @param tbCompanyInto
     */
    void insertCompanyInto(TbCompanyInto tbCompanyInto);

    /**
     *
     * @param tbCompanyInto
     * @return
     */
    Integer getTotalByOrgCodeAndBusinessNum(TbCompanyInto tbCompanyInto);
}