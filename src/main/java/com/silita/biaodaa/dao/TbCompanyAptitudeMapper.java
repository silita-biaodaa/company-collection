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
     *删除全部拆分后的资质证书
     */
    void deleteCompanyAptitude();

    /**
     * 根据企业id获取拆分后的资质证书
     * @param companyId
     * @return
     */
    List<TbCompanyAptitude> listCompanyAptitudeByComPanyId(Integer companyId);

    /**
     * 根据企业id删除拆分后的资质证书
     * @param companyId
     */
    void deleteCompanyAptitudeByCompanyId(Integer companyId);
}