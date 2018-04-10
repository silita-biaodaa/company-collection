package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectDesign;
import com.silita.biaodaa.utils.MyMapper;

public interface TbProjectDesignMapper extends MyMapper<TbProjectDesign> {

    /**
     *
     * @param tbProjectDesign
     */
    void insertProjectDesign(TbProjectDesign tbProjectDesign);

    /**
     *
     * @param checkNo
     * @return
     */
    Integer getProjectDesignTotalByCheckNo(String checkNo);

    /**
     *
     * @param checkNo
     * @return
     */
    Integer getPkidByCheckNo(String checkNo);
}