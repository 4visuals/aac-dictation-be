package github.visual4.aacweb.dictation.service.template;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class TemplateService {

	private final static String TEMPLATE_PATH="/templates/mailing/@tid.html"; // under the 'src/main/resources':templates
	
	public String loadTemplate(String templateId) {
		String path = TEMPLATE_PATH.replace("@tid", templateId);
		InputStream in = this.getClass().getResourceAsStream(path);
		var br = new BufferedReader(new InputStreamReader(in));
		StringBuilder html = new StringBuilder(br.lines().collect(Collectors.joining()));
		return html.toString();
	}
}
