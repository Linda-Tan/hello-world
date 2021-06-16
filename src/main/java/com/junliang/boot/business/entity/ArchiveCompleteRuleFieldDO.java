package com.junliang.boot.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.junliang.boot.business.entity.AbstractAuditDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 档案完整规则配置字段
 * </p>
 *
 * @author Bobby
 * @since 2021-06-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ea_archive_complete_rule_field")
public class ArchiveCompleteRuleFieldDO extends AbstractAuditDO {

    private static final long serialVersionUID = 1L;
    /**
     * 规则id
     */
    private String ruleId;
    /**
     * 资料id
     */
    private Long fromId;

    /**
     * 资料名称
     */
    private Long fromName;

    /**
     * 字段id
     */
    private Long fieldId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段段值
     */
    private String fieldValue;


}
