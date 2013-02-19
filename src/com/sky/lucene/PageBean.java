package com.sky.lucene;

import java.util.ArrayList;
import java.util.List;

public class PageBean {
	  private int count = 0; // 记录总数

	    private int pageSize = 15; // 每页显示记录数

	    private int pageCount = 0; // 总页数

	    private int page = 1; // 当前页数
	    
	    private int jumpPage=0;
	    
	    private String orderBy;// 排序条件
	    
	    private String orderType="desc";//排序是升序还是降序
	    
	    private String oldType;
	    
	    private String opt="1";//1是点出分页菜单,2是列头
	        
	    private String formName="splitPageForm";//formNname
	    
	    @SuppressWarnings("unchecked")
		private List pageItems;//
	    
	    private BaseSearch baseSearch;
	    
		@SuppressWarnings("unchecked")
		public PageBean()
	    {
	    	super();
	    	this.pageItems=new ArrayList();
	    	
	    }
		@SuppressWarnings("unchecked")
		public PageBean(BaseSearch baseSearch)
		{
			super();
			this.pageItems=new ArrayList();
			this.baseSearch=baseSearch;
		}
	    
	    public BaseSearch getBaseSearch() {
			return baseSearch;
		}
	    
		public void setBaseSearch(BaseSearch baseSearch) {
			this.baseSearch = baseSearch;
		}


	    
	    public String getFormName() {
			return formName;
		}

		public void setFormName(String formName) {
			this.formName = formName;
		}

		public int getCount() {
	        return count;
	    }

	    public void setCount(int count) {
	        if (pageSize != 0) {
	            pageCount = count / pageSize;
	            if (count % pageSize != 0) {
	                pageCount++;
	            }
	        }
	        this.count = count;
	    }


	    public int getPage() {
	        return page;
	    }

	    public void setPage(int page) {
	        this.page = page;
	    }

	    public int getPageCount() {
	        return pageCount;
	    }

	    public void setPageCount(int pageCount) {
	        this.pageCount = pageCount;
	    }

	    public int getPageSize() {
	        return pageSize;
	    }

	    public void setPageSize(int pageSize) {
	        this.pageSize = pageSize;
	    }


	    /**
	     * 取得分页的当前控制页的HTML
	     * @return
	     */
		@SuppressWarnings("unused")
		public String getPageControllerHtmlString()
		{
			  StringBuffer str = new StringBuffer("");   
			     int next, prev;   
			     prev = page - 1;   
			     next = page + 1;   
			      
			     if (page > 1) {   
			      str   
			        .append("<a href=\"javascript:document." + formName + ".jumpPage.value=1;document." + formName + ".opt.value=1;document." + formName + ".submit();\">首页</a> ");   
			     } else {   
			         //str.append("<a href=\"#\">首页</a> ");
			         str.append("首页 ");
			     }   
			     if (page > 1) {   
			      str.append("<a href='javascript:document." + formName + ".jumpPage.value="   
			        + prev + ";document." + formName + ".opt.value=1;document." + formName + ".submit();'>上页</a> ");   
			     } else {   
			         //str.append("<a href=\"#\">上页</a> ");   
			         str.append("上页 ");   
			     }   
			     if (page < pageCount) {   
			      str.append("<a href='javascript:document." + formName + ".jumpPage.value="   
			        + next + ";document." + formName + ".opt.value=1;document." + formName + ".submit();'>下页</a> ");   
			     } else {   
			       //str.append("<a href=\"#\" >下页</a> ");   
			         str.append("下页 ");
			     }   
			     if (pageCount > 1 && page != pageCount) {   
			      str.append("<a href='javascript:document." + formName + ".jumpPage.value="   
			        + pageCount   
			        + ";document." + formName + ".opt.value=1;document." + formName + ".submit();'>末页</a>  ");   
			     } else {   
			      //str.append("<a href=\"#\" >末页</a>  ");
			        str.append("末页  ");
			     }   
			     str.append(" 共" + count + "条记录");   
			     str   
			       .append("  每页");      
			     str.append(pageSize);   
			     str.append("条 分" + pageCount + "页显示 转到");   
//			     str.append("<SELECT size=1 name=Pagelist onchange='this.form.jumpPage.value=this.value;document." + formName + ".opt.value=1;this.form.submit();'>");
			     str.append("<SELECT size=1 name=Pagelist onchange='this.form.jumpPage.value=this.value;this.form.submit();'>");
			     for (int i = 1; i < pageCount + 1; i++) {   
			      if (i == page) {   
			       str.append("<OPTION value=" + i + " selected>" + i   
			         + "</OPTION>");   
			      } else {   
			       str.append("<OPTION value=" + i + ">" + i + "</OPTION>");   
			      }   
			     }   
			     str.append("</SELECT>页");   
			     str.append("<INPUT type=hidden  value=" + page   
			       + " name=\"pages\" > "); 
			     str.append("<INPUT type=hidden  value=" + jumpPage   
					       + " name=\"jumpPage\" > "); 			     
			     str.append("<INPUT type=hidden  value=" + pageSize   
			       + " name=\"pageSize\"> ");   
			     str.append("<INPUT type=hidden  value=" + count   
					       + " name=\"count\"> "); 
			     str.append("<INPUT type=hidden  value=" + orderBy   
					       + " name=\"orderBy\"> "); 
			     str.append("<INPUT type=hidden  value=" + orderType  
					       + " name=\"orderType\"> "); 	
			     str.append("<INPUT type=hidden  value=" + opt  
					       + " name=\"opt\"> "); 	
			     str.append("<INPUT type=hidden  value=" + oldType  
					       + " name=\"oldType\"> "); 				     
			     return str.toString(); 
		}
		public String getPageControllerAjaxString()
		{
			  StringBuffer str = new StringBuffer("");   
			     int next, prev;   
			     prev = page - 1;   
			     next = page + 1;   
			      
			     if (page > 1) {   
			      str   
			        .append("<a href=\"#\" onclick=\"document." + formName + ".jumpPage.value=1;document." + formName + ".opt.value=1;submitForm();\">首页</a> ");   
			     } else {   
			         //str.append("<a href=\"#\">首页</a> ");
			         str.append("首页 ");
			     }   
			     if (page > 1) {   
			      str.append("<a href=\"#\" onclick='document." + formName + ".jumpPage.value="   
			        + prev + ";document." + formName + ".opt.value=1;submitForm();'>上页</a> ");   
			     } else {   
			         //str.append("<a href=\"#\">上页</a> ");   
			         str.append("上页 ");   
			     }   
			     if (page < pageCount) {   
			      str.append("<a href=\"#\" onclick='document." + formName + ".jumpPage.value="   
			        + next + ";document." + formName + ".opt.value=1;submitForm();'>下页</a> ");   
			     } else {   
			       //str.append("<a href=\"#\" >下页</a> ");   
			         str.append("下页 ");
			     }   
			     if (pageCount > 1 && page != pageCount) {   
			      str.append("<a href=\"#\"  onclick='document." + formName + ".jumpPage.value="   
			        + pageCount   
			        + ";document." + formName + ".opt.value=1;submitForm();'>末页</a>  ");   
			     } else {   
			      //str.append("<a href=\"#\" >末页</a>  ");
			        str.append("末页  ");
			     }   
			     str.append(" 共" + count + "条记录");   
			     str   
			       .append("  每页");      
			     str.append(pageSize);   
			     str.append("条 分" + pageCount + "页显示 转到");   
//			     str.append("<SELECT size=1 name=Pagelist onchange='this.form.jumpPage.value=this.value;document." + formName + ".opt.value=1;this.form.submit();'>");
			     str.append("<SELECT size=1 name=Pagelist onchange='this.form.jumpPage.value=this.value;submitForm();'>");
			     for (int i = 1; i < pageCount + 1; i++) {   
			      if (i == page) {   
			       str.append("<OPTION value=" + i + " selected>" + i   
			         + "</OPTION>");   
			      } else {   
			       str.append("<OPTION value=" + i + ">" + i + "</OPTION>");   
			      }   
			     }   
			     str.append("</SELECT>页");   
			     str.append("<INPUT type=hidden  value=" + page   
			       + " name=\"pages\" > "); 
			     str.append("<INPUT type=hidden  value=" + jumpPage   
					       + " name=\"jumpPage\" > "); 			     
			     str.append("<INPUT type=hidden  value=" + pageSize   
			       + " name=\"pageSize\"> ");   
			     str.append("<INPUT type=hidden  value=" + count   
					       + " name=\"count\"> "); 
			     str.append("<INPUT type=hidden  value=" + orderBy   
					       + " name=\"orderBy\"> "); 
			     str.append("<INPUT type=hidden  value=" + orderType  
					       + " name=\"orderType\"> "); 	
			     str.append("<INPUT type=hidden  value=" + opt  
					       + " name=\"opt\"> "); 	
			     str.append("<INPUT type=hidden  value=" + oldType  
					       + " name=\"oldType\"> "); 				     
			     return str.toString(); 
		}
		public String getPageControllerHtmlStringTwo()
		{
			  StringBuffer str = new StringBuffer("");   
			     int next, prev;   
			     prev = page - 1;   
			     next = page + 1;   
			      
			     if (page > 1) {   
			      str   
			        .append("<a href=\"#\" onclick=\"document." + formName + ".jumpPage.value=1;document." + formName + ".opt.value=1;document.forms(0).submit();\">首页</a> ");   
			     } else {   
			         //str.append("<a href=\"#\">首页</a> ");
			         str.append("首页 ");
			     }   
			     if (page > 1) {   
			      str.append("<a href=\"#\" onclick='document." + formName + ".jumpPage.value="   
			        + prev + ";document." + formName + ".opt.value=1;document." + formName + ".submit();'>上页</a> ");   
			     } else {   
			         //str.append("<a href=\"#\">上页</a> ");   
			         str.append("上页 ");   
			     }   
			     if (page < pageCount) {   
			      str.append("<a href=\"#\" onclick='document." + formName + ".jumpPage.value="   
			        + next + ";document." + formName + ".opt.value=1;document." + formName + ".submit();'>下页</a> ");   
			     } else {   
			       //str.append("<a href=\"#\" >下页</a> ");   
			         str.append("下页 ");
			     }   
			     if (pageCount > 1 && page != pageCount) {   
			      str.append("<a href=\"#\"  onclick='document." + formName + ".jumpPage.value="   
			        + pageCount   
			        + ";document." + formName + ".opt.value=1;document." + formName + ".submit();'>末页</a>  ");   
			     } else {   
			      //str.append("<a href=\"#\" >末页</a>  ");
			        str.append("末页  ");
			     }   
			     str.append(" 共" + count + "条记录");   
			     str   
			       .append("  每页");      
			     str.append(pageSize);   
			     str.append("条 分" + pageCount + "页显示 转到");   
//			     str.append("<SELECT size=1 name=Pagelist onchange='this.form.jumpPage.value=this.value;document." + formName + ".opt.value=1;this.form.submit();'>");
			     str.append("<SELECT size=1 name=Pagelist onchange='document." + formName + ".jumpPage.value=this.value;document." + formName + ".submit();'>");
			     for (int i = 1; i < pageCount + 1; i++) {   
			      if (i == page) {   
			       str.append("<OPTION value=" + i + " selected>" + i   
			         + "</OPTION>");   
			      } else {   
			       str.append("<OPTION value=" + i + ">" + i + "</OPTION>");   
			      }   
			     }   
			     str.append("</SELECT>页");   
			     str.append("<INPUT type=hidden  value=" + page   
			       + " name=\"pages\" > "); 
			     str.append("<INPUT type=hidden  value=" + jumpPage   
					       + " name=\"jumpPage\" > "); 			     
			     str.append("<INPUT type=hidden  value=" + pageSize   
			       + " name=\"pageSize\"> ");   
			     str.append("<INPUT type=hidden  value=" + count   
					       + " name=\"count\"> "); 
			     str.append("<INPUT type=hidden  value=" + orderBy   
					       + " name=\"orderBy\"> "); 
			     str.append("<INPUT type=hidden  value=" + orderType  
					       + " name=\"orderType\"> "); 	
			     str.append("<INPUT type=hidden  value=" + opt  
					       + " name=\"opt\"> "); 	
			     str.append("<INPUT type=hidden  value=" + oldType  
					       + " name=\"oldType\"> "); 				     
			     return str.toString(); 
		}

		@SuppressWarnings("unchecked")
		public List getPageItems() {
			return pageItems;
		}

		@SuppressWarnings("unchecked")
		public void setPageItems(List pageItems) {
			this.pageItems = pageItems;
		}
		public int getJumpPage() {
			return jumpPage;
		}
		public void setJumpPage(int jumpPage) {
			this.jumpPage = jumpPage;
		}
		public String getOrderBy() {
			return orderBy;
		}
		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}
		public String getOrderType() {
			return orderType;
		}
		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}
		public String getOpt() {
			return opt;
		}
		public void setOpt(String opt) {
			this.opt = opt;
		}
		public String getOldType() {
			return oldType;
		}
		public void setOldType(String oldType) {
			this.oldType = oldType;
		}
		
		

}
