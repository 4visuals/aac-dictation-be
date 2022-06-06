package github.visual4.aacweb.dictation.service.tts;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechResponse;
/**
 * Amazon Polly 한국어 음성(ID: Seoyeon, file: mp3)
 * 
 * @author chminseo
 *
 */
public class PollyTtsHandler implements ITtsHandler {
	
	private final static String DEFAULT_VOICE_ID = "Seoyeon";
	private final static String DEFAULT_VOICE_FORMAT = "mp3";
	private PollyClient client;
	
	public PollyTtsHandler(String accessKey, String secretKey) {
		AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		PollyClient polly = PollyClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
		
		this.client = polly;
	}
	
	@Override
	public ITts speak(String text) {
		SynthesizeSpeechRequest req = SynthesizeSpeechRequest.builder()
				.text(text)
				.voiceId(DEFAULT_VOICE_ID)
				.engine("neural")
				.sampleRate("8000")
				.outputFormat(DEFAULT_VOICE_FORMAT).build();
		
		ResponseInputStream<SynthesizeSpeechResponse> res = client.synthesizeSpeech(req);
		
		return new PollyTts(req, res, text);
	}
}
