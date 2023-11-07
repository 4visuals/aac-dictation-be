package github.visual4.aacweb.dictation.domain.product;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.Asserts;
import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.appconfig.ImportAppConfig;

@Import(ImportProduct.class)
class ProductServiceTest extends BaseDao {

	@Autowired
	ProductService productService;
	
	@Test
	@DisplayName("소매 상품 조회")
	void test_query_retail_products() {
		List<Product> products = productService.filterProducts(product -> product.checkIfRetail());
		assertTrue(products.size() > 0);
		Asserts.asserts(products, (prod) -> prod.checkIfRetail());
	}
	
	@Test
	@DisplayName("[관리자] 소매 상품 등록")
	void test_creat_product() {
		Product dummy = ImportProduct.retailProduct(null);
		int cntBefore = productService.filterProducts(product -> product.checkIfRetail()).size();
		productService.createProduct(ImportAppConfig.admin(), dummy);
		
		Product product = productService.findBy(Product.Column.prod_seq, dummy.getSeq());
		assertEquals(product, dummy);
		product.getSalesType().equals(dummy.getSalesType());
		
		int cntAfter = productService.filterProducts(prd-> prd.checkIfRetail()).size();
		assertTrue (cntBefore + 1 == cntAfter);
	}
	@Test
	@DisplayName("[관리자] 공구 상품 등록")
	void put_groupbying_product() {
		Product dummy = ImportProduct.groupBuyingProduct();
		int cntBefore = productService.filterProducts(product -> product.checkIfGroupBuying()).size();
		productService.createProduct(ImportAppConfig.admin(), dummy);
		
		Product product = productService.findBy(Product.Column.prod_seq, dummy.getSeq());
		assertEquals(product, dummy);
		assertEquals(dummy.getSalesType(), product.getSalesType());
		
		int cntAfter = productService.filterProducts(prd-> prd.checkIfGroupBuying()).size();
		assertTrue (cntBefore + 1 == cntAfter);
	}
	
	@Test
	@DisplayName("[관리자] sales type이 없는 경우")
	void no_sailes_type() {
		Product dummy = ImportProduct.retailProduct(p -> p.setSalesType(null));
		assertThatThrownBy(() -> {
			productService.createProduct(ImportAppConfig.admin(), dummy);	
		}).isInstanceOf(AppException.class);
	}
}
