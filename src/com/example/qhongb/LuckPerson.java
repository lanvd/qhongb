package com.example.qhongb;

import java.io.Serializable;

public class LuckPerson  implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int _id;
	public String title;
	public String desc;
	public String personName ;
	public String time;
	public String money;
	public LuckPerson() {
		
	}
	public LuckPerson(String  title,String desc,String personName,String time ,String money) {
		this.title = title;
		this.desc = desc;
		this.personName = personName;
		this.time = time;
		this.money = money;
		
	}
	public String toString() {
		return personName+ ":" + time + " =" +money;
	}
}
