package com.silita.service;

import com.silita.model.TbPersonChange;
import com.silita.model.TbPersonHunan;

/**
 * Created by Administrator on 2018/5/19.
 */
public interface IPeopleCertService {

    /**
     * 据证书编号、注册类别、公司id判断证书是否存在
     * @param tbPersonHunan
     * @return
     */
    boolean checkPersonHunanIsExist(TbPersonHunan tbPersonHunan);

    /**
     * 添加人员资质证书信息
     * @param tbPersonHunan
     */
    void insertPersonHunan(TbPersonHunan tbPersonHunan);

    /**
     * 添加人员变更信息
     * @param tbPersonChange
     */
    void insertPeopleChange(TbPersonChange tbPersonChange);

    /**
     * 根据企业id删除企业下面的资质证书
     * @param companyId
     */
    void deletePersonHunanByCompanyId(Integer companyId);
}