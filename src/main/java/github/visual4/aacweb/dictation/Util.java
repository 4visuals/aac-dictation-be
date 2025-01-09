package github.visual4.aacweb.dictation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
	public static <K, V> String stringify(ObjectMapper om, Map<K, V> map) {
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

	public static MessageDigest digiester(String algo) {
		try {
			return MessageDigest.getInstance(algo);
		} catch (NoSuchAlgorithmException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, algo);
		}
	}
	
	public static String toHexString(byte[] bytes) {
	    char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j*2] = hexArray[v/16];
	        hexChars[j*2 + 1] = hexArray[v%16];
	    }
	    return new String(hexChars);
	}
	
	public static class Hash {
		public static String sha256(String text) {
			MessageDigest md = Util.digiester("SHA-256");
			byte [] hashed = md.digest(text.getBytes());
			return Util.toHexString(hashed);	 
		}
		public static String md5(String text) {
			MessageDigest md= Util.digiester("MD5");
			byte [] hashed = md.digest(text.getBytes());
			return Util.toHexString(hashed);
		}
	}

	public static <T> void forEach(List<T> list, Consumer<T> fn) {
		for (T obj : list) {
			fn.accept(obj);
		}
	}

	public static void sleep(int secs) {
		try {
			Thread.sleep(3*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public static String join(Object ... tokens) {
		StringBuilder sb = new StringBuilder();
		for (Object token : tokens) {
			sb.append(token);
		}
		return sb.toString();
	}

	public static void write(byte[] data, HttpServletResponse res) {
		ServletOutputStream out = null;
		try  {
			out = res.getOutputStream();
			out.write(data);
			out.flush();	
		} catch(IOException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to flush data");
		}
		
		
	}
	private static void close(Closeable out) {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * in에서 읽어서 out에 써넣음
	 * @param in
	 * @param closeInStream - true이면 다 읽은 후 닫아줌
	 * @param out
	 * @param closeOutStream - true이면 다 읽은 후 닫아줌
	 */
	public static void tranfer(
			InputStream in, boolean closeInStream,
			OutputStream out, boolean closeOutStream) {
		BufferedInputStream bin = in instanceof BufferedInputStream ? (BufferedInputStream) in : new BufferedInputStream(in);
		BufferedOutputStream bout = out instanceof BufferedOutputStream ? (BufferedOutputStream) out: new BufferedOutputStream(out);
		int b= -1;
		try {
			while((b = bin.read()) != -1) {
				bout.write(b);
			}	
			bout.flush();
		} catch (IOException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to transfer data");
		} finally {
			if(closeInStream) {
				close(bin);
			}
			if(closeOutStream) {
				close(bout);
			}
		}
		
	}

	public static String toKoreanTime(Instant utcTime) {
		Instant kr = utcTime.plus(9, ChronoUnit.HOURS);
		return kr.toString().replace("T", " ").replace("Z", "");
	}

	public static String readString(InputStream in) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024 * 600);
		int r = 0;
		while((r = bin.read()) != -1) {
			bos.write(r);
		}
		in.close();
		return new String(bos.toByteArray());
	}
}
