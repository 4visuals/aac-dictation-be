package github.visual4.aacweb.dictation.domain.appconfig;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.domain.user.User;

@Service
public class AppConfigService {

	final AppConfigDao configDao;

	public AppConfigService(AppConfigDao configDao) {
		super();
		this.configDao = configDao;
	}
	
	public AppConfiguration getConfiguration() {
		return this.configDao.getConfig();
	}

	public boolean isAdmin(User user) {
		AppConfiguration config = this.configDao.getConfig();
		return config.isAdmin(user.getSeq());
	}
	
}
