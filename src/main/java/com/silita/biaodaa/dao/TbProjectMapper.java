package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProject;
import com.silita.biaodaa.utils.MyMapper;

public interface TbProjectMapper extends MyMapper<TbProject> {

    /**
     * 添加项目基本信息
     * @param tbProject
     */
    void insertProjectInfo(TbProject tbProject);

    /**
     * 根据项目编号查询项目个数
     * @return
     */
    Integer getProjectTotalByProjectNoAndXmid(TbProject tbProject);

    /**
     * 根据项目编号取得项目主键
     * @return
     */
    Integer getProIdByProNoAndXmid(TbProject tbProject);
}