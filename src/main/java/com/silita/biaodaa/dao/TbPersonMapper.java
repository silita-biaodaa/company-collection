package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPerson;
import com.silita.biaodaa.utils.MyMapper;

public interface TbPersonMapper extends MyMapper<TbPerson> {

    /**
     * 添加人员基本信息 返回
     * @param tbPerson
     */
    void insertPersonInfo(TbPerson tbPerson);

    /**
     *
     * @param tbPerson
     * @return
     */
    Integer getPersonTotalByNameAndIDAndSex(TbPerson tbPerson);

    /**
     *
     * @param tbPerson
     * @return
     */
    Integer getPersonIdByNameAndIDAndSex(TbPerson tbPerson);
}