package com.junliang.boot.business.mapper;

import com.junliang.boot.business.entity.ArchiveCompleteRuleDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 档案完整规则 Mapper 接口
 * </p>
 *
 * @author Bobby
 * @since 2021-06-15
 */
public interface ArchiveCompleteRuleMapper extends BaseMapper<ArchiveCompleteRuleDO> {

    List<ArchiveCompleteRuleDO> test(@Param("test") String test);

}
