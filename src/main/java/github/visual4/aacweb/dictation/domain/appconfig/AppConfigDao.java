package github.visual4.aacweb.dictation.domain.appconfig;

import java.util.Arrays;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.AppStatus;

@Repository
public class AppConfigDao {

	final SqlSession session;
	final Long adminSeq;
	
	public AppConfigDao(SqlSession session, @Value("${dictation.admin.seq}") Long adminSeq) {
		this.session = session;
		this.adminSeq = adminSeq;
	}
	
	public AppConfiguration getConfig() {
		// FIXME 샘플 설정. 나중에 디비에서 읽어들여야 함.
		AppConfiguration config = new AppConfiguration();
		config.setAdminAccountSeq(adminSeq);
		config.setAdminAccounts(Arrays.asList(adminSeq, 42L, 46L));
		config.setDeferredPaymentDurationPerHour(24);
		config.setFreeCertsPerUser(2);
		config.setAppStatus(AppStatus.S);
		return config;
		// return session.selectOne(Dao.mapper(this, "getConfig"));
	}
}
