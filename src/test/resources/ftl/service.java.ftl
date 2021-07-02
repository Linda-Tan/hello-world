package ${package.Service};

import ${superServiceImplClassPackage};
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import org.springframework.stereotype.Service;

/**
 * ${table.comment!} 服务类
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${table.serviceName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> {

}

