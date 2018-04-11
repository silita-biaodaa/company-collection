package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyAptitude;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbCompanyAptitudeMapper extends MyMapper<TbCompanyAptitude> {
    /**
     *
     * @param tbCompanyAptitudes
     */
    void batchInsertCompanyAptitude(List<TbCompanyAptitude> tbCompanyAptitudes);

    /**
     *
     * @param companyName
     * @return
     */
    List<TbCompanyAptitude> getCompanyAptitudeByCompanyName(String companyName);
}