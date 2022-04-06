package github.visual4.aacweb.dictation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Util {
    public static TypeMap parseJson(ObjectMapper om, String json) {
    	try {
			Map<String, Object> map = om.readValue(json, HashMap.class);
			return new TypeMap(map);
		} catch (JsonMappingException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500,
					"error while parsing json(JsonMappingException)");
		} catch (JsonProcessingException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500,
					"error while parsing json(JsonProcessingException)");
		}
    }

	public static String stringify(ObjectMapper om, TypeMap map) {
		try {
			return om.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "error while stringify data");
		}
	}
	
	public static String readFile(String absolutePath) {
		File priv = new File(absolutePath);
		try {
			return Files.readString(priv.toPath());
		} catch (IOException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "check file path: " + absolutePath);
		}
	}
}
