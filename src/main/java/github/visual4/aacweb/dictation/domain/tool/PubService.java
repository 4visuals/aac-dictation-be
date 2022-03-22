package github.visual4.aacweb.dictation.domain.tool;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

@Service
public class PubService {

	@Autowired
	PubDao pubDao;
	
	@Autowired
	DataSource ds;
	
	public void publishDb(String queryBody) {
		ByteArrayInputStream stream = new ByteArrayInputStream(queryBody.getBytes(Charset.forName("UTF-8")));
		Resource file = new InputStreamResource(stream, "DB publisher");
		ResourceDatabasePopulator pop = new ResourceDatabasePopulator(new Resource[] {file});
		pop.execute(ds);
	}
}
