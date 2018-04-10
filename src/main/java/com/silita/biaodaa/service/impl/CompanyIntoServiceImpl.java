package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.TbCompanyIntoMapper;
import com.silita.biaodaa.model.TbCompanyInto;
import com.silita.biaodaa.service.ICompanyIntoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 91567 on 2018/4/9.
 */
@Service("companyIntoService")
public class CompanyIntoServiceImpl implements ICompanyIntoService {

    @Autowired
    private TbCompanyIntoMapper tbCompanyIntoMapper;

    @Override
    public void insertCompanyInto(TbCompanyInto tbCompanyInto) {
        boolean falg = tbCompanyIntoMapper.getTotalByOrgCodeAndBusinessNum(tbCompanyInto) > 0;
        if(!falg) {
            tbCompanyIntoMapper.insertCompanyInto(tbCompanyInto);
        }
    }
}
