package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.AllZh;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyAptitude;
import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.service.ISplitCertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/11.
 */
@Service("splitCertService")
public class SplitCertServiceImpl implements ISplitCertService {
    @Autowired
    private TbCompanyQualificationMapper tbCompanyQualificationMapper;
    @Autowired
    private TbCompanyMapper tbCompanyMapper;
    @Autowired
    private AllZhMapper allZhMapper;
    @Autowired
    private AptitudeDictionaryMapper aptitudeDictionaryMapper;
    @Autowired
    private TbCompanyAptitudeMapper tbCompanyAptitudeMapper;


    @Override
    public int getCompanyQualificationTotalByTabName(String tableName) {
        return tbCompanyQualificationMapper.getCompanyQualificationTotalByTabName(tableName);
    }

    @Override
    public List<TbCompanyQualification> listCompanyQualification(Map<String, Object> params) {
        return tbCompanyQualificationMapper.listCompanyQualification(params);
    }

    @Override
    public int getBeiJinCompanyQualificationTotalByTabName() {
        return tbCompanyQualificationMapper.getBeiJinCompanyQualificationTotalByTabName();
    }

    @Override
    public List<TbCompanyQualification> listBeiJinCompanyQualification(Map<String, Object> params) {
        return tbCompanyQualificationMapper.listBeiJinCompanyQualification(params);
    }


    //#####################################################

    @Override
    public AllZh getAllZhByName(String name) {
        return allZhMapper.getAllZhByName(name);
    }

    @Override
    public String getMajorNameBymajorUuid(String majorUuid) {
        return aptitudeDictionaryMapper.getMajorNameBymajorUuid(majorUuid);
    }

    @Override
    public void batchInsertCompanyAptitude(List<TbCompanyAptitude> tbCompanyAptitudes) {
        tbCompanyAptitudeMapper.batchInsertCompanyAptitude(tbCompanyAptitudes);
    }


    //#####################################################

    @Override
    public Integer getCompanyAptitudeTotal() {
        return tbCompanyAptitudeMapper.getCompanyAptitudeTotal();
    }

    @Override
    public List<TbCompanyAptitude> listCompanyAptitude(Map<String, Object> params) {
        return tbCompanyAptitudeMapper.listCompanyAptitude(params);
    }

    @Override
    public void updateCompanyRangeByComId(TbCompany tbCompany) {
        tbCompanyMapper.updateCompanyRangeByComId(tbCompany);
    }

    @Override
    public void deleteCompanyAptitude() {
        tbCompanyAptitudeMapper.deleteCompanyAptitude();
    }


    //###################################################

    @Override
    public void deleteCcompanyAptitudeByComId(Integer companyId) {
        tbCompanyAptitudeMapper.deleteCompanyAptitudeByCompanyId(companyId);
    }

    @Override
    public List<TbCompanyQualification> getCompanyQualificationByComId(Integer companyId) {
        return tbCompanyQualificationMapper.getCompanyQualificationByComId(companyId);
    }

    @Override
    public List<TbCompanyAptitude> listCompanyAptitudeByCompanyId(Integer companyId) {
        return tbCompanyAptitudeMapper.listCompanyAptitudeByComPanyId(companyId);
    }

}
