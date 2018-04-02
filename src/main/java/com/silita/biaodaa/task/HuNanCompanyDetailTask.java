package com.silita.biaodaa.task;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.model.TbPerson;
import com.silita.biaodaa.model.TbPersonQualification;
import com.silita.biaodaa.service.ICompanyService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 遍历企业列表数据库逐个抓取
 * Created by 91567 on 2018/3/31.
 */
@Component
public class HuNanCompanyDetailTask {

    private int min = 1;
    private int max = 5;
    private Random random = new Random();

    private static final int THREAD_NUMBER = 1;

    @Autowired
    private ICompanyService companyService;

    public void task() {
        int threadCount = THREAD_NUMBER;
        List<String> urls = new ArrayList<String>(2000);
        urls = companyService.getAllCompanyQualificationUrl();
        int every = urls.size() % threadCount == 0 ? urls.size() / threadCount : (urls.size() / threadCount) + 1;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(new HuNanCompanyDetailRun(i * every, (i + 1) * every, latch, urls, companyService)).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 可以用多线程抓
     */
    class HuNanCompanyDetailRun implements Runnable {
        private int startNum;
        private int endNum;
        private CountDownLatch latch;
        private List<String> CompanyQualificationUrls;

        public HuNanCompanyDetailRun(int startNum, int endNum, CountDownLatch latch, List<String> CompanyQualificationUrls, ICompanyService companyService) {
            this.startNum = startNum;
            this.endNum = endNum;
            this.latch = latch;
            this.CompanyQualificationUrls = CompanyQualificationUrls;
        }

        @Override
        public void run() {
            Document companyDetailDoc = null;
            Connection companyDetailConn = null;
            //企业cookie，抓取人员需要企业cookie
            Map<String, String> cookies = null;
            try {
                for (int i = startNum; i < endNum; i++) {
                    String CompanyQualificationUrl = CompanyQualificationUrls.get(i);
                    companyDetailConn = Jsoup.connect(CompanyQualificationUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                    companyDetailDoc = companyDetailConn.get();
                    if (companyDetailConn.response().statusCode() == 200) {
                        cookies = companyDetailConn.response().cookies();
                        //资质证书内部id
                        String corpid = CompanyQualificationUrl.substring(CompanyQualificationUrl.indexOf("=") + 1);
                        Elements companyInfoTable = companyDetailDoc.select("#table1");
                        Elements CompanyAptitudeTable = companyDetailDoc.select("#tablelist").select("#table2").select("#ctl00_ContentPlaceHolder1_td_zzdetail").select("table");
                        //添加企业基本信息 返回主键
                        Integer comId = addCompanyInfo(companyInfoTable);
                        addCompanyAptitude(CompanyAptitudeTable, corpid, comId);
                        //抓人员
                        getPepleList(cookies, comId);
                    } else {
                        System.out.println("获取企业详情信息失败！" + CompanyQualificationUrl);
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
         * @param eles
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
         * @param eles
         * @param companyId
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
                System.out.println("该企业资质数据为空" + companyId);
            }
        }


        /**
         * 进入公告人员列表后便利列表抓人员详情 需要公司cookie
         *
         * @param cookies
         * @param companyId
         */
        void getPepleList(Map<String, String> cookies, Integer companyId) {
            Document peopleListDoc = null;
            Connection peopleListConn = null;//
            String peopleListUrl = "http://qyryjg.hunanjz.com/public/EnterpriseRegPerson.ashx";
            try {
                //进入人员证书列表
                peopleListConn = Jsoup.connect(peopleListUrl).userAgent("Mozilla").timeout(10000 * 60).ignoreHttpErrors(true);
                peopleListConn.cookies(cookies);
                peopleListDoc = peopleListConn.get();
                if (peopleListConn.response().statusCode() == 200) {
                    Elements peopleList = peopleListDoc.select("table").select("tr");
                    if (peopleList.size() > 2) {
                        Document peopleDetailDoc = null;
                        Connection peopleDetailConn = null;
                        //遍历人员列表url 进入详情页面
                        for (int i = 2; i < peopleList.size(); i++) {
                            String PersonQualificationUrl = peopleList.get(i).select("a").first().absUrl("href");
                            peopleDetailConn = Jsoup.connect(PersonQualificationUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                            peopleDetailDoc = peopleDetailConn.get();
                            if (peopleDetailConn.response().statusCode() == 200) {
                                //人员信息
                                Elements peopleInfoTable = peopleDetailDoc.select("#table1");
                                //注册执业信息
                                Elements peopleRegisteredTable = peopleDetailDoc.select("#tablelist").select("#table2").select("#ctl00_ContentPlaceHolder1_td_zzdetail").select("tr");
                                //其他资格信息
                                Elements peopleOtherQualificationsTable = peopleDetailDoc.select("#tablelist").select("#table3").select("#ctl00_ContentPlaceHolder1_td_rylist").select("tr");
                                Integer pkid = addPeopleInfo(peopleInfoTable);
                                addPeopleRegistered(peopleRegisteredTable, PersonQualificationUrl, companyId, pkid);
                            } else {
                                System.out.println("获取人员详情失败" + companyId);
                            }
                        }
                    } else {
                        System.out.println("该企业注册人员数据为空" + companyId);
                    }
                } else {
                    System.out.println("获取人员列表页失败" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }

        /**
         * 人员基本信息
         *
         * @param eles
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
         * @param eles
         * @param companyId
         * @param peopleId
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
                    tbPersonQualification.setValidDate(eles.get(i).select("td").get(5).text());
                    tbPersonQualification.setUrl(PersonQualificationUrl);
                    tbPersonQualification.setInnerid(PersonQualificationUrl.substring(PersonQualificationUrl.indexOf("=") + 1));
                    tbPersonQualification.setPerId(peopleId);
                    tbPersonQualification.setComId(companyId);
                    companyService.insertPersonQualification(tbPersonQualification);
                }
            } else {
                System.out.println("人员执业信息为空" + peopleId);
            }
        }

        /**
         * 人员其他证书信息（安全证书）
         *
         * @param eles
         * @param peopleInnerId
         */
        void addCompanyProjectCert(Elements eles, String peopleInnerId) {
            if (eles.size() > 1) {
                for (int i = 0; i < eles.size(); i++) {
                    String type = eles.get(i).select("td").get(0).text();
                    String companyName = eles.get(i).select("td").get(1).text();
                    String securityId = eles.get(i).select("td").get(2).text();
                    String major = eles.get(i).select("td").get(3).text();
                    String issuingDate = eles.get(i).select("td").get(4).text();
                    String validityDate = eles.get(i).select("td").get(4).text();
                }
            } else {
                System.out.println("人员其他证书信息为空" + peopleInnerId);
            }
        }
    }

}
