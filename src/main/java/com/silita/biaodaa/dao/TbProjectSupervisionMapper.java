package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectSupervision;
import com.silita.biaodaa.utils.MyMapper;

public interface TbProjectSupervisionMapper extends MyMapper<TbProjectSupervision> {

    /**
     *
     * @param tbProjectSupervision
     */
    void insertProjectSupervision(TbProjectSupervision tbProjectSupervision);

    /**
     *
     * @param jlbdxh
     * @return
     */
    Integer getTotalByJlbdxh(String jlbdxh);

    /**
     *
     * @param jlbdxh
     * @return
     */
    Integer getPkidByJlbdxh(String jlbdxh);

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


}