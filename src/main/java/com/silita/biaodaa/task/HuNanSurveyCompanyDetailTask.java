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

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * 遍历勘察企业列表数据库逐个抓取
 * Created by 91567 on 2018/3/31.
 */
@Component
public class HuNanSurveyCompanyDetailTask {

    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private int min = 1;
    private int max = 5;
    private Random random = new Random();

    private static final int THREAD_NUMBER = 1;

    @Autowired
    private ICompanyService companyService;

    /**
     * 勘察
     */
    public void taskSurveyCompany() {
        int threadCount = THREAD_NUMBER;
        List<String> urls = new ArrayList<String>(300);
        urls = companyService.getAllCompanyQualificationUrlByTab("工程勘察企业");
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
                        //添加企业基本信息后 返回主键
                        Integer comId = addCompanyInfo(companyInfoTable);
                        //更新企业资质证书
                        addCompanyAptitude(CompanyAptitudeTable, corpid, comId);
                        //##########抓取人员start##########
                        getPepleList(cookies, comId);
                        //##########抓取项目start##########
                        getProjectList(cookies, comId);
                    } else {
                        System.out.println("获取企业详情信息失败！" + CompanyQualificationUrl);
                    }
                    //随机暂停几秒
                    Thread.sleep(1000 * (random.nextInt(max) % (max - min + 1)));
                }
                //##########拆分资质##############
                splitCompanyQualifications();
                //##########添加企业资质##########
//                updateCompanyAptitudeRange();
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
            company.setRegisAddress(eles.select("#ctl00_ContentPlaceHolder1_lbl_sz").text());
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
                TbCompanyQualification tbCompanyQualification = new TbCompanyQualification();
                tbCompanyQualification.setQualType(eles.select("tr").get(0).select("td").get(1).text());
                tbCompanyQualification.setCertNo(eles.select("tr").get(1).select("td").get(1).text());
                tbCompanyQualification.setCertOrg(eles.select("tr").get(1).select("td").get(3).text());
                tbCompanyQualification.setCertDate(eles.select("tr").get(3).select("td").get(1).text());
                tbCompanyQualification.setValidDate(eles.select("tr").get(3).select("td").get(3).text());
                tbCompanyQualification.setRange(eles.select("tr").get(4).select("td").get(1).text());
                tbCompanyQualification.setComId(companyId);
                tbCompanyQualification.setCorpid(corpid);
                companyService.updateCompanyQualificationUrlByCorpid(tbCompanyQualification);
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
        void getPepleList(Map<String, String> cookies, Integer companyId) {
            Document peopleListDoc;
            Connection peopleListConn;
            String peopleListUrl = "http://qyryjg.hunanjz.com/public/EnterpriseRegPerson.ashx";
            try {
                //进入人员证书列表
                peopleListConn = Jsoup.connect(peopleListUrl).userAgent("Mozilla").timeout(10000 * 60).ignoreHttpErrors(true);
                peopleListConn.cookies(cookies);
                peopleListDoc = peopleListConn.get();
                if (peopleListConn.response().statusCode() == 200) {
                    Elements peopleList = peopleListDoc.select("table").select("tr");
                    if (peopleList.size() > 2) {
                        Document peopleDetailDoc;
                        Connection peopleDetailConn;
                        //遍历人员列表url 进入详情页面
                        for (int i = 2; i < peopleList.size(); i++) {
                            String PersonQualificationUrl = peopleList.get(i).select("a").first().absUrl("href");
                            peopleDetailConn = Jsoup.connect(PersonQualificationUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                            peopleDetailDoc = peopleDetailConn.get();
                            if (peopleDetailConn.response().statusCode() == 200) {
                                logger.error(PersonQualificationUrl);
                                Elements peopleInfoTable = peopleDetailDoc.select("#table1");
                                Elements peopleRegisteredTable = peopleDetailDoc.select("#tablelist").select("#table2").select("#ctl00_ContentPlaceHolder1_td_zzdetail").select("tr");
                                Elements peopleOtherQualificationsTable = peopleDetailDoc.select("#tablelist").select("#table3").select("#ctl00_ContentPlaceHolder1_td_rylist").select("tr");
                                //添加人员基本信息后 返回主键
                                Integer pkid = addPeopleInfo(peopleInfoTable);
                                //注册执业信息
                                addPeopleRegistered(peopleRegisteredTable, PersonQualificationUrl, companyId, pkid);
                                //其他资格信息
                                addProjectOtherCert(peopleOtherQualificationsTable, PersonQualificationUrl, companyId, pkid);

                            } else {
                                System.out.println("获取人员详情失败" + PersonQualificationUrl);
                            }
                        }
                    } else {
                        System.out.println("该企业注册人员数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                    }
                } else {
                    System.out.println("获取人员证书列表页失败" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }

        /**
         * 人员基本信息
         *
         * @param eles 表单数据
         */
        Integer addPeopleInfo(Elements eles) {
            TbPerson tbPerson = new TbPerson();
            tbPerson.setName(eles.select("#ctl00_ContentPlaceHolder1_lbl_xm").text());
            tbPerson.setNation(eles.select("#ctl00_ContentPlaceHolder1_lbl_mc").text());
            tbPerson.setSex(eles.select("#ctl00_ContentPlaceHolder1_lbl_xb").text());
            tbPerson.setIdCard(eles.select("#ctl00_ContentPlaceHolder1_lbl_sfzh").text());
            tbPerson.setEducation(eles.select("#ctl00_ContentPlaceHolder1_lbl_xl").text());
            tbPerson.setDegree(eles.select("#ctl00_ContentPlaceHolder1_lbl_xw").text());
            return companyService.insertPersionInfo(tbPerson);
        }

        /**
         * 人员执业信息
         * 根据证书编号判断是否更新
         *
         * @param eles                   表单数据
         * @param PersonQualificationUrl 人员证书url
         * @param companyId              公司id
         * @param peopleId               人员id
         */
        void addPeopleRegistered(Elements eles, String PersonQualificationUrl, Integer companyId, Integer peopleId) {
            TbPersonQualification tbPersonQualification = null;
            if (eles.size() > 1) {
                for (int i = 1; i < eles.size(); i++) {
                    tbPersonQualification = new TbPersonQualification();
                    tbPersonQualification.setCategory(eles.get(i).select("td").get(0).text());
                    tbPersonQualification.setComName(eles.get(i).select("td").get(1).text());
                    tbPersonQualification.setCertNo(eles.get(i).select("td").get(2).text());
                    tbPersonQualification.setMajor(eles.get(i).select("td").get(3).text());
                    tbPersonQualification.setCertDate(eles.get(i).select("td").get(4).text());
                    tbPersonQualification.setUrl(PersonQualificationUrl);
                    tbPersonQualification.setInnerid(PersonQualificationUrl.substring(PersonQualificationUrl.indexOf("=") + 1));
                    tbPersonQualification.setPerId(peopleId);
                    tbPersonQualification.setComId(companyId);
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
        void addProjectOtherCert(Elements eles, String PersonQualificationUrl, Integer companyId, Integer peopleId) {
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
                    companyService.insertPersonQualification(tbPersonQualification);
                }
            } else {
                System.out.println("人员其他证书信息为空" + PersonQualificationUrl);
            }
        }
    }


    /**
     * 进入项目列表后
     * 再便利列表抓项目详情 需要公司cookie
     *
     * @param cookies   cookie
     * @param companyId 公司id
     */
    void getProjectList(Map<String, String> cookies, Integer companyId) {
        Document projectListDoc;
        Connection projectListConn;
        String peopleListUrl = "http://qyryjg.hunanjz.com/public/EnterpriseProject.ashx";
        try {
            //进入公司项目列表
            projectListConn = Jsoup.connect(peopleListUrl).userAgent("Mozilla").timeout(10000 * 60).ignoreHttpErrors(true);
            projectListConn.cookies(cookies);
            projectListDoc = projectListConn.get();
            if (projectListConn.response().statusCode() == 200) {
                Elements projectList = projectListDoc.select("table").select("tr");
                if (projectList.size() > 1) {
                    Document projectBuildDetailDoc;
                    Connection projectBuildDetailConn;
                    //遍历公司项目列表url 进入详情页面
                    for (int i = 1; i < projectList.size(); i++) {
                        String projectBuildUrl = projectList.get(i).select("a").first().absUrl("href");
                        projectBuildDetailConn = Jsoup.connect(projectBuildUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                        projectBuildDetailDoc = projectBuildDetailConn.get();
                        if (projectBuildDetailConn.response().statusCode() == 200) {
                            if (StringUtils.isNotNull(projectBuildDetailDoc.select("#table1").text())) {
                                logger.error(projectBuildUrl);
                                Elements projectBuildDetailTable = projectBuildDetailDoc.select("#table1");
                                Elements projectBuilderPeopleTable = projectBuildDetailDoc.select("#ctl00_ContentPlaceHolder1_td_rylist").select("table").select("tr");
                                String projectInfoDetaiUrl = projectBuildDetailTable.select("a").first().absUrl("href");
                                //添加项目基本信息
                                Integer projectId = addProjectInfo(projectInfoDetaiUrl);
                                String bdxh = projectInfoDetaiUrl.substring(projectInfoDetaiUrl.indexOf("=") + 1);
                                //添加施工图审查信息（勘察）
                                int projectSurveyId = addProjectSurvey(projectBuildDetailTable, companyId, projectId, bdxh);
                                //勘察人员名单
                                addSurveyPeople(projectBuilderPeopleTable, projectSurveyId, projectBuildUrl);
                            } else {
                                System.out.println("很抱歉，暂时无法访问工程项目信息" + projectBuildUrl);
                            }
                        } else {
                            System.out.println("获取人员详情失败" + projectBuildUrl);
                        }
                    }
                } else {
                    System.out.println("该企业项目数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                }
            } else {
                System.out.println("获取企业项目列表页失败" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 根据项目详情Url取得项目基本信息
     *
     * @param projectInfoUrl 项目详情Url
     */
    Integer addProjectInfo(String projectInfoUrl) {
        Document projectInfoDoc;
        Connection projectInfoConn;
        try {
            //进入项目基本信息
            projectInfoConn = Jsoup.connect(projectInfoUrl).userAgent("Mozilla").timeout(10000 * 60).ignoreHttpErrors(true);
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
                System.out.println("获取项目基本信息失败" + projectInfoUrl);
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
        }
    }

    /**
     * 施工图审查信息（勘察）
     *
     * @param eles      表格数据
     * @param companyId 公司id
     * @param projectId 项目id
     * @param sgtxh      内部id
     */
    int addProjectSurvey(Elements eles, Integer companyId, Integer projectId, String sgtxh) {
        TbProjectDesign tbProjectDesign = new TbProjectDesign();
        tbProjectDesign.setProName(eles.select("#ctl00_ContentPlaceHolder1_hl_gcmc").text());
        tbProjectDesign.setProScope(eles.select("#ctl00_ContentPlaceHolder1_lbl_gm").text());
        tbProjectDesign.setExploreOrg(eles.select("#ctl00_ContentPlaceHolder1_td_kcdw").text());
        tbProjectDesign.setDesignOrg(eles.select("#ctl00_ContentPlaceHolder1_td_sjdw").text());
        tbProjectDesign.setCheckOrg(eles.select("#ctl00_ContentPlaceHolder1_td_scdw").text());
        tbProjectDesign.setCheckNo(eles.select("#ctl00_ContentPlaceHolder1_lbl_sgtscbh").text());
        tbProjectDesign.setCheckFinishDate(eles.select("#ctl00_ContentPlaceHolder1_lbl_scrq").text());
        tbProjectDesign.setCheckPerson(eles.select("#ctl00_ContentPlaceHolder1_lbl_scryxm").text());
        tbProjectDesign.setType("explore");
        tbProjectDesign.setSgtxh(sgtxh);
        tbProjectDesign.setComId(companyId);
        tbProjectDesign.setProId(projectId);
        return companyService.insertProjectDesignTwo(tbProjectDesign);
    }

    /**
     * 勘察设计人员名单（勘察）
     * @param eles      表格数据
     * @param projectSurveyId 项目设计id
     * @param projectDesignUrl 项目设计详情Url
     */
    void addSurveyPeople(Elements eles, Integer projectSurveyId, String projectDesignUrl) {
        if(eles.size() > 2) {
            TbPersonDesign tbProjectDesign;
            for (int i = 2; i < eles.size() - 1; i++) {
                if(StringUtils.isNotNull(eles.get(i).text())) {
                    tbProjectDesign = new TbPersonDesign();
                    tbProjectDesign.setName(eles.get(i).select("td").get(0).text());
                    tbProjectDesign.setCategory(eles.get(i).select("td").get(1).text());
                    tbProjectDesign.setComName(eles.get(i).select("td").get(2).text());
                    tbProjectDesign.setRole(eles.get(i).select("td").get(3).text());
                    String peopleDetailId = eles.get(i).select("td").get(0).select("a").attr("href");
                    tbProjectDesign.setInnerid(peopleDetailId.substring(peopleDetailId.indexOf("=") + 1));
                    tbProjectDesign.setPid(projectSurveyId);
                    companyService.insertPersonDesign(tbProjectDesign);
                }
            }
        } else {
            System.out.println("无勘察设计人员名单人员（勘察）" + projectDesignUrl);
        }
    }

    /**
     * 拆分企业资格证书资质
     */
    void splitCompanyQualifications() {
        int page = 0;
        int batchCount = 1000;
        Integer count = companyService.getCompanyQualificationTotalByTabName("工程勘察企业");
        if (count % batchCount == 0) {
            page = count / batchCount;
        } else {
            page = count / batchCount + 1;
        }
        Map<String, Object> params;
        List<TbCompanyQualification> companyQualificationList;
        //分页 一次1000
        for (int pageNum = 0; pageNum < page; pageNum++) {
            params = new HashMap<>();
            params.put("tableName", "工程勘察企业");
            params.put("start", batchCount * pageNum);
            params.put("pageSize", 1000);
            companyQualificationList = companyService.getCompanyQualifications(params);
            //遍历证书
            for (int i = 0; i < companyQualificationList.size(); i++) {
                int qualId = companyQualificationList.get(i).getPkid();
                String qualRange = companyQualificationList.get(i).getRange();
                int comId =  companyQualificationList.get(i).getComId();
                //有资质
                if (StringUtils.isNotNull(qualRange)) {
                    AllZh allZh;
                    TbCompanyAptitude companyAptitude;
                    List<TbCompanyAptitude> companyQualifications = new ArrayList<>();
                    if (qualRange.contains("；")) {
                        //拆分资质
                        String[] qual = qualRange.split("；");
                        for (int j = 0; j < qual.length; j++) {
                            allZh = companyService.getAllZhByName(qual[j]);
                            if (allZh != null) {
                                companyAptitude = new TbCompanyAptitude();
                                companyAptitude.setQualId(qualId);
                                companyAptitude.setComId(comId);
                                companyAptitude.setAptitudeName(companyService.getMajorNameBymajorUuid(allZh.getMainuuid()));
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
                            allZh = companyService.getAllZhByName(qual[j]);
                            if (allZh != null) {
                                companyAptitude = new TbCompanyAptitude();
                                companyAptitude.setQualId(qualId);
                                companyAptitude.setComId(comId);
                                companyAptitude.setAptitudeName(companyService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                                companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                                companyAptitude.setMainuuid(allZh.getMainuuid());
                                companyAptitude.setType(allZh.getType());
                                companyQualifications.add(companyAptitude);
                            }
                        }
                    } else {
                        allZh = companyService.getAllZhByName(qualRange);
                        if (allZh != null) {
                            companyAptitude = new TbCompanyAptitude();
                            companyAptitude.setQualId(qualId);
                            companyAptitude.setComId(comId);
                            companyAptitude.setAptitudeName(companyService.getMajorNameBymajorUuid(allZh.getMainuuid()));
                            companyAptitude.setAptitudeUuid(allZh.getFinaluuid());
                            companyAptitude.setMainuuid(allZh.getMainuuid());
                            companyAptitude.setType(allZh.getType());
                            companyQualifications.add(companyAptitude);
                        }
                    }
                    if (companyQualifications != null && companyQualifications.size() > 0) {
                        companyService.batchInsertCompanyAptitude(companyQualifications);
                    }
                }
            }
        }
    }

    /**
     *
     */
    void updateCompanyAptitudeRange() {
        int page = 0;
        int batchCount = 1000;
        Integer count = companyService.getCompanyAptitudeTotal();
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
            tbCompanyAptitudes = companyService.listCompanyAptitude(params);
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
                    companyService.updateCompanyRangeByComId(tbCompany);
                }
            }
        }
    }
}
