package github.visual4.aacweb.dictation.service.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IUpfile {

    String getFileName();
    
    int getFileLength();
    
    File getTargetFile();

    InputStream openStream() throws IOException;
    
	void copyTo(File destFile);

	String getContentType();

}
