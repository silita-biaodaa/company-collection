package com.silita.biaodaa.task;

import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.service.ICompanyService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 抓取湖南四库一平台公司列表数据(企业证书列表)
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
        Document doc = null;
        Connection conn = null;
        TbCompanyQualification companyQualification = null;
        List<TbCompanyQualification> companyQualifications = null;

        String __VIEWSTATE = null;
        String __EVENTVALIDATION = null;
        String url = "http://qyryjg.hunanjz.com/public/EnterpriseList.aspx";

        String[] codes = {"ctl00$ContentPlaceHolder1$btn_7", "ctl00$ContentPlaceHolder1$btn_1", "ctl00$ContentPlaceHolder1$btn_2", "ctl00$ContentPlaceHolder1$btn_5", "ctl00$ContentPlaceHolder1$btn_3",
                "ctl00$ContentPlaceHolder1$btn_13", "ctl00$ContentPlaceHolder1$btn_zjzx", "ctl00$ContentPlaceHolder1$btn_sgtsc", "ctl00$ContentPlaceHolder1$btn_jcqy", "ctl00$ContentPlaceHolder1$btn_aqsc",
                "ctl00$ContentPlaceHolder1$btn_ws"};
        String[] tabs = {"建筑业企业", "工程勘察企业", "工程设计企业", "工程监理企业", "工程招标代理机构",
                "设计施工一体化企业", "工程造价咨询企业", "施工图审查机构", "质量检测机构", "安全生产许可证",
                "外省入湘备案"};

        for (int tab = 0; tab < codes.length; tab++) {
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
                    conn.data("ctl00$ContentPlaceHolder1$ddlsz", "0");
                    doc = conn.post();
                }
                System.out.println("########抓取" + tabs[tab] + "栏目第" + pageTemp + "页########");

                __VIEWSTATE = doc.select("#__VIEWSTATE").attr("value");
                __EVENTVALIDATION = doc.select("#__EVENTVALIDATION").attr("value");

                Elements trs = doc.select("#ctl00_ContentPlaceHolder1_div_list").select("#table").select("tr");
                //安全生产许可
                if (tab == 9) {
                    System.out.println("@@@@@还未抓取安全生产许可证@@@@@");
                } else if (tab == 10) {
                    System.out.println("@@@@@@还未抓取外省入湘备案@@@@@@");
                } else {
                    companyQualifications = new ArrayList<>(25);
                    for (int row = 1; row < trs.size(); row++) {
                        companyQualification = new TbCompanyQualification();
                        companyQualification.setComName(trs.get(row).select("td").get(0).select("a").text());
                        companyQualification.setCertNo(trs.get(row).select("td").get(1).text());
                        companyQualification.setCertDate(trs.get(row).select("td").get(2).text());
                        companyQualification.setValidDate(trs.get(row).select("td").get(3).text());
                        companyQualification.setTab(tabs[tab]);
                        String companyQualificationUrl = trs.get(row).select("td").get(0).select("a").first().absUrl("href");
                        companyQualification.setUrl(companyQualificationUrl);
                        companyQualification.setCorpid(companyQualificationUrl.substring(companyQualificationUrl.indexOf("=") + 1));
                        companyQualifications.add(companyQualification);
                    }
                    companyService.batchInsertCompanyQualification(companyQualifications);
                    companyQualifications.clear();
                }
                //随机暂停几秒
                Thread.sleep(1000 * (random.nextInt(max) % (max - min + 1)));
            }
        }
    }
}
