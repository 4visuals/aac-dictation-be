package github.visual4.aacweb.dictation.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.service.codec.Sha256Codec;

public class UuidServiceTest{

	@Import({Sha256Codec.class, UuidService.class })
	public static class Imports {}
	
	@Test
	void test() {
	}

}
