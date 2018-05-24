package com.silita.biaodaa.task;

import com.silita.biaodaa.model.TbCompanyInto;
import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.model.TbSafetyCertificate;
import com.silita.biaodaa.service.ICompanyService;
import com.silita.biaodaa.utils.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 抓取湖南四库一平台企业列表数据
 * http://qyryjg.hunanjz.com/public/EnterpriseList.aspx
 * Created by 91567 on 2018/3/31.
 */
@Component
public class HuNanCompanyQualificationListTask {

    private int min = 1;
    private int max = 5;
    private Random random = new Random();

    @Autowired
    ICompanyService companyService;

    public void getCompanyList() throws Exception {
        Document doc;
        Connection conn;

        String __VIEWSTATE = null;
        String __EVENTVALIDATION = null;
        String url = "http://qyryjg.hunanjz.com/public/EnterpriseList.aspx";

        String[] codes = {"ctl00$ContentPlaceHolder1$btn_7", "ctl00$ContentPlaceHolder1$btn_1", "ctl00$ContentPlaceHolder1$btn_2", "ctl00$ContentPlaceHolder1$btn_5", "ctl00$ContentPlaceHolder1$btn_3",
                "ctl00$ContentPlaceHolder1$btn_13", "ctl00$ContentPlaceHolder1$btn_zjzx", "ctl00$ContentPlaceHolder1$btn_sgtsc", "ctl00$ContentPlaceHolder1$btn_jcqy", "ctl00$ContentPlaceHolder1$btn_aqsc",
                "ctl00$ContentPlaceHolder1$btn_ws"};
        String[] tabs = {"建筑业企业", "工程勘察企业", "工程设计企业", "工程监理企业", "工程招标代理机构",
                "设计施工一体化企业", "工程造价咨询企业", "施工图审查机构", "质量检测机构", "安全生产许可证",
                "外省入湘备案"};

        for (int tab = 0; tab < tabs.length; tab++) {
            int page = 1;
            conn = Jsoup.connect(url).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
            if (tab == 0) {
                doc = conn.get();
            } else {
                conn.data("ctl00_ScriptManager1_HiddenField", "");
                conn.data("__EVENTTARGET", codes[tab]);
                conn.data("__EVENTARGUMENT", "");
                conn.data("__VIEWSTATE", __VIEWSTATE);
                conn.data("__EVENTVALIDATION", __EVENTVALIDATION);
                conn.data("ctl00$ContentPlaceHolder1$txtqymc", "");
                conn.data("ctl00$ContentPlaceHolder1$txtzsbh", "");
                conn.data("ctl00$ContentPlaceHolder1$ddlsz", "0");
                doc = conn.post();
            }

            for (int pageTemp = 1; pageTemp <= page; pageTemp++) {
                if (pageTemp == 1) {
                    String pageStr = doc.select("#ctl00_ContentPlaceHolder1_lbl_count").select("i").last().text().trim();
                    page = Integer.parseInt(pageStr);
                } else {
                    conn = Jsoup.connect(url).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                    conn.data("ctl00_ScriptManager1_HiddenField", "");
                    conn.data("__EVENTTARGET", "ctl00$ContentPlaceHolder1$lkb_next");
                    conn.data("__EVENTARGUMENT", "");
                    conn.data("__VIEWSTATE", __VIEWSTATE);
                    conn.data("__EVENTVALIDATION", __EVENTVALIDATION);
                    conn.data("ctl00$ContentPlaceHolder1$txtqymc", "");
                    conn.data("ctl00$ContentPlaceHolder1$txtzsbh", "");
                    //外省入湘翻页不要这个！！！！！
                    if(tab != 10) {
                        conn.data("ctl00$ContentPlaceHolder1$ddlsz", "0");
                    }
                    doc = conn.post();
                }
                System.out.println("########抓取" + tabs[tab] + "栏目第" + pageTemp + "页########");

                __VIEWSTATE = doc.select("#__VIEWSTATE").attr("value");
                __EVENTVALIDATION = doc.select("#__EVENTVALIDATION").attr("value");

                Elements trs = doc.select("#ctl00_ContentPlaceHolder1_div_list").select("#table").select("tr");
                //安全生产许可
                if (tab == 9) {
                    insertCompanySafetyCert(trs);
                } else if (tab == 10) {
                    //外省入湘
                    insertCompanyInto(trs);
                } else {
                    insertCompanyQualification(trs, tabs[tab]);
                }
                //随机暂停几秒
                Thread.sleep(1000 * (random.nextInt(max) % (max - min + 1)));
            }
        }
    }


    /**
     * 添加企业资质证书
     *
     * @param trs
     * @param tableName
     */
    void insertCompanyQualification(Elements trs, String tableName) {
        TbCompanyQualification companyQualification;
//        List<TbCompanyQualification> companyQualifications = new ArrayList<>(25);
        for (int row = 1; row < trs.size(); row++) {
            companyQualification = new TbCompanyQualification();
            companyQualification.setComName(trs.get(row).select("td").get(0).select("a").text());
            companyQualification.setCertNo(trs.get(row).select("td").get(1).text());
            companyQualification.setCertDate(trs.get(row).select("td").get(2).text());
            companyQualification.setValidDate(trs.get(row).select("td").get(3).text());
            companyQualification.setTab(tableName);
            String companyQualificationUrl = trs.get(row).select("td").get(0).select("a").first().absUrl("href");
            companyQualification.setUrl(companyQualificationUrl);
            companyQualification.setCorpid(companyQualificationUrl.substring(companyQualificationUrl.indexOf("=") + 1));
            companyService.insertCompanyQualification(companyQualification);
//            companyQualifications.add(companyQualification);
        }
//        companyService.batchInsertCompanyQualification(companyQualifications);
//        companyQualifications.clear();
    }


    /**
     * 添加企业安全证书
     *
     * @param trs
     */
    void insertCompanySafetyCert(Elements trs) {
        TbSafetyCertificate safetyCertificate;
//        List<TbSafetyCertificate> safetyCertificates = new ArrayList<>(25);
        for (int row = 1; row < trs.size(); row++) {
            safetyCertificate = new TbSafetyCertificate();
            safetyCertificate.setComName(trs.get(row).select("td").get(0).text());
            safetyCertificate.setCertNo(trs.get(row).select("td").get(1).text());
            safetyCertificate.setCertDate(trs.get(row).select("td").get(2).text());
            safetyCertificate.setValidDate(trs.get(row).select("td").get(3).text());
            companyService.insertSafetyCertificate(safetyCertificate);
//            safetyCertificates.add(safetyCertificate);
        }
//        safetyCertificateService.batchInsertSafetyCertificate(safetyCertificates);
//        safetyCertificates.clear();
    }

    /**
     * 抓取外省入湘企业
     *
     * @param trs
     */
    void insertCompanyInto(Elements trs) {
        Document companyIntoDoc;
        Connection companyIntoConn;
        String companyIntoUrl = "";
        try {
            TbCompanyInto companyInto;
            for (int row = 1; row < trs.size(); row++) {
                companyIntoUrl = trs.get(row).select("td").select("a").first().absUrl("href");
                companyIntoConn = Jsoup.connect(companyIntoUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                companyIntoDoc = companyIntoConn.get();
                if(companyIntoConn.response().statusCode() == 200) {
                    companyInto = new TbCompanyInto();
                    companyInto.setComName(companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_qymc").text());
                    companyInto.setOrgCode(companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_jgdm").text());
                    companyInto.setBusinessNum(companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_yyzz").text());
                    companyInto.setRegisAddress(companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_sz").text());
                    companyInto.setComAddress(companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_dwdz").text());
                    companyInto.setLegalPerson(companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_fddbr").text());
                    //入湘登证号
                    String IntoStr = companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_jjlx").text();
                    if(StringUtils.isNotNull(IntoStr)) {
                        companyInto.setIntoNo(IntoStr.substring(0, IntoStr.indexOf("有效期")-1));
                        companyInto.setIntoValidDate(IntoStr.substring(IntoStr.indexOf("有效期") + 4, IntoStr.indexOf("至")));
                        companyInto.setRegisCapital(companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_zczb").text());
                        companyInto.setCertNo(companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_zsbh").text());
                    }
                    //安全生产许可证
                    String safetyCertStr = companyIntoDoc.select("#table1").select("#ctl00_ContentPlaceHolder1_lbl_rxba").text();
                    if(StringUtils.isNotNull(safetyCertStr)) {
                        companyInto.setSafeCertNo(safetyCertStr.substring(safetyCertStr.indexOf("安全生产许可证") + 8, safetyCertStr.indexOf("有效期") - 1));
                        companyInto.setSafeValidDate(safetyCertStr.substring(safetyCertStr.indexOf("有效期") + 5));
                    }
                    companyInto.setRang(companyIntoDoc.select("#table2").select("#ctl00_ContentPlaceHolder1_rxbazz").text());
                    companyInto.setQybm(companyIntoUrl.substring(companyIntoUrl.indexOf("=") + 1));
                    companyService.insertCompanyInto(companyInto);
                } else {
                    System.out.println("获取外省入湘企详情数据失败！" + companyIntoUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

}
