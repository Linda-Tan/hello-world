package com.junliang.boot.business.controller;


import com.junliang.boot.business.entity.ArchiveCompleteRuleDO;
import com.junliang.boot.business.service.ArchiveCompleteRuleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

        archiveCompleteRuleDO.setRuleCode("qwe");
        archiveCompleteRuleDO.setRuleDesc("test");
        archiveCompleteRuleDO.setRuleName("test");
        archiveCompleteRuleDO.setNecessaryDocument("[1,2]");
        archiveCompleteRuleDO.setTenantId(1L);
        ruleService.save(archiveCompleteRuleDO);
        return "success";
    }


}
