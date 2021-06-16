
package com.junliang.boot;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 分页查询条件
 *
 * @author tongyufu
 * @since 2019年6月4日 上午10:16:50
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PageVO<T> extends Page<T> {

    PageVO(){
    }

    PageVO(int page,int size){
        super(page++,size);
    }
}
