package github.visual4.aacweb.dictation;

public class Dao {
	public static String mapper(Object dao, String method) {
		String clsName = dao.getClass().getSimpleName();
		if (!clsName.endsWith("Dao")) {
			throw new AppException("SERVER_ERROR", 500);
		}
		String nameSpace = clsName.replace("Dao", "Mapper.");
		return nameSpace + method;
	}
}
