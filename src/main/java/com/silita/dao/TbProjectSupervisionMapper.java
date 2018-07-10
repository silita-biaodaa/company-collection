package com.silita.dao;

import com.silita.model.TbProjectSupervision;
import com.silita.utils.MyMapper;

import java.util.Map;

public interface TbProjectSupervisionMapper extends MyMapper<TbProjectSupervision> {

    /**
     *
     * @param tbProjectSupervision
     */
    void insertProjectSupervision(TbProjectSupervision tbProjectSupervision);

    /**
     *
     * @param tbProjectSupervision
     * @return
     */
    Integer getTotalByJlbdxhAndComIdTwo(TbProjectSupervision tbProjectSupervision);

    /**
     *
     * @param tbProjectSupervision
     * @return
     */
    Integer getPkidByJlbdxhAndComIdTwo(TbProjectSupervision tbProjectSupervision);

    /**
     *
     * @param tbProjectSupervision
     * @return
     */
    Integer getTotalByProNameAndSuperOrg(TbProjectSupervision tbProjectSupervision);

    /**
     *
     * @param tbProjectSupervision
     * @return
     */
    Integer getPkidByProNameAndSuperOrg(TbProjectSupervision tbProjectSupervision);

    /**
     *
     * @param params
     * @return
     */
    Integer getTotalByJlbdxhAndComId(Map<String, Object> params);

}