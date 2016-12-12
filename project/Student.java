import java.util.ArrayList;

public class Student {


	ArrayList<Project> preferenceList;
	ArrayList<Project> untouchedPreferenceList;
	Project proj;

	// Used to find index of project in students preference list
	// i.e. index 0 contains preference value for project 0 for student
	int[] rankingList;

	// tracks current best project or assigned project if student is assigned in text input file
	int rankingListTracker;

	//personal details
	String name;

	// applicable to Spa P Approx Promotion only
	boolean promoted;

	public Student(String name) {
		rankingListTracker = 0;
		this.name = name;
		promoted = false;
		preferenceList = new ArrayList<Project>();
	}

	public void promote() {
		promoted = true;
		preferenceList = new ArrayList<Project>(untouchedPreferenceList);
		rankingListTracker = 0;
	}
}
