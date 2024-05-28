package github.visual4.aacweb.dictation.domain.order.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpirationUpdateDto {
	String orderUuid;
	Instant expiredAt;
}
