package github.visual4.aacweb.dictation.domain.product;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.domain.product.Product.Column;

@Service
public class ProductService {

	final ProductDao productDao;
	
	public ProductService(ProductDao productDao) {
		this.productDao = productDao;
	}
	public Product findBy(Column column, Object value) {
		return productDao.findBy(column, value);
	}
	
}
