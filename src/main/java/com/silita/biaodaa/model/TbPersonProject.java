package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbPersonProject {
    /**
     * 项目部人员ID
     */
    private Integer pkid;

    /**
     * 网站内部ID
     */
    private String innerid;

    /**
     * 关联子项目ID
     */
    private Integer pid;

    /**
     * 姓名
     */
    private String name;

    /**
     * 注册类别
     */
    private String category;

    /**
     * 证书编号
     */
    private String certNo;

    /**
     * 安全证书编号
     */
    private String safeNo;

    /**
     * 状态
     */
    private String status;

    /**
     * 监理OR施工类型；监理：supervision；施工：build
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

}