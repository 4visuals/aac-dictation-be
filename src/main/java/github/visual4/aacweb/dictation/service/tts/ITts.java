package github.visual4.aacweb.dictation.service.tts;

import java.io.InputStream;
import java.util.function.Consumer;

public interface ITts {

	void then(Consumer<InputStream> consumer);

	InputStream getStream();
}
