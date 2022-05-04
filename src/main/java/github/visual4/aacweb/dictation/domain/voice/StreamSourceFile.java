package github.visual4.aacweb.dictation.domain.voice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.service.storage.IUpfile;

public class StreamSourceFile implements IUpfile {

	private String fileName;
	private InputStream in;
	private byte [] data;
	public StreamSourceFile(String fileName, InputStream in) {
		this.fileName = fileName;
		this.in = in;
		read();
	}
	@Override
	public int getFileLength() {
		return data.length;
	}

	@Override
	public File getTargetFile() {
		throw new RuntimeException("not impl");
	}

	@Override
	public InputStream openStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	@Override
	public void copyTo(File destFile) {
		throw new RuntimeException("not impl");
		
	}

	@Override
	public String getContentType() {
		return "audio/mpeg";
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	private void read() {
		if (data != null) {
			return;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte [] buf = new byte[4*1024];
		int n;
		try {
			while ((n = in.read(buf)) != -1) {
				bos.write(buf, 0, n);
			}
			bos.flush();
			this.data = bos.toByteArray();
		} catch (IOException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to read stream");
		}
	}

	public Voice toVoice(String textHash, String originText) {
		Voice v = new Voice(); // textHash, "local", getName(), getSize());
		v.setTextHash(textHash);
		v.setOriginText(originText);
		v.setOrigin("ncp");
		v.setFilePath(getFileName());
		v.setFileSize(getFileLength());
		return v;
	}
}
