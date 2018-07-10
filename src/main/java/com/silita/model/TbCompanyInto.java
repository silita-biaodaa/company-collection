package com.silita.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbCompanyInto {
    /**
     * 外省入湘备案自增ID
     */
    private Integer pkid;

    /**
     * 内部ID
     */
    private String qybm;

    /**
     * 企业名称
     */
    private String comName;

    /**
     * 组织机构代码
     */
    private String orgCode;

    /**
     * 工商营业执照号
     */
    private String businessNum;

    /**
     * 注册所在地
     */
    private String regisAddress;

    /**
     * 注册资本
     */
    private String regisCapital;

    /**
     * 企业营业地址
     */
    private String comAddress;

    /**
     * 法人
     */
    private String legalPerson;

    /**
     * 入湘登证号
     */
    private String intoNo;

    /**
     * 登证号有效期
     */
    private String intoValidDate;

    /**
     * 资质证号
     */
    private String certNo;

    /**
     * 安全生产许可证
     */
    private String safeCertNo;

    /**
     * 安需证有效期
     */
    private String safeValidDate;

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
    private String rang;

}