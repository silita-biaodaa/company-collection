package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectDesign;
import com.silita.biaodaa.utils.MyMapper;

import java.util.Map;

public interface TbProjectDesignMapper extends MyMapper<TbProjectDesign> {

    /**
     *
     * @param tbProjectDesign
     */
    void insertProjectDesign(TbProjectDesign tbProjectDesign);

    /**
     *
     * @param tbProjectDesign
     * @return
     */
    Integer getProjectDesignTotalByCheckNoAndDesignOrg(TbProjectDesign tbProjectDesign);

    /**
     *
     * @param tbProjectDesign
     * @return
     */
    Integer getPkidByCheckNoAndDesignOrg(TbProjectDesign tbProjectDesign);

    /**
     *
     * @param tbProjectDesign
     * @return
     */
    Integer getProjectDesignTotalByCheckNoAndExploreOrg(TbProjectDesign tbProjectDesign);

    /**
     *
     * @param tbProjectDesign
     * @return
     */
    Integer getPkidByCheckNoAndExploreOrg(TbProjectDesign tbProjectDesign);

    /**
     *
     * @param params
     * @return
     */
    Integer getTotalBySgtxhAndProType(Map<String, Object> params);


}