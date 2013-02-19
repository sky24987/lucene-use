/**
 * 
 */
package groovy
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder

/**
 * @author Administrator
 *
 */
public class HelloWorld{
	/**
	 * 统计词的频率
	 */
	public  ciping(){
		 def content   =  
		     """
		    The Java Collections API is the basis   for   all the nice support that Groovy gives you
		    through lists and maps. In fact, Groovy not only uses the same abstractions, it
		    even works on the very same classes that make up the Java Collections API.
		     """
		 
		def words  =  content.tokenize()

		def wordFrequency  =  [:]

		words.each {
		    wordFrequency[it]  =  wordFrequency.get(it,  0 )  +   1  
		} 

		def wordList  =  wordFrequency.keySet().toList()

		wordList.sort {wordFrequency[it]} 

		def result  =   ''  

		wordList[ - 1 .. - 6 ].each {
		    result  +=  it.padLeft( 12 )  +   " :  "   +  wordFrequency[it]  +   "  \n  "  
		} 
		println result  
	}
	
	public genXml1(){
		def out  =   new  StringWriter()
		def xml  =   new  MarkupBuilder(out)

		def friendList  =  [ ' Tony ' ,  ' Alan ' ,  ' Leona ' ,  ' Cloudy ' ,  ' terry ' ]

		xml.person {
		    name(type: " 网名 " , "山风小子")
		    address "上海"
		    friends(num: friendList.size()) {
		        for (f in friendList) {
		            friend f
		        }
		    }
		}
		println out.toString()
	}
	
	public gencomlexXml(){
		def friendList = ['Tony', 'Alan', 'Leona', 'Cloudy']
		def xml = new StreamingMarkupBuilder().bind { 
		    mkp.pi(xml: "version='1.0'  encoding='UTF-8'") 
		    
		    mkp.declareNamespace(ns:"http://www.ebay.com")
		    person {
		        ns.name(type: "nickname", "BlueSUN")
		        ns.address "Shanghai"
		        ns.friends(num: friendList.size()) {
		            for (f in friendList) {
		                ns.friend f
		            }
		        }
		    }
		}
		println xml
		
		
	}
	
	
	public genHtml1(){
		
		def out  =   new  StringWriter()
		def html  =   new  MarkupBuilder(out)

		html.html {
		    body {
		        font(color:'red', size:6) {
		            b "Hello, world!"
		        }
		    }
		}

		println out.toString()
		
		
	}
	
	
	static void main(args){
		HelloWorld hell = new HelloWorld();
//		hell.ciping();
//		hell.genXml1();
//		hell.genHtml1();
		
		hell.gencomlexXml();
		
		
	}
	
}
