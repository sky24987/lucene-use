package xmlTojava;

import java.util.Random;

import xmlTojava.bean.Coors;
import xmlTojava.bean.FileUtils;
import xmlTojava.bean.GtsData;
import xmlTojava.bean.GtsResponse;
import xmlTojava.bean.Results;

import com.thoughtworks.xstream.XStream;

public class Test {
	public static void main(String ap[]){
		
		GtsData bean = new GtsData();
		StringBuilder s = FileUtils.readFile(System.getProperty("user.dir")+"/MyXml.xml");
		System.out.println(s.toString());

		XStream xstream = new XStream();
		
		xstream.alias("gts_response", GtsResponse.class);
		xstream.useAttributeFor(GtsResponse.class, "sid");
		xstream.alias("results", Results.class);
		xstream.alias("coor", String.class);
		xstream.alias("coors", Coors.class);
		GtsResponse o = (GtsResponse)xstream.fromXML(s.toString());
		
		System.out.println(o.getSid());
		System.out.println(o.getResults().getCoors().get(0).toString());
		
		System.out.println(new Random().nextInt(100000));
		
	}
}
