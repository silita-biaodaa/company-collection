package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectBuild;
import com.silita.biaodaa.utils.MyMapper;

import java.util.Map;

public interface TbProjectBuildMapper extends MyMapper<TbProjectBuild> {

    /**
     * 添加项目施工基本信息
     */
    void insertProjectBuild(TbProjectBuild tbProjectBuild);

    /**
     * 根据施工许可证和施工内部id取得项目施工合同个数
     * @return
     */
    Integer getTotalByBdxhAndComIdAndBLicence(TbProjectBuild tbProjectBuild);

    /**
     * 根据施工许可证和施工内部id取得项目施工合同id
     * @param tbProjectBuild
     * @return
     */
    Integer getPkidByBdxhAndComIdAndBLicence(TbProjectBuild tbProjectBuild);

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

    /**
     * 根据项目内部id和公司id判断是否抓取
     * @return
     */
    Integer getTotalByBdxhAndComId(Map<String, Object> params);

}