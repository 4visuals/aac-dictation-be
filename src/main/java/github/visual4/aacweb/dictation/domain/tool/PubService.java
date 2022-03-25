package github.visual4.aacweb.dictation.domain.tool;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.tools.runner.ScriptRunner;

@Service
@Transactional
public class PubService {

	@Autowired
	PubDao pubDao;
	
	@Autowired
	DataSource ds;
	
	@Value("${dictation.appdir}") String appRootDir;
	@Value("${spring.datasource.password}") String dbPassword;
	
	final private static Charset utf8 = Charset.forName("UTF-8");
	public void publishDb(String queryBody) {
		// drop 을 먼저 해야 할듯 ?
		String [] dropQueries = {
			"DROP TABLE IF EXISTS wr_mapping;",
			"DROP TABLE IF EXISTS wr_eojeol;",
			"DROP TABLE IF EXISTS wr_sentence;",
			"DROP TABLE IF EXISTS wr_section;",
			"DROP TABLE IF EXISTS wr_desc;",
			"DROP TABLE IF EXISTS wr_chapter;",
		};
		
//		ByteArrayInputStream dropStream = new ByteArrayInputStream(String.join("\n", dropQueries).getBytes(utf8));
//		Resource drop = new InputStreamResource(dropStream);
		
//		ByteArrayInputStream stream = new ByteArrayInputStream();
		try {
			System.out.println(appRootDir);
			File dir = new File(appRootDir);
			System.out.println(dir.getAbsolutePath() + dir.exists());
			File repub = new File(dir, "repub.sql");
			if (!repub.exists()) {
				repub.createNewFile();
			}
			Files.write(repub.toPath(), queryBody.getBytes(utf8), StandardOpenOption.TRUNCATE_EXISTING);
			
			ScriptRunner runner = new ScriptRunner();
			String repubSh = appRootDir + "/repub.sh";
			runner.execute((exitCode) -> {
				System.out.println("[REPUB] " + exitCode);
			}, "bash", repubSh, repub.getAbsolutePath(),  dbPassword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Resource file = new InputStreamResource(stream, "DB publisher");
		// ResourceDatabasePopulator pop = new ResourceDatabasePopulator(new Resource[] {drop, file});
		// pop.execute(ds);
	}
}
