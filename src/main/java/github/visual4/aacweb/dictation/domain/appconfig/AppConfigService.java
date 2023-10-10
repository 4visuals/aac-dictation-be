package github.visual4.aacweb.dictation.domain.appconfig;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.AppStatus;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.product.Product;
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
	/**
	 * 판매 가능한 상품인지 확인함
	 * @param product
	 */
	public void canOrder(Product product) {
		AppConfiguration config = this.getConfiguration();
		if (config.getAppStatus() == AppStatus.S && product.isTrialProduct()) {
			throw new AppException(ErrorCode.PRODUCT_ERROR, 422, "trial product");
		}
		
	}
	
}
