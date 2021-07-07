package com.junliang.boot.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * TODO desc
 *
 * @author junlinag.li
 * @date 2021/6/15
 */
@MapperScan({"com.junliang.boot.business.mapper"})
@Configuration
public class MybatisPlusConfig {

  /**
   * 注意排序
   *
   * @returnnni
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
      @Override
      public Expression getTenantId() {
        // 返回租户id的值, 这里固定写死为1
        // 一般是从当前上下文中取出一个 租户id
        return new LongValue(1367744528473784321L);
      }

      @Override
      public String getTenantIdColumn() {
        // 对应数据库租户ID的列名
        return "tenant_id";
      }

      @Override
      public boolean ignoreTable(String tableName) {
        // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
        boolean b = "ea_archive_complete_rule_field".equals(tableName);
        return false;
      }
    }));
    // 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
    // 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
    // 分页插件
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
  }

  @Bean
  public ConfigurationCustomizer configurationCustomizer() {
    return configuration -> configuration.setUseDeprecatedExecutor(false);
  }


  @Bean
  public MetaObjectHandler metaObjectHandler() {
    return new MetaObjectHandler() {
      @Override
      public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject, "tenantId", Long.class, 1L);
        strictInsertFill(metaObject, "createdBy", Long.class, 1L);
        strictFillStrategy(metaObject, "createdDate", LocalDateTime::now);
        strictUpdateFill(metaObject, "lastModifiedBy", Long.class, 1L);
        strictFillStrategy(metaObject, "lastModifiedDate", LocalDateTime::now);
      }

      @Override
      public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "lastModifiedBy", Long.class, 2L);
        strictFillStrategy(metaObject, "lastModifiedDate", LocalDateTime::now);
      }
    };
  }

  // @Component
  // public static class MyMetaObjectHandler implements MetaObjectHandler{
  //
  //     @Override
  //     public void insertFill(MetaObject metaObject) {
  //         // this.strictInsertFill(metaObject, "operator", String.class, "Jetty");
  //         this.strictInsertFill(metaObject, "createdBy", Long.class, 1L);
  //         this.strictInsertFill(metaObject, "created_by", Long.class, 1L);
  //     }
  //
  //     @Override
  //     public void updateFill(MetaObject metaObject) {
  //         // this.strictUpdateFill(metaObject, "operator", String.class, "Tom");
  //         this.strictUpdateFill(metaObject, "lastModifiedBy", Long.class, 1L);
  //         this.strictUpdateFill(metaObject, "last_modified_by", Long.class, 1L);
  //     }
  // }


}
