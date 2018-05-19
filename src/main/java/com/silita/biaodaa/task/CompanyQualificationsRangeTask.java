package com.silita.biaodaa.task;

import com.silita.biaodaa.model.AllZh;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyAptitude;
import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.service.ICompanyRangeService;
import com.silita.biaodaa.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/11.
 */
@Component
public class CompanyQualificationsRangeTask {

    @Autowired
    ICompanyRangeService companyRangeService;


    /**
     * 建筑业企业、工程设计企业、工程勘察企业、"工程监理企业"
     * 拆资质
     */
    public void splitCompanyQualifications() {
        int page = 0;
        int batchCount = 1000;
        Integer count = companyRangeService.getCompanyQualificationTotalByTabName("工程监理企业");
        if (count % batchCount == 0) {
            page = count / batchCount;
        } else {
            page = count / batchCount + 1;
        }
        Map<String, Object> params;
        List<TbCompanyQualification> companyQualificationList;
        //分页 一次1000
        for (int pageNum = 0; pageNum < page; pageNum++) {
            params = new HashMap<>();
            params.put("tableName", "工程监理企业");
            params.put("start", batchCount * pageNum);
            params.put("pageSize", 1000);
            companyQualificationList = companyRangeService.getCompanyQualifications(params);
            //遍历证书
            for (int i = 0; i < companyQualificationList.size(); i++) {
                int qualId = companyQualificationList.get(i).getPkid();
                String qualRange = companyQualificationList.get(i).getRange();
                int comId = companyQualificationList.get(i).getComId();
                //有资质
                if (StringUtils.isNotNull(qualRange)) {
                    AllZh allZh;
                    TbCompanyAptitude companyAptitude;
                    List<TbCompanyAptitude> companyQualifications = new ArrayList<>();
                    if (qualRange.contains("；")) {
                        //拆分资质
                        String[] qual = qualRange.split("；");
                        for (int j = 0; j < qual.length; j++) {
                            allZh = companyRangeService.getAllZhByName(qual[j]);
                            if (allZh != null) {
                                companyAptitude = new TbCompanyAptitude();
                                companyAptitude.setQualId(qualId);
                                companyAptitude.setComId(comId);
                                companyAptitude.setAptitudeName(companyRangeService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                                companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                                companyAptitude.setMainuuid(allZh.getMainuuid());
                                companyAptitude.setType(allZh.getType());
                                companyQualifications.add(companyAptitude);
                            }
                        }
                    } else if (qualRange.contains(";")) {
                        //拆分资质
                        String[] qual = qualRange.split(";");
                        for (int j = 0; j < qual.length; j++) {
                            allZh = companyRangeService.getAllZhByName(qual[j]);
                            if (allZh != null) {
                                companyAptitude = new TbCompanyAptitude();
                                companyAptitude.setQualId(qualId);
                                companyAptitude.setComId(comId);
                                companyAptitude.setAptitudeName(companyRangeService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                                companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                                companyAptitude.setMainuuid(allZh.getMainuuid());
                                companyAptitude.setType(allZh.getType());
                                companyQualifications.add(companyAptitude);
                            }
                        }
                    } else {
                        allZh = companyRangeService.getAllZhByName(qualRange);
                        if (allZh != null) {
                            companyAptitude = new TbCompanyAptitude();
                            companyAptitude.setQualId(qualId);
                            companyAptitude.setComId(comId);
                            companyAptitude.setAptitudeName(companyRangeService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                            companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                            companyAptitude.setMainuuid(allZh.getMainuuid());
                            companyAptitude.setType(allZh.getType());
                            companyQualifications.add(companyAptitude);
                        }
                    }
                    if (companyQualifications != null && companyQualifications.size() > 0) {
                        companyRangeService.batchInsertCompanyAptitude(companyQualifications);
                    }
                }
            }
        }
    }


    /**
     * 最后更新资质到企业基本信息表
     * 添加企业资质到企业基本信息表（方便业务查询）
     *
     * @param
     */
    public void updateCompanyAptitudeRange() {
        int page = 0;
        int batchCount = 1000;
        Integer count = companyRangeService.getCompanyAptitudeTotal();
        if (count % batchCount == 0) {
            page = count / batchCount;
        } else {
            page = count / batchCount + 1;
        }
        Map<String, Object> params;
        List<TbCompanyAptitude> tbCompanyAptitudes;
        //分页
        for (int pageNum = 0; pageNum < page; pageNum++) {
            params = new HashMap<>();
            params.put("start", batchCount * pageNum);
            params.put("pageSize", 1000);
            tbCompanyAptitudes = companyRangeService.listCompanyAptitude(params);
            TbCompany tbCompany;
            TbCompanyAptitude tbCompanyAptitude;
            int comId;
            String allType;
            String allAptitudeUuid;
            StringBuilder sb;
            //遍历
            for (int i = 0; i < tbCompanyAptitudes.size(); i++) {
                tbCompanyAptitude = tbCompanyAptitudes.get(i);
                comId = tbCompanyAptitude.getComId();
                allType = tbCompanyAptitude.getType();
                allAptitudeUuid = tbCompanyAptitude.getAptitudeUuid();
                if (StringUtils.isNotNull(allType) && StringUtils.isNotNull(allAptitudeUuid)) {
                    sb = new StringBuilder();
                    String[] typeArr = allType.split(",");
                    String[] aptitudeUuidArr = allAptitudeUuid.split(",");
                    if (typeArr.length > 0 && aptitudeUuidArr.length > 0 && typeArr.length == aptitudeUuidArr.length) {
                        for (int j = 0; j < typeArr.length; j++) {
                            if (j == typeArr.length - 1) {
                                sb.append(typeArr[j]).append("/").append(aptitudeUuidArr[j]);
                            } else {
                                sb.append(typeArr[j]).append("/").append(aptitudeUuidArr[j]).append(",");
                            }
                        }
                    }
                    tbCompany = new TbCompany();
                    tbCompany.setComId(comId);
                    tbCompany.setRange(sb.toString());
                    companyRangeService.updateCompanyRangeByComId(tbCompany);
                }
            }
        }
        //删除拆分后的公司资质
//        companyRangeService.deleteCompanyAptitude();
    }

    /**
     * 拆资质(北京)
     * 监理资质\设计资质\建筑业企业资质\招标代理资格\勘察资质\设计与施工一体化资质\造价咨询资质
     */
    public void splitBeijinCompanyQualifications() {
        int page = 0;
        int batchCount = 5000;
        Integer count = companyRangeService.getBeiJinCompanyQualificationTotalByTabName();
        if (count % batchCount == 0) {
            page = count / batchCount;
        } else {
            page = count / batchCount + 1;
        }
        Map<String, Object> params;
        List<TbCompanyQualification> companyQualificationList;
        //分页 一次1000
        for (int pageNum = 0; pageNum < page; pageNum++) {
            params = new HashMap<>();
            params.put("start", batchCount * pageNum);
            params.put("pageSize", 5000);
            companyQualificationList = companyRangeService.getBeiJinCompanyQualifications(params);
            //遍历证书
            for (int i = 0; i < companyQualificationList.size(); i++) {
                int qualId = companyQualificationList.get(i).getPkid();
                String qualRange = companyQualificationList.get(i).getRange();
                if(companyQualificationList.get(i).getComId() != null) {
                    int comId = companyQualificationList.get(i).getComId();
                    //有资质
                    if (StringUtils.isNotNull(qualRange)) {
                        AllZh allZh;
                        TbCompanyAptitude companyAptitude;
                        List<TbCompanyAptitude> companyQualifications = new ArrayList<>();
                        if (qualRange.contains("|")) {
                            //拆分资质
                            String[] qual = qualRange.split("\\|");
                            for (int j = 0; j < qual.length; j++) {
                                allZh = companyRangeService.getAllZhByName(qual[j]);
                                if (allZh != null) {
                                    companyAptitude = new TbCompanyAptitude();
                                    companyAptitude.setQualId(qualId);
                                    companyAptitude.setComId(comId);
                                    companyAptitude.setAptitudeName(companyRangeService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                                    companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                                    companyAptitude.setMainuuid(allZh.getMainuuid());
                                    companyAptitude.setType(allZh.getType());
                                    companyQualifications.add(companyAptitude);
                                }
                            }
                        } else {
                            allZh = companyRangeService.getAllZhByName(qualRange);
                            if (allZh != null) {
                                companyAptitude = new TbCompanyAptitude();
                                companyAptitude.setQualId(qualId);
                                companyAptitude.setComId(comId);
                                companyAptitude.setAptitudeName(companyRangeService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                                companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                                companyAptitude.setMainuuid(allZh.getMainuuid());
                                companyAptitude.setType(allZh.getType());
                                companyQualifications.add(companyAptitude);
                            }
                        }
                        if (companyQualifications != null && companyQualifications.size() > 0) {
                            companyRangeService.batchInsertCompanyAptitude(companyQualifications);
                        }
                    }
                }
            }
        }
    }

    /**
     * 最后更新资质到企业基本信息表（北京）
     * 添加企业资质到企业基本信息表（方便业务查询）
     *
     * @param
     */
    public void updateBeijinCompanyAptitudeRange() {
        int page = 0;
        int batchCount = 5000;
        Integer count = companyRangeService.getCompanyAptitudeTotal();
        if (count % batchCount == 0) {
            page = count / batchCount;
        } else {
            page = count / batchCount + 1;
        }
        Map<String, Object> params;
        List<TbCompanyAptitude> tbCompanyAptitudes;
        //分页
        for (int pageNum = 0; pageNum < page; pageNum++) {
            params = new HashMap<>();
            params.put("start", batchCount * pageNum);
            params.put("pageSize", 5000);
            tbCompanyAptitudes = companyRangeService.listCompanyAptitude(params);
            TbCompany tbCompany;
            TbCompanyAptitude tbCompanyAptitude;
            int comId;
            String allType;
            String allAptitudeUuid;
            StringBuilder sb;
            //遍历
            for (int i = 0; i < tbCompanyAptitudes.size(); i++) {
                tbCompanyAptitude = tbCompanyAptitudes.get(i);
                comId = tbCompanyAptitude.getComId();
                allType = tbCompanyAptitude.getType();
                allAptitudeUuid = tbCompanyAptitude.getAptitudeUuid();
                if (StringUtils.isNotNull(allType) && StringUtils.isNotNull(allAptitudeUuid)) {
                    sb = new StringBuilder();
                    String[] typeArr = allType.split(",");
                    String[] aptitudeUuidArr = allAptitudeUuid.split(",");
                    if (typeArr.length > 0 && aptitudeUuidArr.length > 0 && typeArr.length == aptitudeUuidArr.length) {
                        for (int j = 0; j < typeArr.length; j++) {
                            if (j == typeArr.length - 1) {
                                sb.append(typeArr[j]).append("/").append(aptitudeUuidArr[j]);
                            } else {
                                sb.append(typeArr[j]).append("/").append(aptitudeUuidArr[j]).append(",");
                            }
                        }
                    }
                    tbCompany = new TbCompany();
                    tbCompany.setComId(comId);
                    tbCompany.setRange(sb.toString());
                    companyRangeService.updateCompanyRangeByComId(tbCompany);
                }
            }
        }
        //删除拆分后的公司资质
//        companyRangeService.deleteCompanyAptitude();
    }


}
