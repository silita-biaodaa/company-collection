package com.silita.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbPerson {
    /**
     * 人主键
     */
    private Integer pkid;

    /**
     * 姓名
     */
    private String name;

    /**
     * 名族
     */
    private String nation;

    /**
     * 性别
     */
    private String sex;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 学历
     */
    private String education;

    /**
     * 学历
     */
    private String degree;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 业务
     */
    public Integer companyId;
}