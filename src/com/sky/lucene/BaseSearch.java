package com.sky.lucene;

import java.util.List;

public abstract class BaseSearch {
	private String orderBy;
	private String orderType;
	private Integer roleId;
	private Integer userId;
	private Integer isSystem;
	private Integer loggedInUserId;
	private String isCity;
	private List<String> placeCodes;
	private List<Integer> townshipIDs;
	



	public List<String> getPlaceCodes() {
		return placeCodes;
	}

	public void setPlaceCodes(List<String> placeCodes) {
		this.placeCodes = placeCodes;
	}

	public String getIsCity() {
		return isCity;
	}

	public void setIsCity(String isCity) {
		this.isCity = isCity;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public BaseSearch() {
		super();
	}

	public BaseSearch(String orderBy, String orderType) {
		super();
		this.orderBy = orderBy;
		this.orderType = orderType;
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

	public Integer getLoggedInUserId() {
		return loggedInUserId;
	}

	public void setLoggedInUserId(Integer loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}

	public Integer getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(Integer isSystem) {
		this.isSystem = isSystem;
	}

	public void setTownshipIDs(List<Integer> townshipIDs) {
		this.townshipIDs = townshipIDs;
	}

	public List<Integer> getTownshipIDs() {
		return townshipIDs;
	}

}
