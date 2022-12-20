package github.visual4.aacweb.dictation.domain.order.aim_port;

import java.util.List;
import java.util.Set;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import lombok.Getter;
import lombok.Setter;
/**
 * 
 *  {imp_uid=imp_1234567890, merchant_uid=merchant_1234567890, status=ready}
 * @author chminseo
 *
 */
@Getter
@Setter
public class AimportHook {

	public enum PayStatus {
		ready,
		paid,
		failed,
		cancelled
	}
	String aimportUuid;
	String orderUuid;
	String status;
	
	public static class Builder {
		String ip;
		String xff;
		String imp_uid;
		String merchant_uid;
		String status; // 'paid', 'ready', etc
		final Set<String> validIPs = Set.of("52.78.100.19", "52.78.48.223", "52.78.5.241");
		
		public Builder clientIP(String clientIp, String xff) {
			if (!validIPs.contains(xff)) {
				throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "not a valid ip");
			}
			this.ip = clientIp;
			this.xff = xff;
			return this;
		}

		public AimportHook build() {
			AimportHook hook = new AimportHook();
			hook.aimportUuid = imp_uid;
			hook.orderUuid = merchant_uid;
			hook.status = status;
			return hook;
		}

		public Builder status(String status) {
			this.status = PayStatus.valueOf(status).name();
			return this;
		}

		public Builder uuid(String imp_uid, String merchant_uid) {
			this.imp_uid = imp_uid;
			this.merchant_uid = merchant_uid;
			return this;
		}
	}
	public static Builder newBuilder() {
		return new Builder();
	}
	/**
	 * 
	 * @param aimportUuid
	 * @param orderUuid
	 */
	public boolean checkUuid(String aimportUuid, String orderUuid) {
		return this.aimportUuid.equals(aimportUuid) && this.orderUuid.equals(orderUuid);
	}
	/**
	 * 
	 * @param status
	 * @return
	 */
	public boolean checkStatus(PayStatus status) {
		return this.status.equals(status.name());
	}
}
