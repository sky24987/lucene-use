package xmlTojava.bean;

public class GtsResponse {

	private String  sid;
	private Results results;  //返回的结果
	private String status; //返回的状态
	

	public Results getResults() {
		return results;
	}
	public void setResults(Results results) {
		this.results = results;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}



	
}
