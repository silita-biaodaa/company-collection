package com.silita.task;


import com.alibaba.fastjson.JSONObject;
import com.silita.common.xxl.BaseTask;
import com.silita.common.xxl.MyXxlLogger;
import com.silita.model.*;
import com.silita.service.ICompanyService;
import com.silita.service.IPeopleCertService;
import com.silita.service.ISplitCertService;
import com.xxl.job.core.handler.annotation.JobHander;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@JobHander(value = "HuNanAllCompanyUpdateTask")
public class HuNanAllCompanyUpdateTask extends BaseTask {

    @Override
    public void runTask(JSONObject jsonObject) {
        allCompanyUpdateTask();
    }

    private int min = 1;
    private int max = 5;
    private Random random = new Random();

    private static final int THREAD_NUMBER = 2;

    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IPeopleCertService peopleCertService;
    @Autowired
    private ISplitCertService splitCertService;
    @Autowired
    private com.silita.biaodaa.task.ProjectDataUpdate projectDataUpdate;


    /**
     * 建筑业企业数据更新
     */
    public void allCompanyUpdateTask() {
        int threadCount = THREAD_NUMBER;
        //获取全部企业名、tab
        List<Map<String, Object>> maps = companyService.listComNameAndTab();
        Map<String, Object> params = null;
        List allCerts = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> map : maps) {
            params = new HashMap<>();
//            params.put("tableName", map.get("tab"));
            params.put("comName", map.get("com_name"));
            //根据公司名获取证书url、tab
            List<Map<String, Object>> certs = companyService.listCompanyQualificationByTabAndCompanyName(params);
            allCerts.addAll(certs);
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);
        int every = allCerts.size() % threadCount == 0 ? allCerts.size() / threadCount : (allCerts.size() / threadCount) + 1;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(new HuNanAllCompanyUpdateRun(i * every, (i + 1) * every, latch, allCerts));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    class HuNanAllCompanyUpdateRun implements Runnable {

        private int startNum;
        private int endNum;
        private CountDownLatch latch;
        private List<Map<String, Object>> CompanyQualificationUrls;

        public HuNanAllCompanyUpdateRun(int startNum, int endNum, CountDownLatch latch, List<Map<String, Object>> CompanyQualificationUrls) {
            this.startNum = startNum;
            this.endNum = endNum;
            this.latch = latch;
            this.CompanyQualificationUrls = CompanyQualificationUrls;
        }

        @Override
        public void run() {
            Document companyDetailDoc;
            Connection companyDetailConn;
            //企业cookie，抓取人员、项目需要企业cookie
            Map<String, String> cookies;
            Integer comId = null;
            try {
                for (int i = startNum; i < endNum; i++) {
                    String CompanyQualificationUrl = (String) CompanyQualificationUrls.get(i).get("url");
                    companyDetailConn = Jsoup.connect(CompanyQualificationUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                    companyDetailDoc = companyDetailConn.get();
                    if (companyDetailConn.response().statusCode() == 200) {
                        cookies = companyDetailConn.response().cookies();
                        Elements companyInfoTable = companyDetailDoc.select("#table1");
                        Elements CompanyAptitudeTable = companyDetailDoc.select("#tablelist").select("#table2").select("#ctl00_ContentPlaceHolder1_td_zzdetail").select("table");

                        TbCompanyQualification tbCompanyQualification = companyService.getComIdByUrl(CompanyQualificationUrl);
                        comId = tbCompanyQualification.getComId();
                        //#########更新企业基本信息########
                        updateCompanyInfo(companyInfoTable, comId);
                        //########更新企业资质证书#########
                        updateCompanyAptitude(CompanyAptitudeTable, CompanyQualificationUrl, comId);
                        //########更新人员及人员证书#######
                        updatePeople(cookies, CompanyQualificationUrl, comId);
                        //##########更新项目信息##########
                        String tab = (String) CompanyQualificationUrls.get(i).get("tab");
                        if (tab.equals("建筑业企业")) {
                            projectDataUpdate.getBuilderProjectList(cookies, CompanyQualificationUrl, comId);
                        } else if (tab.equals("工程设计企业")) {
                            projectDataUpdate.getDesignProjectList(cookies, CompanyQualificationUrl, comId);
                        } else if (tab.equals("工程勘察企业")) {
                            projectDataUpdate.getSurveyProjectList(cookies, CompanyQualificationUrl, comId);
                        } else if (tab.equals("工程监理企业")) {
                            projectDataUpdate.getSupervisorProjectList(cookies, CompanyQualificationUrl, comId);
                        }
                    } else {
                        TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                        tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                        tbExceptionUrl.setExceptionMsg("获取企业详情信息失败!");
                        companyService.insertException(tbExceptionUrl);
                    }
                    //随机暂停几秒
                    Thread.sleep(1000 * (random.nextInt(max) % (max - min + 1)));
                }
                //##########拆分&&更新企业资质##########
                splitCompanyQualifications(comId);
                updateCompanyAptitudeRange(comId);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
            latch.countDown();
        }

        /**
         * 更新企业基本信息
         *
         * @param eles
         * @param companyId
         */
        void updateCompanyInfo(Elements eles, Integer companyId) {
            TbCompany company = new TbCompany();
            company.setComName(eles.select("#ctl00_ContentPlaceHolder1_lbl_qymc").text());
            company.setOrgCode(eles.select("#ctl00_ContentPlaceHolder1_lbl_jgdm").text());
            company.setBusinessNum(eles.select("#ctl00_ContentPlaceHolder1_lbl_yyzz").text());
            company.setRegisAddress("湖南省" + eles.select("#ctl00_ContentPlaceHolder1_lbl_sz").text());
            company.setComAddress(eles.select("#ctl00_ContentPlaceHolder1_lbl_dwdz").text());
            company.setLegalPerson(eles.select("#ctl00_ContentPlaceHolder1_lbl_fddbr").text());
            company.setEconomicType(eles.select("#ctl00_ContentPlaceHolder1_lbl_jjlx").text());
            company.setRegisCapital(eles.select("#ctl00_ContentPlaceHolder1_lbl_zczb").text());
            company.setComId(companyId);
            companyService.updateCompany(company);
        }

        /**
         * 更新企业资质证书
         *
         * @param eles
         * @param CompanyQualificationUrl
         * @param companyId
         */
        void updateCompanyAptitude(Elements eles, String CompanyQualificationUrl, Integer companyId) {
            if (eles.size() > 0) {
                //有的下面有多个资质
                for (int i = 0; i < eles.size(); i++) {
                    String type = eles.get(i).select("tr").get(0).select("td").get(1).text();
                    if (type.equals("建筑业") || type.equals("工程设计") || type.equals("工程勘察") || type.equals("工程监理")) {
                        TbCompanyQualification tbCompanyQualification = new TbCompanyQualification();
                        tbCompanyQualification.setQualType(eles.get(i).select("tr").get(0).select("td").get(1).text());
                        tbCompanyQualification.setCertNo(eles.get(i).select("tr").get(1).select("td").get(1).text());
                        tbCompanyQualification.setCertOrg(eles.get(i).select("tr").get(1).select("td").get(3).text());
                        tbCompanyQualification.setCertDate(eles.get(i).select("tr").get(3).select("td").get(1).text());
                        tbCompanyQualification.setValidDate(eles.get(i).select("tr").get(3).select("td").get(3).text());
                        tbCompanyQualification.setRange(eles.get(i).select("tr").get(4).select("td").get(1).text());
                        tbCompanyQualification.setUrl(CompanyQualificationUrl);
                        tbCompanyQualification.setComId(companyId);
                        tbCompanyQualification.setCorpid(CompanyQualificationUrl.substring(CompanyQualificationUrl.indexOf("=") + 1));
                        companyService.updateCompanyQualificationByUrl(tbCompanyQualification);
                    }
                }
            } else {
                MyXxlLogger.info("该企业资质数据为空" + CompanyQualificationUrl);
            }
        }

        /**
         * 更新人员
         *
         * @param cookies
         * @param CompanyQualificationUrl
         * @param companyId
         */
        void updatePeople(Map<String, String> cookies, String CompanyQualificationUrl, Integer companyId) {
            //删除该公司下的人员资质证书
            peopleCertService.deletePersonHunanByCompanyId(companyId);
            Document peopleListDoc;
            Connection peopleListConn;
            String peopleListUrl = "http://qyryjg.hunanjz.com/public/EnterpriseRegPerson.ashx";
            try {
                //进入人员证书列表
                peopleListConn = Jsoup.connect(peopleListUrl).userAgent("Mozilla").timeout(120000 * 60).ignoreHttpErrors(true);
                peopleListConn.cookies(cookies);
                peopleListDoc = peopleListConn.get();
                if (peopleListConn.response().statusCode() == 200) {
                    Elements peopleList = peopleListDoc.select("table").select("tr");
                    if (peopleList.size() > 2) {
                        String PersonQualificationUrl;
                        TbPersonHunan tbPersonHunan;
                        Document peopleDetailDoc;
                        Connection peopleDetailConn;
                        //遍历人员列表url 进入详情页面
                        for (int i = 2; i < peopleList.size(); i++) {
                            PersonQualificationUrl = peopleList.get(i).select("a").first().absUrl("href");
                            //列表获取一部分证书信息
                            tbPersonHunan = new TbPersonHunan();
                            tbPersonHunan.setUrl(PersonQualificationUrl);
                            tbPersonHunan.setName(peopleList.get(i).select("td").get(1).text());
//                            tbPersonHunan.setIdCard(peopleList.get(i).select("td").get(2).text());
                            tbPersonHunan.setCategory(peopleList.get(i).select("td").get(3).text());
                            tbPersonHunan.setCertNo(peopleList.get(i).select("td").get(4).text().trim());
                            if (!StringUtils.isEmpty(peopleList.get(i).select("td").get(5).text().replaceAll("[^0-9]", ""))) {
                                tbPersonHunan.setSealNo(peopleList.get(i).select("td").get(5).text());
                            } else {
                                tbPersonHunan.setMajor(peopleList.get(i).select("td").get(5).text());
                            }
                            tbPersonHunan.setCertDate(peopleList.get(i).select("td").get(6).text());
                            tbPersonHunan.setValidDate(peopleList.get(i).select("td").last().text());
                            tbPersonHunan.setType(1);
                            tbPersonHunan.setInnerid(PersonQualificationUrl.substring(PersonQualificationUrl.indexOf("=") + 1));
                            tbPersonHunan.setComId(companyId);
                            //已存在url,证书编号，公司id(人员证书可能挂靠其他公司)跳过此证书
                            if (peopleCertService.checkPersonHunanIsExist(tbPersonHunan)) {
                                MyXxlLogger.info("已抓取这个公司的这本证书" + PersonQualificationUrl);
                            } else {
                                peopleDetailConn = Jsoup.connect(PersonQualificationUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                                peopleDetailDoc = peopleDetailConn.get();
                                if (peopleDetailConn.response().statusCode() == 200) {
                                    Elements peopleInfoTable = peopleDetailDoc.select("#table1");
                                    Elements peopleRegisteredTable = peopleDetailDoc.select("#tablelist").select("#table2").select("#ctl00_ContentPlaceHolder1_td_zzdetail").select("tr");
                                    Elements CertDetail = peopleDetailDoc.select("table").select(".tab_main").select("tr");
                                    Elements peopleOtherQualificationsTable = peopleDetailDoc.select("#tablelist").select("#table3").select("#ctl00_ContentPlaceHolder1_td_rylist").select("tr");
                                    Elements peopleChangeTable = peopleDetailDoc.select("#tablelist").select("#table6").select("#ctl00_ContentPlaceHolder1_jzs2_history").select("tr");
                                    String sex = peopleInfoTable.select("#ctl00_ContentPlaceHolder1_lbl_xb").text();
                                    String cardId = peopleInfoTable.select("#ctl00_ContentPlaceHolder1_lbl_sfzh").text();
                                    tbPersonHunan.setSex(sex);
                                    tbPersonHunan.setIdCard(cardId);
                                    //#######添加人员执业资质########
                                    String flag = addPeopleMainCert(peopleRegisteredTable, tbPersonHunan, CertDetail);
                                    //#######添加人员其他资质########
                                    addPeopleOtherCert(peopleOtherQualificationsTable, PersonQualificationUrl, companyId, flag);
                                    //#######添加人员变更信息########
                                    addPeopleChange(peopleChangeTable, flag);
                                } else {
                                    TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                                    tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                                    tbExceptionUrl.setExceptionUrl(PersonQualificationUrl);
                                    tbExceptionUrl.setExceptionMsg("获取人员详情失败");
                                    companyService.insertException(tbExceptionUrl);
                                }
                            }
                            //随机暂停几秒
                            Thread.sleep(100 * (random.nextInt(max) % (max - min + 1)));
                        }
                    } else {
                        MyXxlLogger.info("该企业注册人员数据为空" + CompanyQualificationUrl);
                    }
                } else {
                    MyXxlLogger.info("获取人员证书列表页失败" + CompanyQualificationUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }


        /**
         * 添加人员注册执业信息
         *
         * @param peopleRegisteredTable 人员注册执业信息表格
         * @param temp                  证书临时对象
         * @param certDetail            隐藏的表单
         * @return 姓名_性别_身份证
         */
        String addPeopleMainCert(Elements peopleRegisteredTable, TbPersonHunan temp, Elements certDetail) {
            TbPersonHunan tbPersionHunan = temp;
            if (peopleRegisteredTable.size() > 1) {
                String certNo;
                for (int i = 1; i < peopleRegisteredTable.size(); i++) {
                    certNo = peopleRegisteredTable.get(i).select("td").get(2).text().trim();
                    if (certNo.equals(tbPersionHunan.getCertNo())) {
                        tbPersionHunan.setComName(peopleRegisteredTable.get(i).select("td").get(1).text());
                        //注册专业为空,取详情列表补齐
                        if (StringUtils.isEmpty(temp.getMajor())) {
                            temp.setMajor(peopleRegisteredTable.get(i).select("td").get(3).text());
                            break;
                        }
                    }

                }
            }
            String flag = tbPersionHunan.getName() + "_" + tbPersionHunan.getSex() + "_" + tbPersionHunan.getIdCard();
            tbPersionHunan.setFlag(flag);
            //执业印章号为空，取模态窗口数据补齐，同时更新专业
            if (StringUtils.isEmpty(tbPersionHunan.getSealNo())) {
                tbPersionHunan.setSealNo(certDetail.select("tr").get(2).select("#ctl00_ContentPlaceHolder1_d_zcbh").text());
                String major = certDetail.select("tr").get(3).select("#ctl00_ContentPlaceHolder1_d_zy").text();
                //包含多个证书如：（建筑,2015-01-19,2018-01-18.机电,2016-8-17,2019-8-16.市政,2017-7-11,2020-7-10）
                if (major.contains(".")) {
                    String[] majors = major.split("\\.");
                    for (int i = 0; i < majors.length; i++) {
                        majors[i].replaceAll("，", ",");
                        if (majors[i].contains(",")) {
                            String[] tempArr = majors[i].split(",");
                            tbPersionHunan.setMajor(tempArr[0]);
                            tbPersionHunan.setCertDate(tempArr[1]);
                            tbPersionHunan.setValidDate(tempArr[2]);
                        }
                        peopleCertService.insertPersonHunan(tbPersionHunan);
                    }
                } else {
                    peopleCertService.insertPersonHunan(tbPersionHunan);
                }
            } else {
                peopleCertService.insertPersonHunan(tbPersionHunan);
            }
            return flag;
        }

        /**
         * 添加其他资格信息
         *
         * @param peopleOtherQualificationsTable 其他资格信息表格
         * @param url                            人员url
         * @param companyId                      公司id
         * @param flag                           人员唯一标识（ 姓名_性别_身份证）
         */
        void addPeopleOtherCert(Elements peopleOtherQualificationsTable, String url, Integer companyId, String flag) {
            if (peopleOtherQualificationsTable.size() > 1) {
                TbPersonHunan tbPersionHunan;
                for (int i = 1; i < peopleOtherQualificationsTable.size(); i++) {
                    tbPersionHunan = new TbPersonHunan();
                    tbPersionHunan.setCategory(peopleOtherQualificationsTable.get(i).select("td").get(0).text());
                    tbPersionHunan.setComName(peopleOtherQualificationsTable.get(i).select("td").get(1).text());
                    tbPersionHunan.setCertNo(peopleOtherQualificationsTable.get(i).select("td").get(2).text());
                    tbPersionHunan.setMajor(peopleOtherQualificationsTable.get(i).select("td").get(3).text());
                    String dateStr = peopleOtherQualificationsTable.get(i).select("td").get(4).text();
                    if (dateStr.contains("有效期")) {
                        tbPersionHunan.setCertDate(dateStr.substring(0, dateStr.indexOf("有效期") - 1));
                        tbPersionHunan.setValidDate(dateStr.substring(dateStr.indexOf("有效期") + 4, dateStr.length() - 1));
                    } else {
                        tbPersionHunan.setCertDate(dateStr);
                    }
                    tbPersionHunan.setName(flag.split("_")[0]);
                    tbPersionHunan.setSex(flag.split("_")[1]);
                    tbPersionHunan.setIdCard(flag.split("_")[2]);
                    tbPersionHunan.setUrl(url);
                    tbPersionHunan.setInnerid(url.substring(url.indexOf("=") + 1));
                    tbPersionHunan.setComId(companyId);
                    tbPersionHunan.setType(2);
                    tbPersionHunan.setFlag(flag);
                    peopleCertService.insertPersonHunan(tbPersionHunan);
                }
            } else {
                MyXxlLogger.info("人员其他证书信息为空" + url);
            }
        }

        /**
         * 添加人员变更信息
         *
         * @param peopleChangeTable 人员历史变更情况表格
         * @param flag              人员唯一标识（ 姓名_性别_身份证）
         */
        void addPeopleChange(Elements peopleChangeTable, String flag) {
            TbPersonChange tbPersonChange;
            if (peopleChangeTable.size() > 2) {
                for (int i = 2; i < peopleChangeTable.size(); i++) {
                    tbPersonChange = new TbPersonChange();
                    tbPersonChange.setComName(peopleChangeTable.get(i).select("td").get(0).text());
                    tbPersonChange.setMajor(peopleChangeTable.get(i).select("td").get(1).text());
                    tbPersonChange.setChangeDate(peopleChangeTable.get(i).select("td").get(2).text());
                    tbPersonChange.setRemark(peopleChangeTable.get(i).select("td").get(3).text());
                    tbPersonChange.setFlag(flag);
                    peopleCertService.insertPeopleChange(tbPersonChange);
                }
            }
        }
    }

    /**
     * 根据企业id拆分企业资格证书资质
     *
     * @param companyId 企业id
     */
    void splitCompanyQualifications(Integer companyId) {
        //拆之前删除以前资质
        splitCertService.deleteCcompanyAptitudeByComId(companyId);

        List<TbCompanyQualification> companyQualificationList = splitCertService.getCompanyQualificationByComId(companyId);
        //遍历证书
        for (int i = 0; i < companyQualificationList.size(); i++) {
            int qualId = companyQualificationList.get(i).getPkid();
            String qualRange = companyQualificationList.get(i).getRange();
            int comId = companyQualificationList.get(i).getComId();
            //有资质
            if (com.silita.utils.StringUtils.isNotNull(qualRange)) {
                AllZh allZh;
                TbCompanyAptitude companyAptitude;
                List<TbCompanyAptitude> companyQualifications = new ArrayList<>();
                if (qualRange.contains("；")) {
                    //拆分资质
                    String[] qual = qualRange.split("；");
                    for (int j = 0; j < qual.length; j++) {
                        allZh = splitCertService.getAllZhByName(qual[j]);
                        if (allZh != null) {
                            companyAptitude = new TbCompanyAptitude();
                            companyAptitude.setQualId(qualId);
                            companyAptitude.setComId(comId);
                            companyAptitude.setAptitudeName(splitCertService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                            companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                            companyAptitude.setMainuuid(allZh.getMainuuid());
                            companyAptitude.setType(allZh.getType());
                            companyQualifications.add(companyAptitude);
                        }
                    }
                } else if (qualRange.contains(";")) {
                    //拆分资质
                    String[] qual = qualRange.split(";");
                    for (int j = 0; j < qual.length; j++) {
                        allZh = splitCertService.getAllZhByName(qual[j]);
                        if (allZh != null) {
                            companyAptitude = new TbCompanyAptitude();
                            companyAptitude.setQualId(qualId);
                            companyAptitude.setComId(comId);
                            companyAptitude.setAptitudeName(splitCertService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                            companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                            companyAptitude.setMainuuid(allZh.getMainuuid());
                            companyAptitude.setType(allZh.getType());
                            companyQualifications.add(companyAptitude);
                        }
                    }
                } else {
                    allZh = splitCertService.getAllZhByName(qualRange);
                    if (allZh != null) {
                        companyAptitude = new TbCompanyAptitude();
                        companyAptitude.setQualId(qualId);
                        companyAptitude.setComId(comId);
                        companyAptitude.setAptitudeName(splitCertService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                        companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                        companyAptitude.setMainuuid(allZh.getMainuuid());
                        companyAptitude.setType(allZh.getType());
                        companyQualifications.add(companyAptitude);
                    }
                }
                if (companyQualifications != null && companyQualifications.size() > 0) {
                    splitCertService.batchInsertCompanyAptitude(companyQualifications);
                }
            }
        }
    }

    /**
     * 更新合并后的资质到企业表
     *
     * @param companyId
     */
    void updateCompanyAptitudeRange(Integer companyId) {
        List<TbCompanyAptitude> tbCompanyAptitudes = splitCertService.listCompanyAptitudeByCompanyId(companyId);
        TbCompany tbCompany;
        TbCompanyAptitude tbCompanyAptitude;
        int comId;
        String allType;
        String allAptitudeUuid;
        StringBuilder sb;
        //遍历
        for (int i = 0; i < tbCompanyAptitudes.size(); i++) {
            tbCompanyAptitude = tbCompanyAptitudes.get(i);
            comId = tbCompanyAptitude.getComId();
            allType = tbCompanyAptitude.getType();
            allAptitudeUuid = tbCompanyAptitude.getAptitudeUuid();
            if (com.silita.utils.StringUtils.isNotNull(allType) && com.silita.utils.StringUtils.isNotNull(allAptitudeUuid)) {
                sb = new StringBuilder();
                String[] typeArr = allType.split(",");
                String[] aptitudeUuidArr = allAptitudeUuid.split(",");
                if (typeArr.length > 0 && aptitudeUuidArr.length > 0 && typeArr.length == aptitudeUuidArr.length) {
                    for (int j = 0; j < typeArr.length; j++) {
                        if (j == typeArr.length - 1) {
                            sb.append(typeArr[j]).append("/").append(aptitudeUuidArr[j]);
                        } else {
                            sb.append(typeArr[j]).append("/").append(aptitudeUuidArr[j]).append(",");
                        }
                    }
                }
                tbCompany = new TbCompany();
                tbCompany.setComId(comId);
                tbCompany.setRange(sb.toString());
                splitCertService.updateCompanyRangeByComId(tbCompany);
            }
        }
        MyXxlLogger.info("完成id为" + companyId + "的企业数据更新！");
    }

}