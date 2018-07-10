package com.silita.model;

import com.silita.utils.StringUtils;

import java.io.Serializable;

/**
 * @Author:chenzhiqiang
 * @Date:2018/5/8 11:53
 * @Description: 企业基本信息
 */
public class Company implements Serializable {
    private static final long serialVersionUID = 2680866379272673386L;
    /**
     * 企业id
     */
    private String com_id;
    /**
     * 企业名称
     */
    private String com_name;

    /**
     * 企业类型
     */
    private String type;

    /**
     * 统一社会信用代码
     */
    private String credit_code;

    /**
     * 组织机构代码
     */
    private String org_code;
    /**
     * 工商营业执照
     */
    private String business_num;
    /**
     * 注册地址
     */
    private String regis_address;
    /**
     * 企业营业地址
     */
    private String com_address;
    /**
     * 法人
     */
    private String legal_person;
    /**
     * 经济类型
     */
    private String economic_type;
    /**
     * 注册资本
     */
    private String regis_capital;

    /**
     * url
     */
    private String url;

    public String getCom_id() {
        return com_id;
    }

    public void setCom_id(String com_id) {
        this.com_id = com_id;
    }

    public String getCom_name() {
        return com_name;
    }

    public void setCom_name(String com_name) {
        this.com_name = com_name;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getBusiness_num() {
        return business_num;
    }

    public void setBusiness_num(String business_num) {
        this.business_num = business_num;
    }

    public String getRegis_address() {
        return regis_address;
    }

    public void setRegis_address(String regis_address) {
        this.regis_address = regis_address;
    }

    public String getCom_address() {
        return com_address;
    }

    public void setCom_address(String com_address) {
        this.com_address = com_address;
    }

    public String getLegal_person() {
        return legal_person;
    }

    public void setLegal_person(String legal_person) {
        this.legal_person = legal_person;
    }

    public String getEconomic_type() {
        return economic_type;
    }

    public void setEconomic_type(String economic_type) {
        this.economic_type = economic_type;
    }

    public String getRegis_capital() {
        return regis_capital;
    }

    public void setRegis_capital(String regis_capital) {
        this.regis_capital = regis_capital;
    }

    public String getCredit_code() {
        return credit_code;
    }

    public void setCredit_code(String credit_code) {
        this.credit_code = credit_code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Company{" +
                "com_id='" + com_id + '\'' +
                ", com_name='" + com_name + '\'' +
                ", type='" + type + '\'' +
                ", credit_code='" + credit_code + '\'' +
                ", org_code='" + org_code + '\'' +
                ", business_num='" + business_num + '\'' +
                ", regis_address='" + regis_address + '\'' +
                ", com_address='" + com_address + '\'' +
                ", legal_person='" + legal_person + '\'' +
                ", economic_type='" + economic_type + '\'' +
                ", regis_capital='" + regis_capital + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    /**
     *
     * @param com_name 企业名称
     * @param credit_code 统一社会信用代码
     * @param org_code 组织机构代码
     * @param business_num 工商营业执照
     * @return
     */
    public String md5(String com_name, String credit_code, String org_code, String business_num) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(StringUtils.defaultIfBlank(com_name, ""));
        buffer.append(StringUtils.defaultIfBlank(credit_code, ""));
        buffer.append(StringUtils.defaultIfBlank(org_code, ""));
        buffer.append(StringUtils.defaultIfBlank(business_num, ""));
        return StringUtils.md5(buffer.toString());
    }

    public String entityToMD5(Company company) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(StringUtils.defaultIfBlank(company.getCom_name(), ""));
        buffer.append(StringUtils.defaultIfBlank(company.getCredit_code(), ""));
        buffer.append(StringUtils.defaultIfBlank(company.getOrg_code(), ""));
        buffer.append(StringUtils.defaultIfBlank(company.getBusiness_num(), ""));
        buffer.append(StringUtils.defaultIfBlank(company.getRegis_address(), ""));
        buffer.append(StringUtils.defaultIfBlank(company.getCom_address(), ""));
        buffer.append(StringUtils.defaultIfBlank(company.getLegal_person(), ""));
        buffer.append(StringUtils.defaultIfBlank(company.getEconomic_type(), ""));
        buffer.append(StringUtils.defaultIfBlank(company.getRegis_capital(), ""));
        buffer.append(StringUtils.defaultIfBlank(company.getUrl(), ""));
        return StringUtils.md5(buffer.toString());
    }
}
