package github.visual4.aacweb.dictation;

public class Query {

	private String q;

	private StringBuilder sb = new StringBuilder();
	
	public Query(String query) {
		this.q = query;
		sb.append(q);
	}
	
	public Query bind(String prop, String alias, String ... val) {
		int p = sb.lastIndexOf(prop);
		while (p >= 0) {
			sb.delete(p, p + prop.length());
			for(int k = val.length-1 ; k >= 0; k--) {
				sb.insert(p, val[k].toString());
				sb.insert(p, '.');
				sb.insert(p, alias);
				if (k > 0) {
					sb.insert(p, ',');
					sb.insert(p, ' ');
				}
			}
			p = sb.lastIndexOf(prop, p-1);
		}
		return this;
	}
	
	public String get() {
		String q  =sb.toString();
		System.out.println("[QUERY]" + q);
		return q;
	}
	

}
