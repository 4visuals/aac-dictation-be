package github.visual4.aacweb.dictation.config;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.catalina.core.ApplicationContext;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.user.Vendor;

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
		fBean.setTypeHandlers(new VendorTypeHanlder());
		return fBean;
	}
	
	@MappedTypes(Vendor.class)
	static class VendorTypeHanlder implements TypeHandler<Vendor> {

		@Override
		public void setParameter(PreparedStatement ps, int i, Vendor parameter, JdbcType jdbcType) throws SQLException {
			System.out.println("[TH] "+ parameter);
			ps.setString(i, parameter.toCode());
		}

		@Override
		public Vendor getResult(ResultSet rs, String columnName) throws SQLException {
			String vendorCode = rs.getString(columnName);
			return Vendor.fromCode(vendorCode);
		}

		@Override
		public Vendor getResult(ResultSet rs, int columnIndex) throws SQLException {
			String vendorCode = rs.getString(columnIndex);
			return Vendor.fromCode(vendorCode);
		}

		@Override
		public Vendor getResult(CallableStatement cs, int columnIndex) throws SQLException {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "CallableStatement not allowed for column " + columnIndex);
		}
		
	}
}
