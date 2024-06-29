package github.visual4.aacweb.dictation.domain.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentTransferDto {

	public Long studentSeq;
	public Long teacher;
	public String license;
	public Long nextTeacher;
}
