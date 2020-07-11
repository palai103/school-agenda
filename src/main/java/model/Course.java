package model;

public class Course {

	private String name;
	private String id;

	public Course(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
}
