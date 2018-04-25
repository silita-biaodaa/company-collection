package com.silita.biaodaa.task;

import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.ICompanyUpdateService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 91567 on 2018/4/24.
 */
@Component
public class HuNanCompanyUpdateTask {
    private int min = 1;
    private int max = 5;
    private Random random = new Random();
    String dateRegex = "(\\d{4}-\\d{1,2}-\\d{1,2})";

    private static final int THREAD_NUMBER = 1;

    @Autowired
    private ICompanyUpdateService companyUpdateService;
    @Autowired
    private Project project;


    /**
     * 建筑数据更新
     */
    public void taskBuilderCompany(Map<String, Object> params) {
        int threadCount = THREAD_NUMBER;
        List<String> urls = new ArrayList<String>(200);
        urls = companyUpdateService.getAllCompanyQualificationUrlByTabAndCompanyName(params);
        int every = urls.size() % threadCount == 0 ? urls.size() / threadCount : (urls.size() / threadCount) + 1;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(new HuNanCompanyUpdateRun(i * every, (i + 1) * every, latch, urls, params)).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class HuNanCompanyUpdateRun implements Runnable {
        private int startNum;
        private int endNum;
        private CountDownLatch latch;
        private List<String> CompanyQualificationUrls;
        private Map<String, Object> params;

        public HuNanCompanyUpdateRun(int startNum, int endNum, CountDownLatch latch, List<String> CompanyQualificationUrls, Map<String, Object> params) {
            this.startNum = startNum;
            this.endNum = endNum;
            this.latch = latch;
            this.CompanyQualificationUrls = CompanyQualificationUrls;
            this.params = params;
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

                        TbCompanyQualification tbCompanyQualification = companyUpdateService.getComIdByUrl(CompanyQualificationUrl);
                        Integer comId = tbCompanyQualification.getComId();
                        //#########更新企业基本信息########
                        updateCompanyInfo(companyInfoTable, comId);
                        //########更新企业资质证书#########
                        updateCompanyAptitude(CompanyAptitudeTable, corpid, comId);
                        //########更新人员及人员证书#######
                        updatePeople(cookies, CompanyQualificationUrl, comId);
                        //##########更新项目信息##########
                       /* String tab = (String) params.get("tableName");
                        if(tab.equals("建筑业企业")) {
                            project.getBuilderProjectList(cookies, CompanyQualificationUrl, comId);
                        } else if(tab.equals("工程设计企业")) {
                            project.getDesignProjectList(cookies, CompanyQualificationUrl, comId);
                        } else if(tab.equals("工程勘察企业")) {
                            project.getSurveyProjectList(cookies, CompanyQualificationUrl, comId);
                        } else if(tab.equals("工程监理企业")) {
                            project.getSupervisorProjectList(cookies, CompanyQualificationUrl, comId);
                        }*/
                    } else {
                        TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                        tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                        tbExceptionUrl.setExceptionMsg("获取企业详情信息失败!");
                        companyUpdateService.insertException(tbExceptionUrl);
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
            company.setRegisAddress(eles.select("#ctl00_ContentPlaceHolder1_lbl_sz").text());
            company.setComAddress(eles.select("#ctl00_ContentPlaceHolder1_lbl_dwdz").text());
            company.setLegalPerson(eles.select("#ctl00_ContentPlaceHolder1_lbl_fddbr").text());
            company.setEconomicType(eles.select("#ctl00_ContentPlaceHolder1_lbl_jjlx").text());
            company.setRegisCapital(eles.select("#ctl00_ContentPlaceHolder1_lbl_zczb").text());
            company.setComId(companyId);
            companyUpdateService.updateCompany(company);
        }

        /**
         * 更新企业资质证书
         *
         * @param eles
         * @param corpid
         * @param companyId
         */
        void updateCompanyAptitude(Elements eles, String corpid, Integer companyId) {
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
                        companyUpdateService.updateCompanyQualificationByUrl(tbCompanyQualification);
                    }
                }
            } else {
                System.out.println("该企业资质数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
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
            companyUpdateService.deletePersonQualByComId(companyId);
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
                            if (companyUpdateService.checkPersonQualificationExist(params)) {
                                System.out.println("已抓取这个公司的这本证书" + PersonQualificationUrl);
                            } else {
                                peopleDetailConn = Jsoup.connect(PersonQualificationUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                                peopleDetailDoc = peopleDetailConn.get();
                                if (peopleDetailConn.response().statusCode() == 200) {
                                    System.out.println(PersonQualificationUrl);
                                    Elements peopleInfoTable = peopleDetailDoc.select("#table1");
                                    Elements peopleRegisteredTable = peopleDetailDoc.select("#tablelist").select("#table2").select("#ctl00_ContentPlaceHolder1_td_zzdetail").select("tr");
                                    Elements peopleOtherQualificationsTable = peopleDetailDoc.select("#tablelist").select("#table3").select("#ctl00_ContentPlaceHolder1_td_rylist").select("tr");
                                    Elements peopleChangeTable = peopleDetailDoc.select("#tablelist").select("#table6").select("#ctl00_ContentPlaceHolder1_jzs2_history").select("tr");
                                    //添加人员基本信息后 返回主键
                                    Integer pkid = addPeopleInfo(peopleInfoTable, companyId);
                                    //注册执业信息
                                    addPeopleRegistered(peopleRegisteredTable, PersonQualificationUrl, companyId, pkid, validDate);
                                    //其他资格信息
                                    addPeopleOtherCert(peopleOtherQualificationsTable, PersonQualificationUrl, companyId, pkid);
                                    //人员变更信息
                                    addPeopleChange(peopleChangeTable, pkid);
                                } else {
                                    TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                                    tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                                    tbExceptionUrl.setExceptionUrl(PersonQualificationUrl);
                                    tbExceptionUrl.setExceptionMsg("获取人员详情失败");
                                    companyUpdateService.insertException(tbExceptionUrl);
                                }
                            }
                            //随机暂停几秒
                            Thread.sleep(100 * (random.nextInt(max) % (max - min + 1)));
                        }
                    } else {
                        System.out.println("该企业注册人员数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                    }
                } else {
                    System.out.println("获取人员证书列表页失败" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
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
            return companyUpdateService.insertPersionInfo(tbPerson);
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
        void addPeopleRegistered(Elements eles, String PersonQualificationUrl, Integer companyId, Integer peopleId, String validDate) {
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
                    companyUpdateService.insertPersonQualification(tbPersonQualification);
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
        void addPeopleOtherCert(Elements eles, String PersonQualificationUrl, Integer companyId, Integer peopleId) {
            if (eles.size() > 1) {
                TbPersonQualification tbPersonQualification = null;
                for (int i = 1; i < eles.size(); i++) {
                    tbPersonQualification = new TbPersonQualification();
                    tbPersonQualification.setCategory(eles.get(i).select("td").get(0).text());
                    tbPersonQualification.setComName(eles.get(i).select("td").get(1).text());
                    tbPersonQualification.setCertNo(eles.get(i).select("td").get(2).text());
                    tbPersonQualification.setMajor(eles.get(i).select("td").get(3).text());
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
                    companyUpdateService.insertPersonQualification(tbPersonQualification);
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
        TbPersonChange tbPersonChange;
        if (eles.size() > 2) {
            for (int i = 2; i < eles.size(); i++) {
                tbPersonChange = new TbPersonChange();
                tbPersonChange.setComName(eles.get(i).select("td").get(0).text());
                tbPersonChange.setMajor(eles.get(i).select("td").get(1).text());
                tbPersonChange.setChangeDate(eles.get(i).select("td").get(2).text());
                tbPersonChange.setRemark(eles.get(i).select("td").get(3).text());
                tbPersonChange.setPerId(peopleId);
                companyUpdateService.insertPeopleChange(tbPersonChange);
            }
        }
    }

}
