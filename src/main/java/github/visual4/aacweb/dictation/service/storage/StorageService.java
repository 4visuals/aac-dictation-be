package github.visual4.aacweb.dictation.service.storage;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.domain.voice.StorageTarget;
import github.visual4.aacweb.dictation.service.storage.local.LocalStorage;
import github.visual4.aacweb.dictation.service.storage.ncp.NcpStorage;

@Service
public class StorageService {

	private final LocalStorage localStorage;
	private final NcpStorage ncpStorage;

	public StorageService(NcpStorage ncp, LocalStorage local) {
		super();
		this.ncpStorage = ncp;
		this.localStorage = local;
	}

	public void store(IUpfile meta, StorageTarget target) {
		if(target == StorageTarget.ncp) {			
			this.ncpStorage.upload(meta, file -> "voices3/" + file.getFileName());
		} else if (target == StorageTarget.local) {
			this.localStorage.upload(meta, file -> file.getFileName());
		}
	}

	public NcpStorage getNcpStorage() {
		return ncpStorage;
	}

	public LocalStorage getLocalStorage() {
		return localStorage;
	}
}
