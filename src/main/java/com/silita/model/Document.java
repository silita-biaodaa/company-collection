package com.silita.model;

import java.io.Serializable;

/**
 * 采集结果
 */
public class Document implements Serializable {
	private static final long serialVersionUID = 5103551175451516801L;
	
	private Object object;
	
	public Document(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}
