package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyAptitude;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbCompanyAptitudeMapper extends MyMapper<TbCompanyAptitude> {
    void batchInsertCompanyAptitude(List<TbCompanyAptitude> tbCompanyAptitudes);
}