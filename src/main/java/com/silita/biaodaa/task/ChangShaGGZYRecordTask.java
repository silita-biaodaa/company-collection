package com.silita.biaodaa.task;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
        //遍历页数
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
            //遍历每页列表数据
            Elements listDate = doc.select("ul").select("[target=\"_blank\"]");
            try {
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

                    String companyInnerId = hPage.getElementById("gid").getAttribute("value");
                    //########基本信息#########
                    addBasicInfomation(hPage);
                    //#######其他分类数据#######
                    DomNodeList<HtmlElement> tabList = hPage.getElementById("ul_qyxx").getElementsByTagName("li");
                    if (!tabList.isEmpty()) {
                        //遍历右侧tab页详情
                        for (int i = 1; i < tabList.size(); i++) {
                            String tabName = tabList.get(i).getTextContent();
                            //根据tabName名称抓取不同的ajax 数据页面
                            getTabListDate(tabName, companyInnerId, webClient, hPage);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                webClient.close();
            }
        }
    }

    /**
     * 基本信息
     * 包含 主体角色、基本信息、基本账户、营业执照
     *
     * @param hPage
     */
    void addBasicInfomation(HtmlPage hPage) {
        Document basicInfo = Jsoup.parse(hPage.asXml());
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
     * @param currentPage
     */
    void addQualificationCert(HtmlPage currentPage) {
        Document certDoc = Jsoup.parse(currentPage.asXml());
        Elements certEle = certDoc.select("#page-list").select("tr");
        if (certEle.size() != 2) {
            for (int i = 0; i < certEle.size() - 1; i++) {
                String certNo = certEle.get(i).select("td").get(1).text();
                String qualificationType = certEle.get(i).select("td").get(2).text();
                String grade = certEle.get(i).select("td").get(3).text();
                String lssuingOrgan = certEle.get(i).select("td").get(4).text();
                String lssuingDate = certEle.get(i).select("td").get(5).text();
                System.out.println(qualificationType);
            }
        } else {
            System.out.println("暂无数据信息");
        }
    }


    /**
     * 企业业绩
     *
     * @param tempPage  原浏览页面
     * @param webClient 当前浏览器载体
     * @return 原浏览页面
     */
    HtmlPage addAchievement(HtmlPage tempPage, WebClient webClient) {
        Elements tableEle = null;
        HtmlElement detailHref;
        HtmlPage currentPage = tempPage.cloneNode(true);
        try {
            DomNodeList<HtmlElement> achievementTab = tempPage.getElementById("page-list").getElementsByTagName("tr");
            if (achievementTab.size() != 2) {
                //遍历企业业绩进入业绩详情
                for (int i = 0; i < achievementTab.size() - 1; i++) {
                    detailHref = achievementTab.get(i).getElementsByAttribute("a", "href", "javascript:void(0);").get(0);
                    System.out.println(detailHref);
                    tempPage = detailHref.click();
                    webClient.waitForBackgroundJavaScript(30000);
                    tableEle = Jsoup.parse(tempPage.asXml()).select(".qiye-table-style");
                    String oldCompanyName = tableEle.select("tr").get(0).text();
                    String projectName = tableEle.select("tr").get(1).select("td").get(1).text();
                    String projectOwner = tableEle.select("tr").get(2).select("td").get(1).text();
                    String projectLeader = tableEle.select("tr").get(3).select("td").get(1).text();
                    String projectLeaderID = tableEle.select("tr").get(3).select("td").get(3).text();
                    String projectType = tableEle.select("tr").get(4).select("td").get(1).text();
                    String projectAddress = tableEle.select("tr").get(4).select("td").get(3).text();
                    String projectCost = tableEle.select("tr").get(5).select("td").get(1).text();
                    String projectPrice = tableEle.select("tr").get(5).select("td").get(3).text();
                    String TechnicalLeader = tableEle.select("tr").get(6).select("td").get(1).text();
                    String TechnicalLeaderID = tableEle.select("tr").get(6).select("td").get(3).text();
                    String noticeNumber = tableEle.select("tr").get(7).select("td").get(1).text();
                    String completedDate = tableEle.select("tr").get(8).select("td").get(1).text();
                    String projectDescription = tableEle.select("tr").get(9).select("td").get(1).text();
                }
            } else {
                System.out.println("暂无数据信息");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentPage;
    }

    /**
     * 企业获奖
     *
     * @param tempPage  原浏览页面
     * @param webClient 当前浏览器载体
     * @return 原浏览页面
     */
    HtmlPage addAward(HtmlPage tempPage, WebClient webClient) {
        Elements awardEle = null;
        HtmlElement detailHref;
        HtmlPage currentPage = tempPage.cloneNode(true);
        try {
            DomNodeList<HtmlElement> achievementTab = tempPage.getElementById("page-list").getElementsByTagName("tr");
            if (achievementTab.size() != 2) {
                //遍历企业业绩进入业绩详情
                for (int i = 0; i < achievementTab.size() - 1; i++) {
                    detailHref = achievementTab.get(i).getElementsByAttribute("a", "href", "javascript:void(0);").get(0);
                    System.out.println(detailHref);
                    tempPage = detailHref.click();
                    webClient.waitForBackgroundJavaScript(30000);
                    awardEle = Jsoup.parse(tempPage.asXml()).select(".qiye-table-style");
                    String awardName = awardEle.select("tr").get(0).select("td").get(1).text();
                    String projectName = awardEle.select("tr").get(1).select("td").get(1).text();
                    String awardDate = awardEle.select("tr").get(2).select("td").get(1).text();
                    String AwardingDepartment = awardEle.select("tr").get(2).select("td").get(3).text();
                    String awardContent = awardEle.select("tr").get(3).select("td").get(1).text();
                }
            } else {
                System.out.println("暂无数据信息");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentPage;
    }

    /**
     * 信用等级
     *
     * @param currentPage
     */
    void addCreditRating(HtmlPage currentPage) {
        Document creditDoc = Jsoup.parse(currentPage.asXml());
        Elements creditEle = creditDoc.select("#page-list").select("tr");
        if (creditEle.size() != 2) {
            for (int i = 0; i < creditEle.size() - 1; i++) {
                String grade = creditEle.get(i).select("td").get(1).text();
                String openDate = creditEle.get(i).select("td").get(2).text();
                System.out.println(grade);
            }
        } else {
            System.out.println("暂无数据信息");
        }
    }

    /**
     * 安全认证
     *
     * @param currentPage
     */
    void addSecurity(HtmlPage currentPage) {
        Document securityDoc = Jsoup.parse(currentPage.asXml());
        Elements securityEle = securityDoc.select("#right_tb").select("[class=qiye-table-style]");
        String rank = securityEle.select("tr").get(0).select("td").get(1).text();
        String grade = securityEle.select("tr").get(0).select("td").get(3).text();
        System.out.println(rank);
    }

    /**
     * 人员无在建证明（外省）
     *
     * @param currentPage
     */
    void addNoProofConstruction(HtmlPage currentPage) {
        Document creditDoc = Jsoup.parse(currentPage.asXml());
        Elements creditEle = creditDoc.select("#right_tb").select("[class=qiye-table-style]");
        String issuingUnit = creditEle.select("tr").get(0).select("td").get(1).text();
        String issuingDate = creditEle.select("tr").get(1).select("td").get(1).text();
        String expirationDate = creditEle.select("tr").get(1).select("td").get(3).text();
        System.out.println(issuingUnit);
    }

    /**
     * 企业不良行为记录
     *
     * @param currentPage
     */
    void addNquenentCaonduct(HtmlPage currentPage) {
        Document creditDoc = Jsoup.parse(currentPage.asXml());
        Elements creditEle = creditDoc.select("#page-list").select("tr");
        if (creditEle.size() != 2) {
            // TODO: 2018/5/30
        } else {
            System.out.println("暂无数据信息");
        }
    }

    /**
     * 添加企业人员信息
     * @param tempPage
     * @param webClient
     * @return
     */
    HtmlPage addEmployee(HtmlPage tempPage, WebClient webClient) {
        HtmlElement detailHref;
        HtmlPage currentPage = tempPage.cloneNode(true);
        try {
            DomNodeList<HtmlElement> achievementTab = tempPage.getElementById("page-list").getElementsByTagName("tr");
            if (achievementTab.size() != 2) {
                Document employeeDoc;
                Elements employeeBasicInfo;
                //遍历人员列表进入业绩详情
                for (int i = 0; i < achievementTab.size() - 1; i++) {
                    detailHref = achievementTab.get(i).getElementsByAttribute("a", "href", "javascript:void(0);").get(0);
                    tempPage = detailHref.click();
                    webClient.waitForBackgroundJavaScript(30000);
                    employeeDoc = Jsoup.parse(tempPage.asXml());
                    employeeBasicInfo = employeeDoc.select(".qiye-table-style").select("tr");
                    //#######其他分类数据#######
                    Elements tabs = employeeBasicInfo.select("#right_tb").select(".employee_tab li");
                    String scriptStr = tabs.first().select("a").attr("onclick");
                    String employeeInnerId = scriptStr.substring(scriptStr.indexOf(",'"), scriptStr.indexOf("',"));
                    for (int j = 1; j < tabs.size(); j++) {
                        String tabName = tabs.select("a").text();
                        //抓取人员其他tab页
                        getEmployeeTabDate(tabName, employeeInnerId, webClient, tempPage);
                    }
                }
            } else {
                System.out.println("暂无数据信息");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentPage;
    }


    /**
     * 根据tabName名称获取不同的ajax 数据页面（公司信息）
     *
     * @param tabName     分类名称
     * @param companyUuid 网站内部公司唯一编号
     * @param webClient   当前浏览器载体
     * @param oldPage     当前浏览的页面
     * @return 返回当前页面
     */
    HtmlPage getTabListDate(String tabName, String companyUuid, WebClient webClient, HtmlPage oldPage) {
        HtmlPage currentPage = null;
        if ("企业资质".equals(tabName)) {
            System.out.println("###开始抓取企业资质###");
            ScriptResult result = oldPage.executeJavaScript("javascript:changeQyxx(1,'1','" + companyUuid + "');");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
            addQualificationCert(currentPage);
        }
        if ("企业业绩".equals(tabName)) {
            System.out.println("###开始抓取企业业绩###");
            ScriptResult result = oldPage.executeJavaScript("javascript:changeQyxx(1,'2','" + companyUuid + "');");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
            currentPage = addAchievement(currentPage, webClient);
        }
        if ("企业获奖".equals(tabName)) {
            System.out.println("###开始抓取企业获奖###");
            ScriptResult result = oldPage.executeJavaScript("javascript:changeQyxx(1,'3','" + companyUuid + "');");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
            currentPage = addAward(currentPage, webClient);
        }
        if ("信用等级".equals(tabName)) {
            System.out.println("###开始抓取信用等级###");
            ScriptResult result = oldPage.executeJavaScript("javascript:changeQyxx(1,'4','" + companyUuid + "');");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
            addCreditRating(currentPage);
        }
        if ("安全认证".equals(tabName)) {
            System.out.println("###开始抓取安全认证###");
            ScriptResult result = oldPage.executeJavaScript("javascript:changeQyxx(1,'5','" + companyUuid + "');");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
            addSecurity(currentPage);
        }
        if ("人员无在建证明（外省）".equals(tabName)) {
            System.out.println("###开始抓取人员无在建证明（外省）###");
            ScriptResult result = oldPage.executeJavaScript("javascript:changeQyxx(1,'7','" + companyUuid + "');");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
            addNoProofConstruction(currentPage);
        }
        if ("企业不良行为记录".equals(tabName)) {
            System.out.println("###企业不良行为记录###");
            ScriptResult result = oldPage.executeJavaScript("javascript:changeQyxx(1,'8','" + companyUuid + "');");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
            addNquenentCaonduct(currentPage);
        }
        if ("人员列表".equals(tabName)) {
            System.out.println("###人员列表###");
            ScriptResult result = oldPage.executeJavaScript("javascript:changeQyxx(1,'9','" + companyUuid + "');");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
            currentPage = addAward(currentPage, webClient);
        }

        //###获取分页列表数据###
        if((!"安全认证".equals(tabName)) && (!"人员无在建证明（外省）".equals(tabName))) {
            DomElement pagination = currentPage.getElementById("kkpager").querySelector(".infoTextAndGoPageBtnWrap");
            int certPageCount = Integer.parseInt(pagination.querySelector(".totalPageNum").asText());
            if (certPageCount > 1) {
                for (int j = 2; j <= certPageCount; j++) {
                    ScriptResult result = oldPage.executeJavaScript("javascript:return kkpager._clickHandler(" + j + ")");
                    currentPage = (HtmlPage) result.getNewPage();
                    webClient.waitForBackgroundJavaScript(20000);
                    if ("企业资质".equals(tabName)) {
                        addQualificationCert(currentPage);
                    }
                    if ("企业业绩".equals(tabName)) {
                        addAchievement(currentPage, webClient);
                    }
                    if ("企业获奖".equals(tabName)) {
                        addAward(currentPage, webClient);
                    }
                    if ("信用等级".equals(tabName)) {
                        addCreditRating(currentPage);
                    }
                    if ("企业不良行为记录".equals(tabName)) {
                        addNoProofConstruction(currentPage);
                    }
                }
            }
        }
        return currentPage;
    }

    /**
     * 根据tabName名称获取不同的ajax 数据页面(人员信息)
     * @param tabName
     * @param employeeInnerId
     * @param webClient
     * @param oldPage
     */
    void getEmployeeTabDate(String tabName, String employeeInnerId, WebClient webClient, HtmlPage oldPage) {
        HtmlPage currentPage = null;
        if("人员资质".equals(tabName)) {
            ScriptResult result = oldPage.executeJavaScript("javascript:employeeInformation('2','" + employeeInnerId + "',this");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
            DomElement certTab = currentPage.querySelector(".prodject").querySelector("ul");
            if(certTab.getChildElementCount() > 1) {
//                oldPage
            }
        }
        if("人员业绩".equals(tabName)) {
            ScriptResult result = oldPage.executeJavaScript("javascript:employeeInformation('3','" + employeeInnerId + "',this");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
        }
        if("人员获奖".equals(tabName)) {
            ScriptResult result = oldPage.executeJavaScript("javascript:employeeInformation('4','" + employeeInnerId + "',this");
            currentPage = (HtmlPage) result.getNewPage();
            webClient.waitForBackgroundJavaScript(30000);
        }
    }

}
