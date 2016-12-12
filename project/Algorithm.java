import java.util.ArrayList;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Scanner;
import java.util.Random;


// Currently creating projects and students and adding projects to students preference lists.
// Lecturers are also created
public class Algorithm {

	protected ArrayList<Project> testProjects;
  protected ArrayList<Lecturer> testLecturers;
  protected ArrayList<Student> assignedStudents;
  protected ArrayList<Student> unassigned;
  protected ArrayList<Student> projectlessStudents;
	protected StabilityChecker s = new StabilityChecker(this);
	protected Project emptyProject;
	protected ArrayList<Student> untouchedStudents;
	// make copy of unasigned students at the start then create new pref list in new data structure

	public Algorithm() {
		testProjects = new ArrayList<Project>();
		testLecturers = new ArrayList<Lecturer>();
		assignedStudents = new ArrayList<Student>();
		unassigned = new ArrayList<Student>();
		emptyProject = new Project("empty");
		projectlessStudents = new ArrayList<Student>();
		untouchedStudents = new ArrayList<Student>();
	}

	protected void findNextFavouriteProject(Student currentStudent) {
		int max = -1;
		for (int k = 0; k < currentStudent.rankingList.length; k++) { 	//always iterates over students full ranking list
			if (currentStudent.preferenceList.get(k) != emptyProject){
				if (max !=-1){
 					if (currentStudent.rankingList[k] < currentStudent.rankingList[max]) {
						max = k;
					}
				} else {
					max = k;
				}
			}
		}
		if (max == -1) {
			unassigned.remove(currentStudent);
			projectlessStudents.add(currentStudent);
			currentStudent.rankingListTracker = -1;
		} else {
			currentStudent.rankingListTracker = max;
		}
	}

	// Returns lecturersWorstNonEmptyProject
	public static Project lecturersWorstNonEmptyProject(Lecturer firstProjectsLecturer, Project lecturersWorstNonEmptyProject) {
		for (int i = firstProjectsLecturer.projects.size()-1;i>-1; i--){
			if (firstProjectsLecturer.projects.get(i).unpromoted.size()>0){
					lecturersWorstNonEmptyProject = firstProjectsLecturer.projects.get(i);
					i=-1;
			}
		}
		return lecturersWorstNonEmptyProject;
	}

	public void printInstance() {
		int numberOfStudents = unassigned.size() + assignedStudents.size() + projectlessStudents.size();
		System.out.println(testProjects.size() + " " + numberOfStudents + " " + testLecturers.size());
		this.printProjects();
		this.printStudents();
		this.printLecturers();
		this.printMatching();
	}

	void printMatching() {
		System.out.println("PRINTING MATCHING");
		for (Student s:assignedStudents) {
			System.out.println(s.name + " " + s.proj.name);
		}
	}

	void printProjects(){
		System.out.println();
		ArrayList<Project> toPrint = testProjects;
		for (Project p: toPrint) {
			System.out.println(p.name + " " + p.capacity);
		}
		System.out.println();
	}

	void printLecturers() {
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

	void printStudents() {
		for (Student st: untouchedStudents) {
			System.out.print(st.name + " : ");
			for (Project p: st.preferenceList) {
				System.out.print(p.name + " ");
			}
			System.out.println("");
		}
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
	 	 int removeInt = random.nextInt((worstNonEmptyProject.promoted.size()));
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

		if (removeStudent.rankingListTracker != -1){	//if they dont only have rejected projects
			unassigned.add(removeStudent);
		}

		assignedStudents.remove(removeStudent);
		firstProjectsLecturer.assigned--;
	}

	protected void  spaPApproxPromotion(){} //#TODO work out how to make these abstract?

	protected void assignProjectsToStudents() {}
}
