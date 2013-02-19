package com.sky.lucene;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKQueryParser;
import org.wltea.analyzer.lucene.IKSimilarity;

import com.sky.moudle.DataType;
import com.sky.moudle.MapBean;

public class LuceneSearch {
	static int queryCount = 10000000; // 默认返回100条数据
	final static String PATH = LuceneIndexOperator.PATH;
	// 查询字段集合
	private static String[] queryFileds = { "name", "township", "address",
			"eastNew", "northNew", "geom", "phone", "num", "dataType",
			"comType" };
	private static String[] smallqueryFileds = { "name", "township", "address",
			"eastNew", "northNew", "geom", "phone", "num" };

	/**
	 * 
	 * @param key
	 *            搜索内容所在的字段名称
	 * @param value
	 *            所要搜索的内容
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static List search(String key, String value) {
		IndexSearcher searcher;

		// 创建QueryParser对象,第一个参数表示Lucene的版本,第二个表示搜索Field的字段,第三个表示搜索使用分词器
		// Analyzer analyzer = new IKAnalyzer(); //new
		// StandardAnalyzer(Version.LUCENE_36)
		// QueryParser qp = new QueryParser(Version.LUCENE_36, key,analyzer);
		try {
			searcher = new IndexSearcher(IndexReader.open(FSDirectory
					.open(new File(PATH))));
			searcher.setSimilarity(new IKSimilarity());
			System.out.println("在" + key + "中检索" + value);
			Query query = IKQueryParser.parse(key, value);
			TopDocs topDocs = searcher.search(query, 100000);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			System.out.println("命中：" + topDocs.totalHits);
			Highlighter highlighter = createHighlighter(query);

			return processDocuments(topDocs, searcher, highlighter, true);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 高亮显示某一个字段
	 * 
	 * @param highlighter
	 * @param analyzer
	 * @param doc
	 * @param feild
	 * @return
	 */
	public static String hightlightFeild(Highlighter highlighter,
			Analyzer analyzer, Document doc, String feild) {
		String docContent = doc.get(feild);
		try {

			String hc = highlighter
					.getBestFragment(analyzer, feild, docContent);
			if (hc == null) {
				if (docContent.length() >= 50) {
					hc = docContent.substring(0, 50);
				} else {
					hc = docContent;
				}
			}
			doc.getField(feild).setValue(hc);
			// System.out.println(hc);
			return hc;
		} catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docContent;
	}

	/**
	 * 在所有的字段中搜索，如果是坐标的话，需要加上*，才能匹配到，这里采用标准方式获取的集合范围太大了，分词不是很准确
	 * 
	 * @param queryString
	 *            关键字
	 * @throws Exception
	 */
	@Deprecated
	public static void searchList(String queryString) throws Exception {
		IndexSearcher searcher = LuceneUtils.createIndexSearcher();
		Query query = LuceneUtils.createQuery(queryFileds, queryString);
		// 在搜索器中进行查询
		// 对查询内容进行过滤
		Filter filter = null;
		// 一次在索引器查询多少条数据
		int queryCount = 100;

		TopDocs results = searcher.search(query, filter, queryCount);
		System.out.println("总符合: " + results.totalHits + "条数！");

		// 显示记录
		for (ScoreDoc sr : results.scoreDocs) {
			// 文档编号
			int docID = sr.doc;
			// 真正的内容
			Document doc = searcher.doc(docID);
			System.out.println("name = " + doc.get("name"));
			System.out.println("address = " + doc.get("address"));
			System.out.println("eastNew = " + doc.get("eastNew"));
			System.out.println("northNew = " + doc.get("northNew"));
			System.out.println("geom = " + doc.get("geom"));
			System.out.println("phone = " + doc.get("phone"));

		}
	}

	/**
	 * 在所有的字段中搜索，如果是坐标的话，需要加上*，才能匹配到
	 * 
	 * @param queryString
	 *            关键字
	 * @param size
	 *            查询返回数，设置默认返回一千条。
	 * @throws IOException
	 * @throws Exception
	 */
	public static List searchListIK(String queryString, Integer size,
			boolean ishight) {
		// 查询的字符串:输入不存在的字符串是查询不到的,如：中国
		IndexSearcher searcher = null;
		// 这里使用的是IK的
		Query query = LuceneUtils.createQueryIK(queryFileds, queryString);

		// 在搜索器中进行查询
		// 对查询内容进行过滤
		Filter filter = null;
		// 一次在索引器查询多少条数据
		TopDocs results;

		// ezfilter filter1 = new ezfilter();
		// filter1.addFilter("", Value)

		try {
			searcher = LuceneUtils.createIndexSearcher();
			searcher.setSimilarity(new IKSimilarity());
			//
			if (size != null)
				results = searcher.search(query, filter, size);
			else
				results = searcher.search(query, filter, queryCount);
			System.out.println("符合: " + results.totalHits + "条数！");
			Highlighter highlighter = createHighlighter(query);
			return processDocuments(results, searcher, highlighter, ishight);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			LuceneIndexOperator.closeLucene(null, null, searcher);
		}
		return null;
	}

	/**
	 * 组合检索，主要是对dataTypeKey 以及 comTypeKey这样的字段进行过滤，过滤的集合为并集
	 * 也可以考虑采用filter来实现
	 * @param queryString
	 *            关键字
	 * @param size
	 *            查询返回数，设置默认返回一千条。
	 * @throws IOException
	 * @throws Exception
	 */
	public static List searchListIK(String queryString, Integer size,
			boolean ishight, String comtypes[],String datatypes[]) {
		IndexSearcher searcher = null;
		TopDocs results;
		String mainquery = createMainQueryStringexp(queryString);
		String dataqueryStringexp = createQueryString(datatypes,"dataTypeKey");
		String comqueryStringexp = createQueryString(comtypes,"comTypeKey");
		StringBuffer exp=new StringBuffer();
		exp.append("(");
		if(!"".equals(mainquery)){
			exp.append("("+mainquery+")");	
		}
		if(!"".equals(dataqueryStringexp)){
			exp.append("&&("+dataqueryStringexp+")");
		}
		if(!"".equals(comqueryStringexp)){
			exp.append("&&("+comqueryStringexp+")");
		}
		exp.append(")");
		Query resultQuery = IKQueryParser.parse(exp.toString());
		try {
			searcher = LuceneUtils.createIndexSearcher();
			searcher.setSimilarity(new IKSimilarity());
			if (size != null)
				results = searcher.search(resultQuery, null, size);
			else
				results = searcher.search(resultQuery, null, queryCount);
			System.out.println("符合: " + results.totalHits + "条数！");
			Highlighter highlighter = createHighlighter(resultQuery);
			return processDocuments(results, searcher, highlighter, ishight);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			LuceneIndexOperator.closeLucene(null, null, searcher);
		}
		return null;
	}
	
	
	/**
	 * 创建主要字段的查询表达式
	 * @param keyword
	 * @return
	 */
	public static String createMainQueryStringexp(String keyword){
		String exp="";
		for(int j =0 ;j<smallqueryFileds.length;j++){
			exp+=smallqueryFileds[j]+":'"+keyword+"'";
			if(j!=(smallqueryFileds.length-1)){
				exp+="||";
			}
		}
		return exp;
		
	}
	
	/**
	 * 创建类型查询的表达式
	 * @param datatypes
	 * @param feild
	 * @return
	 */
	public  static String createQueryString(String datatypes[],String feild){
		StringBuffer sub = new StringBuffer();
		if (datatypes != null) {
			if(datatypes.length==1&&"".equals(datatypes[0])){
				return "";
			}
			for (int i = 0; i < datatypes.length; i++) {
				if (datatypes[i] != null) {
						sub.append(feild+":'"+datatypes[i]+"'");
					if(i!=(datatypes.length-1)){
						sub.append("||");
					}
					
				}
			}
		}
		return sub.toString();
	}

	/**
	 * 在所有的字段中搜索，如果是坐标的话，需要加上*，这里是采用IK分词的
	 * 
	 * @param queryString
	 *            关键字
	 * @param 分页的大小
	 * @param 当前页
	 * @throws IOException
	 * @throws Exception
	 */
	public static PageBean searchListIKByPage(String queryString,
			PageBean pageBean, boolean ishight) {
		// 查询的字符串:输入不存在的字符串是查询不到的,如：中国
		// 查询字段集合
		IndexSearcher searcher = null;

		// 这里使用的是IK的
		Query query = LuceneUtils.createQueryIK(queryFileds, queryString);
		// 在搜索器中进行查询
		// 对查询内容进行过滤
		Filter filter = null;
		// 一次在索引器查询多少条数据

		TopDocs results;
		try {

			searcher = LuceneUtils.createIndexSearcher();
			searcher.setSimilarity(new IKSimilarity());
			results = searcher.search(query, filter, 10000000);

			// 查询起始记录位置
			int begin = pageBean.getPageSize() * (pageBean.getPage() - 1);
			// 查询终止记录位置
			int end = Math.min(begin + pageBean.getPageSize(),
					results.totalHits);

			System.out.println("总符合: " + results.totalHits + "条数！");

			Highlighter highlighter = createHighlighter(query);
			List list = processDocuments(results, searcher, highlighter, begin,
					end, ishight);
			pageBean.setCount(results.totalHits);
			pageBean.setPageItems(list);
			return pageBean;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			LuceneIndexOperator.closeLucene(null, null, searcher);
		}
		return null;
	}

	public static Highlighter createHighlighter(Query query) {
		Formatter formatter = new SimpleHTMLFormatter("<font color=red>","</font>");
		Scorer fragmentScorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
		Fragmenter fragmenter = new SimpleFragmenter(50);
		highlighter.setTextFragmenter(fragmenter);
		return highlighter;
	}

	/*
	 * 复合条件查询，即 and or 等 BooleanClause.Occur.MUST表示and
	 * BooleanClause.Occur.MUST_NOT表示not BooleanClause.Occur.SHOULD表示or.
	 */
	public static List<MapBean> searchQuery(String queries, String fields[],
			BooleanClause.Occur[] clauses) {

		IndexSearcher searcher = null;
		try {
			searcher = LuceneUtils.createIndexSearcher();
			// BooleanClause.Occur[] clauses = {
			// BooleanClause.Occur.SHOULD,BooleanClause.Occur.SHOULD};
			Query query = IKQueryParser.parseMultiField(fields, queries,
					clauses);
			searcher.setSimilarity(new IKSimilarity());
			TopDocs results = searcher.search(query, null, 10000000);
			Highlighter highlighter = createHighlighter(query);
			System.out.println("总符合: " + results.totalHits + "条数！");
			return processDocuments(results, searcher, highlighter, true);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			LuceneIndexOperator.closeLucene(null, null, searcher);
		}
		return null;

	}

	/**
	 * 获取可以做全文检索的字段
	 * 
	 * @param mapdata
	 * @return
	 */
	public static String[] getQueryFeild(MapBean mapdata) {
		ArrayList<String> feild = new ArrayList<String>();
		Method method[] = mapdata.getClass().getDeclaredMethods();
		for (int i = 0; i < method.length; i++) {
			if (method[i].getName().startsWith("get")) {
				Annotation[] an1 = method[i].getAnnotations();
				for (int j = 0; j < an1.length; j++) {
					if (an1[j] instanceof javax.persistence.Column) {
						javax.persistence.Column an11 = (javax.persistence.Column) an1[j];
						feild.add(an11.name());
					}
				}
			}
		}
		return feild.toArray(new String[feild.size()]);
	}

	/**
	 * 查询出所有的
	 * 
	 * @param scoreDocs
	 * @param searcher
	 * @param hightlighter
	 * @return
	 */
	public static List processDocuments(TopDocs topdocs,
			IndexSearcher searcher, Highlighter hightlighter, boolean ishight) {
		return processDocuments(topdocs, searcher, hightlighter, 0,
				topdocs.totalHits, ishight);
	}

	/**
	 * 处理所有的document
	 * 
	 * @param scoreDocs
	 *            搜索出来的doc数组
	 * @param searcher
	 *            搜索器
	 * @return
	 */
	public static List processDocuments(TopDocs topdocs,
			IndexSearcher searcher, Highlighter hightlighter, int begin,
			int end, boolean ishight) {
		ScoreDoc[] scoreDocs = topdocs.scoreDocs;
		ArrayList al = new ArrayList();
		Analyzer analyzer = new IKAnalyzer();
		for (int i = begin; i < end; i++) {
			int docid = scoreDocs[i].doc;
			try {
				Document doc = searcher.doc(docid);
				if (ishight) {
					hightlightFeild(hightlighter, analyzer, doc, "num");
					hightlightFeild(hightlighter, analyzer, doc, "name");
					hightlightFeild(hightlighter, analyzer, doc, "address");
					hightlightFeild(hightlighter, analyzer, doc, "dataType");
				}
				MapBean map = processSingleDocument(doc, hightlighter);

				// System.out.println(map.getAddress());
				al.add(map);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return al;
	}

	/**
	 * 处理单个document
	 * 
	 * @param doc
	 * @return
	 */
	public static MapBean processSingleDocument(Document doc,
			Highlighter hightlighter) {
		MapBean mapdata = new MapBean();
		mapdata.setName(doc.get("name"));
		mapdata.setAddress(doc.get("address"));
		mapdata.setCity(doc.get("city"));
		mapdata.setEastNew(doc.get("eastNew"));
		mapdata.setNorthNew(doc.get("northNew"));
		mapdata.setDatatype(doc.get("datatype"));
		mapdata.setId(Integer.parseInt(doc.get("id")));
		mapdata.setPhone(doc.get("phone"));
		mapdata.setNum(doc.get("num"));
		mapdata.setNum2(doc.get("num2"));
		mapdata.setTownship(doc.get("township"));
		mapdata.setDataTypeByComType(new DataType(doc.get("comType"), doc.get("comTypeKey")));
		mapdata.setDataTypeByDataType(new DataType(doc.get("dataType"), doc.get("dataTypeKey")));
		return mapdata;
	}

	public static void addIndex(MapBean map) throws IOException {
		Directory fsDir = FSDirectory.open(new File(PATH));
		NRTCachingDirectory cachedFSDir = new NRTCachingDirectory(fsDir, 5.0,
				60.0);
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
				analyzer);
		conf.setMergeScheduler(cachedFSDir.getMergeScheduler());
		IndexWriter writer = new IndexWriter(cachedFSDir, conf);
		Document doc = new Document();
		doc.add(new Field("id", "" + map.getId(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field("name", map.getName(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("township", map.getTownship(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("address", map.getAddress(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("city", map.getCity(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("num", map.getNum(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("num2", map.getNum(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("eastNew", map.getEastNew(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field("northNew", map.getNorthNew(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field("phone", map.getPhone(), Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("geom", map.getEastNew() + "," + map.getNorthNew(),
				Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("dataType", map.getDataTypeByDataType().getDataTypeName(),
				Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("comType", map.getDataTypeByComType().getDataTypeName(),
				Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("dataTypeKey", map.getDataTypeByDataType().getDataTypeKey(),
				Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("comTypeKey", map.getDataTypeByComType().getDataTypeKey(),
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);
		writer.commit();
		writer.close();
		fsDir.close();
	}
	
	public static void combinationQuery(String keyword , String datatypes[] , String comtypes[] ){
		IndexSearcher searcher = null;
		try {
			searcher = LuceneUtils.createIndexSearcher();
			// BooleanClause.Occur[] clauses = {
			// BooleanClause.Occur.SHOULD,BooleanClause.Occur.SHOULD};
			Query query = IKQueryParser.parseMultiField(smallqueryFileds,keyword);
			BooleanQuery boolquery = new BooleanQuery();
			boolquery.add(query, BooleanClause.Occur.MUST);
			BooleanQuery datatype = new BooleanQuery();
			for(int i=0;i<datatypes.length;i++){
				if(datatypes[i] != null){
					Query partQuery = IKQueryParser.parse("dataTypeKey" , datatypes[i]);
					if(partQuery != null && 
					          (!(partQuery instanceof BooleanQuery) || ((BooleanQuery)partQuery).getClauses().length>0)){
						datatype.add(partQuery, Occur.SHOULD); 
					}
				}			
				
			}
			
			BooleanQuery comtype = new BooleanQuery();
			for(int j=0;j<comtypes.length;j++){
				if(comtypes[j] != null){
					Query partQuery = IKQueryParser.parse("comTypeKey" , comtypes[j]);
					if(partQuery != null && 
					          (!(partQuery instanceof BooleanQuery) || ((BooleanQuery)partQuery).getClauses().length>0)){
						comtype.add(partQuery, Occur.SHOULD); 
					}
				}			
			}
			
			boolquery.add(datatype,BooleanClause.Occur.MUST);
			
			boolquery.add(comtype,BooleanClause.Occur.MUST);
			
			System.out.println(boolquery);
			searcher.setSimilarity(new IKSimilarity());
			TopDocs results = searcher.search(boolquery, null, 10000000);
			Highlighter highlighter = createHighlighter(query);
			System.out.println("总符合: " + results.totalHits + "条数！");
		  //  processDocuments(results, searcher, highlighter, true);
			
			for(int m = 0; m<results.totalHits;m++){
				int docid = results.scoreDocs[m].doc;
				Document doc = searcher.doc(docid);
				System.out.println("name = " + doc.get("name"));
				System.out.println("dataType = " + doc.get("dataType"));
				System.out.println("dataTypeKey = " + doc.get("dataTypeKey"));
				System.out.println("comtype = " + doc.get("comType"));
				System.out.println("comTypeKey = " + doc.get("comTypeKey"));
			}
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			LuceneIndexOperator.closeLucene(null, null, searcher);
		}
		
	}
	
	
	public static void main(String a[]){
		//LuceneIndexOperator.createIndex();
		combinationQuery("体育",new String[]{"WANBA","WIFI"},new String[]{"CMCC","TELECOM"});
		
	}

}
