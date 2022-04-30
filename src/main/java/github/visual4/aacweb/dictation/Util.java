package github.visual4.aacweb.dictation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.domain.product.Product;


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

	public static <T> void notNull(T instance, ErrorCode serverError, int code, String msg) {
		if (instance == null) {
			throw new AppException(serverError, code, msg);
		}
		
	}
	
	public static <T> T createInstance (Class<?> cls, Object [] args, Class<?> ...argsTypes ) {
		Constructor<?> c = null;
		boolean accessible = false;
		try {
//			 c = cls.getConstructor(argsTypes);
			c = cls.getDeclaredConstructor(argsTypes);
			accessible = c.canAccess(null);
			c.setAccessible(true);
			
			return (T)c.newInstance(args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} finally {
			c.setAccessible(accessible);
		}
	}
}
