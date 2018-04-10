package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.AllZh;
import com.silita.biaodaa.utils.MyMapper;

public interface AllZhMapper extends MyMapper<AllZh> {
    AllZh getAllZhByName(String name);
}