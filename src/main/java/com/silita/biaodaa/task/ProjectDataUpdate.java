package com.silita.biaodaa.task;

import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.ICompanyService;
import com.silita.biaodaa.service.IProjectService;
import com.silita.biaodaa.utils.StringUtils;
import org.apache.commons.collections.map.HashedMap;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 91567 on 2018/4/24.
 */
@Component
public class ProjectDataUpdate {

    private int min = 1;
    private int max = 5;
    private Random random = new Random();
    String dateRegex = "(\\d{4}-\\d{1,2}-\\d{1,2})";

    @Autowired
    private IProjectService projectService;
    @Autowired
    private ICompanyService companyService;

    /**
     * 进入施工项目列表后
     * 再便利列表抓项目详情 需要公司cookie
     *
     * @param cookies   cookie
     * @param companyId 公司id
     */
    void getBuilderProjectList(Map<String, String> cookies, String CompanyQualificationUrl, Integer companyId) {
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
                            System.out.println("已抓取这个施工项目" + projectBuildUrl);
                        } else {
                            projectBuildDetailConn = Jsoup.connect(projectBuildUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                            projectBuildDetailDoc = projectBuildDetailConn.get();
                            if (projectBuildDetailConn.response().statusCode() == 200) {
                                if (StringUtils.isNotNull(projectBuildDetailDoc.select("#table1").text())) {
                                    System.out.println(projectBuildUrl);
                                    Elements projectBuildDetailTable = projectBuildDetailDoc.select("#table1");
                                    Elements projectBuilderPeopleTable = projectBuildDetailDoc.select("#ctl00_ContentPlaceHolder1_td_rylist").select("table").select("tr");
                                    String projectInfoDetaiUrl = projectBuildDetailTable.select("a").first().absUrl("href");
                                    //进入项目详情添加项目基本信息
                                    Integer projectId = addBuilderProjectInfo(projectInfoDetaiUrl, CompanyQualificationUrl);
                                    //施工合同段信息
                                    int projectBuilderId = addProjectBuild(projectBuildDetailTable, companyId, projectId, bdxh, proType);
                                    //添加项目部人员（施工）
                                    addBuilderProjectPeople(projectBuilderPeopleTable, projectBuilderId, projectBuildUrl);
                                } else {
                                    System.out.println("很抱歉，暂时无法访问工程项目信息" + projectBuildUrl);
                                }
                            } else {
                                TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                                tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                                tbExceptionUrl.setExceptionUrl(projectBuildUrl);
                                tbExceptionUrl.setExceptionMsg("获取项目详情失败");
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
                System.out.println("获取企业项目列表页失败" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
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
    Integer addBuilderProjectInfo(String projectInfoUrl, String CompanyQualificationUrl) {
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
                return projectService.insertProjectInfo(tbProject);
            } else {
                TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                tbExceptionUrl.setExceptionUrl(projectInfoUrl);
                tbExceptionUrl.setExceptionMsg("获取项目基本信息失败");
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
    void addBuilderProjectPeople(Elements eles, Integer projectBuilderId, String projectBuildUrl) {
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
                    projectService.insertPersonProject(tbPersonProject);
                }
            }
        } else {
            System.out.println("无项目部人员（施工）" + projectBuildUrl);
        }
    }



    /**
     * 进入设计项目列表后
     * 再便利列表抓项目详情 需要公司cookie
     *
     * @param cookies   cookie
     * @param companyId 公司id
     */
    void getDesignProjectList(Map<String, String> cookies, String CompanyQualificationUrl, Integer companyId) {
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
                    Map<String, Object> param;
                    Document projectBuildDetailDoc;
                    Connection projectBuildDetailConn;
                    //遍历公司项目列表url 进入详情页面
                    for (int i = 1; i < projectList.size(); i++) {
                        proType = projectList.get(i).select("td").first().text();
                        projectBuildUrl = projectList.get(i).select("a").first().absUrl("href");
                        //施工图审查信息内部id（设计）
                        String sgtxh = projectBuildUrl.substring(projectBuildUrl.indexOf("=") + 1);
                        param = new HashedMap();
                        param.put("sgtxh", sgtxh);
                        param.put("comId", companyId);
                        param.put("proType", proType);
                        if (projectService.checkProjectDesignExist(param)) {
                            System.out.println("该证书下的设计项目已存在" + projectBuildUrl);
                        } else {
                            projectBuildDetailConn = Jsoup.connect(projectBuildUrl).userAgent("Mozilla").timeout(5000 * 60).ignoreHttpErrors(true);
                            projectBuildDetailDoc = projectBuildDetailConn.get();
                            if (projectBuildDetailConn.response().statusCode() == 200) {
                                System.out.println(projectBuildUrl);
//                                logger.error(projectBuildUrl);
                                if (StringUtils.isNotNull(projectBuildDetailDoc.select("#table1").text())) {
                                    Elements projectBuildDetailTable = projectBuildDetailDoc.select("#table1");
                                    Elements projectDesignPeopleTable = projectBuildDetailDoc.select("#ctl00_ContentPlaceHolder1_td_rylist").select("tr");
                                    String projectInfoDetaiUrl = projectBuildDetailTable.select("a").first().absUrl("href");
                                    //添加项目基本信息
                                    Integer projectId = addDesignProjectInfo(projectInfoDetaiUrl, CompanyQualificationUrl);
                                    //添加施工图审查信息（设计）
                                    int projectDesignId = addProjectDesign(projectBuildDetailTable, companyId, projectId, sgtxh, proType);
                                    //添加设计人员名单
                                    addDesignPeople(projectDesignPeopleTable, projectDesignId, projectBuildUrl);
                                } else {
                                    System.out.println("很抱歉，暂时无法访问工程项目信息" + projectBuildUrl);
                                }
                            } else {
                                TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                                tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                                tbExceptionUrl.setExceptionUrl(projectBuildUrl);
                                tbExceptionUrl.setExceptionMsg("获取项目详情失败");
                                companyService.insertException(tbExceptionUrl);
                            }
                        }
                    }
                    //随机暂停几秒
                    Thread.sleep(000 * (random.nextInt(max) % (max - min + 1)));
                } else {
                    System.out.println("该企业项目数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                }
            } else {
                System.out.println("获取企业项目列表页失败" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
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
    Integer addDesignProjectInfo(String projectInfoUrl, String CompanyQualificationUrl) {
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
                return projectService.insertProjectInfo(tbProject);
            } else {
                TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                tbExceptionUrl.setExceptionUrl(projectInfoUrl);
                tbExceptionUrl.setExceptionMsg("获取项目基本信息失败");
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
     * 施工图审查信息（设计）
     *
     * @param eles
     * @param companyId
     * @param projectId
     * @param sgtxh
     */
    int addProjectDesign(Elements eles, Integer companyId, Integer projectId, String sgtxh, String proType) {
        TbProjectDesign tbProjectDesign = new TbProjectDesign();
        tbProjectDesign.setProName(eles.select("#ctl00_ContentPlaceHolder1_hl_gcmc").text());
        tbProjectDesign.setProScope(eles.select("#ctl00_ContentPlaceHolder1_lbl_gm").text());
        tbProjectDesign.setExploreOrg(eles.select("#ctl00_ContentPlaceHolder1_td_kcdw").text());
        tbProjectDesign.setDesignOrg(eles.select("#ctl00_ContentPlaceHolder1_td_sjdw").text());
        tbProjectDesign.setCheckOrg(eles.select("#ctl00_ContentPlaceHolder1_td_scdw").text());
        tbProjectDesign.setCheckNo(eles.select("#ctl00_ContentPlaceHolder1_lbl_sgtscbh").text());
        tbProjectDesign.setCheckFinishDate(eles.select("#ctl00_ContentPlaceHolder1_lbl_scrq").text());
        tbProjectDesign.setCheckPerson(eles.select("#ctl00_ContentPlaceHolder1_lbl_scryxm").text());
        tbProjectDesign.setType("design");
        tbProjectDesign.setProType(proType);
        tbProjectDesign.setSgtxh(sgtxh);
        tbProjectDesign.setComId(companyId);
        tbProjectDesign.setProId(projectId);
        return projectService.insertProjectDesign(tbProjectDesign);
    }

    /**
     * 勘察设计人员名单(设计)
     *
     * @param eles             表格数据
     * @param projectDesignId  项目设计id
     * @param projectDesignUrl 项目设计详情Url
     */
    void addDesignPeople(Elements eles, Integer projectDesignId, String projectDesignUrl) {
        if (eles.size() > 2) {
            TbPersonProject tbPersonProject;
            for (int i = 2; i < eles.size() - 1; i++) {
                if (StringUtils.isNotNull(eles.get(i).text())) {
                    tbPersonProject = new TbPersonProject();
                    tbPersonProject.setName(eles.get(i).select("td").get(0).text());
                    tbPersonProject.setCategory(eles.get(i).select("td").get(1).text());
                    tbPersonProject.setComName(eles.get(i).select("td").get(2).text());
                    tbPersonProject.setRole(eles.get(i).select("td").get(3).text());
                    tbPersonProject.setType("design");
                    String peopleDetailUrl = eles.get(i).select("td").get(0).select("a").attr("href");
                    tbPersonProject.setInnerid(peopleDetailUrl.substring(peopleDetailUrl.indexOf("=") + 1));
                    tbPersonProject.setPid(projectDesignId);
                    projectService.insertPersonProject(tbPersonProject);
                }
            }
        } else {
            System.out.println("无勘察设计人员名单人员（设计）" + projectDesignUrl);
        }
    }



    /**
     * 进入勘察项目列表后
     * 再便利列表抓项目详情 需要公司cookie
     *
     * @param cookies   cookie
     * @param companyId 公司id
     */
    void getSurveyProjectList(Map<String, String> cookies, String CompanyQualificationUrl, Integer companyId) {
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
                    Map<String, Object> param;
                    Document projectBuildDetailDoc;
                    Connection projectBuildDetailConn;
                    //遍历公司项目列表url 进入详情页面
                    for (int i = 1; i < projectList.size(); i++) {
                        proType = projectList.get(i).select("td").first().text();
                        projectBuildUrl = projectList.get(i).select("a").first().absUrl("href");
                        String sgtxh = projectBuildUrl.substring(projectBuildUrl.indexOf("=") + 1);
                        param = new HashedMap();
                        param.put("sgtxh", sgtxh);
                        param.put("comId", companyId);
                        param.put("proType", proType);
                        if (projectService.checkProjectDesignExist(param)) {
                            System.out.println("该证书下的勘察项目已存在" + projectBuildUrl);
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
                                    //添加项目基本信息
                                    Integer projectId = addSurveyProjectInfo(projectInfoDetaiUrl, CompanyQualificationUrl);
                                    //添加施工图审查信息（勘察）
                                    int projectSurveyId = addProjectSurvey(projectBuildDetailTable, companyId, projectId, sgtxh, proType);
                                    //勘察人员名单
                                    addSurveyPeople(projectBuilderPeopleTable, projectSurveyId, projectBuildUrl);
                                } else {
                                    System.out.println("很抱歉，暂时无法访问工程项目信息" + projectBuildUrl);
                                }
                            } else {
                                TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                                tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                                tbExceptionUrl.setExceptionUrl(projectBuildUrl);
                                tbExceptionUrl.setExceptionMsg("获取项目详情失败");
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
                System.out.println("获取企业项目列表页失败" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
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
    Integer addSurveyProjectInfo(String projectInfoUrl, String CompanyQualificationUrl) {
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
                return projectService.insertProjectInfo(tbProject);
            } else {
                TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                tbExceptionUrl.setExceptionUrl(projectInfoUrl);
                tbExceptionUrl.setExceptionMsg("获取项目基本信息失败");
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
     * 施工图审查信息（勘察）
     *
     * @param eles      表格数据
     * @param companyId 公司id
     * @param projectId 项目id
     * @param sgtxh     内部id
     */
    int addProjectSurvey(Elements eles, Integer companyId, Integer projectId, String sgtxh, String proType) {
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
        tbProjectDesign.setProType(proType);
        tbProjectDesign.setSgtxh(sgtxh);
        tbProjectDesign.setComId(companyId);
        tbProjectDesign.setProId(projectId);
        return projectService.insertProjectDesign(tbProjectDesign);
    }

    /**
     * 勘察设计人员名单（勘察）
     *
     * @param eles             表格数据
     * @param projectSurveyId  项目设计id
     * @param projectDesignUrl 项目设计详情Url
     */
    void addSurveyPeople(Elements eles, Integer projectSurveyId, String projectDesignUrl) {
        if (eles.size() > 2) {
            TbPersonProject tbPersonProject;
            for (int i = 2; i < eles.size() - 1; i++) {
                if (StringUtils.isNotNull(eles.get(i).text())) {
                    tbPersonProject = new TbPersonProject();
                    tbPersonProject.setName(eles.get(i).select("td").get(0).text());
                    tbPersonProject.setCategory(eles.get(i).select("td").get(1).text());
                    tbPersonProject.setComName(eles.get(i).select("td").get(2).text());
                    tbPersonProject.setRole(eles.get(i).select("td").get(3).text());
                    tbPersonProject.setType("design");
                    String peopleDetailUrl = eles.get(i).select("td").get(0).select("a").attr("href");
                    tbPersonProject.setInnerid(peopleDetailUrl.substring(peopleDetailUrl.indexOf("=") + 1));
                    tbPersonProject.setPid(projectSurveyId);
                    projectService.insertPersonProject(tbPersonProject);
                }
            }
        } else {
            System.out.println("无勘察设计人员名单人员（勘察）" + projectDesignUrl);
        }
    }



    /**
     * 进入监理项目列表后
     * 再便利列表抓项目详情 需要公司cookie
     *
     * @param cookies   cookie
     * @param companyId 公司id
     */
    void getSupervisorProjectList(Map<String, String> cookies, String CompanyQualificationUrl, Integer companyId) {
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
                        //监理合同段信息内部id
                        String jlbdxh = projectBuildUrl.substring(projectBuildUrl.indexOf("=") + 1);
                        params.put("jlbdxh", jlbdxh);
                        params.put("comId", companyId);
                        if (projectService.checkProjectSupervisionExist(params)) {
                            System.out.println("该证书下的监理项目已存在" + projectBuildUrl);
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
                                    //添加项目基本信息
                                    Integer projectId = addSupervisorProjectInfo(projectInfoDetaiUrl, CompanyQualificationUrl);
                                    //监理合同段信息
                                    int projectSupervisorId = addProjectSupervisor(projectBuildDetailTable, companyId, projectId, jlbdxh, proType);
                                    //添加项目部人员（监理）
                                    addSupervisorProjectPeople(projectBuilderPeopleTable, projectSupervisorId, projectBuildUrl);
                                } else {
                                    System.out.println("很抱歉，暂时无法访问工程项目信息" + projectBuildUrl);
                                }
                            } else {
                                TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                                tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                                tbExceptionUrl.setExceptionUrl(projectBuildUrl);
                                tbExceptionUrl.setExceptionMsg("获取项目详情失败");
                                companyService.insertException(tbExceptionUrl);
                            }
                        }
                        //随机暂停几秒
                        Thread.sleep(000 * (random.nextInt(max) % (max - min + 1)));
                    }
                } else {
                    System.out.println("该企业项目数据为空" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
                }
            } else {
                System.out.println("获取企业项目列表页失败" + "http://qyryjg.hunanjz.com/public/EnterpriseDetail.aspx?corpid=" + companyId);
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
    Integer addSupervisorProjectInfo(String projectInfoUrl, String CompanyQualificationUrl) {
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
                return projectService.insertProjectInfo(tbProject);
            } else {
                TbExceptionUrl tbExceptionUrl = new TbExceptionUrl();
                tbExceptionUrl.setComQuaUrl(CompanyQualificationUrl);
                tbExceptionUrl.setExceptionUrl(projectInfoUrl);
                tbExceptionUrl.setExceptionMsg("获取项目基本信息失败");
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
     * 添加监理合同段信息
     *
     * @param eles      表格数据
     * @param companyId 公司id
     * @param projectId 项目id
     * @param jlbdxh    内部id
     */
    int addProjectSupervisor(Elements eles, Integer companyId, Integer projectId, String jlbdxh, String proType) {
        TbProjectSupervision tbProjectSupervision = new TbProjectSupervision();
        tbProjectSupervision.setProName(eles.select("#ctl00_ContentPlaceHolder1_hl_gcmc").text());
        tbProjectSupervision.setBScope(eles.select("#ctl00_ContentPlaceHolder1_lbl_bdgm").text());
        tbProjectSupervision.setSuperOrg(eles.select("#ctl00_ContentPlaceHolder1_td_sgdw").text());
        tbProjectSupervision.setBidRemark(eles.select("#ctl00_ContentPlaceHolder1_lbl_zbba").text());
        String conteactRemarkStr = eles.select("#ctl00_ContentPlaceHolder1_lbl_htba").text();
        tbProjectSupervision.setContractRemark(conteactRemarkStr);
        if (StringUtils.isNotNull(conteactRemarkStr)) {
            if (conteactRemarkStr.contains("合同日期") && conteactRemarkStr.contains("合同价格")) {
                tbProjectSupervision.setContractDate(conteactRemarkStr.substring(conteactRemarkStr.indexOf("合同日期") + 5, conteactRemarkStr.indexOf("合同价格") - 1));
                tbProjectSupervision.setContractPrice(conteactRemarkStr.substring(conteactRemarkStr.indexOf("合同价格") + 5, conteactRemarkStr.indexOf("万元")));
            }
        }
        tbProjectSupervision.setProType(proType);
        tbProjectSupervision.setJlbdxh(jlbdxh);
        tbProjectSupervision.setComId(companyId);
        tbProjectSupervision.setProId(projectId);
        return projectService.insertProjectSupervisor(tbProjectSupervision);
    }

    /**
     * 添加项目部人员（监理）
     *
     * @param eles             表格数据
     * @param projectBuilderId 项目施工id
     * @param projectBuildUrl  项目施工详情Url
     */
    void addSupervisorProjectPeople(Elements eles, Integer projectBuilderId, String projectBuildUrl) {
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
                    tbPersonProject.setType("supervision");
                    String peopleDetailId = eles.get(i).select("td").get(0).select("a").attr("href");
                    tbPersonProject.setInnerid(peopleDetailId.substring(peopleDetailId.indexOf("=") + 1));
                    tbPersonProject.setPid(projectBuilderId);
                    projectService.insertPersonProject(tbPersonProject);
                }
            }
        } else {
            System.out.println("无项目部人员（监理）" + projectBuildUrl);
        }
    }


}
