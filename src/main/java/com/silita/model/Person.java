package com.silita.model;

import com.silita.utils.StringUtils;

import java.io.Serializable;

/**
 * @Author:chenzhiqiang
 * @Date:2018/5/12 16:09
 * @Description: 人员基本信息、资质
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 5908416762655580767L;
    /**
     * 人员主键
     */
    private int pkid;

    /**
     * 表名
     */
    private String table;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 身份证号码
     */
    private String id_card;

    /**
     * 网站内部id
     */
    private String innerid;

    /**
     * 注册类别
     */
    private String category;

    /**
     * 证书编号
     */
    private String cert_no;

    /**
     * 执业印章号
     */
    private String seal_no;

    /**
     * 注册专业
     */
    private String major;

    /**
     * 注册日期
     */
    private String cert_date;

    /**
     * 有效期
     */
    private String valid_date;

    /**
     * url
     */
    private String url;

    /**
     * 唯一标志
     */
    private String flag;

    /**
     * 所在企业id
     */
    private String com_id;

    /**
     * 所在企业名称
     */
    private String com_name;

    private int type = 1;

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
        this.pkid = pkid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getInnerid() {
        return innerid;
    }

    public void setInnerid(String innerid) {
        this.innerid = innerid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCert_no() {
        return cert_no;
    }

    public void setCert_no(String cert_no) {
        this.cert_no = cert_no;
    }

    public String getSeal_no() {
        return seal_no;
    }

    public void setSeal_no(String seal_no) {
        this.seal_no = seal_no;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Person{" +
                "pkid=" + pkid +
                ", table='" + table + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", id_card='" + id_card + '\'' +
                ", innerid='" + innerid + '\'' +
                ", category='" + category + '\'' +
                ", cert_no='" + cert_no + '\'' +
                ", seal_no='" + seal_no + '\'' +
                ", major='" + major + '\'' +
                ", cert_date='" + cert_date + '\'' +
                ", valid_date='" + valid_date + '\'' +
                ", url='" + url + '\'' +
                ", flag='" + flag + '\'' +
                ", com_id=" + com_id +
                ", com_name='" + com_name + '\'' +
                ", type=" + type +
                '}';
    }

    /**
     *
     * @param name 姓名
     * @param id_card 身份证号
     * @param category 注册类别
     * @param cert_no 证书编号
     * @param seal_no 执业印章号
     * @param major 注册专业
     * @return
     */
    public String md5(String name, String id_card, String category, String cert_no, String seal_no, String major) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(StringUtils.defaultIfBlank(name, ""));
        buffer.append(StringUtils.defaultIfBlank(id_card, ""));
        buffer.append(StringUtils.defaultIfBlank(category, ""));
        buffer.append(StringUtils.defaultIfBlank(cert_no, ""));
        buffer.append(StringUtils.defaultIfBlank(seal_no, ""));
        buffer.append(StringUtils.defaultIfBlank(major, ""));
        return StringUtils.md5(buffer.toString());
    }

    public String entityToMD5(Person personHunan) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(StringUtils.defaultIfBlank(personHunan.getName(), ""));
        buffer.append(StringUtils.defaultIfBlank(personHunan.getSex(), ""));
        buffer.append(StringUtils.defaultIfBlank(personHunan.getId_card(), ""));
        buffer.append(StringUtils.defaultIfBlank(personHunan.getCert_no(), ""));
        buffer.append(StringUtils.defaultIfBlank(personHunan.getSeal_no(), ""));
        buffer.append(StringUtils.defaultIfBlank(personHunan.getMajor(), ""));
        buffer.append(StringUtils.defaultIfBlank(personHunan.getCert_date(), ""));
        buffer.append(StringUtils.defaultIfBlank(personHunan.getValid_date(), ""));
        buffer.append(StringUtils.defaultIfBlank(personHunan.getUrl(), ""));
        buffer.append(StringUtils.defaultIfBlank(personHunan.getCom_name(), ""));
        return StringUtils.md5(buffer.toString());
    }
}
