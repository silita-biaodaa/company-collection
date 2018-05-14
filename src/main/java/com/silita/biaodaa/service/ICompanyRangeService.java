package com.silita.biaodaa.service;

import com.silita.biaodaa.model.AllZh;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyAptitude;
import com.silita.biaodaa.model.TbCompanyQualification;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/11.
 */
public interface ICompanyRangeService {

    /**
     * 删除拆分后的资质
     */
    void deleteCompanyAptitude();

    /**
     *
     * @param tableName
     * @return
     */
    int getCompanyQualificationTotalByTabName(String tableName);

    /**
     *
     * @param params
     * @return
     */
    List<TbCompanyQualification> getCompanyQualifications(Map<String, Object> params);

    /**
     * 根据资质别名取得资质
     * @param name
     * @return
     */
    AllZh getAllZhByName(String name);

    /**
     * 根据资质别名id取得资质标准名称
     * @param majorUuid
     * @return
     */
    String getMajorNameBymajorUuid(String majorUuid);

    /**
     * 批量插入拆分后的企业证书资质
     * @param tbCompanyAptitudes
     */
    void batchInsertCompanyAptitude(List<TbCompanyAptitude> tbCompanyAptitudes);



    //#########################################


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



    //#####################################北京

    /**
     *
     * @return
     */
    int getBeiJinCompanyQualificationTotalByTabName();

    /**
     *
     * @param params
     * @return
     */
    List<TbCompanyQualification> getBeiJinCompanyQualifications(Map<String, Object> params);



}
