package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.TbPersonChangeMapper;
import com.silita.biaodaa.dao.TbPersonHunanMapper;
import com.silita.biaodaa.model.TbPersonChange;
import com.silita.biaodaa.model.TbPersonHunan;
import com.silita.biaodaa.service.IPeopleCertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2018/5/19.
 */
@Service("peopleCertService")
public class PeopleCertServiceImpl implements IPeopleCertService {

    @Autowired
    private TbPersonHunanMapper tbPersonHunanMapper;
    @Autowired
    private TbPersonChangeMapper tbPersonChangeMapper;


    @Override
    public boolean checkPersonHunanIsExist(TbPersonHunan tbPersonHunan) {
        return tbPersonHunanMapper.getTotalByComIdAndCertNoAndCategory(tbPersonHunan) > 0;
    }

    @Override
    public void insertPersonHunan(TbPersonHunan tbPersonHunan) {
        boolean flag = tbPersonHunanMapper.getTotalByComIdAndCertNoAndCategoryAndMajor(tbPersonHunan) > 0;
        if(!flag) {
            tbPersonHunanMapper.insertPersonHunan(tbPersonHunan);
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
    public void deletePersonHunanByCompanyId(Integer companyId) {
        tbPersonHunanMapper.deletePersonHunanByCompanyId(companyId);
    }
}
