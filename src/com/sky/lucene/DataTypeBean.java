package com.sky.lucene;


public class DataTypeBean implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer dataTypeId;
	private String dataTypeName;
	private String dataTypeKey;
	private String dataTypeDesc;
	private String fromType;


	public DataTypeBean() {
		super();
	}
	public DataTypeBean(String dataTypeName, String dataTypeKey) {
		super();
		this.dataTypeKey = dataTypeKey;
		this.dataTypeName = dataTypeName;
	}
	public Integer getDataTypeId() {
		return dataTypeId;
	}

	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	public String getDataTypeKey() {
		return dataTypeKey;
	}

	public void setDataTypeKey(String dataTypeKey) {
		this.dataTypeKey = dataTypeKey;
	}

	public String getDataTypeDesc() {
		return dataTypeDesc;
	}

	public void setDataTypeDesc(String dataTypeDesc) {
		this.dataTypeDesc = dataTypeDesc;
	}

	public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

}
