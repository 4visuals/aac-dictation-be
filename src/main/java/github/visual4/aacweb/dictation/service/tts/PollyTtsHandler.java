package github.visual4.aacweb.dictation.service.tts;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
/**
 * Amazon Polly 한국어 음성(ID: Seoyeon, file: mp3)
 * 
 * @author chminseo
 *
 */
public class PollyTtsHandler implements ITtsHandler {
	
	private final static String DEFAULT_VOICE_ID = "Seoyeon";
	private final static String DEFAULT_VOICE_FORMAT = "mp3";
	
	private AmazonPollyClient client;
	
	public PollyTtsHandler(String accessKey, String secretKey) {
		AmazonPollyClient client = new AmazonPollyClient(
				new BasicAWSCredentials(accessKey, secretKey), 
				new ClientConfiguration());
		client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
		this.client = client;
	}
	
	@Override
	public ITts speak(String text) {
		SynthesizeSpeechRequest req = new SynthesizeSpeechRequest();
		req.withText(text)
			.withSampleRate("8000")
			.withVoiceId(DEFAULT_VOICE_ID)
			.withOutputFormat(DEFAULT_VOICE_FORMAT);
		
		SynthesizeSpeechResult res = client.synthesizeSpeech(req);
		return new PollyTts(req, res, text);
	}
}
