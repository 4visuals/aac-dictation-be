package github.visual4.aacweb.dictation.domain.product;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfigService;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfiguration;
import github.visual4.aacweb.dictation.domain.product.Product.Column;
import github.visual4.aacweb.dictation.domain.user.User;

@Service
public class ProductService {

	final private static String PRODUCT_CODE_PREFIX = "prod-cert-";
	final ProductDao productDao;
	final AppConfigService appConfigService;
	
	public ProductService(ProductDao productDao, AppConfigService appConfigService) {
		this.productDao = productDao;
		this.appConfigService = appConfigService;
	}
	/**
	 * 판매용 상품 정보를 조회함(상품 결제 페이지에서 노출됨)
	 * @param productCode
	 * @return
	 */
	public Product findProductForPurchase(String productCode) {
		// theme을 바인딩한 상품 정보를 사용해야 함
		List<Product> products = this.findProducts();
		Product product = products
				.stream()
				.filter(prod -> prod.getCode().equals(productCode))
				.findFirst()
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_ERROR, 422, "cannot find product by code"));
		appConfigService.canOrder(product);
		return product;
	}
	public Product findBy(Column column, Object value) {

		return productDao.findBy(column, value);
	}
	public List<Product> findProducts() {
		AppConfiguration config = appConfigService.getConfiguration();
		List<Product> products = productDao.findProducts(config.getAppStatus());
		bindThemes(products);
		return products;
	}
	/**
	 * front-end에서 상품 디자인을 결정하는 theme value를 바인딩함
	 * @param products
	 */
	private void bindThemes(List<Product> products) {
		String [] themeNames = {"green", "picton-blue", "yellow", "violet"};
		int idx = 0;
		for (Product product : products) {
			String theme = themeNames[idx];
			product.setTheme(theme);
			idx = (idx + 1) % themeNames.length;
		}
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
	/**
	 * 상품 정보 수정 - 상품명과 상세 내용만 수정 가능함
	 * @param product
	 * @return 
	 */
	public Product updateBasicInfo(Product product) {
		productDao.updateBasicInfo(product);
		return productDao.findBy(Product.Column.prod_seq, product.seq);
		
	}
	
}
