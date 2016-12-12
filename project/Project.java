import java.util.ArrayList;

public class Project {

	Lecturer lecturer;
	String name;

	ArrayList<Student> currentlyAssignedStudents;

	ArrayList<Student> currentlyAssignedPromotedStudents;

	int capacity;


	public Project(String name) {
		this.name = name;
		this.capacity = 1;
		currentlyAssignedStudents = new ArrayList<Student>();
		currentlyAssignedPromotedStudents = new ArrayList<Student>();
		//could make capacity random int between 1-3
	}

	public Project(String name, int capacity) {
		this.name = name;
		this.capacity = capacity;
		currentlyAssignedStudents = new ArrayList<Student>();
		currentlyAssignedPromotedStudents = new ArrayList<Student>();
	}
}
