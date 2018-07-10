package com.silita.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbCompanyAptitude {
    /**
     * 资质自增ID
     */
    private Integer pkid;

    /**
     * 关联企业证书ID
     */
    private Integer qualId;

    /**
     * 企业id
     */
    private Integer comId;

    /**
     * 资质名称
     */
    private String aptitudeName;

    /**
     * 关联资质UUID
     */
    private String aptitudeUuid;

    private String mainuuid;

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