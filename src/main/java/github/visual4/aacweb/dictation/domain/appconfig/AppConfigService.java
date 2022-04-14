package github.visual4.aacweb.dictation.domain.appconfig;

import org.springframework.stereotype.Service;

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
	
}
