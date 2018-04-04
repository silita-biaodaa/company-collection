package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public void batchInsertCompanyQualification(List<TbCompanyQualification> companyQualifications) {
        tbCompanyQualificationMapper.batchInsertCompanyQualification(companyQualifications);
    }

    @Override
    public void InsertCompanyQualification(TbCompanyQualification companyQualification) {
        tbCompanyQualificationMapper.InsertCompanyQualification(companyQualification);
    }

    @Override
    public boolean checkCompanyQualificationByCertNo(String certNo) {
        return tbCompanyQualificationMapper.getTotalByCertNo(certNo) > 0;
    }

    @Override
    public List<String> getAllCompanyQualificationUrl() {
        return tbCompanyQualificationMapper.getAllCompanyQualificationUrl();
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
    public void insertProjectBuild(TbProjectBuild tbProjectBuild) {
        if ("无施工许可证信息".equals(tbProjectBuild.getBLicence())) {
            tbProjectBuildMapper.insertProjectBuild(tbProjectBuild);
        } else {
            boolean flag = tbProjectBuildMapper.getTotalByBLicence(tbProjectBuild.getBLicence()) > 0;
            if (!flag) {
                tbProjectBuildMapper.insertProjectBuild(tbProjectBuild);
            }
        }
    }
}
