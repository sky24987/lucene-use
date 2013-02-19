//package com.sky.hib.util;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//import java.util.UUID;
//
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopDocs;
//import org.apache.lucene.search.TopScoreDocCollector;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.store.RAMDirectory;
//import org.apache.lucene.store.SimpleFSDirectory;
//import org.dom4j.DocumentException;
//import org.wltea.analyzer.lucene.IKAnalyzer;
//import org.wltea.analyzer.lucene.IKQueryParser;
//import org.wltea.analyzer.lucene.IKSimilarity;
//
//import com.hd.annotation.lucene.IndexField;
//import com.hd.assist.entity.Field4Lucene;
//import com.hd.assist.entity.Lucene;
//import com.hd.comm.constants.FieldType;
//import com.hd.comm.constants.XmlAssistType;
//
//
///**
// * 索引工具类
// * @author YUNFENGCHENG
// *
// */
//public class LuceneUtils {
//	private LuceneUtils(){};
//	/**
//	 * indexWrite静态集合
//	 */
//	public static Map<String, IndexWriter> indexWriters = new HashMap<String, IndexWriter>();
//	/**
//	 * 辅助类结合
//	 */
//	public static Map<String, List<Field4Lucene>> strFields = new HashMap<String, List<Field4Lucene>>();
//	/**
//	 * 缓存查询结果集合
//	 */
//	public static Map<String,Map<String,ScoreDoc[]>> scores = new TreeMap<String, Map<String,ScoreDoc[]>>();
//	/**
//	 * 中文分词器
//	 */
//	public static Analyzer analyzer = new IKAnalyzer();
//	/**
//	 * indexSearcher静态集合
//	 */
//	public static Map<String,IndexSearcher> indexSearchers = new HashMap<String, IndexSearcher>();
//	/**
//	 * 查询索引
//	 * @param oc
//	 * @param keyWord
//	 */
//	public static Map<Integer,List<Object>> queryAllByKeyWord(Class<?> oc,String keyWord,int start,int limit){
//		long star = 0L;
//		long end = 0L;
//		try {
//			star = System.currentTimeMillis();
//			IndexSearcher indexSearcher = null;
//			if(indexSearchers.get(oc.getName()) != null){
//				indexSearcher = indexSearchers.get(oc.getName());
//			}else{
//				String indexDir = SystemAssist.getSysRootPath()+XmlAssistType.INDEXDIR.showInfo+
//				oc.getPackage().getName()+"/"+oc.getSimpleName()+"/";
//				File file = new File(indexDir); // 指定索引文件夹
//				boolean isExist = false; // 文件是否存在
//				isExist = file.exists(); // 文件夹是否存在
//				if (!isExist) {
//					return new HashMap<Integer, List<Object>>();
//				}
//				Directory directory = FSDirectory.open(file);
//				//加入缓存
//				directory = new RAMDirectory(directory);
//				IndexReader reader = IndexReader.open(directory, true);
//				indexSearcher = new IndexSearcher(reader);
//				//使用相似评估器
//				indexSearcher.setSimilarity(new IKSimilarity());
//				indexSearchers.put(oc.getName(), indexSearcher);
//			}
//			List<Field4Lucene> fields = new ArrayList<Field4Lucene>();
//			if(strFields.get(oc.getName()) != null){
//				fields = strFields.get(oc.getName());
//			}else{
//				getFields(oc, null);
//				fields = strFields.get(oc.getName());
//			}
//			List<String> fieldsStr = new ArrayList<String>();
//			for(Field4Lucene lucene : fields){
//				fieldsStr.add(lucene.getfName());
//			}
//			String[] fs = new String[fieldsStr.size()];
//			for(int i = 0 ;i<fieldsStr.size() ;i++){
//				fs[i] =  fieldsStr.get(i);
//			}
//			Query query = IKQueryParser.parseMultiField(fs, keyWord);
//			// 分页设置
//			TopScoreDocCollector results = TopScoreDocCollector.create(start+limit, false);
//			indexSearcher.search(query, results);
//			TopDocs topDocs = results.topDocs(start, limit);
//			//得到全部记录条数
//			int totalCount = topDocs.totalHits;
//			// 查询结果
//			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//			//准备数据
//			Map<Integer,List<Object>>  data = new TreeMap<Integer, List<Object>>();
//			List<Object> objects = new ArrayList<Object>();
//			for(ScoreDoc doc : scoreDocs){
//				Document targetDoc = indexSearcher.doc(doc.doc);
//				objects.add(DocumentUtils.docConvert(analyzer,fs,targetDoc,oc));
//			}
//			data.put(totalCount, objects);
//			end = System.currentTimeMillis();
//			System.out.println("*********************索引查询耗时："+(end-star)+"毫秒**********************");
//			return data;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		} 
//	}
//	/**
//	 * 创建或追加索引
//	 * @param directory
//	 * @param field
//	 */
//	public static void createOrupdateIndex(Class<?> oc , List<Field> fields){
//		try {
//			String indexDir = SystemAssist.getSysRootPath()+XmlAssistType.INDEXDIR.showInfo+
//			oc.getPackage().getName()+"/"+oc.getSimpleName()+"/";			
//			IndexWriter indexWriter = getIndexWriter(indexDir);
//			org.apache.lucene.document.Document document = null;
//			document = new org.apache.lucene.document.Document();
//			for(Field field : fields){
//				document.add(field);
//			}
//			try {
//				indexWriter.addDocument(document);	
//			} catch (Exception e) {
//			}
//			indexWriter.commit();
//			//这是个隐患以后注意
//			if(indexWriters.get(indexDir)==null){
//				indexWriter.close();
//			}
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 删除索引
//	 * @param directory
//	 * @param terms
//	 * @return
//	 */
//	public static int deleteIndex(String indexDir , List<Term> terms){
//		try {
//			IndexWriter indexWriter = getIndexWriter(indexDir);
//			for(Term term : terms){
//				try{
//					indexWriter.deleteDocuments(term);
//				}catch(Exception e){
//					System.out.println(e);
//				}
//			}
//			indexWriter.commit();
//	        //indexWriter.close();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
//		return terms.size();
//	}
//	public static List<Field> getFields(Class<?> c ,Object object) throws Exception{
//		//索引项集合
//		List<Field> fields = new ArrayList<Field>();
//		//反射项集合
//		java.lang.reflect.Field[] refFields = ReflectionUtils.getInstance().getFields(c);
//		//辅助类集合
//		List<Field4Lucene> field4Lucenes = new ArrayList<Field4Lucene>();
//		for(java.lang.reflect.Field field : refFields){
//			Field4Lucene field4Lucene = null;
//			String fName = field.getName();
//			//String methodName = "get"+(fName.substring(0,1)).toUpperCase()+fName.substring(1, fName.length());
//			String methodName = MethodNameUtils.getReadMethod(fName);
//			Annotation[] anns = field.getAnnotations(); 
//			for(Annotation ann : anns){
//				String annName = ann.annotationType().getSimpleName();
//				if("NoIndex".equals(annName)){
//					continue;
//				}else{//返回TRUE表明他是要被创建索引的
//					if("IndexField".equals(annName)){
//						field4Lucene = new Field4Lucene();
//						field4Lucene.setfName(fName); //设这字典名称
//						IndexField annIndexField = (IndexField)ann;//强制转型
//						if(annIndexField.fieldType() != FieldType.ARRAY &&
//								annIndexField.fieldType() != FieldType.ENTITY){
//							if(object != null){
//								Object value = ReflectionUtils.getInstance().invokeMethod(object, methodName, null);
//								if(annIndexField.fieldType() == FieldType.DATE){
//									value = FormatDate4Json.getStringByYYYYMMDD4EnglishDate(value.toString());
//								}
//								field4Lucene.setfValue(""+value);
//							}else{
//								field4Lucene.setfValue("系统自动但无数值");
//							}
//						}else{
//							field4Lucene.setfValue("");
//						}
//						field4Lucene.setIndex(annIndexField.index());//设置索引模式
//						field4Lucene.setStore(annIndexField.store());//设这存储模式
//						field4Lucene.setTermVector(annIndexField.termVector());
//					}else{
//						continue;
//					}
//				}
//			}
//			if(field4Lucene!=null){
//				field4Lucenes.add(field4Lucene);
//			}
//		}
//		Field field = null;
//		for(Field4Lucene f : field4Lucenes){
//			field = new Field(f.getfName(), f.getfValue(), f.getStore(), f.getIndex(),f.getTermVector());
//			fields.add(field);
//		}
//		//我们自己为索引增加的一个字段
//		Field fieldIndexId = new Field("INDEXID",UUID.randomUUID()+"",Field.Store.YES, Field.Index.NO);
//		fields.add(fieldIndexId);
//		//放到缓存中
//		strFields.put(c.getName(), field4Lucenes);
//		//删除读取索引集合
//		if(indexSearchers.get(c.getName()) != null){
//			indexSearchers.remove(c.getName());
//		}
//		return fields;
//	}
//	private static IndexWriter getIndexWriter(String indexDir) throws DocumentException, IOException{
//		IndexWriter indexWriter = null;
//		if(indexWriters.get(indexDir) != null){
//			indexWriter = indexWriters.get(indexDir);
//			return indexWriter;
//		}else{
//			File file = new File(indexDir); // 指定索引文件夹
//			boolean isExist = false; // 文件是否存在
//			isExist = file.exists(); // 文件夹是否存在
//			if (!isExist) {
//				file.mkdir();
//			}
//			Directory directory = new SimpleFSDirectory(file);
//			//把索引加载到内存当中,如果系统很慢建议使用
//			//directory = new RAMDirectory(directory);
//			//系统初始化就会实例化这个类
//			Lucene lucene = Lucene.getInstance();
//			indexWriter = new IndexWriter(directory,analyzer,(!isExist),IndexWriter.MaxFieldLength.UNLIMITED);
//			indexWriter.setMergeFactor(lucene.getMergeFactor());
//			indexWriter.setMaxMergeDocs(lucene.getMaxMergeDocs()); 
//			indexWriter.setMaxBufferedDocs(lucene.getMaxBufferedDocs());
//			indexWriter.setUseCompoundFile(lucene.isUseCompoundFile());
//			//优化索引
//			indexWriter.optimize();
//			if(isExist){//已经存在索引路径则不把新增读写器放到缓存中
//				indexWriters.put(indexDir, indexWriter);
//			}
//			return indexWriter;
//		}
//	}
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

