package com.silita.biaodaa.task;

import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.ICompanyService;
import com.silita.biaodaa.utils.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 遍历企业列表数据库逐个抓取
 * Created by 91567 on 2018/3/31.
 */
@Component
public class HuNanBuilderCompanyDetailTask {

    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private int min = 1;
    private int max = 5;
    private Random random = new Random();
    String dateRegex = "(\\d{4}-\\d{1,2}-\\d{1,2})";

    private static final int THREAD_NUMBER = 6;

    @Autowired
    private ICompanyService companyService;

    /**
     * 建筑
     */
    public void taskBuilderCompany() {
        int threadCount = THREAD_NUMBER;
        List<String> urls = new ArrayList<String>(11000);
        urls = companyService.getAllCompanyQualificationUrlByTab("建筑业企业");
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
     * z抓取程序具体实现
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
                System.out.println("该企业资质数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
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
                        String validDate;
                        String PersonQualificationUrl;
                        Map<String, Object> params = null;
                        Document peopleDetailDoc;
                        Connection peopleDetailConn;
                        //遍历人员列表url 进入详情页面
                        for (int i = 2; i < peopleList.size(); i++) {
                            validDate = peopleList.get(i).select("td").last().text();
                            PersonQualificationUrl = peopleList.get(i).select("a").first().absUrl("href");
                            params = new HashMap<>();
                            params.put("url", PersonQualificationUrl);
                            params.put("comId", companyId);
                            if (companyService.checkPersonQualificationExist(params)) {
                                System.out.println("已抓取这个公司的这本证书" + PersonQualificationUrl);
                            } else {
                                peopleDetailConn = Jsoup.connect(PersonQualificationUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                                peopleDetailDoc = peopleDetailConn.get();
                                if (peopleDetailConn.response().statusCode() == 200) {
                                    System.out.println(PersonQualificationUrl);
//                                    logger.error(PersonQualificationUrl);
                                    Elements peopleInfoTable = peopleDetailDoc.select("#table1");
                                    Elements peopleRegisteredTable = peopleDetailDoc.select("#tablelist").select("#table2").select("#ctl00_ContentPlaceHolder1_td_zzdetail").select("tr");
                                    Elements peopleOtherQualificationsTable = peopleDetailDoc.select("#tablelist").select("#table3").select("#ctl00_ContentPlaceHolder1_td_rylist").select("tr");
                                    Elements peopleChangeTable = peopleDetailDoc.select("#tablelist").select("#table6").select("#ctl00_ContentPlaceHolder1_jzs2_history").select("tr");
                                    //添加人员基本信息后 返回主键
                                    Integer pkid = addPeopleInfo(peopleInfoTable, companyId);
                                    //有余字段（人员姓名）存入人员资质表用于业务查询
                                    String PersionName = peopleInfoTable.select("#ctl00_ContentPlaceHolder1_lbl_xm").text();
                                    //注册执业信息
                                    addPeopleRegistered(peopleRegisteredTable, PersonQualificationUrl, companyId, pkid, validDate, PersionName);
                                    //其他资格信息
                                    addPeopleOtherCert(peopleOtherQualificationsTable, PersonQualificationUrl, companyId, pkid, PersionName);
                                    //人员变更信息
                                    addPeopleChange(peopleChangeTable, pkid);
                                } else {
                                    TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                                    tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                                    tbExceptionUrl.setExceptionUrl(PersonQualificationUrl);
                                    tbExceptionUrl.setExceptionMsg("获取人员详情失败");
                                    tbExceptionUrl.setTab("建筑业企业");
                                    companyService.insertException(tbExceptionUrl);
                                }
                            }
                            //随机暂停几秒
                            Thread.sleep(100 * (random.nextInt(max) % (max - min + 1)));
                        }
                    } else {
                        System.out.println("该企业注册人员数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                    }
                } else {
                    TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                    tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                    tbExceptionUrl.setExceptionUrl(peopleListUrl);
                    tbExceptionUrl.setExceptionMsg("获取人员证书列表页失败" + cookies.toString());
                    tbExceptionUrl.setTab("建筑业企业");
                    companyService.insertException(tbExceptionUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }

        /**
         * 人员基本信息
         *
         * @param eles 表单数据
         */
        Integer addPeopleInfo(Elements eles, Integer companyId) {
            TbPerson tbPerson = new TbPerson();
            tbPerson.setName(eles.select("#ctl00_ContentPlaceHolder1_lbl_xm").text());
            tbPerson.setNation(eles.select("#ctl00_ContentPlaceHolder1_lbl_mc").text());
            tbPerson.setSex(eles.select("#ctl00_ContentPlaceHolder1_lbl_xb").text());
            tbPerson.setIdCard(eles.select("#ctl00_ContentPlaceHolder1_lbl_sfzh").text());
            tbPerson.setEducation(eles.select("#ctl00_ContentPlaceHolder1_lbl_xl").text());
            tbPerson.setDegree(eles.select("#ctl00_ContentPlaceHolder1_lbl_xw").text());
            tbPerson.setCompanyId(companyId);
            return companyService.insertPersionInfo(tbPerson);
        }

        /**
         * 人员执业信息
         * 根据证书编号公司名称判断是否更新
         *
         * @param eles                   表单数据
         * @param PersonQualificationUrl 人员证书url
         * @param companyId              公司id
         * @param peopleId               人员id
         */
        void addPeopleRegistered(Elements eles, String PersonQualificationUrl, Integer companyId, Integer peopleId, String validDate, String persionName) {
            TbPersonQualification tbPersonQualification = null;
            if (eles.size() > 1) {
                for (int i = 1; i < eles.size(); i++) {
                    tbPersonQualification = new TbPersonQualification();
                    tbPersonQualification.setCategory(eles.get(i).select("td").get(0).text());
                    tbPersonQualification.setComName(eles.get(i).select("td").get(1).text());
                    tbPersonQualification.setCertNo(eles.get(i).select("td").get(2).text());
                    tbPersonQualification.setMajor(eles.get(i).select("td").get(3).text());
                    tbPersonQualification.setCertDate(eles.get(i).select("td").get(4).text());
                    tbPersonQualification.setValidDate(validDate);
                    tbPersonQualification.setUrl(PersonQualificationUrl);
                    tbPersonQualification.setInnerid(PersonQualificationUrl.substring(PersonQualificationUrl.indexOf("=") + 1));
                    tbPersonQualification.setPerId(peopleId);
                    tbPersonQualification.setComId(companyId);
                    tbPersonQualification.setType(1);
                    tbPersonQualification.setPerName(persionName);
                    companyService.insertPersonQualification(tbPersonQualification);
                }
            } else {
                System.out.println("人员执业信息为空" + PersonQualificationUrl);
            }
        }

        /**
         * 人员其他证书信息（如安全证书）
         * 注意证书有效期字符串处理(2013-6-28(有效期：2019-6-28))
         *
         * @param eles                   表单数据
         * @param PersonQualificationUrl 人员证书url
         * @param companyId              公司id
         * @param peopleId               人员id
         */
        void addPeopleOtherCert(Elements eles, String PersonQualificationUrl, Integer companyId, Integer peopleId, String persionName) {
            if (eles.size() > 1) {
                TbPersonQualification tbPersonQualification = null;
                for (int i = 1; i < eles.size(); i++) {
                    tbPersonQualification = new TbPersonQualification();
                    tbPersonQualification.setCategory(eles.get(i).select("td").get(0).text());
                    tbPersonQualification.setComName(eles.get(i).select("td").get(1).text());
                    tbPersonQualification.setCertNo(eles.get(i).select("td").get(2).text());
                    tbPersonQualification.setMajor(eles.get(i).select("td").get(3).text());
//                    2013-6-28(有效期：2019-6-28)
                    String dateStr = eles.get(i).select("td").get(4).text();
                    if (dateStr.contains("有效期")) {
                        tbPersonQualification.setCertDate(dateStr.substring(0, dateStr.indexOf("有效期") - 1));
                        tbPersonQualification.setValidDate(dateStr.substring(dateStr.indexOf("有效期") + 4, dateStr.length() - 1));
                    } else {
                        tbPersonQualification.setCertDate(dateStr);
                    }
                    tbPersonQualification.setUrl(PersonQualificationUrl);
                    tbPersonQualification.setInnerid(PersonQualificationUrl.substring(PersonQualificationUrl.indexOf("=") + 1));
                    tbPersonQualification.setPerId(peopleId);
                    tbPersonQualification.setComId(companyId);
                    tbPersonQualification.setType(2);
                    tbPersonQualification.setPerName(persionName);
                    companyService.insertPersonQualification(tbPersonQualification);
                }
            } else {
                System.out.println("人员其他证书信息为空" + PersonQualificationUrl);
            }
        }
    }

    /**
     * 添加人员变更信息
     *
     * @param eles     表单数据
     * @param peopleId 人员id
     */
    void addPeopleChange(Elements eles, Integer peopleId) {
//        List<TbPersonChange> tbPersonChanges;
        TbPersonChange tbPersonChange;
        if (eles.size() > 2) {
//            tbPersonChanges = new  ArrayList<TbPersonChange>();
            for (int i = 2; i < eles.size(); i++) {
                tbPersonChange = new TbPersonChange();
                tbPersonChange.setComName(eles.get(i).select("td").get(0).text());
                tbPersonChange.setMajor(eles.get(i).select("td").get(1).text());
                tbPersonChange.setChangeDate(eles.get(i).select("td").get(2).text());
                tbPersonChange.setRemark(eles.get(i).select("td").get(3).text());
                tbPersonChange.setPerId(peopleId);
//                tbPersonChanges.add(tbPersonChange);
                companyService.insertPeopleChange(tbPersonChange);
            }
//            companyService.batchInsertPeopleChange(tbPersonChanges);
        }
    }

    /**
     * 进入项目列表后
     * 再便利列表抓项目详情 需要公司cookie
     *
     * @param cookies   cookie
     * @param companyId 公司id
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
                        if (companyService.checkProjectBuildExist(params)) {
                            System.out.println("已抓取这个施工项目" + projectBuildUrl);
                        } else {
                            projectBuildDetailConn = Jsoup.connect(projectBuildUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                            projectBuildDetailDoc = projectBuildDetailConn.get();
                            if (projectBuildDetailConn.response().statusCode() == 200) {
                                if (StringUtils.isNotNull(projectBuildDetailDoc.select("#table1").text())) {
                                    System.out.println(projectBuildUrl);
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
                                    System.out.println("很抱歉，暂时无法访问工程项目信息" + projectBuildUrl);
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
                    System.out.println("该企业项目数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
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
                tbProject.setProWhere(projectTable.select("#ctl00_ContentPlaceHolder1_lbl_sz").text());
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
                return companyService.insertProjectInfo(tbProject);
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
        return companyService.insertProjectBuild(tbProjectBuild);
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
            TbPersonProject tbPersonProject;
            for (int i = 2; i < eles.size() - 1; i++) {
                if (StringUtils.isNotNull(eles.get(i).text())) {
                    tbPersonProject = new TbPersonProject();
                    tbPersonProject.setName(eles.get(i).select("td").get(0).text());
                    tbPersonProject.setRole(eles.get(i).select("td").get(1).text());
                    tbPersonProject.setCertNo(eles.get(i).select("td").get(2).text());
                    tbPersonProject.setSafeNo(eles.get(i).select("td").get(3).text());
                    tbPersonProject.setStatus(eles.get(i).select("td").get(4).text());
                    tbPersonProject.setType("build");
                    String peopleDetailUrl = eles.get(i).select("td").get(0).select("a").attr("href");
                    tbPersonProject.setInnerid(peopleDetailUrl.substring(peopleDetailUrl.indexOf("=") + 1));
                    tbPersonProject.setPid(projectBuilderId);
                    companyService.insertPersonProject(tbPersonProject);
                }
            }
        } else {
            System.out.println("无项目部人员（施工）" + projectBuildUrl);
        }
    }
}
