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
            if(tbPersonHunan.getCategory().equals("注册建造师（一级临时）")) {
                tbPersonHunan.setCategory("一级临时注册建造师");
            } else if(tbPersonHunan.getCategory().equals("注册建造师（一级）")) {
                tbPersonHunan.setCategory("一级注册建造师");
            } else if(tbPersonHunan.getCategory().equals("一级注册结构师")) {
                tbPersonHunan.setCategory("一级注册结构工程师");
            } else if(tbPersonHunan.getCategory().equals("注册建造师(二级临时)")) {
                tbPersonHunan.setCategory("'二级临时注册建造师");
            } else if(tbPersonHunan.getCategory().equals("注册建造师（二级临时）")) {
                tbPersonHunan.setCategory("二级临时注册建造师");
            } else if(tbPersonHunan.getCategory().equals("注册建造师(二级)")) {
                tbPersonHunan.setCategory("二级注册建造师");
            } else if(tbPersonHunan.getCategory().equals("注册建造师（二级）")) {
                tbPersonHunan.setCategory("二级注册建造师");
            } else if(tbPersonHunan.getCategory().equals("二级注册结构师")) {
                tbPersonHunan.setCategory("二级注册结构工程师");
            } else if(tbPersonHunan.getCategory().equals("监理工程师")) {
                tbPersonHunan.setCategory("注册监理工程师");
            } else if(tbPersonHunan.getCategory().equals("造价工程师")) {
                tbPersonHunan.setCategory("注册造价工程师");
            }
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
