package github.visual4.aacweb.dictation.service.tts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;


import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;

public class PollyTts implements ITts {
	private final static String DEFAULT_VOICE_ID = "Seoyeon";

	private SynthesizeSpeechRequest req;
	private InputStream res;
	private String text;

	public PollyTts(SynthesizeSpeechRequest req, InputStream res, String text) {
		this.req = req;
		this.res = res;
		this.text = text;
	}
	
	public void then(Consumer<InputStream> consumer) {
		
		try(InputStream in = this.res) {
			 consumer.accept(in);
		} catch (IOException e) {
			throw new AppException(ErrorCode.VOICE_ERROR, 500, DEFAULT_VOICE_ID, text);
		}
	}

	@Override
	public InputStream getStream() {
		return this.res;
	}
}
