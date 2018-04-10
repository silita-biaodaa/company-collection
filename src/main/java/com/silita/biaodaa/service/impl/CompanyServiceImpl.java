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

    @Override
    public void batchInsertCompanyQualification(List<TbCompanyQualification> companyQualifications) {
        tbCompanyQualificationMapper.batchInsertCompanyQualification(companyQualifications);
    }

    @Override
    public void InsertCompanyQualification(TbCompanyQualification companyQualification) {
        boolean flag = tbCompanyQualificationMapper.getTotalByCertNo(companyQualification.getCertNo()) > 0;
        if(!flag) {
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
        boolean flag = tbPersonQualificationMapper.getTotalByCertNo(tbPersonQualification.getCertNo()) > 0;
        if (!flag) {
            tbPersonQualificationMapper.insertPersonQualification(tbPersonQualification);
        }
    }

    @Override
    public int insertProjectInfo(TbProject tbProject) {
        boolean flag = tbProjectMapper.getProjectTotalByProjectNo(tbProject.getProNo()) > 0;
        if (!flag) {
            tbProjectMapper.insertProjectInfo(tbProject);
            return tbProject.getProId();
        } else {
            return tbProjectMapper.getProIdByProNo(tbProject.getProNo());
        }
    }

    @Override
    public int insertProjectBuild(TbProjectBuild tbProjectBuild) {
        if ("无施工许可证信息".equals(tbProjectBuild.getBLicence())) {
            //无施工许可证编号用标段名称、施工单位判断
//            boolean flag = tbProjectBuildMapper.getTotalByBNameAndBOrg(tbProjectBuild) > 0;
            boolean flag = tbProjectBuildMapper.getTotalByBdxh(tbProjectBuild.getBdxh()) > 0;
            if(!flag) {
                tbProjectBuildMapper.insertProjectBuild(tbProjectBuild);
                return tbProjectBuild.getPkid();
            } else {
//                return tbProjectBuildMapper.getPkidByBNameAndBOrg(tbProjectBuild);
                return tbProjectBuildMapper.getPkidByBdxh(tbProjectBuild.getBdxh());
            }
        } else {
            boolean flag = tbProjectBuildMapper.getTotalByBLicence(tbProjectBuild.getBLicence()) > 0;
            if (!flag) {
                tbProjectBuildMapper.insertProjectBuild(tbProjectBuild);
                return tbProjectBuild.getPkid();
            } else {
                return tbProjectBuildMapper.getPkidByBLicence(tbProjectBuild.getBLicence());
            }
        }
    }

    public void insertPersonProject(TbPersonProject tbPersonProject) {
        boolean flag = tbPersonProjectMapper.getPersionProjectTotalByNameAndCertNoAndSafeNo(tbPersonProject) > 0;
        if(!flag) {
            tbPersonProjectMapper.insertPersionProject(tbPersonProject);
        }
    }

    @Override
    public int insertProjectDesign(TbProjectDesign tbProjectDesign) {
        boolean falg = tbProjectDesignMapper.getProjectDesignTotalByCheckNoAndDesignOrg(tbProjectDesign) > 0;
        if(!falg) {
            tbProjectDesignMapper.insertProjectDesign(tbProjectDesign);
            return tbProjectDesign.getPkid();
        } else {
            return tbProjectDesignMapper.getPkidByCheckNoAndDesignOrg(tbProjectDesign);
        }
    }

    @Override
    public void insertPersonDesign(TbPersonDesign tbPersonDesign) {
        boolean flag = tbPersonDesignMapper.getTotalByNameAndCompanyNameAndRole(tbPersonDesign) > 0;
        if(!flag) {
            tbPersonDesignMapper.insertPersionDesign(tbPersonDesign);
        }
    }

    @Override
    public int insertProjectDesignTwo(TbProjectDesign tbProjectDesign) {
        boolean falg = tbProjectDesignMapper.getProjectDesignTotalByCheckNoAndExploreOrg(tbProjectDesign) > 0;
        if(!falg) {
            tbProjectDesignMapper.insertProjectDesign(tbProjectDesign);
            return tbProjectDesign.getPkid();
        } else {
            return tbProjectDesignMapper.getPkidByCheckNoAndExploreOrg(tbProjectDesign);
        }
    }

    @Override
    public int insertProjectSupervisor(TbProjectSupervision tbProjectSupervision) {
        boolean falg = tbProjectSupervisionMapper.getTotalByJlbdxh(tbProjectSupervision.getJlbdxh()) > 0;
        if(!falg) {
            tbProjectSupervisionMapper.insertProjectSupervision(tbProjectSupervision);
            return tbProjectSupervision.getPkid();
        } else {
            return tbProjectSupervisionMapper.getPkidByJlbdxh(tbProjectSupervision.getJlbdxh());
        }
    }

    @Override
    public int getTotalCompanyQualificationByTabName(String tableName) {
        return tbCompanyQualificationMapper.getTotalCompanyQualificationByTabName(tableName);
    }

    @Override
    public List<TbCompanyQualification> getCompanyQualification(Map<String, Object> params) {
        return tbCompanyQualificationMapper.getCompanyQualification(params);
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
}
