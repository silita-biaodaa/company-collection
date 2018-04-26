package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/2.
 */
@Service("companyService")
public class CompanyServiceImpl implements ICompanyService {

    @Autowired
    private TbCompanyQualificationMapper tbCompanyQualificationMapper;
    @Autowired
    private TbCompanyMapper tbCompanyMapper;
    @Autowired
    private TbPersonMapper tbPersonMapper;
    @Autowired
    private TbPersonQualificationMapper tbPersonQualificationMapper;
    @Autowired
    private TbProjectMapper tbProjectMapper;
    @Autowired
    private TbProjectBuildMapper tbProjectBuildMapper;
    @Autowired
    private TbPersonProjectMapper tbPersonProjectMapper;
    @Autowired
    private TbProjectDesignMapper tbProjectDesignMapper;
    @Autowired
    private TbPersonDesignMapper tbPersonDesignMapper;
    @Autowired
    private TbProjectSupervisionMapper tbProjectSupervisionMapper;
    @Autowired
    private AllZhMapper allZhMapper;
    @Autowired
    private AptitudeDictionaryMapper aptitudeDictionaryMapper;
    @Autowired
    private TbCompanyAptitudeMapper tbCompanyAptitudeMapper;
    @Autowired
    private TbPersonChangeMapper tbPersonChangeMapper;
    @Autowired
    private TbExceptionUrlMapper tbExceptionUrlMapper;

    @Override
    public void batchInsertCompanyQualification(List<TbCompanyQualification> companyQualifications) {
        tbCompanyQualificationMapper.batchInsertCompanyQualification(companyQualifications);
    }

    @Override
    public void InsertCompanyQualification(TbCompanyQualification companyQualification) {
        boolean flag = tbCompanyQualificationMapper.getTotalByCertNo(companyQualification.getCertNo()) > 0;
        if (!flag) {
            tbCompanyQualificationMapper.InsertCompanyQualification(companyQualification);
        }
    }

    @Override
    public List<String> getAllCompanyQualificationUrlByTab(String tableName) {
        return tbCompanyQualificationMapper.getAllCompanyQualificationUrlByTabName(tableName);
    }

    @Override
    public void updateCompanyQualificationUrlByCorpid(TbCompanyQualification companyQualification) {
        tbCompanyQualificationMapper.updateCompanyQualificationUrlByCorpid(companyQualification);
    }

    @Override
    public int insertCompanyInfo(TbCompany tbCompany) {
        boolean flag = tbCompanyMapper.getCompanyTotalForOrgCodeAndBusinessNum(tbCompany) > 0;
        if (!flag) {
            //不存在新增、并返回新增的 comId
            tbCompanyMapper.insertCompany(tbCompany);
            return tbCompany.getComId();
        } else {
            //存在返回查询到的 comId
            return tbCompanyMapper.getCompanyIdByOrgCodeAndBusinessNum(tbCompany);
        }
    }

    @Override
    public int insertPersionInfo(TbPerson tbPerson) {
        boolean flag = tbPersonMapper.getPersonTotalByNameAndIDAndSex(tbPerson) > 0;
        if (!flag) {
            //不存在新增、并返回新增的 pkid
            tbPersonMapper.insertPersonInfo(tbPerson);
            return tbPerson.getPkid();
        } else {
            //存在返回查询到的 pkid
            return tbPersonMapper.getPersonIdByNameAndIDAndSex(tbPerson);
        }
    }

    @Override
    public void insertPersonQualification(TbPersonQualification tbPersonQualification) {
        boolean flag = tbPersonQualificationMapper.getTotalByCertNoAndComId(tbPersonQualification) > 0;
        if (!flag) {
            tbPersonQualificationMapper.insertPersonQualification(tbPersonQualification);
        }
    }

    @Override
    public int insertProjectInfo(TbProject tbProject) {
        boolean flag = tbProjectMapper.getProjectTotalByProjectNoAndXmid(tbProject) > 0;
        if (!flag) {
            tbProjectMapper.insertProjectInfo(tbProject);
            return tbProject.getProId();
        } else {
            return tbProjectMapper.getProIdByProNoAndXmid(tbProject);
        }
    }

    @Override
    public int insertProjectBuild(TbProjectBuild tbProjectBuild) {
        boolean flag = tbProjectBuildMapper.getTotalByBdxhAndComIdAndBLicence(tbProjectBuild) > 0;
        if (!flag) {
            tbProjectBuildMapper.insertProjectBuild(tbProjectBuild);
            return tbProjectBuild.getPkid();
        } else {
            return tbProjectBuildMapper.getPkidByBdxhAndComIdAndBLicence(tbProjectBuild);
        }
    }

    public void insertPersonProject(TbPersonProject tbPersonProject) {
        boolean flag = tbPersonProjectMapper.getPersionProjectTotalByCertNoAndPid(tbPersonProject) > 0;
        if (!flag) {
            tbPersonProjectMapper.insertPersionProject(tbPersonProject);
        }
    }

    @Override
    public int insertProjectDesign(TbProjectDesign tbProjectDesign) {
        boolean falg = tbProjectDesignMapper.getTotalBySgtxhAndComIdCheckNo(tbProjectDesign) > 0;
        if (!falg) {
            tbProjectDesignMapper.insertProjectDesign(tbProjectDesign);
            return tbProjectDesign.getPkid();
        } else {
            return tbProjectDesignMapper.getPkidBySgtxhAndComIdCheckNo(tbProjectDesign);
        }
    }

    @Override
    public void insertPersonDesign(TbPersonDesign tbPersonDesign) {
        boolean flag = tbPersonDesignMapper.getTotalByNameAndCategoryAndPid(tbPersonDesign) > 0;
        if (!flag) {
            tbPersonDesignMapper.insertPersionDesign(tbPersonDesign);
        }
    }

    @Override
    public int insertProjectDesignTwo(TbProjectDesign tbProjectDesign) {
        boolean falg = tbProjectDesignMapper.getTotalBySgtxhAndComIdCheckNo(tbProjectDesign) > 0;
        if (!falg) {
            tbProjectDesignMapper.insertProjectDesign(tbProjectDesign);
            return tbProjectDesign.getPkid();
        } else {
            return tbProjectDesignMapper.getPkidBySgtxhAndComIdCheckNo(tbProjectDesign);
        }
    }

    @Override
    public int insertProjectSupervisor(TbProjectSupervision tbProjectSupervision) {
        boolean falg = tbProjectSupervisionMapper.getTotalByJlbdxhAndComIdTwo(tbProjectSupervision) > 0;
        if (!falg) {
            tbProjectSupervisionMapper.insertProjectSupervision(tbProjectSupervision);
            return tbProjectSupervision.getPkid();
        } else {
            return tbProjectSupervisionMapper.getPkidByJlbdxhAndComIdTwo(tbProjectSupervision);
        }
    }

    public boolean checkPersonQualificationExist(Map<String, Object> params) {
        return tbPersonQualificationMapper.getTolalByPersonQualificationUrlAndComId(params) > 0;
    }

    public boolean checkProjectBuildExist(Map<String, Object> params) {
        return tbProjectBuildMapper.getTotalByBdxhAndComId(params) > 0;
    }

    @Override
    public boolean checkProjectDesignExist(Map<String, Object> params) {
        return tbProjectDesignMapper.getTotalBySgtxhAndProTypeAndComId(params) > 0;
    }

    @Override
    public boolean checkProjectSupervisionExist(Map<String, Object> params) {
        return tbProjectSupervisionMapper.getTotalByJlbdxhAndComId(params) > 0;
    }

    public void batchInsertPeopleChange(List<TbPersonChange> tbPersonChanges) {
        tbPersonChangeMapper.batchInsertPeopleChange(tbPersonChanges);
    }

    @Override
    public void insertPeopleChange(TbPersonChange tbPersonChange) {
        boolean flag = tbPersonChangeMapper.getTotalByPerIdChangeDate(tbPersonChange) > 0;
        if (!flag) {
            tbPersonChangeMapper.insertPeopleChange(tbPersonChange);
        }
    }

    @Override
    public void insertException(TbExceptionUrl tbExceptionUrl) {
        tbExceptionUrlMapper.insertExceptionUrl(tbExceptionUrl);
    }


    //####################以下为拆分资质相关业务########################

    @Override
    public int getCompanyQualificationTotalByTabName(String tableName) {
        return tbCompanyQualificationMapper.getCompanyQualificationTotalByTabName(tableName);
    }

    @Override
    public List<TbCompanyQualification> getCompanyQualifications(Map<String, Object> params) {
        return tbCompanyQualificationMapper.listCompanyQualification(params);
    }

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
}
