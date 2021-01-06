package com.hyll.godtools.config;

import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@Log4j2
public class MybatisConfiguration {

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Autowired DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        try {
            // 加载MyBatis配置文件
            PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            // 能加载多个，所以可以配置通配符(如：classpath*:mapper/**/*.xml)
            sqlSessionFactoryBean.setMapperLocations(
//                    resourcePatternResolver.getResources("classpath:com/hyll/godtools/**/*.xml")
                    resourcePatternResolver.getResources("classpath:mapper/**/*.xml")
            );
            sqlSessionFactoryBean.setTypeAliasesPackage("com.hyll.godtools.pojo.**");
            return sqlSessionFactoryBean.getObject();
        } catch (Exception e) {
            log.error("SqlSessionFactory create error!", e);
        }
        return null;
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
