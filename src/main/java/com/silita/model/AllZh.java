package com.silita.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllZh {

    private String uuid;

    /**
     * 资质类型code
     */
    private String type;

    /**
     * 资质标准表uuid
     */
    private String mainuuid;

    /**
     * 别名
     */
    private String name;

    /**
     * 等级：0=特级，1=一级，2=二级，11=一级及以上，21=二级及以上，31=三级及以上，-1=甲级，-2=乙级，-3=丙级
     */
    private String rank;

    /**
     * 最终uuid：mainUuid+/rank
     */
    private String finaluuid;
}