package github.visual4.aacweb.dictation.service.tts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

public class PollyTts implements ITts {
	private final static String DEFAULT_VOICE_ID = "Seoyeon";

	private SynthesizeSpeechRequest req;
	private SynthesizeSpeechResult res;
	private String text;

	public PollyTts(SynthesizeSpeechRequest req, SynthesizeSpeechResult res, String text) {
		this.req = req;
		this.res = res;
		this.text = text;
		ResponseMetadata md = this.res.getSdkResponseMetadata();
		System.out.println(md);
	}
	
	public void then(Consumer<InputStream> consumer) {
		
		try(InputStream in = res.getAudioStream()) {
			 consumer.accept(in);
		} catch (IOException e) {
			throw new AppException(ErrorCode.VOICE_ERROR, 500, DEFAULT_VOICE_ID, text);
		}
	}

	@Override
	public InputStream getStream() {
		return res.getAudioStream();
	}
}
