package xmlTojava.bean;

import java.util.List;

public class Results {
	private List<String> coors;

	public List<String> getCoors() {
		return coors;
	}

	public void add(String coor){
		coors.add(coor);
	}


	
}
