package com.sky.lucene;
import java.util.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

public class ezfilter{   
        private List<Filter> filterList;
        public ezfilter(){
            filterList = new ArrayList<Filter>();
        }
        public void addFilter(String Field,String Value){
            Term term=new Term(Field,Value);//添加term
            QueryWrapperFilter filter=new QueryWrapperFilter(new TermQuery(term));//添加过滤器
            filterList.add(filter);//加入List，可以增加多个过滤
        }
        public Query getFilterQuery(Query query){
            for(int i=0;i<filterList.size();i++){
                //取出多个过滤器，在结果中再次定位结果
                query = new FilteredQuery(query, filterList.get(i));
            }
            return query;
        }   
}