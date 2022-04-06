package github.visual4.aacweb.dictation.domain.user;

import java.time.Instant;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.TypeMap;
import lombok.Getter;

@Getter
public class Membership {

	User user;
	TypeMap profile;

	String email;
	Boolean verified;
	Instant verifiedTime;
	String vendor;
	
	public Membership(User user, TypeMap profile, String vendor) {
		this.user = user;
		this.profile = profile;
		
		this.verified = Boolean.TRUE;
		this.email = this.profile.getStr("email");
		this.verifiedTime = user == null ? null : user.getCreationTime();
		this.vendor = vendor;
	}

}
