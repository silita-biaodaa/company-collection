package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/19.
 */
@Service("companyService")
public class CompanyServiceImpl implements ICompanyService {

    @Autowired
    private TbCompanyMapper tbCompanyMapper;
    @Autowired
    private TbCompanyQualificationMapper tbCompanyQualificationMapper;
    @Autowired
    private TbCompanyIntoMapper tbCompanyIntoMapper;
    @Autowired
    private TbSafetyCertificateMapper tbSafetyCertificateMapper;
    @Autowired
    private TbExceptionUrlMapper tbExceptionUrlMapper;



    @Override
    public void insertCompanyInto(TbCompanyInto tbCompanyInto) {
        boolean falg = tbCompanyIntoMapper.getTotalByOrgCodeAndBusinessNum(tbCompanyInto) > 0;
        if(!falg) {
            tbCompanyIntoMapper.insertCompanyInto(tbCompanyInto);
        }
    }

    @Override
    public void batchInsertSafetyCertificate(List<TbSafetyCertificate> safetyCertificates) {
        tbSafetyCertificateMapper.batchInsertSafetyCertificate(safetyCertificates);
    }

    @Override
    public void insertSafetyCertificate(TbSafetyCertificate safetyCertificates) {
        boolean flag = tbSafetyCertificateMapper.getTotalByCertNoAndCompanyName(safetyCertificates) > 0;
        if(!flag) {
            tbSafetyCertificateMapper.insertSafetyCertificate(safetyCertificates);
        } else {
            TbSafetyCertificate old = tbSafetyCertificateMapper.getSafetyCertificateByCertNoAndCompanyName(safetyCertificates);
            if(!StringUtils.isEmpty(old.getValidDate()) && !StringUtils.isEmpty(safetyCertificates.getValidDate())) {
                Integer oldDate = Integer.parseInt(old.getValidDate().replaceAll("-", ""));
                Integer newDate = Integer.parseInt(safetyCertificates.getValidDate().replaceAll("-", ""));
                //替换有效期小的
                if(newDate > oldDate) {
                    tbSafetyCertificateMapper.updateSafetyCertificate(safetyCertificates);
                }
            }
        }
    }

    @Override
    public void insertCompanyQualification(TbCompanyQualification companyQualification) {
        boolean flag = tbCompanyQualificationMapper.getTotalByCertNoAndTab(companyQualification) > 0;
        if (!flag) {
            tbCompanyQualificationMapper.InsertCompanyQualification(companyQualification);
        }
    }

    @Override
    public void batchInsertCompanyQualification(List<TbCompanyQualification> companyQualifications) {
        tbCompanyQualificationMapper.batchInsertCompanyQualification(companyQualifications);
    }

    @Override
    public List<String> listCompanyQualificationUrlByTab(String tableName) {
        return tbCompanyQualificationMapper.getAllCompanyQualificationUrlByTabName(tableName);
    }

    @Override
    public int insertCompanyInfo(TbCompany tbCompany) {
        boolean flag = tbCompanyMapper.getCompanyTotalByOrgCodeOrCompanyName(tbCompany) > 0;
        if (!flag) {
            //不存在新增、并返回新增的 comId
            tbCompanyMapper.insertCompany(tbCompany);
            return tbCompany.getComId();
        } else {
            //存在返回查询到的 comId
            return tbCompanyMapper.getCompanyIdByOrgCodeOrCompanyName(tbCompany);
        }
    }

    @Override
    public void updateCompanyQualificationUrlByCorpid(TbCompanyQualification companyQualification) {
        tbCompanyQualificationMapper.updateCompanyQualificationUrlByCorpidAndCertId(companyQualification);
    }

    //###############数据更新相关###############

    @Override
    public List<Map<String, Object>> listComNameAndTab() {
        return tbCompanyQualificationMapper.listComNameAndTab();
    }

    @Override
    public List<Map<String, Object>> listComNameAndTabByTab(String tab) {
        return tbCompanyQualificationMapper.listComNameAndTabByTab(tab);
    }

    @Override
    public List<String> getAllCompanyQualificationUrlByTabAndCompanyName(Map<String, Object> params) {
        return tbCompanyQualificationMapper.getAllCompanyQualificationUrlByTabAndCompanyName(params);
    }

    @Override
    public List<Map<String, Object>> listCompanyQualificationByTabAndCompanyName(Map<String, Object> params) {
        return tbCompanyQualificationMapper.listCompanyQualificationByTabAndCompanyName(params);
    }

    @Override
    public void updateCompany(TbCompany tbCompany) {
        tbCompanyMapper.updateCompany(tbCompany);
    }

    @Override
    public void updateCompanyQualificationByUrl(TbCompanyQualification tbCompanyQualification) {
        /*Integer channel = tbCompanyQualificationMapper.getCompanyQualificationChannelByUrl(tbCompanyQualification.getUrl());
        if(channel == 3) {
            //todo 人工
        } else if(channel == 2) {
            //todo 湖南
            tbCompanyQualificationMapper.updateTbCompanyQualificationByUrl(tbCompanyQualification);
        } else if (channel == 1) {
            //todo 全国
        }*/
        tbCompanyQualificationMapper.updateTbCompanyQualificationByUrl(tbCompanyQualification);
    }

    @Override
    public TbCompanyQualification getComIdByUrl(String url) {
        return tbCompanyQualificationMapper.getComIdByUrl(url);
    }


    @Override
    public void insertException(TbExceptionUrl tbExceptionUrl) {
        tbExceptionUrlMapper.insertExceptionUrl(tbExceptionUrl);
    }


}
