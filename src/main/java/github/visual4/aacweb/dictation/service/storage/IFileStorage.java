package github.visual4.aacweb.dictation.service.storage;

public interface IFileStorage {
	/**
	 * 파일 저장 
	 * @param file
	 * @param resolver
	 */
    public void upload(IUpfile file, INameResolver resolver);
    /**
     * 파일 삭제
     * @param path
     */
	public void deleteFile(String path);
}
