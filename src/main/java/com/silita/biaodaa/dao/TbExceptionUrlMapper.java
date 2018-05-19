package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbExceptionUrl;
import com.silita.biaodaa.utils.MyMapper;

public interface TbExceptionUrlMapper extends MyMapper<TbExceptionUrl> {
    /**
     * 抓取异常
     * @param tbExceptionUrl
     */
    void insertExceptionUrl(TbExceptionUrl tbExceptionUrl);
}