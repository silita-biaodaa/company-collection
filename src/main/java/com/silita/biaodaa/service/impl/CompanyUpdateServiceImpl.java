package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.ICompanyUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/24.
 */
@Service("companyUpdateService")
public class CompanyUpdateServiceImpl implements ICompanyUpdateService {
    @Autowired
    private TbCompanyQualificationMapper tbCompanyQualificationMapper;
    @Autowired
    private TbExceptionUrlMapper tbExceptionUrlMapper;
    @Autowired
    private TbCompanyMapper tbCompanyMapper;
    @Autowired
    private TbPersonQualificationMapper tbPersonQualificationMapper;
    @Autowired
    private TbPersonMapper tbPersonMapper;
    @Autowired
    private TbPersonChangeMapper tbPersonChangeMapper;


    @Override
    public List<String> getAllCompanyQualificationUrlByTabAndCompanyName(Map<String, Object> params) {
        return tbCompanyQualificationMapper.getAllCompanyQualificationUrlByTabAndCompanyName(params);
    }

    @Override
    public void insertException(TbExceptionUrl tbExceptionUrl) {
        tbExceptionUrlMapper.insertExceptionUrl(tbExceptionUrl);
    }

    @Override
    public TbCompanyQualification getComIdByUrl(String url) {
        return tbCompanyQualificationMapper.getComIdByUrl(url);
    }

    @Override
    public void updateCompany(TbCompany tbCompany) {
        tbCompanyMapper.updateCompany(tbCompany);
    }

    @Override
    public void updateCompanyQualificationByUrl(TbCompanyQualification tbCompanyQualification) {
        tbCompanyQualificationMapper.updateTbCompanyQualificationByUrl(tbCompanyQualification);
    }

    @Override
    public boolean checkPersonQualificationExist(Map<String, Object> params) {
        return tbPersonQualificationMapper.getTolalByPersonQualificationUrlAndComId(params) > 0;
    }


    public void deletePersonQualByComId(Integer comId) {
        tbPersonQualificationMapper.deletePersonQualByComId(comId);
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
    public void insertPeopleChange(TbPersonChange tbPersonChange) {
        boolean flag = tbPersonChangeMapper.getTotalByPerIdChangeDate(tbPersonChange) > 0;
        if (!flag) {
            tbPersonChangeMapper.insertPeopleChange(tbPersonChange);
        }
    }

    @Override
    public List<Map<String, Object>> listComNameAndTab() {
        return tbCompanyQualificationMapper.listComNameAndTab();
    }

}
