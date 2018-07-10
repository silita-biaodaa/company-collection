package com.silita.dao;

import com.silita.model.TbPerson;
import com.silita.utils.MyMapper;

public interface TbPersonMapper extends MyMapper<TbPerson> {

    /**
     * 添加人员基本信息
     * @param tbPerson
     */
    void insertPersonInfo(TbPerson tbPerson);

    /**
     * 根据人员姓名、身份证6位、性别判断人员是否存在
     * @param tbPerson
     * @return
     */
    Integer getPersonTotalByNameAndIDAndSex(TbPerson tbPerson);

    /**
     * 根据人员姓名、身份证6位、性别获取人员主键
     * @param tbPerson
     * @return
     */
    Integer getPersonIdByNameAndIDAndSex(TbPerson tbPerson);
}