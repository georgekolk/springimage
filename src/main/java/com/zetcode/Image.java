package com.zetcode;

public class Image {
	
	private Long id;
	
	private String name;
	
	public Image(){};

	public Image(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Image [id=" + id + ", name=" + name + "]";
	}
	
	
	
	

}
