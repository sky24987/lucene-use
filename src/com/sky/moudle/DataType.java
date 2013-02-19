package com.sky.moudle;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * DataType entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "data_type", catalog = "map")
public class DataType implements java.io.Serializable {

	// Fields

	private Integer dataTypeId;
	private String dataTypeName;
	private String dataTypeKey;
	private String dataTypeDesc;
	private String fromType;
	private Set<MapBean> mapDatasForDataType = new HashSet<MapBean>(0);
	private Set<MapBean> mapDatasForComType = new HashSet<MapBean>(0);

	// Constructors

	/** default constructor */
	public DataType() {
	}
	
	
	/** default constructor */
	public DataType(String dataTypeName,String dataTypeKey) {
		this.dataTypeName=dataTypeName;
		this.dataTypeKey=dataTypeKey;
	}


	/** minimal constructor */
	public DataType(String dataTypeName, String dataTypeKey,
			String dataTypeDesc, String fromType) {
		this.dataTypeName = dataTypeName;
		this.dataTypeKey = dataTypeKey;
		this.dataTypeDesc = dataTypeDesc;
		this.fromType = fromType;
	}

	/** full constructor */
	public DataType(String dataTypeName, String dataTypeKey,
			String dataTypeDesc, String fromType,
			Set<MapBean> mapDatasForDataType, Set<MapBean> mapDatasForComType) {
		this.dataTypeName = dataTypeName;
		this.dataTypeKey = dataTypeKey;
		this.dataTypeDesc = dataTypeDesc;
		this.fromType = fromType;
		this.mapDatasForDataType = mapDatasForDataType;
		this.mapDatasForComType = mapDatasForComType;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "data_type_id", nullable = false)
	public Integer getDataTypeId() {
		return this.dataTypeId;
	}

	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}

	@Column(name = "data_type_name", nullable = false, length = 32)
	public String getDataTypeName() {
		return this.dataTypeName;
	}

	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	@Column(name = "data_type_key", nullable = false, length = 32)
	public String getDataTypeKey() {
		return this.dataTypeKey;
	}

	public void setDataTypeKey(String dataTypeKey) {
		this.dataTypeKey = dataTypeKey;
	}

	@Column(name = "data_type_desc", nullable = false, length = 64)
	public String getDataTypeDesc() {
		return this.dataTypeDesc;
	}

	public void setDataTypeDesc(String dataTypeDesc) {
		this.dataTypeDesc = dataTypeDesc;
	}

	@Column(name = "from_type", nullable = false, length = 10)
	public String getFromType() {
		return this.fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dataTypeByDataType")
	public Set<MapBean> getMapDatasForDataType() {
		return this.mapDatasForDataType;
	}

	public void setMapDatasForDataType(Set<MapBean> mapDatasForDataType) {
		this.mapDatasForDataType = mapDatasForDataType;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dataTypeByComType")
	public Set<MapBean> getMapDatasForComType() {
		return this.mapDatasForComType;
	}

	public void setMapDatasForComType(Set<MapBean> mapDatasForComType) {
		this.mapDatasForComType = mapDatasForComType;
	}

}