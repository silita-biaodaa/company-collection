package com.silita.service.impl;

import com.silita.dao.*;
import com.silita.model.*;
import com.silita.service.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Administrator on 2018/5/19.
 */
@Service("projectService")
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private TbProjectBuildMapper tbProjectBuildMapper;
    @Autowired
    private TbProjectMapper tbProjectMapper;
    @Autowired
    private TbPersonProjectMapper tbPersonProjectMapper;
    @Autowired
    private TbProjectDesignMapper tbProjectDesignMapper;
    @Autowired
    private TbProjectSupervisionMapper tbProjectSupervisionMapper;


    @Override
    public int insertProjectInfo(TbProject tbProject) {
        boolean flag = tbProjectMapper.getProjectTotalByProjectNoAndXmid(tbProject) > 0;
        if (!flag) {
            tbProjectMapper.insertProjectInfo(tbProject);
            return tbProject.getProId();
        } else {
            return tbProjectMapper.getProIdByProNoAndXmid(tbProject);
        }
    }



    @Override
    public boolean checkProjectBuildExist(Map<String, Object> params) {
        return tbProjectBuildMapper.getTotalByBdxhAndComId(params) > 0;
    }

    @Override
    public int insertProjectBuild(TbProjectBuild tbProjectBuild) {
        boolean flag = tbProjectBuildMapper.getTotalByBdxhAndComIdAndBLicence(tbProjectBuild) > 0;
        if (!flag) {
            tbProjectBuildMapper.insertProjectBuild(tbProjectBuild);
            return tbProjectBuild.getPkid();
        } else {
            return tbProjectBuildMapper.getPkidByBdxhAndComIdAndBLicence(tbProjectBuild);
        }
    }

    @Override
    public void insertPersonProject(TbPersonProject tbPersonProject) {
        boolean flag = tbPersonProjectMapper.getPersionProjectTotalByCertNoAndPid(tbPersonProject) > 0;
        if (!flag) {
            tbPersonProjectMapper.insertPersionProject(tbPersonProject);
        }
    }


    //#################施工类end####################

    @Override
    public boolean checkProjectDesignExist(Map<String, Object> params) {
        return tbProjectDesignMapper.getTotalBySgtxhAndProTypeAndComId(params) > 0;
    }

    @Override
    public int insertProjectDesign(TbProjectDesign tbProjectDesign) {
        boolean falg = tbProjectDesignMapper.getTotalBySgtxhAndComIdCheckNo(tbProjectDesign) > 0;
        if (!falg) {
            tbProjectDesignMapper.insertProjectDesign(tbProjectDesign);
            return tbProjectDesign.getPkid();
        } else {
            return tbProjectDesignMapper.getPkidBySgtxhAndComIdCheckNo(tbProjectDesign);
        }
    }


    //#################设计、勘察end####################

    @Override
    public boolean checkProjectSupervisionExist(Map<String, Object> params) {
        return tbProjectSupervisionMapper.getTotalByJlbdxhAndComId(params) > 0;
    }

    @Override
    public int insertProjectSupervisor(TbProjectSupervision tbProjectSupervision) {
        boolean falg = tbProjectSupervisionMapper.getTotalByJlbdxhAndComIdTwo(tbProjectSupervision) > 0;
        if (!falg) {
            tbProjectSupervisionMapper.insertProjectSupervision(tbProjectSupervision);
            return tbProjectSupervision.getPkid();
        } else {
            return tbProjectSupervisionMapper.getPkidByJlbdxhAndComIdTwo(tbProjectSupervision);
        }
    }


    //#################监理end####################

}
