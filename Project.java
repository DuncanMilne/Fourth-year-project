public class Project {

	Lecturer lecturer;
	String name;

	int capacity;

	int numberOfAssignees;

	public Project(String name) {
		this.name = name;
		this.capacity = 1;
		//could make capacity random int between 1-3
	}
}
