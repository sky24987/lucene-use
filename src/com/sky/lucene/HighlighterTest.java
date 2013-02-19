//package com.sky.lucene;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.queryParser.MultiFieldQueryParser;
//import org.apache.lucene.queryParser.QueryParser;
//import org.apache.lucene.search.Filter;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopDocs;
//import org.apache.lucene.search.highlight.Formatter;
//import org.apache.lucene.search.highlight.Fragmenter;
//import org.apache.lucene.search.highlight.Highlighter;
//import org.apache.lucene.search.highlight.QueryScorer;
//import org.apache.lucene.search.highlight.Scorer;
//import org.apache.lucene.search.highlight.SimpleFragmenter;
//import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.util.Version;
//import org.junit.Test;
//
//import com.bin.lucene.utils.File2DocUtils;
//
///**
// * 测试高亮器
// * @author Administrator
// *
// */
//public class HighlighterTest {
//
//	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
//	String filePath = "D:\\workspace\\myeclipse\\LuceneDemo\\luceneDatassource\\CHANGES.txt";
//	String indexPath = "D:\\workspace\\myeclipse\\LuceneDemo\\luceneIndex";
//	
//	@Test
//	public void test1() throws Exception {
//		
//		//先查询
//		String queryStr = "Lucene";
//		QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35, new String[]{"name", "content"}, analyzer);
//		Query query = parser.parse(queryStr);
//		Filter filter = null;
//		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(indexPath)));
//		IndexSearcher searcher = new IndexSearcher(indexReader);
//		TopDocs topDocs = searcher.search(query, filter, 10000);
//		System.out.println("总共有【" + topDocs.totalHits + "】条匹配结果");
//		
//		List<Document> recordList = new ArrayList<Document>();
//		Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
//		Scorer fragmentScorer = new QueryScorer(query);
//		Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
//		Fragmenter fragmenter = new SimpleFragmenter(50);
//		highlighter.setTextFragmenter(fragmenter);
//		
//		
//		//取出当前页的数据
//		int pageSize = 20;
//		int pageNo = 1;
//		int firstResult = (pageNo-1) * pageSize;
//		int end = Math.min(pageNo * pageSize, topDocs.totalHits);
//		System.out.println(firstResult + ":" + end);
//		for(int i=firstResult; i<end; i++) {
//			ScoreDoc scoreDoc = topDocs.scoreDocs[i];
//			int docSn = scoreDoc.doc;	//文档内部编号
//			Document doc = searcher.doc(docSn);	//根据文档编号取出文档
//			
//			//高亮===================
//			String docContent = doc.get("content");
//			String hc = highlighter.getBestFragment(analyzer, "content", docContent);
//			if(hc == null) {
//				if(docContent.length()>=50) {
//					hc = docContent.substring(0, 50);
//				} else {
//					hc = docContent;
//				}
//			}
//			doc.getField("content").setValue(hc);
//			//高亮===================
//			
//			recordList.add(doc);
//		}
//
//		//显示
//		for(Document doc : recordList) {
//			File2DocUtils.printDocumentInfo(doc);
//		}
//	}
//	
//}