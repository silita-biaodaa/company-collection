package com.silita.dao;

import com.silita.model.TbExceptionUrl;
import com.silita.utils.MyMapper;

public interface TbExceptionUrlMapper extends MyMapper<TbExceptionUrl> {
    /**
     * 抓取异常
     * @param tbExceptionUrl
     */
    void insertExceptionUrl(TbExceptionUrl tbExceptionUrl);
}