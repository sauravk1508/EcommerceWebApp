package com.eCommerce.Entity;

public class Student {

	
	private int id ;
	private String name;
	private String prefession;
	
	
	public Student(int id, String name, String prefession) {
		super();
		this.id = id;
		this.name = name;
		this.prefession = prefession;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrefession() {
		return prefession;
	}
	public void setPrefession(String prefession) {
		this.prefession = prefession;
	}
	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", prefession=" + prefession + "]";
	}
	
	
}
