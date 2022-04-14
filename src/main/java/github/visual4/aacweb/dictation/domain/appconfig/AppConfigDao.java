package github.visual4.aacweb.dictation.domain.appconfig;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
public class AppConfigDao {

	final SqlSession session;
	
	public AppConfigDao(SqlSession session) {
		this.session = session;
	}
	
	public AppConfiguration getConfig() {
		// FIXME 샘플 설정. 나중에 디비에서 읽어들여야 함.
		AppConfiguration config = new AppConfiguration();
		config.setDeferredPaymentDurationPerHour(48);
		config.setFreeCertsPerUser(2);
		return config;
		// return session.selectOne(Dao.mapper(this, "getConfig"));
	}
}
