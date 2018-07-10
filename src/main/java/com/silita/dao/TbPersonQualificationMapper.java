package com.silita.dao;

import com.silita.model.TbPersonQualification;
import com.silita.utils.MyMapper;

public interface TbPersonQualificationMapper extends MyMapper<TbPersonQualification> {

    /**
     * 添加人员职业证书
     * @param tbPersonQualification
     */
    void insertPersonQualification(TbPersonQualification tbPersonQualification);

    /**
     * 根据证书编号、注册类别、公司id判断证书是否存在
     * @param tbPersonQualification
     * @return
     */
    Integer getTotalByCertNoAndComIdAndCategory(TbPersonQualification tbPersonQualification);

    /**
     * 根据证书编号、注册类别、公司id、专业判断证书是否存在
     * @param tbPersonQualification
     * @return
     */
    Integer getTotalByCertNoAndComIdAndCategoryAndMajor(TbPersonQualification tbPersonQualification);

    /**
     * 根据公司id删除人员证书
     * @param comId
     */
    void deletePersonQualByComId(Integer comId);


}