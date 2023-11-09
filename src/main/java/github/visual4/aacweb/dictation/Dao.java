package github.visual4.aacweb.dictation;

import org.apache.ibatis.session.SqlSession;

public class Dao {
	
	private Dao() {}
	public static String mapper(Object dao, String method) {
		String clsName = dao.getClass().getSimpleName();
		if (!clsName.endsWith("Dao")) {
			throw new AppException(ErrorCode.APP_BUG, 500, "should end with *Dao, but" + clsName);
		}
		String nameSpace = clsName.replace("Dao", "Mapper.");
		return nameSpace + method;
	}
	/**
	 * [0, 1]개 row 수정 보장
	 * @param session
	 * @param queryPath
	 * @param param
	 */
	public static void updateOne(SqlSession session, String queryPath, Object param) {
		int nUpdated = session.update(queryPath, param);
		if (nUpdated > 1) {
			throw new AppException(ErrorCode.APP_BUG, 500, "tried to update " + nUpdated + " rows.");
		}
	}
	/**
	 * [0, 1]개 삭제 보장
	 * @param session
	 * @param queryPath
	 * @param param
	 */
	public static void deleteOne(SqlSession session, String queryPath, Object param) {
		int nDeleted = session.delete(queryPath, param);
		if (nDeleted > 1) {
			throw new AppException(ErrorCode.APP_BUG, 500, "tried to delete " + nDeleted + " rows.");
		}
	}
	
}
