import java.util.ArrayList;

public class Student {


	ArrayList<Project> preferenceList;
	ArrayList<Project> untouchedPreferenceList;
	Project currentlyAssignedProject;

	// Used to find index of project in students preference list
	// i.e. index 0 contains preference value for project 0 for student
	int[] rankingList;

	// tracks current best project or assigned project if student is assigned in text input file
	int rankingListTracker;

	//personal details
	String name;

	public Student(String name) {
		rankingListTracker = 0;
		this.name = name;
		this.preferenceList = new ArrayList<Project>();
	}
}
