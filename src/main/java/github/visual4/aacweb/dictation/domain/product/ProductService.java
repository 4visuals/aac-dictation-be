package github.visual4.aacweb.dictation.domain.product;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfigService;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfiguration;
import github.visual4.aacweb.dictation.domain.product.Product.Column;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@Service
public class ProductService {

	final private static String PRODUCT_CODE_PREFIX = "prod-cert-";
	final ProductDao productDao;
	final AppConfigService appConfigService;
	
	public ProductService(ProductDao productDao, AppConfigService appConfigService) {
		this.productDao = productDao;
		this.appConfigService = appConfigService;
	}
	public Product findBy(Column column, Object value) {
		return productDao.findBy(column, value);
	}
	
	public List<Product> findProducts() {
		AppConfiguration config = appConfigService.getConfiguration();
		return productDao.findProducts(config.getAppStatus());
	}
	public List<Product> findProducts(User adminUser) {
		return productDao.findAllProducts();
	}
	
	public Product createProduct(User adminUser, Product product) {
		
		product.setActivatedAt(Instant.now());
		product.setCode(PRODUCT_CODE_PREFIX + UUID.randomUUID().toString());
		product.setCreatedBy(adminUser.getEmail());
		product.setType("S");
		
		Products.assertProduct(product);
		productDao.insertProduct(product);
		return product;
	}
	
}
