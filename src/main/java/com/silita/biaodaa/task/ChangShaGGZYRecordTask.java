package com.silita.biaodaa.task;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

@Component
public class ChangShaGGZYRecordTask {

    private Random random = new Random();

    public void task() throws IOException {

        Document doc = null;
        Connection conn = null;

        HtmlPage hPage = null;
        WebClient webClient = null;

        int pageCount = 1;
        String url = "https://ggzy.changsha.gov.cn/jrfwpt/ThemeStandard/Standard/ZT/baseInfoList.do";
        for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
            conn = Jsoup.connect(url).userAgent("Mozilla").timeout(1000 * 60).ignoreHttpErrors(true);
            conn.data("ZTJS", "");
            conn.data("ZTMC", "");
            conn.data("ZTDM", "");
            conn.data("_pageSize", "15");
            if (currentPage == 1) {
                conn.data("currentPage", "1");
                doc = conn.post();
                pageCount = Integer.parseInt(doc.select("#Page_TotalPage").attr("value"));
            } else {
                conn.data("currentPage", String.valueOf(currentPage));
                doc = conn.post();
            }
            //列表数据
            Elements listDate = doc.select("ul").select("[target=\"_blank\"]");
            for (int trs = 1; trs < listDate.size(); trs++) {
                String detailUrl = listDate.get(trs).select("a").first().absUrl("href");
                webClient = new WebClient();
                webClient.getOptions().setUseInsecureSSL(true);
                webClient.getOptions().setJavaScriptEnabled(true);
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setRedirectEnabled(true);
                webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                webClient.getOptions().setThrowExceptionOnScriptError(false);
                webClient.getOptions().setTimeout(50000);
//                hPage = webClient.getPage(detailUrl);
                hPage = webClient.getPage("https://ggzy.changsha.gov.cn/jrfwpt/ThemeStandard/Standard/ZT/enterpriseInformation.do?QYID=8b911084-79b9-432d-94a5-2097571bc85f&ZTDM=91430000591043159P");
                webClient.waitForBackgroundJavaScript(30000);
                Document basicDoc = Jsoup.parse(hPage.asXml());
                //########基本信息#########
                addBasicInfomation(basicDoc);
                //#######其他分类数据#######
                DomNodeList<HtmlElement> tabList = hPage.getElementById("ul_qyxx").getElementsByTagName("li");
                if (!tabList.isEmpty()) {
                    HtmlPage tempPage;
                    for (int i = 0; i < tabList.size(); i++) {
                        //抓取企业资质
                        if("企业资质".equals(tabList.get(i).getTextContent())) {
                            HtmlElement certTab = tabList.get(i).getElementsByAttribute("a", "href", "javascript:void(0);").get(0);
                            //获取第一页资质证书
                            tempPage = certTab.click();
                            webClient.waitForBackgroundJavaScript(30000);
                            //资质证书
                            addQualificationCert(tempPage);
                            DomElement pagination = tempPage.getElementById("kkpager").querySelector(".infoTextAndGoPageBtnWrap");
                            int certPageCount = Integer.parseInt(pagination.querySelector(".totalPageNum").asText());
                            //资质证书有分页，则遍历
                            if(certPageCount > 1) {
                                for (int j = 2; j <= certPageCount; j++) {
                                    ScriptResult result = hPage.executeJavaScript("javascript:return kkpager._clickHandler(" + j + ")");
                                    tempPage = (HtmlPage) result.getNewPage();
                                    webClient.waitForBackgroundJavaScript(30000);
                                    //资质证书
                                    addQualificationCert(tempPage);
                                }
                            }
                           /* DomElement zzTab = tempPage.getElementById("page-list");
                            System.out.println(zzTab);
                            ScriptResult result = hPage.executeJavaScript("javascript:changeQyxx(1,'1','d58ae07c-2e69-41d0-b453-f7fc71486dc2');");
                            tempPage = (HtmlPage) result.getNewPage();
                            webClient.waitForBackgroundJavaScript(30000);
                            Document docment = Jsoup.parse(tempPage.asXml());
                            System.out.println(docment);*/
                        }
                       /* if ("企业业绩".equals(tabList.get(i).getTextContent())) {
                            HtmlElement achievementTab = tabList.get(i).getElementsByAttribute("a", "href", "javascript:void(0);").get(0);
                            //获取第一页企业业绩
                            tempPage = achievementTab.click();
//                            HtmlPage customaryPage = tempPage;
                            webClient.waitForBackgroundJavaScript(30000);
                            //进入企业业绩详情
                            tempPage = addAchievement(tempPage, webClient);
                            DomElement pagination = tempPage.getElementById("kkpager").querySelector(".infoTextAndGoPageBtnWrap");
                            int certPageCount = Integer.parseInt(pagination.querySelector(".totalPageNum").asText());
                            if (certPageCount > 1) {
                                for (int j = 2; j <= certPageCount; j++) {
                                    ScriptResult result = hPage.executeJavaScript("javascript:return kkpager._clickHandler(" + j + ")");
                                    tempPage = (HtmlPage) result.getNewPage();
                                    webClient.waitForBackgroundJavaScript(30000);
                                    //进入企业业绩详情
                                    addAchievement(tempPage, webClient);
                                }
                            }
                        }*/
                        if ("企业获奖".equals(tabList.get(i).getTextContent())) {
                            tempPage = tabList.get(i).click();
                        }
                        if ("信用等级".equals(tabList.get(i).getTextContent())) {
                            tempPage = tabList.get(i).click();
                        }
                        if ("安全认证".equals(tabList.get(i).getTextContent())) {
                            tempPage = tabList.get(i).click();
                        }
                        if ("人员无在建证明（外省）".equals(tabList.get(i).getTextContent())) {
                            tempPage = tabList.get(i).click();
                        }
                        if ("企业不良行为记录".equals(tabList.get(i).getTextContent())) {
                            tempPage = tabList.get(i).click();
                        }
                        if ("人员列表".equals(tabList.get(i).getTextContent())) {
                            tempPage = tabList.get(i).click();
                        }
                    }
                }
                webClient.close();
            }
        }

    }

    /**
     * 基本信息
     *
     * @param basicInfo
     */
    void addBasicInfomation(Document basicInfo) {
        Element roleTab = basicInfo.select("#right_tb").select(".qiye-table-style").get(0);
        Element infoTab = basicInfo.select("#right_tb").select(".qiye-table-style").get(1);
        Element accountNumberTab = basicInfo.select("#right_tb").select(".qiye-table-style").get(2);
        Element businessLicenseTab = basicInfo.select("#right_tb").select(".qiye-table-style").get(3);
        addBasicRole(roleTab);
        addBasicInfo(infoTab);
        addBasicAccountNumber(accountNumberTab);
        addBasicBusinessLicense(businessLicenseTab);
    }

    /**
     * 抓取主体角色
     *
     * @param roleTab
     */
    void addBasicRole(Element roleTab) {
        if (roleTab.select("tr").size() == 1) {
            String roleName = roleTab.select("tr").get(0).select("td").get(1).text();
        }
    }

    /**
     * 抓取基本信息
     *
     * @param infoTab
     */
    void addBasicInfo(Element infoTab) {
        if (infoTab.select("tr").size() > 1) {
            String companyName = infoTab.select("tr").get(0).select("td").get(1).text();
            String mainType = infoTab.select("tr").get(0).select("td").get(3).text();
            String legalPerson = infoTab.select("tr").get(1).select("td").get(1).text();
            String legalPersonPhone = infoTab.select("tr").get(1).select("td").get(3).text();
            String type = infoTab.select("tr").get(2).select("td").get(1).text();
            String industry = infoTab.select("tr").get(2).select("td").get(3).text();
            String nation = infoTab.select("tr").get(3).select("td").get(1).text();
            String registerCode = infoTab.select("tr").get(3).select("td").get(3).text();
            String address = infoTab.select("tr").get(4).select("td").get(1).text();
            String postalCode = infoTab.select("tr").get(4).select("td").get(3).text();
            String contacts = infoTab.select("tr").get(5).select("td").get(1).text();
            String contactsPhone = infoTab.select("tr").get(5).select("td").get(3).text();
        }
    }

    /**
     * 基本账户
     *
     * @param accountNumberTab
     */
    void addBasicAccountNumber(Element accountNumberTab) {
        if (accountNumberTab.select("tr").size() > 1) {
            String companyName = accountNumberTab.select("tr").get(0).select("td").get(1).text();
            String basicAccountNumber = accountNumberTab.select("tr").get(0).select("td").get(3).text();
            String openBank = accountNumberTab.select("tr").get(1).select("td").get(1).text();
            String accountNumber = accountNumberTab.select("tr").get(1).select("td").get(3).text();
        }
    }

    /**
     * 营业执照
     *
     * @param businessLicenseTab
     */
    void addBasicBusinessLicense(Element businessLicenseTab) {
        if (businessLicenseTab.select("tr").size() > 1) {
            String type = businessLicenseTab.select("tr").get(0).select("td").get(1).text();
            String registerCapital = businessLicenseTab.select("tr").get(0).select("td").get(3).text();
            String socialCreditCode = businessLicenseTab.select("tr").get(1).select("td").get(1).text();
            String expirationDate = businessLicenseTab.select("tr").get(1).select("td").get(3).text();
        }
    }


    /**
     * 资质证书
     *
     * @param certPage
     */
    void addQualificationCert(HtmlPage certPage) {
        Document certDoc = Jsoup.parse(certPage.asXml());
        Elements certEle = certDoc.select("#page-list").select("tr");
        String certNo = certEle.select("td").get(1).text();
        String qualificationType = certEle.select("td").get(2).text();
        String grade = certEle.select("td").get(3).text();
        String lssuingOrgan = certEle.select("td").get(4).text();
        String lssuingDate = certEle.select("td").get(5).text();
    }


    /**
     * 企业业绩
     * @param tempPage
     * @param webClient
     * @return 返回原HtmlPage
     */
    HtmlPage addAchievement(HtmlPage tempPage, WebClient webClient) {
        HtmlPage customaryPage = tempPage.cloneNode(true);
        try {
            DomNodeList<HtmlElement> achievementTab = tempPage.getElementById("page-list").getElementsByTagName("tr");
            //遍历企业业绩进入业绩详情
            for (int i = 0; i < achievementTab.size() - 1; i++) {
                HtmlElement achievementDetailHref = achievementTab.get(i).getElementsByAttribute("a", "href", "javascript:void(0);").get(0);
                System.out.println(achievementDetailHref);
                tempPage = achievementDetailHref.click();
                webClient.waitForBackgroundJavaScript(30000);
//              Jsoup.parse(tempPage.asXml());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customaryPage;
    }

    /**
     *
     * @param tabName 分类名称
     * @param companyUuid 公司唯一编号
     * @param webClient
     * @param oldPage 旧的当前页面
     * @return 返回当前页面
     */
    HtmlPage getTabListDate(String tabName, String companyUuid, WebClient webClient, HtmlPage oldPage) {
        HtmlPage currentPage = null;
        if("企业资质".equals(tabName)) {
            //获取企业第一页数据
            ScriptResult result = oldPage.executeJavaScript("javascript:changeQyxx(1,'1','" + companyUuid  + "');");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
        }
        DomElement pagination = currentPage.getElementById("kkpager").querySelector(".infoTextAndGoPageBtnWrap");
        int certPageCount = Integer.parseInt(pagination.querySelector(".totalPageNum").asText());
        //不止一页，遍历其他页数据
        if(certPageCount > 1) {
            for (int j = 2; j <= certPageCount; j++) {
                ScriptResult result = currentPage.executeJavaScript("javascript:return kkpager._clickHandler(" + j + ")");
                currentPage = (HtmlPage) result.getNewPage();
                webClient.waitForBackgroundJavaScript(30000);
            }
        }
        return currentPage;
    }

}
