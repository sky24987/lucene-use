package com.sky.lucene;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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

import com.sky.lucene.util.TaskEngine;
import com.sky.moudle.MapBean;

public class IndexOperator {

	final static String PATH =LuceneUtils.INDEXPATH;
	static TimerTask task = new TimerTask(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				createIndex();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	};
	/**
	 * 创建索引
	 */
	private static void createIndex() throws IOException {

		Directory fsDir = FSDirectory.open(new File(PATH));
		NRTCachingDirectory cachedFSDir = new NRTCachingDirectory(fsDir, 5.0,
				60.0);
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
				analyzer);
		conf.setMergeScheduler(cachedFSDir.getMergeScheduler());
		IndexWriter writer = new IndexWriter(cachedFSDir, conf);
		writer.deleteAll();
		
		//索引的数据源
		List<MapBean> ls = LuceneIndexOperator.createDataSource();
		int i=0;
		for(MapBean mdata:ls){
			//mdata.getClass().getDeclaredMethods();
			Document doc = new Document();
			doc.add(new Field("id",""+mdata.getId(),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new Field("name",mdata.getName(),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new Field("address",mdata.getAddress(),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new Field("city",mdata.getCity(),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new Field("num",mdata.getNum(),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new Field("eastNew",nullToBlank(mdata.getEastNew()),Field.Store.YES,Field.Index.NOT_ANALYZED));
			doc.add(new Field("northNew",nullToBlank(mdata.getNorthNew()),Field.Store.YES,Field.Index.NOT_ANALYZED));
			doc.add(new Field("datatype",mdata.getDatatype(),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new Field("phone",mdata.getPhone(),Field.Store.YES,Field.Index.NOT_ANALYZED));
			doc.add(new Field("geom",mdata.getEastNew()+","+mdata.getNorthNew(),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new Field("dataType", nullToBlank(mdata.getDatatype()), Field.Store.YES,
					Field.Index.ANALYZED));
			doc.add(new Field("comType", nullToBlank(mdata.getDataTypeByComType().getDataTypeName()), Field.Store.YES,
					Field.Index.ANALYZED));
			doc.add(new Field("dataTypeKey", nullToBlank(mdata.getDataTypeByDataType().getDataTypeKey()), Field.Store.YES,
					Field.Index.ANALYZED));
			doc.add(new Field("comTypeKey", nullToBlank(mdata.getDataTypeByComType().getDataTypeKey()), Field.Store.YES,
					Field.Index.ANALYZED));
		//	ReflectFillTheBean(mdata,doc);
		//	System.out.println(nullToBlank(mdata.getDataTypeByDataType().getDataTypeKey()));
			i++;
			writer.addDocument(doc);
			if(i%10000==0){
				writer.commit();
				System.out.println("提交"+i);
			}
		}
		writer.optimize(true);
		writer.commit();
		writer.close();
	}

	/**
	 * 删除索引
	 */
	public void deleteIndex(){
		Directory fsDir;
		try {
			fsDir = FSDirectory.open(new File(PATH));
			NRTCachingDirectory cachedFSDir = new NRTCachingDirectory(fsDir, 5.0,
					60.0);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
					new StandardAnalyzer(Version.LUCENE_36));
			conf.setMergeScheduler(cachedFSDir.getMergeScheduler());
			IndexWriter writer = new IndexWriter(cachedFSDir, conf);
			writer.deleteAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	/**
	 * 
	 * @param key     搜索内容所在的字段名称
	 * @param value   所要搜索的内容
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void search(String key, String value)
			throws CorruptIndexException, IOException, ParseException {
		IndexSearcher searcher;
		
		// 创建QueryParser对象,第一个参数表示Lucene的版本,第二个表示搜索Field的字段,第三个表示搜索使用分词器
		//Analyzer analyzer = new IKAnalyzer(); //new StandardAnalyzer(Version.LUCENE_36)
		//QueryParser qp = new QueryParser(Version.LUCENE_36, key,analyzer);
		searcher = new IndexSearcher(IndexReader.open(FSDirectory
				.open(new File(PATH))));
		searcher.setSimilarity(new IKSimilarity());
		Query query = IKQueryParser.parse(key, value);
	//	Query tq = qp.parse(value);
		TopDocs topDocs = searcher.search(query , 10000);
	//	searcher.search(query, results); // new function
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		System.out.println("命中："+topDocs.totalHits);
		Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
		Scorer fragmentScorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
		Fragmenter fragmenter = new SimpleFragmenter(50);
		highlighter.setTextFragmenter(fragmenter);
		Analyzer analyzer = new IKAnalyzer();
		for (int i = 0; i < scoreDocs.length; i++) {
			ScoreDoc scoreDoc = topDocs.scoreDocs[i];
			int docSn = scoreDoc.doc;	//文档内部编号
			Document doc = searcher.doc(docSn);	//根据文档编号取出文档
		
			hightlightFeild(highlighter,analyzer,doc,"name");
			hightlightFeild(highlighter,analyzer,doc,"address");
			hightlightFeild(highlighter,analyzer,doc,"datatype");
			//scoreDocs[i].
			System.out.println("name:"+doc.get("name")); // new function
			System.out.println("address:"+doc.get("address"));
			System.out.println("datatype:"+doc.get("datatype"));
			System.out.println("geom:"+doc.get("geom"));
		}
	}
	
	public static String hightlightFeild(Highlighter highlighter,Analyzer analyzer,Document doc,String feild ){
		String docContent = doc.get(feild);
		try {
		
			String hc = highlighter.getBestFragment(analyzer,feild, docContent);
			if(hc == null) {
				if(docContent.length()>=50) {
					hc = docContent.substring(0, 50);
				} else {
					hc = docContent;
				}
			}
			doc.getField(feild).setValue(hc);
			//System.out.println(hc);
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
	 *  在所有的字段中搜索，如果是坐标的话，需要加上*，才能匹配到，这里采用标准方式获取的集合范围太大了，分词不是很准确
	 * @param queryString  关键字
	 * @throws Exception
	 */
	@Deprecated  
	public static void searchList(String queryString) throws Exception {
		// 查询的字符串:输入不存在的字符串是查询不到的,如：中国
		// 查询字段集合
		String[] queryFileds = {"name","city","address","eastNew","northNew","geom","phone"};
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
	 *  在所有的字段中搜索，如果是坐标的话，需要加上*，才能匹配到
	 * @param queryString  关键字
	 * @throws Exception
	 */
	public static void searchListIK(String queryString) throws Exception {
		// 查询的字符串:输入不存在的字符串是查询不到的,如：中国
		// 查询字段集合
		String[] queryFileds = {"name","city","address","eastNew","northNew","geom","phone"};
		IndexSearcher searcher = LuceneUtils.createIndexSearcher();
		//这里使用的是IK的
		Query query = LuceneUtils.createQueryIK(queryFileds, queryString);
		// 在搜索器中进行查询
		// 对查询内容进行过滤
		Filter filter = null;
		// 一次在索引器查询多少条数据
		int queryCount = 100;

		TopDocs results =null;
	//	= searcher.search(query, filter, queryCount);
	//	System.out.println("总符合: " + results.totalHits + "条数！");
		
		
		ezfilter filter1 = new ezfilter(); 
		filter1.addFilter("datatype", "基站");
		filter1.addFilter("datatype", "网吧");
		
		query = filter1.getFilterQuery(query);
		
	    results = searcher.search(query, filter, queryCount);
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
			System.out.println("datatype = " + doc.get("datatype"));

		}
	}
	
	
	/**
	 *  在所有的字段中搜索，如果是坐标的话，需要加上*，才能匹配到
	 * @param queryString  关键字
	 * @throws Exception
	 */
	public static void searchListIKWithExp(String queryString) throws Exception {
		// 查询的字符串:输入不存在的字符串是查询不到的,如：中国
		// 查询字段集合
		IndexSearcher searcher = LuceneUtils.createIndexSearcher();
		//这里使用的是IK的
		Query query = IKQueryParser.parse(queryString);
		System.out.println(query);
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
			System.out.println("id = " + doc.get("id"));
			System.out.println("datatype = " + doc.get("datatype"));
			System.out.println("dataTypeKey = " + doc.get("dataTypeKey"));

		}
	}
	
	

	/* 
	 * 复合条件查询，即 and or 等 BooleanClause.Occur.MUST表示and
	 * BooleanClause.Occur.MUST_NOT表示not BooleanClause.Occur.SHOULD表示or.
	 */
	public static void searchQuery(String[] queries,String[] fields ) throws Exception {

		IndexSearcher searcher = LuceneUtils.createIndexSearcher();

		//String[] queries = { "南城","网吧"};
		//String[] fields = { "name","city"};
		BooleanClause.Occur[] clauses = { BooleanClause.Occur.SHOULD,BooleanClause.Occur.SHOULD
				};
		Query query = MultiFieldQueryParser.parse(Version.LUCENE_36, queries,
				fields, clauses, new StandardAnalyzer(Version.LUCENE_36));

		TopDocs results = searcher.search(query, null, 100);
		System.out.println("总符合: " + results.totalHits + "条数！");

		// 显示记录
		for (ScoreDoc sr : results.scoreDocs) {
			// 文档编号
			int docID = sr.doc;
			// 真正的内容
			Document doc = searcher.doc(docID);

			System.out.println("name = " + doc.get("name"));
			System.out.println("address = " + doc.get("address"));
			System.out.println("city = " + doc.get("city"));
			System.out.println("lnglat = " + doc.get("lnglat"));

		}
	}

	/**
	 * 获取可以做全文检索的字段
	 * @param mapdata
	 * @return
	 */
	public static String[] getQueryFeild(MapBean mapdata){
		ArrayList<String> feild=new ArrayList<String>();
		Method method[] = mapdata.getClass().getDeclaredMethods();
		for(int i=0;i<method.length;i++){
			if(method[i].getName().startsWith("get")){
				Annotation[] an1 = method[i].getAnnotations();
				for(int j=0;j<an1.length;j++){
					if( an1[j] instanceof javax.persistence.Column){
						javax.persistence.Column an11 =(javax.persistence.Column) an1[j];
						feild.add(an11.name());
					}
				}	
			}			
		}
		return feild.toArray(new String[feild.size()]);
	}
	
	
	
	/**
	 * 处理所有的document
	 * @param scoreDocs  搜索出来的doc数组
	 * @param searcher   搜索器
	 * @return
	 */
	public ArrayList processDocuments(ScoreDoc[] scoreDocs,IndexSearcher searcher ){
		ArrayList al = new ArrayList();
		for(int i=0;i<scoreDocs.length;i++){
			int docid = scoreDocs[i].doc;
			try {
				Document doc = searcher.doc(docid);
				MapBean map = processSingleDocument(doc);
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
	 * @param doc
	 * @return
	 */
	public MapBean processSingleDocument(Document doc){
		MapBean mapdata = new MapBean();
		mapdata.setAddress(doc.get("address"));
		mapdata.setCity(doc.get("city"));
		mapdata.setEastNew(doc.get("eastNew"));
		mapdata.setNorthNew(doc.get("northNew"));
		mapdata.setDatatype(doc.get("datatype"));
		mapdata.setId(Integer.parseInt(doc.get("id")));
		mapdata.setPhone(doc.get("phone"));
		mapdata.setNum(doc.get("num"));	
		return mapdata;
	}
	
	/**
	 * 根据MapData自动构建索引
	 * @param mapdata
	 */
	public static void ReflectFillTheBean(MapBean mapdata, Document doc){
		Method method[] = mapdata.getClass().getDeclaredMethods();
		for(int i=0;i<method.length;i++){
			if(method[i].getName().startsWith("get")){
				//System.out.println(method[i].getName());
				Annotation[] an1 = method[i].getAnnotations();
				for(int j=0;j<an1.length;j++){
					if( an1[j] instanceof javax.persistence.Column){
						javax.persistence.Column an11 =(javax.persistence.Column) an1[j];
						try {
							if(method[i].invoke(mapdata)!=null)
							doc.add(new Field(an11.name(),method[i].invoke(mapdata).toString(),Field.Store.YES,Field.Index.ANALYZED));
						//	System.out.println(an11.name()+">>>>>>>"+method[i].invoke(mapdata));
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}	
			}	
		}
	}
	
	/**
	 * 周期性的创建索引
	 */
	public static void createIndexAsTask(){
		TaskEngine.getInstance().cancelScheduledTask(task);
		TaskEngine.getInstance().scheduleAtFixedRate(task, new Date(), 24*60*60*1000);
	}
	
	/**
	 * null转换为空
	 * 
	 * @param s
	 * @return
	 */
	public static String nullToBlank(Object s) {
		if (s == null) {
			return "";
		} else if ("undefined".equals(s.toString())) {
			return "";
		}
		return s.toString();
	}
	
	
	
	
	
	public static IndexWriter getIndexWriter(){
		Directory fsDir;
		IndexWriter writer=null;
		try {
			fsDir = FSDirectory.open(new File(PATH));
			System.out.println("在目录:"+PATH+" 开始创建索引");
			NRTCachingDirectory cachedFSDir = new NRTCachingDirectory(fsDir, 5.0,
					60.0);
			Analyzer analyzer = new IKAnalyzer();
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,analyzer);
			conf.setMergeScheduler(cachedFSDir.getMergeScheduler());
			writer = new IndexWriter(cachedFSDir, conf);
			return writer;
			
		}catch(Exception e){
			
		}
		return writer;
		
	}
	
	
	public static  boolean  deleteIndex(MapBean map){
		boolean success =false;
		IndexWriter indexWriter = getIndexWriter();
		Term t = new Term("id",""+map.getId());
		System.out.println("删除索引"+map.getId());
		try {
			indexWriter.deleteDocuments(t);
			indexWriter.optimize();
			indexWriter.commit();
			indexWriter.close();
			success= true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}return success;
	}
	
	public static boolean addOrUpdateIndex(MapBean mdata){
		boolean success =false;
		try {
			IndexWriter writer = getIndexWriter();
		//	deleteIndex(mdata);
			Document doc = new Document();
			doc = new Document();
			doc.add(new Field("id", "" + mdata.getId(), Field.Store.YES,Field.Index.NOT_ANALYZED));
					doc.add(new Field("name", nullToBlank(mdata.getName()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("address", nullToBlank(mdata.getAddress()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("city", nullToBlank(mdata.getCity()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("num", nullToBlank(mdata.getNum()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("eastNew", nullToBlank(mdata.getEastNew()), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("northNew", nullToBlank(mdata.getNorthNew()), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("datatype", nullToBlank(mdata.getDatatype()), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("phone", nullToBlank(mdata.getPhone()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("geom", nullToBlank(mdata.getEastNew()) + ","
							+ nullToBlank(mdata.getNorthNew()), Field.Store.YES,
							Field.Index.ANALYZED));
			writer.updateDocument(new Term("id",""+mdata.getId()),doc);
			writer.optimize();
			writer.commit();
			writer.close();
			success= true;
			System.out.println("索引建立完毕！！！");
		}catch(Exception e){
			e.printStackTrace();
		}
		return success;
		
	}
	
	
	public static void updateIndex(MapBean map){
		//先删除索引
		deleteIndex(map);
		//再添加
		addOrUpdateIndex(map);
		
	}
	
	
	
	public static void main(String[] args) throws Exception {
		//IndexOperator.createIndex();
		//IndexOperator.search("num","46001A");
		//IndexOperator.searchList("address:泰力网吧");
		//IndexOperator.searchListIK("东莞");
		IndexOperator.searchListIKWithExp("((name:'体育'||township:'体育'||address:'体育'||eastNew:'体育'||northNew:'体育'||geom:'体育'||phone:'体育'||num:'体育')&&(dataTypeKey:'WANBA'||dataTypeKey:'ADSL')&&(comTypeKey:'CMCC'))");
		
		
//		MapData mdata = new MapData();
//		mdata.setId(909090);
//		mdata.setAddress("东莞达到1121东莞系");
//		IndexOperator.addOrUpdateIndex(mdata);
//		IndexOperator.addOrUpdateIndex(mdata);
//		IndexOperator.searchListIKWithExp("id:'909090'");
//		IndexOperator.deleteIndex(mdata);
//		IndexOperator.searchListIKWithExp("id:'909090'");
//		IndexOperator.searchListIKWithExp("address:'东莞达到1121东莞系'");
//		IndexOperator.searchListIK("东莞达到1121东莞系");
		//IndexOperator.create();
		//IndexOperator.searchList("0769*");
		
//		String ikQueryExp = "(id='ABcdRf'  && keyword:'^魔兽中国$')   -name:'张涵 '";
//		Query result = IKQueryParser.parse(ikQueryExp);
//		Query result = IKQueryParser.parse("(newsKeyword='---' || newsTitle:'---' || newsContent:'---') && newsClass='1'");
//		System.out.println(result);

	}
}