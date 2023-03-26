package github.visual4.aacweb.dictation.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.TypeMap;

class GoogleAuthServiceTest {

	@Test
	void test_getProfile() {
		GoogleAuthService svc = new GoogleAuthService("482880627778-rt7oi0of0fppg08opa0ob8fgqiuepain.apps.googleusercontent.com", null);
		svc.init();
		String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ijc3Y2MwZWY0YzcxODFjZjRjMGRjZWY3YjYwYWUyOGNjOTAyMmM3NmIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJuYmYiOjE2NjY5MzQyMDEsImF1ZCI6IjQ4Mjg4MDYyNzc3OC1ydDdvaTBvZjBmcHBnMDhvcGEwb2I4ZmdxaXVlcGFpbi5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwMDIwMTAwMTE2MDExNDkzNTM4NCIsImVtYWlsIjoieWVvcmkuc2VvQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI0ODI4ODA2Mjc3NzgtcnQ3b2kwb2YwZnBwZzA4b3BhMG9iOGZncWl1ZXBhaW4uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJuYW1lIjoiWU4gU2VvIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FMbTV3dTFNZmZYWnI4TjNwS1YyVXFSR2NkenJ2a3J1Wk9PZEtETXY5MUJxPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IllOIiwiZmFtaWx5X25hbWUiOiJTZW8iLCJpYXQiOjE2NjY5MzQ1MDEsImV4cCI6MTY2NjkzODEwMSwianRpIjoiYTg5NWNjZmQ2ZWJmMDZkMzQ2YzA2NGY3YmJhYmQzYjUzOTFiNTkyZCJ9.MIvoH46JviyGIWhSilwgqfMrY8-3v8xdXyVQ3u_kHWegcmZJdA162Wdfrzj4lSXNlJrtuVsuPfFh6yTJieFND3Vrd7u8F2B2IqAhF2FSupuprdAxmyQ8IrwGkihDGGCX5xRw31dG7Mma7ID2MF8CV-2mMmYVUfSxkLtW4i9YEi7IekIvntKuPZ_0FM-hlbufDrXGCCuYYAKbd_ZWVtyJQJp8HFRzhzpOS6_kOcYMlK4Pt2gSj4hAO9Pf90-Nf5mOyuBNXVnycW39QO3InEYZE9nnYM1XgEUzKPBHLwUWKuu-wNvACnuPdTetJ307VmJYY2tGRjrglh-WBCCE8hqAyA";
		TypeMap res = svc.getUserProfile(TypeMap.with("token", token));
		System.out.println(res);
	}
	
	@Test
	public void test_uuid() {
		System.out.println(UUID.randomUUID().toString().length());
	}
	
	@Test
	public void test_userinfo() {
		GoogleAuthService svc = new GoogleAuthService("482880627778-rt7oi0of0fppg08opa0ob8fgqiuepain.apps.googleusercontent.com", new ObjectMapper());
		TypeMap userInfo = svc.getUserProfile("ya29.A0ARrdaM_ywc3yHF8NYGGya0BBcA4ZVnIiNbhA_whuu3iQAA1nIhbnVPLk0x41q2qCv-VFUyl-oWfZo5bVx9FglIr2JZaLofEjX4EJwjWp_aF2EX0KQ32jNkxxr6VX7mJIE9CkcU3VRY7NSrfPVEFTD5BE5jzajg");
		System.out.println(userInfo);
	}

}
