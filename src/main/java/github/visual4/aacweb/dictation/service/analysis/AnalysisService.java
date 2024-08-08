package github.visual4.aacweb.dictation.service.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.section.Section;
import github.visual4.aacweb.dictation.korean.Mark;
import github.visual4.aacweb.dictation.korean.level.LevelContext;

@Service
public class AnalysisService {

	
	Map<String, SentenceTag> taggingMap;
	public AnalysisService() {
		ObjectMapper om = new ObjectMapper();
		InputStream in = AnalysisService.class.getResourceAsStream("/greamhangul.txt");
		
//		String json = "[{\"sen\":\"아기 나무\",\"tags\":[{\"pos\":[0,2],\"pumsa\":\"NNG\"},{\"pos\":[3,5],\"pumsa\":\"NNG\"}]}]";
		
		try {
			if(in == null) {
				throw new RuntimeException("null");
			}
			String json = Util.readString(in);
			TypeReference<ArrayList<SentenceTag>> typeRef = new TypeReference<>() {};
			ArrayList<SentenceTag> data = om.readValue(json, typeRef);
			taggingMap = new HashMap<>();
			for (SentenceTag sen : data) {
				taggingMap.put(sen.sen, sen);
			}
//			System.out.println(map);
			
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Mark parseDifficulties(String text) {
		LevelContext ctx = new LevelContext(this.taggingMap);
		Mark mark = ctx.parseDifficulties(text.trim());
		return mark;
	}

	public TypeMap parseDifficulties(Section section) {
		LevelContext ctx = new LevelContext(this.taggingMap);
		TypeMap dfMap = new TypeMap();
		section.getSentences().forEach(sen -> {
			Mark mark = ctx.parseDifficulties(sen.getSentence().trim());
			dfMap.put("" +sen.getSeq(), mark.toMap());
		});
		return dfMap;
		
	}
}
