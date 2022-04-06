package github.visual4.aacweb.dictation.domain.user;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

	@EqualsAndHashCode.Include
	Long seq;
	String email;
	String pass;
	Vendor vendor;
	Instant creationTime;
}
