package com.silita.task;

import com.alibaba.fastjson.JSONObject;
import com.silita.common.config.CustomizedPropertyConfigurer;
import com.silita.common.xxl.BaseTask;
import com.silita.common.xxl.MyXxlLogger;
import com.silita.model.*;
import com.silita.service.ICompanyService;
import com.silita.utils.kafka.KafkaUtils;
import com.xxl.job.core.handler.annotation.JobHander;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@JobHander(value = "HuNanCompanyKafkaTask")
public class HuNanCompanyKafkaTask extends BaseTask {

    @Override
    public void runTask(JSONObject jsonObject) throws Exception {
        taskBuilderCompany();
    }

    private int min = 1;
    private int max = 5;
    private Random random = new Random();
    private static final int THREAD_NUMBER = 1;

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private KafkaUtils kafkaUtils;

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
                        String comId = addCompanyInfo(companyInfoTable);
//                        //########更新企业资质证书#########
//                        addCompanyAptitude(CompanyAptitudeTable, corpid, comId);
//                        //##########抓取人员start##########
//                        getPepleList(cookies, CompanyQualificationUrl, comId);
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
        String addCompanyInfo(Elements eles) {
            Company company = new Company();
            company.setCom_name(eles.select("#ctl00_ContentPlaceHolder1_lbl_qymc").text());
            company.setOrg_code(eles.select("#ctl00_ContentPlaceHolder1_lbl_jgdm").text());
            company.setBusiness_num(eles.select("#ctl00_ContentPlaceHolder1_lbl_yyzz").text());
            company.setRegis_address("湖南省" + eles.select("#ctl00_ContentPlaceHolder1_lbl_sz").text());
            company.setCom_address(eles.select("#ctl00_ContentPlaceHolder1_lbl_dwdz").text());
            company.setLegal_person(eles.select("#ctl00_ContentPlaceHolder1_lbl_fddbr").text());
            company.setEconomic_type(eles.select("#ctl00_ContentPlaceHolder1_lbl_jjlx").text());
            company.setRegis_capital(eles.select("#ctl00_ContentPlaceHolder1_lbl_zczb").text());

            String md5 = company.md5(company.getCom_name(), company.getCredit_code(), company.getOrg_code(), company.getBusiness_num());
            String entityMD5 = company.entityToMD5(company);
            company.setCom_id(md5);
//            company.setMd5(entityMD5);
            com.silita.model.Document docObject = new com.silita.model.Document(company);
            int partition = kafkaUtils.randomPartition();
            kafkaUtils.send((String) CustomizedPropertyConfigurer.getContextProperty("kafka.topic"), 0, entityMD5, docObject);
            return md5;
//            return companyService.insertCompanyInfo(company);
        }

        /**
         * 抓取企业资质信息
         *
         * @param eles      表单数据
         * @param corpid    资质证书内部id
         * @param companyId 公司id
         */
        void addCompanyAptitude(Elements eles, String corpid, String companyId) {
            if (eles.size() > 0) {
                //有的下面有多个资质
                for (int i = 0; i < eles.size(); i++) {
                    if (eles.get(i).select("tr").get(0).select("td").get(1).text().contains("建筑")) {
                        CompanyQualification companyQualification = new CompanyQualification();
                        companyQualification.setQual_type(eles.get(i).select("tr").get(0).select("td").get(1).text());
                        companyQualification.setCert_no(eles.get(i).select("tr").get(1).select("td").get(1).text());
                        companyQualification.setCert_org(eles.get(i).select("tr").get(1).select("td").get(3).text());
                        companyQualification.setCert_date(eles.get(i).select("tr").get(3).select("td").get(1).text());
                        companyQualification.setValid_date(eles.get(i).select("tr").get(3).select("td").get(3).text());
                        companyQualification.setRange(eles.get(i).select("tr").get(4).select("td").get(1).text());
//                        companyQualification.setCom_id(companyId);
                        companyQualification.setCorpid(corpid);

                        String md5 = companyQualification.md5(companyQualification.getCom_id(), companyQualification.getCert_no(), companyQualification.getQual_type(), companyQualification.getValid_date());
                        String entityMD5 = companyQualification.entityToMD5(companyQualification);
                        companyQualification.setCom_id(md5);
//                        companyQualification.setMd5(entityMD5);
                        com.silita.model.Document docObject = new com.silita.model.Document(companyQualification);
                        int partition = kafkaUtils.randomPartition();
                        kafkaUtils.send((String) CustomizedPropertyConfigurer.getContextProperty("kafka.topic"), partition, entityMD5, docObject);
//                        companyService.updateCompanyQualificationUrlByCorpid(tbCompanyQualification);
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
        void getPepleList(Map<String, String> cookies, String CompanyQualificationUrl, String companyId) {
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
                        Person persion;
                        Document peopleDetailDoc;
                        Connection peopleDetailConn;
                        //遍历人员列表url 进入详情页面
                        for (int i = 2; i < peopleList.size(); i++) {
                            PersonQualificationUrl = peopleList.get(i).select("a").first().absUrl("href");
                            //列表获取一部分证书信息
                            persion = new Person();
                            persion.setUrl(PersonQualificationUrl);
                            persion.setName(peopleList.get(i).select("td").get(1).text());
//                            tbPersonHunan.setIdCard(peopleList.get(i).select("td").get(2).text());
                            persion.setCategory(peopleList.get(i).select("td").get(3).text());
                            persion.setCert_no(peopleList.get(i).select("td").get(4).text().trim());
                            if (!org.springframework.util.StringUtils.isEmpty(peopleList.get(i).select("td").get(5).text().replaceAll("[^0-9]", ""))) {
                                persion.setSeal_no(peopleList.get(i).select("td").get(5).text());
                            } else {
                                persion.setMajor(peopleList.get(i).select("td").get(5).text());
                            }
                            persion.setCert_date(peopleList.get(i).select("td").get(6).text());
                            persion.setValid_date(peopleList.get(i).select("td").last().text());
                            persion.setType(1);
                            persion.setInnerid(PersonQualificationUrl.substring(PersonQualificationUrl.indexOf("=") + 1));
                            persion.setCom_id(companyId);
//                            //已存在url,证书编号，公司id(人员证书可能挂靠其他公司)跳过此证书
//                            if (peopleCertService.checkPersonHunanIsExist(tbPersonHunan)) {
//                                MyXxlLogger.info("已抓取这个公司的这本证书" + PersonQualificationUrl);
//                            } else {
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
                                persion.setSex(sex);
                                persion.setId_card(cardId);
                                //#######添加人员执业资质########
                                String flag = addPeopleMainCert(peopleRegisteredTable, persion, CertDetail);
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
//                            }
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
        String addPeopleMainCert(Elements peopleRegisteredTable, Person temp, Elements certDetail) {
            Person tbPersionHunan = temp;
            if (peopleRegisteredTable.size() > 1) {
                String certNo;
                for (int i = 1; i < peopleRegisteredTable.size(); i++) {
                    certNo = peopleRegisteredTable.get(i).select("td").get(2).text().trim();
                    if (certNo.equals(tbPersionHunan.getCert_no())) {
                        tbPersionHunan.setCom_name(peopleRegisteredTable.get(i).select("td").get(1).text());
                        //注册专业为空,取详情列表补齐
                        if (org.springframework.util.StringUtils.isEmpty(temp.getMajor())) {
                            temp.setMajor(peopleRegisteredTable.get(i).select("td").get(3).text());
                            break;
                        }
                    }

                }
            }
            String flag = tbPersionHunan.getName() + "_" + tbPersionHunan.getSex() + "_" + tbPersionHunan.getId_card();
            tbPersionHunan.setFlag(flag);
            //执业印章号为空，取模态窗口数据补齐，同时更新专业
            if (org.springframework.util.StringUtils.isEmpty(tbPersionHunan.getSeal_no())) {
                tbPersionHunan.setSeal_no(certDetail.select("tr").get(2).select("#ctl00_ContentPlaceHolder1_d_zcbh").text());
                String major = certDetail.select("tr").get(3).select("#ctl00_ContentPlaceHolder1_d_zy").text();
                //包含多个证书如：（建筑,2015-01-19,2018-01-18.机电,2016-8-17,2019-8-16.市政,2017-7-11,2020-7-10）
                if (major.contains(".")) {
                    String[] majors = major.split("\\.");
                    for (int i = 0; i < majors.length; i++) {
                        majors[i].replaceAll("，", ",");
                        if (majors[i].contains(",")) {
                            String[] tempArr = majors[i].split(",");
                            tbPersionHunan.setMajor(tempArr[0]);
                            tbPersionHunan.setCert_date(tempArr[1]);
                            tbPersionHunan.setValid_date(tempArr[2]);
                        }
                        String md5 = tbPersionHunan.md5(tbPersionHunan.getName(), tbPersionHunan.getId_card(), tbPersionHunan.getCategory(), tbPersionHunan.getCert_no(), tbPersionHunan.getSeal_no(), tbPersionHunan.getMajor());
                        String entityMD5 = tbPersionHunan.entityToMD5(tbPersionHunan);
                        tbPersionHunan.setCom_id(md5);
//                        tbPersionHunan.setMd5(entityMD5);
                        com.silita.model.Document docObject = new com.silita.model.Document(tbPersionHunan);
                        int partition = kafkaUtils.randomPartition();
                        kafkaUtils.send((String) CustomizedPropertyConfigurer.getContextProperty("kafka.topic"), partition, entityMD5, docObject);

//                        peopleCertService.insertPersonHunan(tbPersionHunan);
                    }
                } else {
                    String md5 = tbPersionHunan.md5(tbPersionHunan.getName(), tbPersionHunan.getId_card(), tbPersionHunan.getCategory(), tbPersionHunan.getCert_no(), tbPersionHunan.getSeal_no(), tbPersionHunan.getMajor());
                    String entityMD5 = tbPersionHunan.entityToMD5(tbPersionHunan);
                    tbPersionHunan.setCom_id(md5);
//                        tbPersionHunan.setMd5(entityMD5);
                    com.silita.model.Document docObject = new com.silita.model.Document(tbPersionHunan);
                    int partition = kafkaUtils.randomPartition();
                    kafkaUtils.send((String) CustomizedPropertyConfigurer.getContextProperty("kafka.topic"), partition, entityMD5, docObject);

//                    peopleCertService.insertPersonHunan(tbPersionHunan);
                }
            } else {
                String md5 = tbPersionHunan.md5(tbPersionHunan.getName(), tbPersionHunan.getId_card(), tbPersionHunan.getCategory(), tbPersionHunan.getCert_no(), tbPersionHunan.getSeal_no(), tbPersionHunan.getMajor());
                String entityMD5 = tbPersionHunan.entityToMD5(tbPersionHunan);
                tbPersionHunan.setCom_id(md5);
//                        tbPersionHunan.setMd5(entityMD5);
                com.silita.model.Document docObject = new com.silita.model.Document(tbPersionHunan);
                int partition = kafkaUtils.randomPartition();
                kafkaUtils.send((String) CustomizedPropertyConfigurer.getContextProperty("kafka.topic"), partition, entityMD5, docObject);

//                peopleCertService.insertPersonHunan(tbPersionHunan);
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
        void addPeopleOtherCert(Elements peopleOtherQualificationsTable, String url, String companyId, String flag) {
            if (peopleOtherQualificationsTable.size() > 1) {
                Person tbPersionHunan;
                for (int i = 1; i < peopleOtherQualificationsTable.size(); i++) {
                    tbPersionHunan = new Person();
                    tbPersionHunan.setCategory(peopleOtherQualificationsTable.get(i).select("td").get(0).text());
                    tbPersionHunan.setCom_name(peopleOtherQualificationsTable.get(i).select("td").get(1).text());
                    tbPersionHunan.setCert_no(peopleOtherQualificationsTable.get(i).select("td").get(2).text());
                    tbPersionHunan.setMajor(peopleOtherQualificationsTable.get(i).select("td").get(3).text());
                    String dateStr = peopleOtherQualificationsTable.get(i).select("td").get(4).text();
                    if (dateStr.contains("有效期")) {
                        tbPersionHunan.setCert_date(dateStr.substring(0, dateStr.indexOf("有效期") - 1));
                        tbPersionHunan.setValid_date(dateStr.substring(dateStr.indexOf("有效期") + 4, dateStr.length() - 1));
                    } else {
                        tbPersionHunan.setCert_date(dateStr);
                    }
                    tbPersionHunan.setName(flag.split("_")[0]);
                    tbPersionHunan.setSex(flag.split("_")[1]);
                    tbPersionHunan.setId_card(flag.split("_")[2]);
                    tbPersionHunan.setUrl(url);
                    tbPersionHunan.setInnerid(url.substring(url.indexOf("=") + 1));
                    tbPersionHunan.setCom_id(companyId);
                    tbPersionHunan.setType(2);
                    tbPersionHunan.setFlag(flag);

                    String md5 = tbPersionHunan.md5(tbPersionHunan.getName(), tbPersionHunan.getId_card(), tbPersionHunan.getCategory(), tbPersionHunan.getCert_no(), tbPersionHunan.getSeal_no(), tbPersionHunan.getMajor());
                    String entityMD5 = tbPersionHunan.entityToMD5(tbPersionHunan);
                    tbPersionHunan.setCom_id(md5);
//                        tbPersionHunan.setMd5(entityMD5);
                    com.silita.model.Document docObject = new com.silita.model.Document(tbPersionHunan);
                    int partition = kafkaUtils.randomPartition();
                    kafkaUtils.send((String) CustomizedPropertyConfigurer.getContextProperty("kafka.topic"), partition, entityMD5, docObject);
//                    peopleCertService.insertPersonHunan(tbPersionHunan);
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
            PersonChange tbPersonChange;
            if (peopleChangeTable.size() > 2) {
                for (int i = 2; i < peopleChangeTable.size(); i++) {
                    tbPersonChange = new PersonChange();
                    tbPersonChange.setCom_name(peopleChangeTable.get(i).select("td").get(0).text());
                    tbPersonChange.setMajor(peopleChangeTable.get(i).select("td").get(1).text());
                    tbPersonChange.setChange_date(peopleChangeTable.get(i).select("td").get(2).text());
                    tbPersonChange.setRemark(peopleChangeTable.get(i).select("td").get(3).text());
                    tbPersonChange.setFlag(flag);

                    String md5 = tbPersonChange.md5(tbPersonChange.getFlag());
                    String entityMD5 = tbPersonChange.entityToMD5(tbPersonChange);
                    tbPersonChange.setPkid(md5);
//                    tbPersonChange.setMd5(entityMD5);
                    com.silita.model.Document docObject = new com.silita.model.Document(tbPersonChange);
                    int partition = kafkaUtils.randomPartition();
                    kafkaUtils.send((String) CustomizedPropertyConfigurer.getContextProperty("kafka.topic"), partition, entityMD5, docObject);
//                    peopleCertService.insertPeopleChange(tbPersonChange);
                }
            }
        }
    }

}
