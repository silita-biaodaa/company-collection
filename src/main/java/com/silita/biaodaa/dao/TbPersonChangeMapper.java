package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonChange;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbPersonChangeMapper extends MyMapper<TbPersonChange> {
    /**
     *
     * @param tbPersonChangeList
     */
    void batchInsertPeopleChange(List<TbPersonChange> tbPersonChangeList);

    /**
     *
     * @param tbPersonChange
     * @return
     */
    Integer getTotalByPerIdChangeDate(TbPersonChange tbPersonChange);

    /**
     *
     * @param tbPersonChange
     */
    void insertPeopleChange(TbPersonChange tbPersonChange);
}