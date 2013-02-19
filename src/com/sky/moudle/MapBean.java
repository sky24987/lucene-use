package com.sky.moudle;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * MapData entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "map_data", catalog = "map", uniqueConstraints = {
		@UniqueConstraint(columnNames = "data_type"),
		@UniqueConstraint(columnNames = "com_type") })
public class MapBean implements java.io.Serializable {

	// Fields

	private Integer id;
	private DataType dataTypeByDataType;
	private DataType dataTypeByComType;
	private String east;
	private String north;
	private String name;
	private String address;
	private String phone;
	private String city;
	private String township;
	private String datatype;
	private String num;
	private String content;
	private Timestamp dateAdded;
	private String eastNew;
	private String northNew;
	private String code;
	private String type;
	private String num2;

	/** default constructor */
	public MapBean() {
	}

	/** minimal constructor */
	public MapBean(String address) {
		this.address = address;
	}

	/** full constructor */
	public MapBean(DataType dataTypeByDataType, DataType dataTypeByComType,
			String east, String north, String name, String address,
			String phone, String city, String township, String datatype,
			String num, String content, Timestamp dateAdded, String eastNew,
			String northNew, String code, String type) {
		this.dataTypeByDataType = dataTypeByDataType;
		this.dataTypeByComType = dataTypeByComType;
		this.east = east;
		this.north = north;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.city = city;
		this.township = township;
		this.datatype = datatype;
		this.num = num;
		this.content = content;
		this.dateAdded = dateAdded;
		this.eastNew = eastNew;
		this.northNew = northNew;
		this.code = code;
		this.type = type;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "data_type", unique = true)
	public DataType getDataTypeByDataType() {
		return this.dataTypeByDataType;
	}

	public void setDataTypeByDataType(DataType dataTypeByDataType) {
		this.dataTypeByDataType = dataTypeByDataType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "com_type", unique = true)
	public DataType getDataTypeByComType() {
		return this.dataTypeByComType;
	}

	public void setDataTypeByComType(DataType dataTypeByComType) {
		this.dataTypeByComType = dataTypeByComType;
	}

	@Column(name = "East", length = 50)
	public String getEast() {
		return this.east;
	}

	public void setEast(String east) {
		this.east = east;
	}

	@Column(name = "North", length = 50)
	public String getNorth() {
		return this.north;
	}

	public void setNorth(String north) {
		this.north = north;
	}

	@Column(name = "name", length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "address", nullable = false, length = 200)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "phone", length = 100)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "city", length = 10)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "township", length = 10)
	public String getTownship() {
		return this.township;
	}

	public void setTownship(String township) {
		this.township = township;
	}

	@Column(name = "datatype", length = 10)
	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	@Column(name = "num", length = 50)
	public String getNum() {
		return this.num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	@Column(name = "content", length = 100)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "date_added", length = 19)
	public Timestamp getDateAdded() {
		return this.dateAdded;
	}

	public void setDateAdded(Timestamp dateAdded) {
		this.dateAdded = dateAdded;
	}

	@Column(name = "east_new", length = 30)
	public String getEastNew() {
		return this.eastNew;
	}

	public void setEastNew(String eastNew) {
		this.eastNew = eastNew;
	}

	@Column(name = "north_new", length = 30)
	public String getNorthNew() {
		return this.northNew;
	}

	public void setNorthNew(String northNew) {
		this.northNew = northNew;
	}

	@Column(name = "code", length = 20)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "type", length = 10)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getNum2() {
		return num2;
	}

	public void setNum2(String num2) {
		this.num2 = num2;
	}


}