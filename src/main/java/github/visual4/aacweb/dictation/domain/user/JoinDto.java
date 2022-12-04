package github.visual4.aacweb.dictation.domain.user;

import github.visual4.aacweb.dictation.TypeMap;
import lombok.Getter;
import lombok.Setter;
/**
 * email, password를 통한 회원 가입. PG사 심사를 위해서 추가함(toss)
 * @author chminseo
 *
 */
@Getter
@Setter
public class JoinDto {

	String userId;
	String email;
	String password;
	/**
	 * 프로필 정보로 변환 - 기존에 OAuth를 통해 얻어온 프로필 정보를 비슷하게 흉내내야 함
	 * 
	 * @return
	 */
	public TypeMap toProfile() {
		
		return null;
	}
}
