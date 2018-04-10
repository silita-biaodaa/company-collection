package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectBuild;
import com.silita.biaodaa.utils.MyMapper;

public interface TbProjectBuildMapper extends MyMapper<TbProjectBuild> {

    /**
     * 添加项目施工基本信息
     */
    void insertProjectBuild(TbProjectBuild tbProjectBuild);

    /**
     * 根据施工许可证取得项目施工合同个数
     * @return
     */
    Integer getTotalByBLicence(String bLicence);

    /**
     * 根据施工许可证取得项目施工合同id
     * @param bLicence
     * @return
     */
    Integer getPkidByBLicence(String bLicence);

    /**
     * 根据标段名称、施工单位取得项目施工合同个数
     * @param tbProjectBuild
     * @return
     */
    Integer getTotalByBNameAndBOrg(TbProjectBuild tbProjectBuild);

    /**
     * 根据标段名称、施工单位取得项目施工合同id
     * @param tbProjectBuild
     * @return
     */
    Integer getPkidByBNameAndBOrg(TbProjectBuild tbProjectBuild);

    /**
     * 根据施工合同内部id取得项目施工合同个数
     * @param bdxh
     * @return
     */
    Integer getTotalByBdxh(String bdxh);

    /**
     * 根据施工合同内部id取得项目施工合同id
     * @param bdxh
     * @return
     */
    Integer getPkidByBdxh(String bdxh);

}