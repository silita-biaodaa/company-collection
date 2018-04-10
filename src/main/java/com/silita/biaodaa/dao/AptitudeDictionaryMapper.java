package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.utils.MyMapper;

public interface AptitudeDictionaryMapper extends MyMapper<AptitudeDictionary> {
    String getMajorNameBymajorUuid(String majorUuid);
}