package com.silita.biaodaa.task;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.common.xxl.BaseTask;
import com.silita.biaodaa.common.xxl.MyXxlLogger;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.ICompanyService;
import com.silita.biaodaa.service.IPeopleCertService;
import com.silita.biaodaa.service.IProjectService;
import com.silita.biaodaa.utils.StringUtils;
import com.xxl.job.core.handler.annotation.JobHander;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 遍历企业列表数据库逐个抓取
 * Created by 91567 on 2018/3/31.
 */
@Component
@JobHander(value = "HuNanBuilderCompanyDetailTask")
public class HuNanBuilderCompanyDetailTask extends BaseTask {

    @Override
    public void runTask(JSONObject jsonObject) throws Exception {
        taskBuilderCompany();
    }

    private int min = 1;
    private int max = 5;
    private Random random = new Random();
    String dateRegex = "(\\d{4}-\\d{1,2}-\\d{1,2})";
    private static final int THREAD_NUMBER = 1;

    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IPeopleCertService peopleCertService;
    @Autowired
    private IProjectService projectService;

    /**
     * 建筑企业增量抓取
     */
    public void taskBuilderCompany() {
        int threadCount = THREAD_NUMBER;
        List<String> urls = new ArrayList<String>(11000);
        urls = companyService.listCompanyQualificationUrlByTab("建筑业企业");
        MyXxlLogger.info("本次增量抓取建筑业企业数：" + urls.size());
        int every = urls.size() % threadCount == 0 ? urls.size() / threadCount : (urls.size() / threadCount) + 1;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(new HuNanCompanyDetailRun(i * every, (i + 1) * every, latch, urls)).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 抓取程序具体实现
     * 可以用多线程抓
     */
    class HuNanCompanyDetailRun implements Runnable {
        private int startNum;
        private int endNum;
        private CountDownLatch latch;
        private List<String> CompanyQualificationUrls;

        public HuNanCompanyDetailRun(int startNum, int endNum, CountDownLatch latch, List<String> CompanyQualificationUrls) {
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
            try {
                for (int i = startNum; i < endNum; i++) {
                    String CompanyQualificationUrl = CompanyQualificationUrls.get(i);
                    companyDetailConn = Jsoup.connect(CompanyQualificationUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                    companyDetailDoc = companyDetailConn.get();
                    if (companyDetailConn.response().statusCode() == 200) {
                        cookies = companyDetailConn.response().cookies();
                        String corpid = CompanyQualificationUrl.substring(CompanyQualificationUrl.indexOf("=") + 1);
                        Elements companyInfoTable = companyDetailDoc.select("#table1");
                        Elements CompanyAptitudeTable = companyDetailDoc.select("#tablelist").select("#table2").select("#ctl00_ContentPlaceHolder1_td_zzdetail").select("table");
                        //###添加企业基本信息后 返回主键###
                        Integer comId = addCompanyInfo(companyInfoTable);
                        //########更新企业资质证书#########
                        addCompanyAptitude(CompanyAptitudeTable, corpid, comId);
                        //##########抓取人员start##########
                        getPepleList(cookies, CompanyQualificationUrl, comId);
                        //##########抓取项目start##########
                        getProjectList(cookies, CompanyQualificationUrl, comId);
                    } else {
                        TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                        tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                        tbExceptionUrl.setExceptionMsg("获取企业详情信息失败!");
                        tbExceptionUrl.setTab("建筑业企业");
                        companyService.insertException(tbExceptionUrl);
                    }
                    //随机暂停几秒
                    Thread.sleep(1000 * (random.nextInt(max) % (max - min + 1)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
            latch.countDown();
        }

        /**
         * 抓取企业基本信息
         * 注意要返回添加的企业id用于企业资质证书外键
         *
         * @param eles 表单数据
         */
        Integer addCompanyInfo(Elements eles) {
            TbCompany company = new TbCompany();
            company.setComName(eles.select("#ctl00_ContentPlaceHolder1_lbl_qymc").text());
            company.setOrgCode(eles.select("#ctl00_ContentPlaceHolder1_lbl_jgdm").text());
            company.setBusinessNum(eles.select("#ctl00_ContentPlaceHolder1_lbl_yyzz").text());
            company.setRegisAddress("湖南省" + eles.select("#ctl00_ContentPlaceHolder1_lbl_sz").text());
            company.setComAddress(eles.select("#ctl00_ContentPlaceHolder1_lbl_dwdz").text());
            company.setLegalPerson(eles.select("#ctl00_ContentPlaceHolder1_lbl_fddbr").text());
            company.setEconomicType(eles.select("#ctl00_ContentPlaceHolder1_lbl_jjlx").text());
            company.setRegisCapital(eles.select("#ctl00_ContentPlaceHolder1_lbl_zczb").text());
            return companyService.insertCompanyInfo(company);
        }

        /**
         * 抓取企业资质信息
         *
         * @param eles      表单数据
         * @param corpid    资质证书内部id
         * @param companyId 公司id
         */
        void addCompanyAptitude(Elements eles, String corpid, Integer companyId) {
            if (eles.size() > 0) {
                //有的下面有多个资质
                for (int i = 0; i < eles.size(); i++) {
                    if (eles.get(i).select("tr").get(0).select("td").get(1).text().contains("建筑")) {
                        TbCompanyQualification tbCompanyQualification = new TbCompanyQualification();
                        tbCompanyQualification.setQualType(eles.get(i).select("tr").get(0).select("td").get(1).text());
                        tbCompanyQualification.setCertNo(eles.get(i).select("tr").get(1).select("td").get(1).text());
                        tbCompanyQualification.setCertOrg(eles.get(i).select("tr").get(1).select("td").get(3).text());
                        tbCompanyQualification.setCertDate(eles.get(i).select("tr").get(3).select("td").get(1).text());
                        tbCompanyQualification.setValidDate(eles.get(i).select("tr").get(3).select("td").get(3).text());
                        tbCompanyQualification.setRange(eles.get(i).select("tr").get(4).select("td").get(1).text());
                        tbCompanyQualification.setComId(companyId);
                        tbCompanyQualification.setCorpid(corpid);
                        companyService.updateCompanyQualificationUrlByCorpid(tbCompanyQualification);
                    }
                }
            } else {
                MyXxlLogger.info("该企业资质数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
            }
        }


        /**
         * 进入公司人员证书列表后
         * 再便利列表抓人员详情 需要公司cookie
         *
         * @param cookies   cookie
         * @param companyId 公司id
         */
        void getPepleList(Map<String, String> cookies, String CompanyQualificationUrl, Integer companyId) {
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
                            if (!org.springframework.util.StringUtils.isEmpty(peopleList.get(i).select("td").get(5).text().replaceAll("[^0-9]", ""))) {
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
                        MyXxlLogger.info("该企业注册人员数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                    }
                } else {
                    MyXxlLogger.info("获取人员证书列表页失败" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
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
                        if (org.springframework.util.StringUtils.isEmpty(temp.getMajor())) {
                            temp.setMajor(peopleRegisteredTable.get(i).select("td").get(3).text());
                            break;
                        }
                    }

                }
            }
            String flag = tbPersionHunan.getName() + "_" + tbPersionHunan.getSex() + "_" + tbPersionHunan.getIdCard();
            tbPersionHunan.setFlag(flag);
            //执业印章号为空，取模态窗口数据补齐，同时更新专业
            if (org.springframework.util.StringUtils.isEmpty(tbPersionHunan.getSealNo())) {
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


        /**
         * 进入项目列表后
         * 再便利列表抓项目详情 需要公司cookie
         *
         * @param cookies                 cookie
         * @param CompanyQualificationUrl 证书url
         * @param companyId               公司id
         */
        void getProjectList(Map<String, String> cookies, String CompanyQualificationUrl, Integer companyId) {
            Document projectListDoc;
            Connection projectListConn;
            String peopleListUrl = "http://qyryjg.hunanjz.com/public/EnterpriseProject.ashx";
            try {
                //进入公司项目列表
                projectListConn = Jsoup.connect(peopleListUrl).userAgent("Mozilla").timeout(120000 * 60).ignoreHttpErrors(true);
                projectListConn.cookies(cookies);
                projectListDoc = projectListConn.get();
                if (projectListConn.response().statusCode() == 200) {
                    Elements projectList = projectListDoc.select("table").select("tr");
                    if (projectList.size() > 1) {
                        String proType;
                        String projectBuildUrl;
                        Map<String, Object> params;
                        Document projectBuildDetailDoc;
                        Connection projectBuildDetailConn;
                        //遍历公司项目列表url 进入详情页面
                        for (int i = 1; i < projectList.size(); i++) {
                            proType = projectList.get(i).select("td").first().text();
                            projectBuildUrl = projectList.get(i).select("a").first().absUrl("href");
                            params = new HashMap<>();
                            //施工合同段信息内部id
                            String bdxh = projectBuildUrl.substring(projectBuildUrl.indexOf("=") + 1);
                            params.put("bdxh", bdxh);
                            params.put("comId", companyId);
                            if (projectService.checkProjectBuildExist(params)) {
                                MyXxlLogger.info("已抓取这个施工项目" + projectBuildUrl);
                            } else {
                                projectBuildDetailConn = Jsoup.connect(projectBuildUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                                projectBuildDetailDoc = projectBuildDetailConn.get();
                                if (projectBuildDetailConn.response().statusCode() == 200) {
                                    if (StringUtils.isNotNull(projectBuildDetailDoc.select("#table1").text())) {
                                        MyXxlLogger.info(projectBuildUrl);
//                                    logger.error(projectBuildUrl);
                                        Elements projectBuildDetailTable = projectBuildDetailDoc.select("#table1");
                                        Elements projectBuilderPeopleTable = projectBuildDetailDoc.select("#ctl00_ContentPlaceHolder1_td_rylist").select("table").select("tr");
                                        String projectInfoDetaiUrl = projectBuildDetailTable.select("a").first().absUrl("href");
                                        //进入项目详情添加项目基本信息
                                        Integer projectId = addProjectInfo(projectInfoDetaiUrl, CompanyQualificationUrl);
                                        //施工合同段信息
                                        int projectBuilderId = addProjectBuild(projectBuildDetailTable, companyId, projectId, bdxh, proType);
                                        //添加项目部人员（施工）
                                        addProjectPeople(projectBuilderPeopleTable, projectBuilderId, projectBuildUrl);
                                    } else {
                                        MyXxlLogger.info("很抱歉，暂时无法访问工程项目信息" + projectBuildUrl);
                                    }
                                } else {
                                    TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                                    tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                                    tbExceptionUrl.setExceptionUrl(projectBuildUrl);
                                    tbExceptionUrl.setExceptionMsg("获取项目详情失败");
                                    tbExceptionUrl.setTab("建筑业企业");
                                    companyService.insertException(tbExceptionUrl);
                                }
                            }
                            //随机暂停几秒
                            Thread.sleep(100 * (random.nextInt(max) % (max - min + 1)));
                        }
                    } else {
                        MyXxlLogger.info("该企业项目数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                    }
                } else {
                    TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                    tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                    tbExceptionUrl.setExceptionUrl(peopleListUrl);
                    tbExceptionUrl.setExceptionMsg("获取企业项目列表页失败" + cookies.toString());
                    tbExceptionUrl.setTab("建筑业企业");
                    companyService.insertException(tbExceptionUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }

        /**
         * 根据项目详情Url取得项目基本信息
         *
         * @param projectInfoUrl 项目详情Url
         */
        Integer addProjectInfo(String projectInfoUrl, String CompanyQualificationUrl) {
            Document projectInfoDoc;
            Connection projectInfoConn;
            try {
                //进入项目基本信息
                projectInfoConn = Jsoup.connect(projectInfoUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                projectInfoDoc = projectInfoConn.get();
                if (projectInfoConn.response().statusCode() == 200) {
                    Elements projectTable = projectInfoDoc.select("#table1");
                    TbProject tbProject = new TbProject();
                    tbProject.setProName(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_gcmc").text());
                    tbProject.setProNo(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_prjnum").text());
                    tbProject.setProOrg(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_jsdw").text());
                    tbProject.setProWhere("湖南省" + projectTable.select("#ctl00_ContentPlaceHolder1_lbl_sz").text());
                    tbProject.setProAddress(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_gcdd").text());
                    tbProject.setInvestAmount(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_ztze").text().replaceAll("[\\u4e00-\\u9fa5]", ""));
                    tbProject.setApprovalNum(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_lxwh").text());
                    tbProject.setProType(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_gclb").text());
                    tbProject.setBuildType(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_jsxz").text());
                    tbProject.setProScope(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_gcgm").text());
                    tbProject.setLandLicence(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_jsydxkz").text());
                    tbProject.setPlanLicence(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_gcghxkz").text());
                    tbProject.setMoneySource(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_zjsm").text());
                    tbProject.setXmid(projectInfoUrl.substring(projectInfoUrl.indexOf("=") + 1));
                    return projectService.insertProjectInfo(tbProject);
                } else {
                    TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                    tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                    tbExceptionUrl.setExceptionUrl(projectInfoUrl);
                    tbExceptionUrl.setExceptionMsg("获取项目基本信息失败");
                    tbExceptionUrl.setTab("建筑业企业");
                    companyService.insertException(tbExceptionUrl);
                    return -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            } finally {
            }
        }

        /**
         * 添加施工合同段信息
         *
         * @param eles      表格数据
         * @param companyId 公司id
         * @param projectId 项目id
         * @param bdxh      内部id
         */
        int addProjectBuild(Elements eles, Integer companyId, Integer projectId, String bdxh, String proType) {
            TbProjectBuild tbProjectBuild = new TbProjectBuild();
            tbProjectBuild.setProName(eles.select("#ctl00_ContentPlaceHolder1_hl_gcmc").text());
            tbProjectBuild.setBName(eles.select("#ctl00_ContentPlaceHolder1_lbl_bdmc").text());
            tbProjectBuild.setBScope(eles.select("#ctl00_ContentPlaceHolder1_lbl_bdgm").text());
            tbProjectBuild.setBOrg(eles.select("#ctl00_ContentPlaceHolder1_td_sgdw").text());
            //中标备案情况
            String bidRemark = eles.select("#ctl00_ContentPlaceHolder1_lbl_zbba").text();
            tbProjectBuild.setBidRemark(bidRemark);
            if (bidRemark.contains("中标价格") && bidRemark.contains("万元")) {
                tbProjectBuild.setBidPrice(bidRemark.substring(bidRemark.indexOf("中标价格") + 5, bidRemark.indexOf("万元")));
            }
            tbProjectBuild.setContractRemark(eles.select("#ctl00_ContentPlaceHolder1_lbl_htba").text());
            tbProjectBuild.setBLicence(eles.select("#ctl00_ContentPlaceHolder1_lbl_sgxkzh").text());
            tbProjectBuild.setLicenceDate(eles.select("#ctl00_ContentPlaceHolder1_lbl_sgxkzrq").text());
            //竣工验收备案情况
            String completeRemark = eles.select("#ctl00_ContentPlaceHolder1_lbl_jgysbh").text();
            tbProjectBuild.setCompleteRemark(completeRemark);
            Pattern datePat = Pattern.compile(dateRegex);
            Matcher dateMat = datePat.matcher(completeRemark);
            while (dateMat.find()) {
                //竣工时间
                tbProjectBuild.setCompleteDate(dateMat.group());
            }
            tbProjectBuild.setProType(proType);
            tbProjectBuild.setSubContrace(eles.select("#ctl00_ContentPlaceHolder1_td_fblist").text());
            tbProjectBuild.setBdxh(bdxh);
            tbProjectBuild.setComId(companyId);
            tbProjectBuild.setProId(projectId);
            return projectService.insertProjectBuild(tbProjectBuild);
        }

        /**
         * 添加项目部人员
         *
         * @param eles             表格数据
         * @param projectBuilderId 项目施工id
         * @param projectBuildUrl  项目施工详情Url
         */
        void addProjectPeople(Elements eles, Integer projectBuilderId, String projectBuildUrl) {
            if (eles.size() > 2) {
                Document PeopleDetailDoc;
                Connection PeopleDetailConn;
                TbPersonProject tbPersonProject;
                try {
                    for (int i = 2; i < eles.size() - 1; i++) {
                        if (StringUtils.isNotNull(eles.get(i).text())) {
                            tbPersonProject = new TbPersonProject();
                            tbPersonProject.setName(eles.get(i).select("td").get(0).text());
                            tbPersonProject.setRole(eles.get(i).select("td").get(1).text());
                            tbPersonProject.setCertNo(eles.get(i).select("td").get(2).text());
                            tbPersonProject.setSafeNo(eles.get(i).select("td").get(3).text());
                            tbPersonProject.setStatus(eles.get(i).select("td").get(4).text());
                            tbPersonProject.setType("build");
                            String peopleDetailUrl = eles.get(i).select("td").get(0).select("a").first().absUrl("href");
                            PeopleDetailConn = Jsoup.connect(peopleDetailUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                            PeopleDetailDoc = PeopleDetailConn.get();
                            //判断项目部人员url链接是否有效
                            if(!org.springframework.util.StringUtils.isEmpty(PeopleDetailDoc.select("#ctl00_ContentPlaceHolder1_lbl_xm").text())) {
                                tbPersonProject.setInnerid(peopleDetailUrl.substring(peopleDetailUrl.indexOf("=") + 1));
                            }
                            tbPersonProject.setPid(projectBuilderId);
                            projectService.insertPersonProject(tbPersonProject);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                MyXxlLogger.info("无项目部人员（施工）" + projectBuildUrl);
            }
        }
    }
}
