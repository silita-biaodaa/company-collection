package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbCompanyQualification {
    /**
     * 企业资质ID
     */
    private Integer pkid;

    /**
     * 网站内部ID
     */
    private String corpid;

    /**
     * 网站标签类别
     */
    private String tab;

    /**
     * 资质类别
     */
    private String qualType;

    /**
     * 证书编号
     */
    private String certNo;

    /**
     * 发证机构
     */
    private String certOrg;

    /**
     * 发证日期
     */
    private String certDate;

    /**
     * 证书有效期
     */
    private String validDate;

    /**
     * 企业ID
     */
    private Integer comId;

    /**
     * 企业名称
     */
    private String comName;

    /**
     * 资质ID
     */
    private Integer qualId;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 资质范围
     */
    private String range;

    /**
     * 抓取URL
     */
    private String url;

}