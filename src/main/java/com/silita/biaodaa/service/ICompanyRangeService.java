package com.silita.biaodaa.service;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyAptitude;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/11.
 */
public interface ICompanyRangeService {
    /**
     * 取得拆分后的企业证书资质
     * @return
     */
    Integer getCompanyAptitudeTotal();

    /**
     * 按批次取拆分后的企业证书资质
     */
    List<TbCompanyAptitude> listCompanyAptitude(Map<String, Object> params);

    /**
     * 添加企业资质到企业基本信息表（方便业务查询）
     * @param tbCompany
     */
    void updateCompanyRangeByComId(TbCompany tbCompany);
}
