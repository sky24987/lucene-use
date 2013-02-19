package com.sky.lucene;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.apache.lucene.util.Version;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.sky.hib.util.HibernateUtil;
import com.sky.lucene.util.TaskEngine;
import com.sky.moudle.MapBean;
/**
 * 主要进行索引的创建，以及删除等操作，索引操作最好将虚拟机的内存增加到512m以上
 * 
 * @author zhanghan
 *
 */


public class LuceneIndexOperator {

	final static String PATH = LuceneUtils.PATH;
	static SimpleDateFormat longsmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static String firstruntime = "23:59:59";
	static long period = 24*60*60*1000;
	static long gcperiod = 60*1000*10;
	static TimerTask task = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			LuceneIndexOperator.createIndex();
		}

	};
	
	static TimerTask gctask = new TimerTask(){
		@Override
		public void run(){
			System.gc();
		}
	};


	
    /**
     * map_data的数据源
     * @return
     */
	public static List createDataSource() {
		List list = new ArrayList();
		Query q = HibernateUtil.currentSession().createQuery("from MapBean");
		list = q.list();
		return list;
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
	
	/**
	 * 创建索引，IKAnalyzer分词，更加细微的分词，需要对算法进行扩展，使用jdbc创建索引，可以有效降低内存占用率
	 */
	public static void createIndex(){
		long begintime = System.currentTimeMillis();
		createIndexByJdbc();
		long endtime = System.currentTimeMillis();
		System.out.println("建立索引用时："+(endtime - begintime)+" ms");
	}
	
	/**
	 * 采用jdbc的方式
	 */
	private static void createIndexByJdbc(){
		Directory fsDir;
		try {
			fsDir = FSDirectory.open(new File(PATH));
			NRTCachingDirectory cachedFSDir = new NRTCachingDirectory(fsDir, 5.0,
					60.0);
			Analyzer analyzer = new IKAnalyzer();
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
					analyzer);
			conf.setMergeScheduler(cachedFSDir.getMergeScheduler());
			IndexWriter writer = new IndexWriter(cachedFSDir, conf);
			writer.deleteAll();
			// 索引的数据源
//			List<MapBean> list = createDataSource();
			Document doc =null;
			//MapBean mdata  =null;

			Session session = HibernateUtil.currentSession();
			Connection connection =  session.connection();
			String sql = "select * from v_map_data";
			Statement st  = null;
			ResultSet rs =null;
			writer.setMaxBufferedDocs(10000);  //用时间换取空间
			try {
				st =connection.createStatement();
				rs = st.executeQuery(sql);
				int i=0;
				while(rs.next()){
					doc = new Document();
					doc.add(new Field("id", "" + rs.getString("id"), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("name", nullToBlank(rs.getString("name")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("township", nullToBlank(rs.getString("township")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("address", (rs.getString("address")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("city", nullToBlank(rs.getString("city")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("num", nullToBlank(rs.getString("num")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("num2", nullToBlank(rs.getString("num")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("eastNew", nullToBlank(rs.getString("eastNew")), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("northNew", nullToBlank(rs.getString("northNew")), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("phone", nullToBlank(rs.getString("phone")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("geom", nullToBlank(rs.getString("eastNew")) + ","
							+ nullToBlank(rs.getString("northNew")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("dataType", nullToBlank(rs.getString("data_type_name")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("comType", nullToBlank(rs.getString("com_type_name")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("dataTypeKey", nullToBlank(rs.getString("data_type_key")), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("comTypeKey", nullToBlank(rs.getString("com_type_key")), Field.Store.YES,
							Field.Index.ANALYZED));
					writer.addDocument(doc);
					i++;
					if(i%10000==0){
						writer.commit();
						System.out.println("提交："+i);
					}
					doc=null;
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(session!=null){
					session.flush();
					session.close();
				}if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(st!=null){
					try {
						st.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			System.out.println("即将优化索引！！！");
			writer.optimize();
			System.out.println("提交索引数据！！！");
			writer.commit();
			writer.close();
			fsDir.close();
			System.out.println("索引建立完毕！！！");
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			System.gc();
		}
		
	}
	
	/**
	 * 创建索引，采用那个IKAnalyzer，使用hibernate创建索引
	 */
	private static void createIndexByHibernate(){
		Directory fsDir;
		try {
			fsDir = FSDirectory.open(new File(PATH));
			System.out.println("在目录:"+PATH+" 开始创建索引");
			NRTCachingDirectory cachedFSDir = new NRTCachingDirectory(fsDir, 5.0,
					60.0);
			Analyzer analyzer = new IKAnalyzer();
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
					analyzer);
			conf.setMergeScheduler(cachedFSDir.getMergeScheduler());
			IndexWriter writer = new IndexWriter(cachedFSDir, conf);
			writer.deleteAll();
			// 索引的数据源
			List<MapBean> list = createDataSource();
			Document doc =null;
			MapBean mdata  =null;
			writer.setMaxBufferedDocs(10000);
			for (int i=0;i<list.size();i++) {
				// mdata.getClass().getDeclaredMethods();
				if(list.get(i)!=null){
					mdata  = (MapBean)list.get(i);
					doc = new Document();
					doc.add(new Field("id", "" + mdata.getId(), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("name", nullToBlank(mdata.getName()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("address", (mdata.getAddress()), Field.Store.YES,
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
					writer.addDocument(doc);
					
				}
			}
			System.out.println("准备提交数据！！！");
			writer.commit();
			writer.close();
			System.out.println("索引建立完毕！！！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	

	/**
	 * 删除索引
	 */
	public void deleteIndex() {
		Directory fsDir;
		try {
			fsDir = FSDirectory.open(new File(PATH));
			NRTCachingDirectory cachedFSDir = new NRTCachingDirectory(fsDir,
					5.0, 60.0);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
					new StandardAnalyzer(Version.LUCENE_36));
			conf.setMergeScheduler(cachedFSDir.getMergeScheduler());
			IndexWriter writer = new IndexWriter(cachedFSDir, conf);
			writer.deleteAll();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			
			
		}

	}

	/**
	 * 根据MapData自动构建索引
	 * 
	 * @param mapdata
	 */
	public static void ReflectFillTheBean(MapBean mapdata, Document doc) {
		Method method[] = mapdata.getClass().getDeclaredMethods();
		for (int i = 0; i < method.length; i++) {
			if (method[i].getName().startsWith("get")) {
				// System.out.println(method[i].getName());
				Annotation[] an1 = method[i].getAnnotations();
				for (int j = 0; j < an1.length; j++) {
					if (an1[j] instanceof javax.persistence.Column) {
						javax.persistence.Column an11 = (javax.persistence.Column) an1[j];
						try {
							if (method[i].invoke(mapdata) != null)
								doc.add(new Field(an11.name(), method[i]
										.invoke(mapdata).toString(),
										Field.Store.YES, Field.Index.ANALYZED));
							// System.out.println(an11.name()+">>>>>>>"+method[i].invoke(mapdata));
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
	
	
	public static void closeLucene(Directory directory,IndexWriter write,IndexSearcher isearcher){
		if(isearcher != null){
			try {
				isearcher.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(write!=null){
			try {
				write.close();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(directory != null){
			try {
				directory.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 周期性的创建索引，目前先设置为跟随系统启动创建索引，并延迟5秒，执行周期为24小时
	 */
	public static void createIndexAsTask() {
		System.out.println("系统即将建立初始化索引任务");
		//开机延迟5秒建立索引，之后在每天的半夜执行索引
		TaskEngine.getInstance().schedule(task, 5000);
		System.out.println("计划任务：建立索引，将在"+Time2LongDate(firstruntime)+"周期执行" );
	    TaskEngine.getInstance().cancelScheduledTask(task);
		// task.run();
		
		TaskEngine.getInstance().schedule(task, Time2LongDate(firstruntime), period);
		
		//系统定时清理内存
		System.out.println("计划任务：定时回收垃圾");
		TaskEngine.getInstance().schedule(gctask,5000, gcperiod);
		
	}
	
	public static Date Time2LongDate(String dt){
		 try {
			 Calendar dte = Calendar.getInstance();
			 String tr=(dte.get(Calendar.YEAR))+"-"+(dte.get(Calendar.MONTH)+1)+"-"+dte.get(Calendar.DAY_OF_MONTH)+" "+dt;
			return longsmt.parse(tr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
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
	
	/**
	 * 根据id删除索引
	 * @param map
	 * @return
	 */
	public static  boolean  deleteIndex(MapBean map){
		boolean success =false;
		IndexWriter indexWriter = getIndexWriter();
		Term t = new Term("id",""+map.getId());
		System.out.println("删除索引"+map.getId());
		try {
			indexWriter.deleteDocuments(t);
			indexWriter.commit();
			indexWriter.close();
			success= true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}return success;
	}
	
	/**
	 * 新建一条索引
	 * @param map
	 */
	public static boolean addOrUpdateIndex(MapBean map){
		boolean success=false;
		try {
			IndexWriter writer = getIndexWriter();

			MapBean mdata  =map;
			Document doc = new Document();
			doc.add(new Field("id", "" + mdata.getId(), Field.Store.YES,Field.Index.NOT_ANALYZED));
					doc.add(new Field("name", nullToBlank(mdata.getName()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("address", nullToBlank(mdata.getAddress()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("city", nullToBlank(mdata.getCity()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("num", nullToBlank(mdata.getNum()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("num2", nullToBlank(mdata.getNum()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("eastNew", nullToBlank(mdata.getEastNew()), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("northNew", nullToBlank(mdata.getNorthNew()), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("datatype", nullToBlank(mdata.getDatatype()), Field.Store.YES,
							Field.Index.NOT_ANALYZED));
					doc.add(new Field("phone", nullToBlank(mdata.getPhone()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("dataType", nullToBlank(mdata.getDataTypeByDataType().getDataTypeName()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("comType", nullToBlank(mdata.getDataTypeByComType().getDataTypeName()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("dataTypeKey", nullToBlank(mdata.getDataTypeByDataType().getDataTypeKey()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("comTypeKey", nullToBlank(mdata.getDataTypeByComType().getDataTypeKey()), Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("geom", nullToBlank(mdata.getEastNew()) + ","
							+ nullToBlank(mdata.getNorthNew()), Field.Store.YES,
							Field.Index.ANALYZED));
			//writer.addDocument(doc);
			writer.updateDocument(new Term("id",""+mdata.getId()),doc);
			System.out.println("准备提交数据！！！");
			writer.commit();
			writer.close();
			System.out.println("索引建立完毕！！！");
			success= true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return success;
		
	}
	
	/**
	 * 更新索引,先删除在添加
	 * @param map
	 */
	public static boolean  updateIndex(MapBean map){
		//先删除索引
		boolean sucess = deleteIndex(map);
		//再添加
		boolean addsucess = addOrUpdateIndex(map);
		
		return sucess&&addsucess;
	}
	
	
	

	/**
	 * 
	 * 取消索引任务
	 */
	public static void cancelIndexTask() {
		System.out.println("系统信息：取消索引任务");
		TaskEngine.getInstance().cancelScheduledTask(task);
	}
	
	


}