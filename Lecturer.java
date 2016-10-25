import java.util.ArrayList;

public class Lecturer {

	String name;

	ArrayList<Project> projectList = new ArrayList<Project>();

	int worstNonEmptyProject; // simply indexes the ranking list which gets from point in projectList

	// Used to find index of project in students preference list
	// i.e. index 0 contains preference value for project 0 for student
	int[] rankingList;

	int capacity;

	int numberOfAssignees;

	public Lecturer(String name) {
		this.name = name;
		this.projectList = new ArrayList<Project>();
		this.capacity = 1;
		this.numberOfAssignees = 0;
	}
}
