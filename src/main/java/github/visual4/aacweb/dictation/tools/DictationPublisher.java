package github.visual4.aacweb.dictation.tools;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import github.visual4.aacweb.dictation.tools.Sentence.SentenceType;

public class DictationPublisher {
	
	private DataSource ds;
	
	public DictationPublisher(DataSource ds) {
		this.ds = ds;
	}
	private void run() {
		
		Connection con = Db.open(ds);
		
		insertEojeols(con);
		
		List<Section> sectionMap = insertSections(con, Origin.L);
		List<Sentence> sentences = insertSentences(con, sectionMap, Origin.L);
		
		Map<String, Eojeol> ejMap = listEojeols(con);
		
		bindMappings(con, sentences, ejMap);
		
	}
	
	void bindMappings(Connection con, List<Sentence> sentences, Map<String, Eojeol> ejMap) {
		clear(con, "wr_mapping");
		Stmt querySentenceElem = Db.stmt(con, 
				  " SELECT\n"
				+ "	se.origin_word,\n"
				+ "	se.word_type,\n"
				+ "	p.origin, \n"
				+ "	p.gen_name \n"
				+ " FROM sentence_elems se \n"
				+ " JOIN pics p ON se.pic_ref = p.seq \n"
				+ " WHERE  se.sentence  = ?");
		Stmt insertMapping = Db.stmt(con, "INSERT INTO wr_mapping (wr_sen, wr_eojeol, pumsa, pic, ordernum) VALUES (?, ?, ?, ?, ?)");
		for (Sentence sen : sentences) {
			Rset rset = querySentenceElem.clear().bind(1, sen.getAacSentenceRef()).select();
			int [] order = {0};
			rset.each((row, index) -> {
				String originWord = row.str("origin_word");
				Integer senRef = sen.getSeq();
				String origin = row.str("origin");
				Eojeol ej = ejMap.get(originWord.trim());
				if (ej == null) {
					throw new RuntimeException("없는 어절 발견: " + originWord.trim() + " at sentence " + sen );
				}
				String pumsa = row.str("word_type");
				String pic = row.str("gen_name");
				insertMapping
					.clear()
					.bind(1, senRef)
					.bind(2, ej.getSeq())
					.bind(3, pumsa)
					.bind(4, "aacweb:/" + origin + "/" + pic)
					.bind(5, order[0]++)
					.update();
			});
			
		}
		
	}
	void clear(Connection con, String tableName) {
		String query = "DELETE FROM " + tableName;
		int cnt = Db.stmt(con, query).update();
		info("CLEAR", "table %s: %d deleted",tableName, cnt);
	}
	
	/**
	 * sentence_basket => section 추출
	 * @param con
	 * @return
	 */
	List<Section> insertSections(Connection con, Origin origin) {
		
		clear(con, "wr_section");
		clear(con, "wr_chapter");
		
		String query = "SELECT \n"
				+ " sb.seq, \n"
				+ " sb.basket_name \n"
				+ " FROM sentence_basket sb\n"
				+ " WHERE  sb.owner = 7 AND sb.`type` = 'SENTENCE' AND sb.seq >= 10768;";
		Rset rset = Db.stmt(con, query).select();
		Stmt insertSection = Db.stmt(con, "INSERT INTO wr_section (seq, `desc`, `level`, chapter) VALUES (?, ?, ?, ? )");
		Stmt insertChapter = Db.stmt(con, "INSERT INTO wr_chapter( seq, `desc`, origin ) VALUES (?, ?, ?)");
		List<Section> sections = new ArrayList<>();
		List<Section> sectionsInChapter = new ArrayList<>();
		int [] chapterSeq = {1};
		rset.each((row, index) -> {
			Section s = new Section();
			String desc = row.str("basket_name");
			s.setSeq(index+1);
			s.setBasketRef(row.Int("seq"));
			
			Integer level = inferSectionLevel(desc);
			s.setLevel(level);
			
			desc = normalizeDesc(desc, level);
			s.setDescription(desc);
			/*
			stmt.clear()
				.bind(1, s.getSeq())
				.bind(2, s.getDescription())
				.bind(3, s.getLevel())
				.update();
			*/
			// sections.add(s);
			
			sectionsInChapter.add(s);
			if (level == -1) {
				createChapter(insertChapter, chapterSeq[0]++, s.getDescription(), origin, 
						insertSection, sectionsInChapter);
				sections.addAll(sectionsInChapter);
				sectionsInChapter.clear();
			}
			
		});
		return sections;
	}
	
	private void createChapter(
			Stmt insertChapter, int chapterSeq, String chapterDesc, Origin chapterOrigin,
			Stmt insertSection,
			List<Section> sectionsInChapter) {
		insertChapter.clear()
			.bind(1, chapterSeq)
			.bind(2, chapterDesc)
			.bind(3, chapterOrigin)
			.update();
		for (Section s : sectionsInChapter) {
			insertSection.clear()
				.bind(1, s.getSeq())
				.bind(2, s.getDescription())
				.bind(3, s.getLevel())
				.bind(4, chapterSeq)
				.update();
		}
	}
	private String normalizeDesc(String desc, Integer level) {
		if (level == -1) {
			int s = desc.indexOf('(');
			int e = desc.lastIndexOf(')');
			return desc.substring(s+1, e);
		} else {
			int s = desc.indexOf('.');
			return desc.substring(s +1);
		}
	}

	/**
	 * sentence_basket 의 basket_name 을 보고 level을 얻어냄(23.ㅇㅇㅇ, 24.ㅎㅎㅎ, 종합.ㅎㅎㅎ)
	 * @param desc
	 * @return
	 */
	Integer inferSectionLevel(String desc) {
		// '종합' 아니면 '숫자.' 로 제목이 시작됨
		if (desc.startsWith("종합")) {
			return -1;
		}
		int dot = desc.indexOf('.');
		if (dot < 0) {
			// '종합'도 아니고 '숫자.'으로 시작하지도 않음. 확인 필요
			throw new RuntimeException("부적합한 제목 패턴: '종합' 또는 '숫자.'이어야 함: " + desc);
		}
		String n = desc.substring(0, dot);
		try {
			return Integer.parseInt(n.trim());			
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("'숫자.' 패턴이 아님 [" + n + "]");
		}
	}

	List<Sentence> insertSentences(Connection con, List<Section> sections, Origin origin) {
		
		clear(con, "wr_sentence");
		
		Stmt querySentence = Db.stmt(con, 
				"select seq, sentence from sentence_one so where so.basket = ? ");
		final int [] seq = {1};
		Stmt insertSentence = Db.stmt(con, "INSERT INTO wr_sentence ("
				+ " seq, sentence, level, origin, type, cate "
				+ " ) VALUES( "
				+ "?, ?, ?, ?, ?, ? "
				+ " )");
		List<Sentence> sentences = new ArrayList<>();
		for (Section section : sections) {
			Rset rset = querySentence.clear().bind(1, section.getBasketRef()).select();
			rset.each((row, index) -> {
				// each sentence
				String sentence = row.str("sentence");
				boolean isWord = sentence.trim().split(" ").length == 1;
				Sentence sen = new Sentence(seq[0]++, 
						sentence,
						section.getLevel(),
						origin,
						isWord ? SentenceType.W : SentenceType.S,
						section.getSeq());
				insertSentence.clear()
					.bind(1, sen.getSeq())
					.bind(2, sen.getSentence())
					.bind(3, sen.getLevel())
					.bind(4, sen.getOrigin()) // 단계별학습
					.bind(5, sen.getType()) // 문장학습
					.bind(6, sen.getSectionRef())
					.update();
				sen.setAacSentenceRef(row.Int("seq"));
				sentences.add(sen);
			});
			
		}
		querySentence.close();
		insertSentence.close();
		return sentences;
		
	}

	Map<String, Eojeol> listEojeols(Connection con) {
		Stmt stmt = Db.stmt(con, "select * from wr_eojeol");
		Rset rset = stmt.select();
		Map<String, Eojeol> map = new HashMap<>();
		rset.each((row, index) -> {
			Integer seq = row.Int("seq");
			String text = row.str("text");
			Integer level = row.Int("level");
			Eojeol ej = new Eojeol(seq, text, level);
			map.put(text, ej);
		});
		return map;
	}
	
	void insertEojeols(Connection con) {
		
		clear(con, "wr_eojeol");
		
		String FLUSH_EOJEL_SENTENCE = 
				  " INSERT INTO wr_eojeol  "
				+ " SELECT   "
				+ "  	@rn := @rn + 1 as seq,  "
				+ " 	ej.text, "
				+ " 	0 as level "
				+ " FROM  ( SELECT "
				+ " 			DISTINCT (TRIM(se.origin_word)) as `text` "
				+ " 		FROM sentence_basket sb "
				+ " 		JOIN sentence_one so ON so.basket = sb.seq  "
				+ " 		JOIN sentence_elems se on se.sentence = so.seq  "
				+ " 		WHERE  sb.owner = 7 AND sb.`type` = 'SENTENCE' AND sb.seq >= 10768 "
				+ " ) ej "
				+ " JOIN ( select @rn := 0 ) _";
		Stmt stmt = Db.stmt(con, FLUSH_EOJEL_SENTENCE);
		int cnt = stmt.update();
		info("EOJEOL", "%d inserted\n", cnt);
		stmt.close();
	}

	void info(String label, String format, Object ... args) {
		System.out.printf("[%s] ", label);
		System.out.printf(format, args);
		System.out.println();
		
	}

	public static void main(String[] args) {
		String url = "jdbc:mariadb://localhost:3306/aacweb";
		DataSource ds = new  DriverManagerDataSource(url, "root", "1111");
		
		DictationPublisher m = new DictationPublisher(ds);
		m.run();
	}
	

	
}
