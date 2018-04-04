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
}