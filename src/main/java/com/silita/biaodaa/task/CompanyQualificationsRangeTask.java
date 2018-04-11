package com.silita.biaodaa.task;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyAptitude;
import com.silita.biaodaa.service.ICompanyRangeService;
import com.silita.biaodaa.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/11.
 */
@Component
public class CompanyQualificationsRangeTask {

    @Autowired
    ICompanyRangeService companyRangeService;

    /**
     * 最后更新资质到企业基本信息表
     * 添加企业资质到企业基本信息表（方便业务查询）
     * @param
     */
    public void updateCompanyAptitudeRange() {
        int page = 0;
        int batchCount = 1000;
        Integer count = companyRangeService.getCompanyAptitudeTotal();
        if (count % batchCount == 0) {
            page = count / batchCount;
        } else {
            page = count / batchCount + 1;
        }
        Map<String, Object> params;
        List<TbCompanyAptitude> tbCompanyAptitudes ;
        //分页
        for (int pageNum = 0; pageNum < page; pageNum++) {
            params = new HashMap<>();
            params.put("start", batchCount * pageNum);
            params.put("pageSize", 1000);
            tbCompanyAptitudes = companyRangeService.listCompanyAptitude(params);
            TbCompany tbCompany;
            TbCompanyAptitude tbCompanyAptitude;
            int comId;
            String allType;
            String allAptitudeUuid;
            StringBuilder sb ;
            //遍历
            for (int i = 0; i < tbCompanyAptitudes.size(); i++) {
                tbCompanyAptitude = tbCompanyAptitudes.get(i);
                comId = tbCompanyAptitude.getComId();
                allType = tbCompanyAptitude.getType();
                allAptitudeUuid = tbCompanyAptitude.getAptitudeUuid();
                if(StringUtils.isNotNull(allType) && StringUtils.isNotNull(allAptitudeUuid)) {
                    sb = new StringBuilder();
                    String[] typeArr = allType.split(",");
                    String[] aptitudeUuidArr = allAptitudeUuid.split(",");
                    if(typeArr.length > 0 && aptitudeUuidArr.length > 0 && typeArr.length == aptitudeUuidArr.length) {
                        for (int j = 0; j < typeArr.length; j++) {
                            if(j == typeArr.length - 1) {
                                sb.append(typeArr[j]).append("/").append(aptitudeUuidArr[j]);
                            } else {
                                sb.append(typeArr[j]).append("/").append(aptitudeUuidArr[j]).append(",");
                            }
                        }
                    }
                    tbCompany = new TbCompany();
                    tbCompany.setComId(comId);
                    tbCompany.setRange(sb.toString());
                    companyRangeService.updateCompanyRangeByComId(tbCompany);
                }
            }
        }
    }
}
