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


}