package com.silita.dao;

import com.silita.model.AllZh;
import com.silita.utils.MyMapper;

public interface AllZhMapper extends MyMapper<AllZh> {
    AllZh getAllZhByName(String name);
}