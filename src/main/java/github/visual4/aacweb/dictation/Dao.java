package github.visual4.aacweb.dictation;

public class Dao {
	public static String mapper(Object dao, String method) {
		String clsName = dao.getClass().getSimpleName();
		if (!clsName.endsWith("Dao")) {
			throw new AppException(ErrorCode.APP_BUG, 500, "should end with *Dao, but" + clsName);
		}
		String nameSpace = clsName.replace("Dao", "Mapper.");
		return nameSpace + method;
	}
}
