package com.junliang.boot.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.junliang.boot.business.entity.AbstractAuditDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 档案完整规则
 * </p>
 *
 * @author JunLiang
 * @since 2021-06-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ea_archive_complete_rule")
public class ArchiveCompleteRuleDO extends AbstractAuditDO {

  private static final long serialVersionUID = 1L;

  /**
   * 规则编码
   */
  private String ruleCode;

  /**
   * 规则名称
   */
  private String ruleName;

  /**
   * 规则描述
   */
  private String ruleDesc;

  /**
   * 资料大类id
   */
  private Long fromId;

  /**
   * 资料大类名称
   */
  private String fromName;

  /**
   * 必要资料类型
   */
  private String necessaryDocument;

  /**
   * 启用标记
   */
  private Boolean isEnabled;


}
