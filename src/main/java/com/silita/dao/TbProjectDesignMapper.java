package com.silita.dao;

import com.silita.model.TbProjectDesign;
import com.silita.utils.MyMapper;

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
    Integer getTotalBySgtxhAndComIdCheckNo(TbProjectDesign tbProjectDesign);

    /**
     *
     * @param tbProjectDesign
     * @return
     */
    Integer getPkidBySgtxhAndComIdCheckNo(TbProjectDesign tbProjectDesign);

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
    Integer getTotalBySgtxhAndProTypeAndComId(Map<String, Object> params);


}