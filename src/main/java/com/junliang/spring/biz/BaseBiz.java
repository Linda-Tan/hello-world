package com.junliang.spring.biz;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.junliang.spring.pojo.vo.TableResultResponse;
import com.junliang.spring.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tony
 * @date 2018/03/15
 */
public abstract class BaseBiz<M extends Mapper<T>, T> {

    @Autowired
    protected M mapper;

    public void setMapper(M mapper) {
        this.mapper = mapper;
    }


    public void insert(T entity) {
        EntityUtils.setEntityInfo(entity, EntityUtils.cfields);
        mapper.insert(entity);
    }


    public void insertSelective(T entity) {
        EntityUtils.setEntityInfo(entity, EntityUtils.cfields);
        mapper.insertSelective(entity);
    }



    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
    }


    public void updateById(T entity) {
        //  EntityUtils.setUpdatedInfo(entity);
        EntityUtils.setEntityInfo(entity, EntityUtils.ufields);
        mapper.updateByPrimaryKey(entity);
    }


    public void updateSelectiveById(T entity) {
        //  EntityUtils.setUpdatedInfo(entity);
        EntityUtils.setEntityInfo(entity, EntityUtils.ufields);
        mapper.updateByPrimaryKeySelective(entity);

    }


    public TableResultResponse<T> selectByQuery(LinkedHashMap<String, Object> query) {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Example example = new Example(clazz);
        if (query.entrySet().size() > 0) {
            Example.Criteria criteria = example.createCriteria();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                criteria.andLike(entry.getKey(), "%" + entry.getValue().toString() + "%");
            }
        }
        Page<Object> result = PageHelper.startPage(Integer.parseInt(query.get("page").toString()), Integer.parseInt(query.get("limit").toString()));
        List<T> list = mapper.selectByExample(example);
        return new TableResultResponse<T>(result.getTotal(), list);
    }

}
