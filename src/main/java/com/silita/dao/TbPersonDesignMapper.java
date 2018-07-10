package com.silita.dao;

import com.silita.model.TbPersonDesign;
import com.silita.utils.MyMapper;

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