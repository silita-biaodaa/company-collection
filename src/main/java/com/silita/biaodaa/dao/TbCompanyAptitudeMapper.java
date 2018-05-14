package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyAptitude;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbCompanyAptitudeMapper extends MyMapper<TbCompanyAptitude> {

    /**
     * 批量添加拆分后的公司资质
     * @param tbCompanyAptitudes
     */
    void batchInsertCompanyAptitude(List<TbCompanyAptitude> tbCompanyAptitudes);

    /**
     * 取得拆分后的公司资质个数
     */
    Integer getCompanyAptitudeTotal();

    /**
     * 按批次取得拆分后的公司资质个数
     * @param params
     * @return
     */
    List<TbCompanyAptitude> listCompanyAptitude(Map<String,Object> params);

    /**
     *
     */
    void deleteCompanyAptitude();
}