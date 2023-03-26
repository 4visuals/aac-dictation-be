package github.visual4.aacweb.dictation.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.service.codec.ICodec;

@Service
public class UuidService {

	final ICodec<String, String> codec;
	
	public UuidService(@Qualifier("sha256") ICodec<String, String> sha256Codec) {
		this.codec = sha256Codec;
	}
	
	public String createUuid(GroupOrderForm form) {
		String source = form.getSenderEmail()
				+ form.getSenderName()
				+ form.getCreationTime().toEpochMilli()
				+ Math.random();
		String k0 = codec.encode(source);
		String k1 = codec.encode(form.getContent());
		return codec.encode(k0 + k1);
	}
}
