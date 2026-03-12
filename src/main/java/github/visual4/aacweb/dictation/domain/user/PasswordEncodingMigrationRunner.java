package github.visual4.aacweb.dictation.domain.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PasswordEncodingMigrationRunner implements ApplicationRunner {

	private final UserDao userDao;
	private final PasswordEncoder passwordEncoder;
	private final boolean enabled;
	private final int batchSize;
	private final boolean dryRun;

	public PasswordEncodingMigrationRunner(
			UserDao userDao,
			PasswordEncoder passwordEncoder,
			@Value("${dictation.migration.encode-passwords:false}") boolean enabled,
			@Value("${dictation.migration.encode-passwords.batch-size:500}") int batchSize,
			@Value("${dictation.migration.encode-passwords.dry-run:false}") boolean dryRun) {
		this.userDao = userDao;
		this.passwordEncoder = passwordEncoder;
		this.enabled = enabled;
		this.batchSize = batchSize;
		this.dryRun = dryRun;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!enabled) {
			return;
		}

		int fetched = 0;
		int updated = 0;
		int skipped = 0;
		int alreadyEncoded = 0;
		int failed = 0;

		log.info("[PASSWORD MIGRATION] start (batchSize={}, dryRun={})", batchSize, dryRun);

		while (true) {
			List<User> users = userDao.findUsersWithUnencodedPass(batchSize);
			if (users == null || users.isEmpty()) {
				break;
			}
			fetched += users.size();

			for (User user : users) {
				String rawPass = user.getPass();
				if (rawPass == null || rawPass.isBlank()) {
					skipped++;
					continue;
				}

				String encodedPass = rawPass;
				if (!isEncodedPassword(rawPass)) {
					encodedPass = passwordEncoder.encode(rawPass);
				} else {
					alreadyEncoded++;
				}

				if (dryRun) {
					continue;
				}

				boolean ok = userDao.updatePasswordEncoded(user.getSeq(), encodedPass);
				if (ok) {
					updated++;
				} else {
					failed++;
				}
			}

			if (dryRun) {
				break;
			}
		}

		log.info(
				"[PASSWORD MIGRATION] done (fetched={}, updated={}, alreadyEncoded={}, skipped={}, failed={}, dryRun={})",
				fetched, updated, alreadyEncoded, skipped, failed, dryRun);
	}

	private boolean isEncodedPassword(String password) {
		return password != null
				&& (password.startsWith("$2a$")
						|| password.startsWith("$2b$")
						|| password.startsWith("$2y$"));
	}
}
