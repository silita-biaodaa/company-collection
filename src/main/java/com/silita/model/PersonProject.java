package com.silita.model;

import java.io.Serializable;

/**
 * @Author:chenzhiqiang
 * @Date:2018/5/14 20:14
 * @Description:    项目和人员关系
 */
public class PersonProject implements Serializable {
    private static final long serialVersionUID = 8187930481824856143L;
    /**
     * 主键id
     */
    private int pkid;

    /**
     * 网站内部id
     */
    private String innerid;

    /**
     * 项目id
     */
    private int pro_id;

    /**
     * 子项目id
     */
    private int pid;

    /**
     * 企业名称
     */
    private String com_name;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String id_card;

    /**
     * 注册类型
     */
    private String category;

    /**
     * 执业印章号
     */
    private String cert_no;

    /**
     * 人员在项目中担任的角色
     */
    private String role;

    /**
     * 专业名称
     */
    private String major;

    /**
     * 关联的表名的后缀
     */
    private String type;

    /**
     * 备注
     */
    private String remark;

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
        this.pkid = pkid;
    }

    public String getInnerid() {
        return innerid;
    }

    public void setInnerid(String innerid) {
        this.innerid = innerid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getCom_name() {
        return com_name;
    }

    public void setCom_name(String com_name) {
        this.com_name = com_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
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

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPro_id() {
        return pro_id;
    }

    public void setPro_id(int pro_id) {
        this.pro_id = pro_id;
    }
}
