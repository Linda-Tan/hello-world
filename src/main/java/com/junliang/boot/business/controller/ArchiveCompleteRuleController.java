package com.junliang.boot.business.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junliang.boot.business.entity.ArchiveCompleteRuleDO;
import com.junliang.boot.business.service.ArchiveCompleteRuleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 档案完整规则 前端控制器
 * </p>
 *
 * @author Bobby
 * @since 2021-06-15
 */
@RestController
@RequestMapping("/business/archive-complete-rule-do")
public class ArchiveCompleteRuleController {

    @Resource
    private ArchiveCompleteRuleService ruleService;

    @GetMapping("/test")
    public String test(){

        ArchiveCompleteRuleDO archiveCompleteRuleDO = new ArchiveCompleteRuleDO();

        archiveCompleteRuleDO.setRuleCode("qwe1");
        archiveCompleteRuleDO.setRuleDesc("test");
        archiveCompleteRuleDO.setRuleName("test");
        archiveCompleteRuleDO.setNecessaryDocument("[1,2]");
        // archiveCompleteRuleDO.setTenantId(1L);
        ruleService.save(archiveCompleteRuleDO);
        return "success";
    }

    @GetMapping("/update")
    public String update(){

        ArchiveCompleteRuleDO archiveCompleteRuleDO = new ArchiveCompleteRuleDO();
        archiveCompleteRuleDO.setId(1404981451360104449L);
        archiveCompleteRuleDO.setRuleCode("asdas");
        archiveCompleteRuleDO.setRuleDesc("asfaf");
        archiveCompleteRuleDO.setRuleName("asfasfa");
        archiveCompleteRuleDO.setNecessaryDocument("[1,2]");
        archiveCompleteRuleDO.setIsEnabled(false);
        ruleService.updateById(archiveCompleteRuleDO);
        return "success";
    }
    @GetMapping("/del")
    public String del(){

        ArchiveCompleteRuleDO archiveCompleteRuleDO = new ArchiveCompleteRuleDO();
        archiveCompleteRuleDO.setId(1404981451360104449L);
        archiveCompleteRuleDO.setRuleCode("asdas");
        archiveCompleteRuleDO.setRuleDesc("asfaf");
        archiveCompleteRuleDO.setRuleName("asfasfa");
        archiveCompleteRuleDO.setNecessaryDocument("[1,2]");
        archiveCompleteRuleDO.setIsEnabled(false);
        System.out.println(ruleService.getBaseMapper().deleteById(archiveCompleteRuleDO.getId()));
        System.out.println("---------------------------");

        System.out.println(ruleService.removeById(archiveCompleteRuleDO.getId()));
        return "success";
    }

    @GetMapping("/select")
    public Object select(){
        List<ArchiveCompleteRuleDO> list = ruleService.list();
        return list;
    }
    @GetMapping("/selectOne")
    public Object selectOne(){
        ArchiveCompleteRuleDO list = ruleService.getOne(Wrappers.<ArchiveCompleteRuleDO>lambdaQuery().eq(ArchiveCompleteRuleDO::getIsEnabled, true));
        return list;
    }

    @GetMapping("/page")
    public Page page(){

        Page<ArchiveCompleteRuleDO> page = new Page<>(1, 1);
        // 执行分页查询
        Page<ArchiveCompleteRuleDO> userPage =
                ruleService.getBaseMapper().selectPage(page, Wrappers.<ArchiveCompleteRuleDO>lambdaQuery().eq(ArchiveCompleteRuleDO::getIsEnabled, true));
        System.out.println("总记录数 = " + userPage.getTotal());
        System.out.println("总页数 = " + userPage.getPages());
        System.out.println("当前页码 = " + userPage.getCurrent());
        // 获取分页查询结果
        List<ArchiveCompleteRuleDO> records = userPage.getRecords();
        records.forEach(System.out::println);


        // List<ArchiveCompleteRuleDO> list = ruleService.test();
        return userPage;
    }

}
