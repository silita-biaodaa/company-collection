package com.silita.dao;

import com.silita.model.AptitudeDictionary;
import com.silita.utils.MyMapper;

public interface AptitudeDictionaryMapper extends MyMapper<AptitudeDictionary> {
    String getMajorNameBymajorUuid(String majorUuid);
}