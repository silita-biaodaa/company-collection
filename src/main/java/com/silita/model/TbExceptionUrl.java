package com.silita.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbExceptionUrl {
    /**
     * 主键
     */
    private Integer pkid;

    /**
     * 异常证书url
     */
    private String comQuaUrl;

    /**
     * 异常url
     */
    private String exceptionUrl;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 错误信息
     */
    private String exceptionMsg;

    /**
     * 网站标签类别
     */
    private String tab;

}