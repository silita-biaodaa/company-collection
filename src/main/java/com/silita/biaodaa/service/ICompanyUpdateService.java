package com.silita.biaodaa.service;

import com.silita.biaodaa.model.*;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/24.
 */
public interface ICompanyUpdateService {

    /**
     *
     * @param params
     * @return
     */
    List<String> getAllCompanyQualificationUrlByTabAndCompanyName(Map<String, Object> params);

    /**
     * 插入异常信息
     * @param tbExceptionUrl
     */
    void insertException(TbExceptionUrl tbExceptionUrl);

    /**
     *
     * @param url
     * @return
     */
    TbCompanyQualification getComIdByUrl(String url);

    /**
     *
     * @param tbCompany
     */
    void updateCompany(TbCompany tbCompany);

    /**
     *
     * @param tbCompanyQualification
     */
    void updateCompanyQualificationByUrl(TbCompanyQualification tbCompanyQualification);

    /**
     *
     * @param params
     * @return
     */
    boolean checkPersonQualificationExist(Map<String, Object> params);

    /**
     *
     * @param comId
     */
    void deletePersonQualByComId(Integer comId);

    /**
     *
     * @param tbPerson
     * @return
     */
    int insertPersionInfo(TbPerson tbPerson);

    /**
     *
     * @param tbPersonQualification
     */
    void insertPersonQualification(TbPersonQualification tbPersonQualification);

    /**
     *
     * @param tbPersonChange
     */
    void insertPeopleChange(TbPersonChange tbPersonChange);
}
