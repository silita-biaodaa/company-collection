package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.TbCompanyAptitudeMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyAptitude;
import com.silita.biaodaa.service.ICompanyRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/11.
 */
@Service("companyRangeService")
public class CompanyRangeServiceImpl implements ICompanyRangeService {
    @Autowired
    private TbCompanyMapper tbCompanyMapper;
    @Autowired
    private TbCompanyAptitudeMapper tbCompanyAptitudeMapper;

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
