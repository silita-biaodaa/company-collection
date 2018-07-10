package com.silita.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbProjectDesign {
    /**
     * 勘查设计项目自增ID
     */
    private Integer pkid;

    /**
     * 实体类MD5
     */
    private String md5;

    /**
     * 网站内部ID
     */
    private String sgtxh;

    /**
     * 项目ID
     */
    private Integer proId;

    /**
     * 项目名称
     */
    private String proName;

    /**
     * 项目类型
     */
    private String proType;

    /**
     * 企业ID
     */
    private Integer comId;

    /**
     * 勘查单位
     */
    private String exploreOrg;

    /**
     * 设计单位
     */
    private String designOrg;

    /**
     * 施工图审查单位
     */
    private String checkOrg;

    /**
     * 施工图审查机构组织机构代码 湖南不用
     */
    private String checkOrgCode;

    /**
     * 施工图审查合格书编号
     */
    private String checkNo;

    /**
     * 施工图审查合格书编号 湖南不要
     */
    private String checkNumber;

    /**
     * 施工图审查完成日期
     */
    private String checkFinishDate;

    /**
     * 施工图审查人
     */
    private String checkPerson;

    /**
     * 勘查OR设计类型，勘查：explore；设计：design
     */
    private String type;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 项目规模
     */
    private String proScope;

}