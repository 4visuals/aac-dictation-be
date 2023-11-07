package github.visual4.aacweb.dictation.domain.appconfig;

import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.domain.user.User;

@Import({AppConfigService.class, AppConfigDao.class})
public class ImportAppConfig {

	public static User admin() {
		User dummyAdmin = new User();
		dummyAdmin.setEmail("dummy@dumm.y");
		return dummyAdmin;
	}}
