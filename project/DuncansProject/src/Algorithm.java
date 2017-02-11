import java.util.ArrayList;
import java.util.Random;

import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBVar;

public class Algorithm implements Cloneable {

	protected ArrayList<Project> projects;
	protected ArrayList<Lecturer> lecturers;
	protected ArrayList<Student> assignedStudents;
	protected ArrayList<Student> unassigned;
	protected ArrayList<Student> projectlessStudents;
	protected StabilityChecker s = new StabilityChecker(this);
	protected Project emptyProject;
	protected ArrayList<Student> untouchedStudents;
	protected int instances;
	
	ArrayList<Student> applyingStudent;
	ArrayList<Project> applyingProject;

	public Algorithm() {
		projects = new ArrayList<Project>();
		lecturers = new ArrayList<Lecturer>();
		assignedStudents = new ArrayList<Student>();
		unassigned = new ArrayList<Student>();
		emptyProject = new Project("empty");
		projectlessStudents = new ArrayList<Student>();
		untouchedStudents = new ArrayList<Student>();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {

	    return super.clone();
	}

	protected void findNextFavouriteProject(Student currentStudent) {
		int max = -1;

		// iterates over students full ranking list
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
			projectlessStudents.add(currentStudent);
			currentStudent.rankingListTracker = -1;
		} else {

			// set students favourite project tracker to max
			currentStudent.rankingListTracker = max;
		}
	}

	// Returns lecturersWorstNonEmptyProject
	public static Project lecturersWorstNonEmptyProject(Lecturer fPL, Project lecturersWorstNonEmptyProject) {
		boolean foundNonEmpty = false;
		// iterate from the end as the last entry will contain the worst project
		for (int i = fPL.projects.size()-1; i>-1; i--){

			// if project is not empty
			if (fPL.projects.get(i).unpromoted.size() + fPL.projects.get(i).promoted.size()>0){
					lecturersWorstNonEmptyProject = fPL.projects.get(i);
					i=-1;
					foundNonEmpty = true;
			}
		}
		if (foundNonEmpty == true) {
			return lecturersWorstNonEmptyProject;
		} else { 
			return fPL.projects.get(fPL.projects.size()-1); // we return their worst as opposed to returning their optimal, if optimal is the only option
		}
	}

	public void printInstance(int constraint) {

		//int numberOfStudents = unassigned.size() + assignedStudents.size() + projectlessStudents.size();
		//System.out.println(projects.size() + " " + numberOfStudents + " " + testLecturers.size());
		
		this.printProjects();

		this.printStudents();

		this.printLecturers();
		
		if (constraint == 0 ) { 
			this.printMatching();
			/*for (int i = 0; i<this.applyingStudent.size(); i++) {
				System.out.println(applyingStudent.get(i).name + " is applying to " + applyingProject.get(i).name);
			}*/
		} else  {
			this.printConstraintMatching();
		}
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
		for (Student st: assignedStudents) {
			System.out.print(st.name + " : ");
			for (Project p: st.preferenceList) {
				System.out.print(p.name + " ");
			}
			System.out.println("");
		}
	}

	void printLecturers() {
		System.out.println("PRINTING LECTURERS");
		ArrayList<Lecturer> toPrint = lecturers;
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
			System.out.println(s.name + " " + s.proj.name);
		}
		System.out.println(assignedStudents.size() + " students were assigned a project");
        
        
	}
	
	void printConstraintMatching() {
		System.out.println("PRINTING MATCHING");
		int countOfMatched = 0;
		for (Student s:assignedStudents) {
			if (s.proj != null) {
				//System.out.println(s.name + " " + s.proj.name);
				if (s.proj != emptyProject) {
					countOfMatched++;
					System.out.println(s.name + " " + s.proj.name);
				}
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
