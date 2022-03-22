package github.visual4.aacweb.dictation.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.catalina.core.ApplicationContext;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class AacDaoConfig {
	
	public AacDaoConfig() {
		System.out.println("[AAC DAO CONFIG]");
	}
	
	@Bean
	public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException {
		SqlSessionFactoryBean fBean = new SqlSessionFactoryBean();
		
		fBean.setDataSource(dataSource);
		fBean.setTypeAliasesPackage("github.visual4.aacweb.dictation.domain.*");
		
		Resource[] mappers = new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mappers/**.xml");
		fBean.setMapperLocations(mappers);
		return fBean;
	}
}
