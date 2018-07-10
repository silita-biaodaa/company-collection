package com.silita.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbProject {
    /**
     * 项目ID
     */
    private Integer proId;

    /**
     * 实体MD5
     */
    private String md5;

    /**
     * 网站内部ID
     */
    private String xmid;

    /**
     * 项目所属企业名称 湖南不用
     */
    private String comName;

    /**
     * 项目名称
     */
    private String proName;

    /**
     * 省级项目编号
     */
    private String proNo;

    /**
     * 项目编号
     */
    private String projectNo;

    /**
     * 建设单位
     */
    private String proOrg;

    /**
     * 建设单位组织机构代码 湖南不用
     */
    private String proOrgCode;

    /**
     * 所在市州
     */
    private String proWhere;

    /**
     * 项目地点
     */
    private String proAddress;

    /**
     * 计划总投资额
     */
    private String investAmount;

    /**
     * 立项批准文号
     */
    private String approvalNum;

    /**
     * 工程类别
     */
    private String proType;

    /**
     * 建设性质
     */
    private String buildType;

    /**
     * 建设面积 湖南不用
     */
    private String acreage;

    /**
     * 建设用地规划许可证
     */
    private String landLicence;

    /**
     * 工程规划许可证
     */
    private String planLicence;

    /**
     * 资金来源
     */
    private String moneySource;

    /**
     * 立项级别
     */
    private String proLevel;

    /**
     * 工程用途
     */
    private String proUse;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 工程规模
     */
    private String proScope;

    /**
     * url 湖南不用
     */
    private String url;

}