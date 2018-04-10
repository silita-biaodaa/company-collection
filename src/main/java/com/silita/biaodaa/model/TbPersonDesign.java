package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbPersonDesign {
    /**
     * 勘查设计人员自增ID
     */
    private Integer pkid;

    /**
     * 网站内部ID
     */
    private String innerid;

    /**
     * 关联项目ID
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
     * 企业名称
     */
    private String comName;

    /**
     * 担任角色
     */
    private String role;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

}