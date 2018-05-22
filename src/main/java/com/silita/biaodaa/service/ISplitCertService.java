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
public interface ISplitCertService {

    /**
     * 根据类型获取该类型证书总数
     * @param tableName
     * @return
     */
    int getCompanyQualificationTotalByTabName(String tableName);

    /**
     * 分页获取某种证书
     * @param params
     * @return
     */
    List<TbCompanyQualification> listCompanyQualification(Map<String, Object> params);


    /**
     * 根据类型获取该类型证书总数（北京）
     * @return
     */
    int getBeiJinCompanyQualificationTotalByTabName();

    /**
     * 分页获取某种证书（北京）
     * @param params
     * @return
     */
    List<TbCompanyQualification> listBeiJinCompanyQualification(Map<String, Object> params);


    //###################################

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


    //###################################

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

    /**
     * 删除拆分后的资质
     */
    void deleteCompanyAptitude();


    //###################################################

    /**
     * 根据公司id删除该公司已拆分的资质
     * @param companyId
     */
    void deleteCcompanyAptitudeByComId(Integer companyId);

    /**
     * 根据企业id获取企业资质证书
     * @param companyId
     * @return
     */
    List<TbCompanyQualification> getCompanyQualificationByComId(Integer companyId);

    /**
     * 按公司取拆分后的企业证书资质
     */
    List<TbCompanyAptitude> listCompanyAptitudeByCompanyId(Integer companyId);


}
