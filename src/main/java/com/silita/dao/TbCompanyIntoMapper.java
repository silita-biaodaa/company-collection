package com.silita.dao;

import com.silita.model.TbCompanyInto;
import com.silita.utils.MyMapper;

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