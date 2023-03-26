package github.visual4.aacweb.dictation.service.mailing;

import java.io.InputStream;

import github.visual4.aacweb.dictation.TypeMap;

public class Attachment {
    final private static String KEY_CONTENT_TYPE = "Content-Type";
    final String fileName;
    final InputStream stream;
    final TypeMap meta;
    
    public Attachment(InputStream in, String fileName, String contentType) {
        this.stream = in;
        this.fileName = fileName;
        this.meta = new TypeMap();
        this.meta.put(KEY_CONTENT_TYPE, contentType);
    }
    public InputStream getInputStream() {
        return stream;
    }
    public TypeMap getMeta() {
        return meta;
    }
    public Attachment meta(String key, Object value) {
        meta.put(key, value);
        return this;
    }
    public String getContentType() {
        return this.meta.getStr(KEY_CONTENT_TYPE);
    }
    public String getFileName() {
        return fileName;
    }
}
