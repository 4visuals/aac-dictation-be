package github.visual4.aacweb.dictation.service.storage.local;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.service.storage.IFileStorage;
import github.visual4.aacweb.dictation.service.storage.INameResolver;
import github.visual4.aacweb.dictation.service.storage.IUpfile;
/**
 * 
 * @author chminseo
 *
 */
@Component
public class LocalStorage implements IFileStorage {
	@Value("${dictation.local.storage.root-dir}") String rootDir;
	
	private File voiceDir;
	
	@PostConstruct
	public void checkRootDir() {
		voiceDir = new File(rootDir + "/voices");
		if(!voiceDir.exists()) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "check root-dir path of local storage");
		}
	}
	/**
	 * 빈 파일 생성함
	 * @param fileName
	 * @return
	 */
	public File touchFile(String fileName) {
		File file = new File(voiceDir, fileName);
		try {
			if(file.exists()) {
				return file;
			}
			if(file.createNewFile()) {
				return file;
			} else {
				throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to create voice file: " + fileName);
			}
		} catch (IOException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to create voice file: " + fileName);
		}
	}
	public File getVoiceFile(String fileName) {
		return voiceFileByName(fileName, true);
	}
	@Override
	public void upload(IUpfile file, INameResolver resolver) {
		File f = voiceFileByName(file.getFileName(), false);
		
		try(InputStream in = file.openStream()) {
			copy(in, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void copy(InputStream in, File file) {
		try(OutputStream out = new BufferedOutputStream(new FileOutputStream(file)))  {
			int c = 0;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			out.flush();
		} catch (IOException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to store file: " + file.getName());
		}
	}

	@Override
	public void deleteFile(String path) {
		throw new AppException(ErrorCode.SERVER_ERROR, 500, "not implemented");
		
	}
	
	private File voiceFileByName (String fileName, boolean shouldExist) {
		File f = new File(voiceDir, fileName);
		if (!shouldExist && f.exists()) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "existing file"); 
		}
		if (shouldExist && !f.exists()) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "no such file"); 
		}
		return f;
	}
	public byte[] open(String category, String fileName) {
		if("voice".equals(category)) {
			File voiceFile = voiceFileByName(fileName, true);
			
			try {
				return Files.readAllBytes(voiceFile.toPath());
			} catch (IOException e) {
				throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to read file: " + fileName);
			}
		} else {
			throw new AppException(ErrorCode.INVALID_VALUE, 400, "invalid access");
		}
	}
}
