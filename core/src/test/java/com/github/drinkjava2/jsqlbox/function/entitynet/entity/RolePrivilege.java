package com.github.drinkjava2.jsqlbox.function.entitynet.entity;

import com.github.drinkjava2.jdialects.annotation.jdia.SingleFKey;
import com.github.drinkjava2.jdialects.annotation.jdia.UUID25;
import com.github.drinkjava2.jdialects.annotation.jpa.Id;
import com.github.drinkjava2.jdialects.annotation.jpa.Table;
import com.github.drinkjava2.jsqlbox.ActiveRecord;

@Table(name = "RolePrivilegetb")
public class RolePrivilege extends ActiveRecord<RolePrivilege> {
	@Id
	@UUID25
	String id;

	@SingleFKey(refs = { "roletb", "gid" })
	String rid;

	@SingleFKey(refs = { "privilegetb", "gid" })
	String pid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}