import java.util.ArrayList;
import java.util.Random;

import gurobi.GRBException;



// Currently creating projects and students and adding projects to students preference lists.
// Lecturers are also created
public class Algorithm {

	protected ArrayList<Project> projects;
	protected ArrayList<Lecturer> testLecturers;
	protected ArrayList<Student> assignedStudents;
	protected ArrayList<Student> unassigned;
	protected ArrayList<Student> projectlessStudents;
	protected StabilityChecker s = new StabilityChecker(this);
	protected Project emptyProject;
	protected ArrayList<Student> untouchedStudents;
	protected int instances;
	
	// make copy of unasigned students at the start then create new pref list in new data structure

	public Algorithm() {
		projects = new ArrayList<Project>();
		testLecturers = new ArrayList<Lecturer>();
		assignedStudents = new ArrayList<Student>();
		unassigned = new ArrayList<Student>();
		emptyProject = new Project("empty");
		projectlessStudents = new ArrayList<Student>();
		untouchedStudents = new ArrayList<Student>();
	}

	protected void findNextFavouriteProject(Student currentStudent) {
		int max = -1;

		// =iterates over students full ranking list
		for (int k = 0; k < currentStudent.rankingList.length; k++) {

			// found potential next favourite project
			if (currentStudent.preferenceList.get(k) != emptyProject){

				// if previous contender has been found
				if (max !=-1){

					// compare current with max
 					if (currentStudent.rankingList[k] < currentStudent.rankingList[max]) {
						max = k;
					}
				} else { // no contender found, this must be favourite
					max = k;
				}
			}
		}

		// if we didn't find a contender
		if (max == -1) {

			// student is now projectless
			unassigned.remove(currentStudent);
			projectlessStudents.add(currentStudent);	//#TODO could abstract this out
			currentStudent.rankingListTracker = -1;
		} else {

			// set students favourite project tracker to max
			currentStudent.rankingListTracker = max;
		}
	}

	// Returns lecturersWorstNonEmptyProject
	public static Project lecturersWorstNonEmptyProject(Lecturer firstProjectsLecturer, Project lecturersWorstNonEmptyProject) {

		// iterate from the end as the last entry will contain the worst project
		for (int i = firstProjectsLecturer.projects.size()-1;i>-1; i--){

			// if project is not empty
			if (firstProjectsLecturer.projects.get(i).unpromoted.size() + firstProjectsLecturer.projects.get(i).promoted.size()>0){
					lecturersWorstNonEmptyProject = firstProjectsLecturer.projects.get(i);
					i=-1;
			}
		}
		return lecturersWorstNonEmptyProject;
	}

	public void printInstance(int constraint) {

		int numberOfStudents = unassigned.size() + assignedStudents.size() + projectlessStudents.size();
		//System.out.println(projects.size() + " " + numberOfStudents + " " + testLecturers.size());

		//this.printProjects();

		//this.printStudents();

		//this.printLecturers();
		
		if (constraint == 0 ) 
			this.printMatching();
		else 
			this.printConstraintMatching();
	}

	void printProjects(){
		System.out.println("PRINTING PROJECTS");
		System.out.println();
		ArrayList<Project> toPrint = projects;
		for (Project p: toPrint) {
			System.out.println(p.name + " " + p.capacity);
		}
		System.out.println();
	}

	void printStudents() {
		System.out.println("PRINTING STUDENTS");
		for (Student st: untouchedStudents) {
			System.out.print(st.name + " : ");
			for (Project p: st.preferenceList) {
				System.out.print(p.name + " ");
			}
			System.out.println("");
		}
	}

	void printLecturers() {
		System.out.println("PRINTING LECTURERS");
		ArrayList<Lecturer> toPrint = testLecturers;
		for (Lecturer l: toPrint) {
			System.out.print(l.name + " : " + l.capacity + " : ");
			for (Project p: l.projects) {
				System.out.print(p.name + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	void printMatching() {
		System.out.println("PRINTING MATCHING");
		for (Student s:assignedStudents) {
			//System.out.println(s.name + " " + s.proj.name);
		}
		System.out.println(assignedStudents.size() + " students were assigned a project");
	}
	
	void printConstraintMatching() {
		System.out.println("PRINTING MATCHING");
		int countOfMatched = 0;
		for (Student s:untouchedStudents) {
			if (s.proj!=null)
				//System.out.println(s.name + " " + s.proj.name);
			if (s.proj != emptyProject) {
				countOfMatched++;
			}
		}
		System.out.println(countOfMatched + " students were matched");
	}

	void removeStudentFromArrayList(Lecturer firstProjectsLecturer,Project worstNonEmptyProject) {
		Random random = new Random();
		Student removeStudent;
		if (worstNonEmptyProject.unpromoted.size() > 0) {

			int removeInt = random.nextInt((worstNonEmptyProject.unpromoted.size()));
			if (removeInt != 0) {
				removeInt--; // allows access to each student
			}
			// remove a random student from the lecturersWorstNonEmptyProject
			removeStudent = worstNonEmptyProject.unpromoted.get(removeInt);
		} else {
	 	 int removeInt = random.nextInt((worstNonEmptyProject.promoted.size())-1);
	 	 if (removeInt != 0) {
	 		 removeInt--; // allows access to each student
	 	 }
	 	 // remove a random student from the lecturersWorstNonEmptyProject
	 	 removeStudent = worstNonEmptyProject.promoted.get(removeInt);
	  }
		worstNonEmptyProject.unpromoted.remove(removeStudent);
		removeStudent.proj = null;

		removeStudent.preferenceList.set(removeStudent.preferenceList.indexOf(worstNonEmptyProject), emptyProject);

		findNextFavouriteProject(removeStudent);

		if (removeStudent.rankingListTracker != -1){	//if they don't only have rejected projects
			unassigned.add(removeStudent);
		}

		assignedStudents.remove(removeStudent);
		firstProjectsLecturer.assigned--;
	}

	protected void  spaPApproxPromotion(){} //#TODO work out how to make these abstract?

	protected void assignProjectsToStudents() {}
	
	protected void printMatchingOutput(int avg, int max, int min) {
		System.out.println("Average matching size was " + avg);
		System.out.println("Maximum matching size was " + max);
		System.out.println("Minimum matching size was " + min);
	}

	public void assignConstraints(Algorithm a) throws GRBException {
		
	}
}
