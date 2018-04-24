package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonDesign;
import com.silita.biaodaa.utils.MyMapper;

public interface TbPersonDesignMapper extends MyMapper<TbPersonDesign> {

    /**
     *
     * @param tbPersonDesign
     */
    void insertPersionDesign(TbPersonDesign tbPersonDesign);

    /**
     *
     * @param tbPersonDesign
     * @return
     */
    Integer getTotalByNameAndCategoryAndPid(TbPersonDesign tbPersonDesign);

}