package com.silita.model;

import com.silita.utils.StringUtils;

import java.io.Serializable;

/**
 * @Author:chenzhiqiang
 * @Date:2018/5/8 16:27
 * @Description: 企业资质
 */
public class CompanyQualification implements Serializable {
    private static final long serialVersionUID = -1652063863328030018L;
    /**
     * 企业资质id
     */
    private int pkid;

    /**
     * 来源类型 1表示全国四库一
     */
    private int channel = 1;

    /**
     * 网站内部id
     */
    private String corpid;

    /**
     * 页面tab项
     */
    private String tab;

    /**
     * 资质类别
     */
    private String qual_type;

    /**
     * 证书编号
     */
    private String cert_no;

    /**
     * 发证机构
     */
    private String cert_org;

    /**
     * 发证日期
     */
    private String cert_date;

    /**
     * 证书有效期
     */
    private String valid_date;

    /**
     * 资质名称
     */
    private String qual_name;

    /**
     * 资质范围
     */
    private String range;

    /**
     * url
     */
    private String url;

    /**
     * 企业id
     */
    private String com_id;

    /**
     * 企业名称
     */
    private String com_name;

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
        this.pkid = pkid;
    }

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public String getQual_type() {
        return qual_type;
    }

    public void setQual_type(String qual_type) {
        this.qual_type = qual_type;
    }

    public String getCert_no() {
        return cert_no;
    }

    public void setCert_no(String cert_no) {
        this.cert_no = cert_no;
    }

    public String getCert_org() {
        return cert_org;
    }

    public void setCert_org(String cert_org) {
        this.cert_org = cert_org;
    }

    public String getCert_date() {
        return cert_date;
    }

    public void setCert_date(String cert_date) {
        this.cert_date = cert_date;
    }

    public String getValid_date() {
        return valid_date;
    }

    public void setValid_date(String valid_date) {
        this.valid_date = valid_date;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getQual_name() {
        return qual_name;
    }

    public void setQual_name(String qual_name) {
        this.qual_name = qual_name;
    }

    @Override
    public String toString() {
        return "CompanyQualification{" +
                "pkid=" + pkid +
                ", corpid='" + corpid + '\'' +
                ", tab='" + tab + '\'' +
                ", qual_type='" + qual_type + '\'' +
                ", cert_no='" + cert_no + '\'' +
                ", cert_org='" + cert_org + '\'' +
                ", cert_date='" + cert_date + '\'' +
                ", valid_date='" + valid_date + '\'' +
                ", qual_name='" + qual_name + '\'' +
                ", range='" + range + '\'' +
                ", url='" + url + '\'' +
                ", com_id=" + com_id +
                ", com_name='" + com_name + '\'' +
                '}';
    }

    /**
     *
     * @param com_id 企业id
     * @param cert_no 证书编号
     * @param qual_type 资质类别
     * @param valid_date 证书有效期
     * @return
     */
    public String md5(String com_id, String cert_no, String qual_type, String valid_date) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(StringUtils.defaultIfBlank(com_id, ""));
        buffer.append(StringUtils.defaultIfBlank(cert_no, ""));
        buffer.append(StringUtils.defaultIfBlank(qual_type, ""));
        buffer.append(StringUtils.defaultIfBlank(valid_date, ""));
        return StringUtils.md5(buffer.toString());
    }

    public String entityToMD5(CompanyQualification companyQualification) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(StringUtils.defaultIfBlank(companyQualification.getQual_type(), ""));
        buffer.append(StringUtils.defaultIfBlank(companyQualification.getCert_no(), ""));
        buffer.append(StringUtils.defaultIfBlank(companyQualification.getCert_org(), ""));
        buffer.append(StringUtils.defaultIfBlank(companyQualification.getCert_date(), ""));
        buffer.append(StringUtils.defaultIfBlank(companyQualification.getValid_date(), ""));
        buffer.append(StringUtils.defaultIfBlank(companyQualification.getQual_name(), ""));
        buffer.append(StringUtils.defaultIfBlank(companyQualification.getRange(), ""));
        buffer.append(StringUtils.defaultIfBlank(companyQualification.getUrl(), ""));
        buffer.append(StringUtils.defaultIfBlank(companyQualification.getCom_name(), ""));
        return StringUtils.md5(buffer.toString());
    }
}
