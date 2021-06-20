package com.junliang.boot.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.junliang.boot.business.entity.AbstractAuditDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 字段编码
 * </p>
 *
 * @author JunLiang
 * @since 2021-06-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ea_archive_complete_rule_field")
public class ArchiveCompleteRuleFieldDO extends AbstractAuditDO {

    private static final long serialVersionUID = 1L;

    /**
     * 规则id
     */
    private Long ruleId;

    /**
     * 资料id
     */
    private Long fromId;

    /**
     * 资料名称
     */
    private String fromName;

    /**
     * 字段id
     */
    private Long fieldId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字段段值
     */
    private String fieldValue;


}
