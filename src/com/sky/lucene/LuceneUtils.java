package com.sky.lucene;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKQueryParser;
public class LuceneUtils {

    //当前目录位置  
    private static final String USERDIR = System.getProperty("user.dir");  
    //存放索引的目录  
    public static final String INDEXPATH = LuceneUtils.class.getResource("/").getPath();  
    
    static String PATH = new File(INDEXPATH).getParentFile().getParent()+"//index";
   
    //使用版本  
    public static final Version version = Version.LUCENE_36;  
      
    /** 
     * 获取分词器 ，创建标准的分词器
     * */  
    public static Analyzer getAnalyzer(){  
        // 分词器  
        Analyzer analyzer = new StandardAnalyzer(version);  
        return analyzer;  
    }  
    
    

  
    /** 
     * 创建一个索引器的操作类 
     *  
     * @param openMode 
     * @return 
     * @throws Exception 
     */  
    public static IndexWriter createIndexWriter(OpenMode openMode)  
            throws Exception {  
        // 索引存放位置设置  
        Directory dir = FSDirectory.open(new File(PATH));        
        // 索引配置类设置  
        IndexWriterConfig iwc = new IndexWriterConfig(version,  
                getAnalyzer());  
        iwc.setOpenMode(openMode);  
        IndexWriter writer = new IndexWriter(dir, iwc);  
        return writer;  
    }  
  
    /*** 
     * 创建一个搜索的索引器 
     * @throws IOException  
     * @throws CorruptIndexException  
     * */  
    public static IndexSearcher createIndexSearcher() throws CorruptIndexException, IOException {  
        IndexReader reader = IndexReader.open(FSDirectory.open(new File(PATH)));  
        IndexSearcher searcher = new IndexSearcher(reader);  
        return searcher;  
    }  
  
    /** 
     * 创建一个查询器 
     * @param queryFileds  在哪些字段上进行查询 
     * @param queryString  查询内容 
     * @return 
     * @throws ParseException 
     */  
    public static Query createQuery(String [] queryFileds,String queryString) throws ParseException{  
    	
    	PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(getAnalyzer());
    	//analyzer.addAnalyzer("eastNew", new KeywordAnalyzer());
    	//analyzer.addAnalyzer("northNew", new KeywordAnalyzer());
    	
         QueryParser parser = new MultiFieldQueryParser(version, queryFileds, getAnalyzer());  
        // parser.set
         Query query = parser.parse(queryString);  
         return query;  
    }  
      
    
    
    
    
    
    /** 
     * 创建一个查询器 
     * @param queryFileds  在哪些字段上进行查询 
     * @param queryString  查询内容 
     * @return 
     * @throws ParseException 
     */  
    public static Query createQueryIK(String [] queryFileds,String queryString){  
    	
    	//PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(getAnalyzer());
    	//analyzer.addAnalyzer("eastNew", new KeywordAnalyzer());
    	//analyzer.addAnalyzer("northNew", new KeywordAnalyzer());
    	
       //  QueryParser parser = new MultiFieldQueryParser(version, queryFileds, getAnalyzer());  
        // parser.set
        Query query=null;
		try {
			query = IKQueryParser.parseMultiField(queryFileds,queryString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
         return query;  
    }  
      

}