package com.silita.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbPersonChange {
    /**
     * 自增主键
     */
    private Integer pkid;

    /**
     * 实体类MD5
     */
    private String md5;

    /**
     * 单位名称
     */
    private String comName;

    /**
     * 注册专业
     */
    private String major;

    /**
     * 变更日期
     */
    private String changeDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 人员ID
     */
    private Integer perId;

    /**
     * 人员唯一标准
     */
    private String flag;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

}