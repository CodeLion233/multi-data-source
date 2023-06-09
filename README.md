# 多数据源

## 基于druid的多数据源

配置文件

```yaml
logging: #打印sql
  level:
    com.example.demo.mapper.db1: debug
    com.example.demo.mapper.db2: debug
spring:
  datasource: #数据源配置
    type: com.alibaba.druid.pool.DruidDataSource
    db1:
      type: com.alibaba.druid.pool.DruidDataSource
      driverClassName: com.mysql.cj.jdbc.Driver
      name: mysql
      url: jdbc:mysql://***REMOVED***
      username: ***REMOVED***
      password: ***REMOVED***
    db2:
      type: com.alibaba.druid.pool.DruidDataSource
      driverClassName: dm.jdbc.driver.DmDriver
      name: dm
      url: jdbc:dm://***REMOVED***
      username: ***REMOVED***
      password: ***REMOVED***
```

> 在`application.yml`配置文件中驱动名称使用`driverClassName`，而不是driver-class-name

为每个数据源配置连接池和sessionFactory

```java
@Configuration
// 扫描mapper接口包路径
@MapperScan(basePackages = "com.gdie.upms.mapper.mysql", sqlSessionTemplateRef = "db1SqlSessionTemplate")
public class DB1DataSourceConfig {
    
    // 配置连接池，这里直接new一个Druid连接池，
    // 也可以new其他的连接池，比如spring boot默认的hikari连接池
    @Bean(name = "db1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    @Primary
    public DataSource setDataSource() {
        return new DruidDataSource();
    }

    // 事务配置
    @Bean(name = "db1TransactionManager")
    @Primary
    public DataSourceTransactionManager setTransactionManager(@Qualifier("db1DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(new DruidDataSource());
    }

    // 配置sessionFactory，这里的多数据源就是每个数据源对应一个sessionFactory
    // 下面getResources的就是mapper.xml文件
    @Bean(name = "db1SqlSessionFactory")
    @Primary
    public SqlSessionFactory setSqlSessionFactory(@Qualifier("db1DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/mysql/*.xml"));
        return bean.getObject();
    }

    // 配置SqlSessionTemplate
    @Bean(name = "db1SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate setSqlSessionTemplate(@Qualifier("db1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```

第二个数据源也是这样配置，@ConfigurationProperties注解来自动读取配置，亦可通过@Value进行读取配置。

> `service.impl`实现类中没有指定`DataSourceTransactionManager`会使用默认的事务管理器，即`db1`的事务管理器，需要使用其他的事务管理器，使用`@Transactional`注解指定

以下为使用不同数据源进行查询的SQL打印日志

![image-20230523101040109](多数据源.assets/image-20230523101040109.png)

![image-20230524095335856](多数据源.assets/image-20230524095335856.png)

## 基于AbstractRoutingDataSource的多数据源动态切换

核心在 `AbstractRoutingDataSource` 类上

```java
// 它继承AbstractDataSource，AbstractDataSource实现了DataSource接口，这个接口非常关键
public abstract class AbstractRoutingDataSource extends AbstractDataSource implements InitializingBean {
	// 这两个方法实现了DataSource接口
	@Override
	public Connection getConnection() throws SQLException {
		return determineTargetDataSource().getConnection();
	}
 
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return determineTargetDataSource().getConnection(username, password);
	}
}
	// 通过datasouce的名称来查找dataSource
    protected DataSource determineTargetDataSource() {
		Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
		Object lookupKey = determineCurrentLookupKey();
		DataSource dataSource = this.resolvedDataSources.get(lookupKey);
		if (dataSource == null && (this.lenientFallback || lookupKey == null)) {
			dataSource = this.resolvedDefaultDataSource;
		}
		if (dataSource == null) {
			throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
		}
		return dataSource;
	}

```

主要就是那个`getConnection`方法，每次执行`sql`会触发这个`getConnection`方法，在这里我们就可以给它返回不同的`Connection`对象，来达到动态切换数据源的目的。

```java
/**
 * 多数据源，切面处理类
 */
@Aspect
@Component
public class DataSourceAspect implements Ordered {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
	 * 针对上面注解做切面拦截
 	 */
    @Pointcut("@annotation(com.gateway.admin.datasources.annotation.DataSource)")
    public void dataSourcePointCut() {}
 
    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DataSource ds = method.getAnnotation(DataSource.class);
        if(ds == null){
        	//如果没有注解,使用默认数据源
            DynamicDataSource.setDataSource(DataSourceNames.FIRST);
        }else {
        	//根据注解中设置的数据源名称,选择对应的数据源
            DynamicDataSource.setDataSource(ds.name());
            logger.debug("set datasource is " + ds.name());
        }
 
        try {
            return point.proceed();
        } finally {
        	//清除数据源配置
            DynamicDataSource.clearDataSource();
        }
    }
 
    @Override
    public int getOrder() {
        return 1;
    }
}
```

```java
/**
 * 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
 
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }
 
    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }
 
    public static void setDataSource(String dataSource) {
        contextHolder.set(dataSource);
    }
 
    public static String getDataSource() {
        return contextHolder.get();
    }
 
    public static void clearDataSource() {
        contextHolder.remove();
    }
 
}
```

```java
/**
 * 多数据源配置类
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "db1DataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db1")
    public DataSource db1() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "db2DataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db2")
    public DataSource db2() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary // Primary 一定要写在这里，写上面任意一个bean相当于指定唯一数据源
    public DynamicDataSource dataSource(
            @Autowired @Qualifier("db1DataSourceProperties") DataSource firstDataSource,
            @Autowired @Qualifier("db2DataSourceProperties") DataSource secondDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>(16);
        targetDataSources.put("db1", firstDataSource);
        targetDataSources.put("db2", secondDataSource);
        return new DynamicDataSource(firstDataSource, targetDataSources);
    }
}
```

测试结果

![image-20230525104015443](多数据源.assets/image-20230525104015443.png)

![image-20230525165006052](多数据源.assets/image-20230525165006052.png)
