package com.silita.dao;

import com.silita.model.TbPersonHunan;
import com.silita.utils.MyMapper;

public interface TbPersonHunanMapper extends MyMapper<TbPersonHunan> {

    /**
     * 根据证书编号、注册类别、公司id判断证书是否存在
     * @return
     */
    Integer getTotalByComIdAndCertNoAndCategory(TbPersonHunan tbPersonHunan);

    /**
     * 根据证书编号、注册类别、公司id、专业判断证书是否存在
     * @return
     */
    Integer getTotalByComIdAndCertNoAndCategoryAndMajor(TbPersonHunan tbPersonHunan);

    /**
     * 添加人员资质证书
     */
    void insertPersonHunan(TbPersonHunan tbPersonHunan);

    /**
     * 根据公司id删除全部证书
     */
    void deletePersonHunanByCompanyId(Integer companyId);
}