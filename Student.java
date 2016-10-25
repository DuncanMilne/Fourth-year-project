import java.util.ArrayList;

public class Student {

	
	ArrayList<Project> preferenceList;
	Project currentlyAssignedProject;

	// Used to find index of project in students preference list
	// i.e. index 0 contains preference value for project 0 for student
	int[] rankingList; 
	
	//personal details
	String name;
	
	public Student(String name) {
		this.name = name;
		this.preferenceList = new ArrayList<Project>();
		// instantiate when preference list exists
		//this.rankingList = new int[10]; //10 for now should change to size of preferenceList
	}
}
