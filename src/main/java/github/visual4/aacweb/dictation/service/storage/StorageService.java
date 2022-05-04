package github.visual4.aacweb.dictation.service.storage;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.service.storage.ncp.NcpStorage;

@Service
public class StorageService {

//	final private LocalStorage storage;
	final private NcpStorage storage;

	public StorageService(NcpStorage storage) {
		super();
		this.storage = storage;
	}

	public void store(IUpfile meta) {
		this.storage.upload(meta, (file) -> "voices/" + file.getFileName());
	}
}
