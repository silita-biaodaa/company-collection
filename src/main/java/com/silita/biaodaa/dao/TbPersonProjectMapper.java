package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonProject;
import com.silita.biaodaa.utils.MyMapper;

public interface TbPersonProjectMapper extends MyMapper<TbPersonProject> {

    /**
     * 添加项目部人员
     * @param tbPersonProject
     */
    void insertPersionProject(TbPersonProject tbPersonProject);

    /**
     * 根据项目id、证书编号、安全证书编号判断是否抓取
     * @param tbPersonProject
     * @return
     */
    Integer getPersionProjectTotalByCertNoAndPid(TbPersonProject tbPersonProject);
}