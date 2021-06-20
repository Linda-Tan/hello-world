package ${package.Service};


import ${superServiceClassPackage};
import ${superServiceImplClassPackage};
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${table.serviceName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${superServiceClass}<${entity}> {

}

