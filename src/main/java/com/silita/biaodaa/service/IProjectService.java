package com.silita.biaodaa.service;

import com.silita.biaodaa.model.*;

import java.util.Map;

/**
 * Created by Administrator on 2018/5/19.
 */
public interface IProjectService {

    /**
     * 根据项目编号、内部id判断是否添加项目基本信息
     *
     * @param tbProject
     */
    int insertProjectInfo(TbProject tbProject);



    /**
     * 根据施工项目内部id判断该施工项目是否已抓取
     * 一个施工类企业有多个施工类资质证书、多个施工类资质证书对应一个公司的施工项目
     * @param params
     * @return
     */
    boolean checkProjectBuildExist(Map<String, Object> params);

    /**
     * 添加施工合段信息
     * 根据施工许可证号判断是否添加
     *
     * @param tbProjectBuild
     */
    int insertProjectBuild(TbProjectBuild tbProjectBuild);

    /**
     * 添加项目人员
     * 根据证书编号施工项目id、判断是否添加
     * @param tbPersonProject
     */
    void insertPersonProject(TbPersonProject tbPersonProject);


    //#################施工end####################

    /**
     * 设计、勘察
     * @param
     * @return
     */
    boolean checkProjectDesignExist(Map<String, Object> params);

    /**
     * 添加施工图审查信息(设计)
     * 根据施工图审查合格书编号、设计单位判断是否添加
     * @param tbProjectDesign
     */
    int insertProjectDesign(TbProjectDesign tbProjectDesign);


    //#################设计、勘察end####################

    /**
     * 监理
     * @param params
     * @return
     */
    boolean checkProjectSupervisionExist(Map<String, Object> params);

    /**
     * 添加监理合同段信息
     * 根据监理内部id判断是否添加
     * @return
     */
    int insertProjectSupervisor(TbProjectSupervision tbProjectSupervision);


    //#################监理end####################

}
