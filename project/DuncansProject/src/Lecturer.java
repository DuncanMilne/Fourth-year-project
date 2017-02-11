import java.util.ArrayList;

public class Lecturer implements Cloneable {

	String name;

	ArrayList<Project> projects = new ArrayList<Project>();

	int worstNonEmptyProject; // simply indexes the ranking list which gets from point in projects

	// Used to find index of project in students preference list
	// i.e. index 0 contains preference value for project 0 for student
	int[] rankingList;

	int capacity;

	int assigned;

	public Lecturer(String name) {
		this.name = name;
		this.projects = new ArrayList<Project>();
		this.capacity = 1;
		this.assigned = 0;
	}

	public Lecturer(String name, int capacity) {
		this.name = name;
		this.projects = new ArrayList<Project>();
		this.capacity = capacity;
		this.assigned = 0;
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {

	    return super.clone();
	}

}

// should create full method in lecturer that returns true if lecturer.capacity = lecturer.assigned
