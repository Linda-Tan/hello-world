package com.junliang.boot.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通用审计字段
 *
 * @author junlinag.li
 * @date 2021/6/15
 */
@Data
public abstract class AbstractAuditDO {
    /**
     * id主键
     */
    private Long          id;
    /**
     * 租户id
     */
    private Long          tenantId;
    /**
     * 创建人
     */
    private Long          createdBy;
    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;
    /**
     * 最后修改人
     */
    private Long          lastModifiedBy;
    /**
     * 最后更新日期
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime lastModifiedDate;
    /**
     * 删除标记
     */
    @TableLogic
    private Boolean       isDeleted;

}
