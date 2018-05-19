package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonChange;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbPersonChangeMapper extends MyMapper<TbPersonChange> {
    /**
     * 批量添加人员变更信息
     * @param tbPersonChangeList
     */
    void batchInsertPeopleChange(List<TbPersonChange> tbPersonChangeList);

    /**
     * 根据人员id变更时间判断是否存在
     * @param tbPersonChange
     * @return
     */
    Integer getTotalByPerIdChangeDate(TbPersonChange tbPersonChange);

    /**
     * 添加人员变更信息
     * @param tbPersonChange
     */
    void insertPeopleChange(TbPersonChange tbPersonChange);
}