package model;

public class Course {

	private String name;
	private String id;
	private String cfu;

	public Course(String id, String name, String cfu) {
		this.id = id;
		this.name = name;
		this.cfu = cfu;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", name=" + name + ", CFU=" + cfu + "]";
	}

	public String getId() {
		return id;
	}
	
	public String getCFU() {
		return cfu;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
