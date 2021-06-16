package com.junliang.boot.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.junliang.boot.business.entity.ArchiveCompleteRuleDO;
import com.junliang.boot.business.entity.ArchiveCompleteRuleFieldDO;
import com.junliang.boot.business.mapper.ArchiveCompleteRuleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 档案完整规则 服务实现类
 * </p>
 *
 * @author Bobby
 * @since 2021-06-15
 */
@Service
public class ArchiveCompleteRuleService extends ServiceImpl<ArchiveCompleteRuleMapper, ArchiveCompleteRuleDO> implements IService<ArchiveCompleteRuleDO> {

    public List<ArchiveCompleteRuleDO> test(){
        List<ArchiveCompleteRuleDO> test = baseMapper.test("test");

        return test;
    }
}
