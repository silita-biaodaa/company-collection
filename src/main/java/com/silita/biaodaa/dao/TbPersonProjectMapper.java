package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonProject;
import com.silita.biaodaa.utils.MyMapper;

public interface TbPersonProjectMapper extends MyMapper<TbPersonProject> {

    /**
     *
     * @param tbPersonProject
     */
    void insertPersionProject(TbPersonProject tbPersonProject);

    /**
     *
     * @param tbPersonProject
     * @return
     */
    Integer getPersionProjectTotalByNameAndCertNoAndSafeNo(TbPersonProject tbPersonProject);
}