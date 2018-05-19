package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbPersonHunan {
    /**
     * 人员资质主键
     */
    private Integer pkid;

    /**
     * 人员名称（用于业务查询）
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 网站内部ID
     */
    private String innerid;

    /**
     * 注册类别
     */
    private String category;

    /**
     * 证书编号
     */
    private String certNo;

    /**
     * 执业印章号
     */
    private String sealNo;

    /**
     * 注册专业
     */
    private String major;

    /**
     * 注册日期
     */
    private String certDate;

    /**
     * 有效期
     */
    private String validDate;

    /**
     * 企业ID
     */
    private Integer comId;

    /**
     * 单位名称
     */
    private String comName;

    /**
     * 1、注册执业信息2、其他资格信息
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    private Integer px;

    /**
     * 抓取URL
     */
    private String url;

    /**
     *
     */
    private String flag;

}