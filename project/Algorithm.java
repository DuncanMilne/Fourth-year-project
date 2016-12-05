import java.util.ArrayList;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Random;
import java.util.Collections;
import java.util.Scanner;

// Currently creating projects and students and adding projects to students preference lists.
// Lecturers are also created
public class Algorithm {

	protected ArrayList<Project> testProjects;
  protected ArrayList<Lecturer> testLecturers;
  protected ArrayList<Student> assignedStudents;
  protected ArrayList<Student> unassignedStudents;
  protected ArrayList<Student> projectlessStudents;
	protected StabilityChecker s = new StabilityChecker(this);
	protected Project emptyProject;
	protected ArrayList<Student> untouchedStudents;
	// make copy of unasigned students at the start then create new pref list in new data structure

	public Algorithm() {
		testProjects = new ArrayList<Project>();
		testLecturers = new ArrayList<Lecturer>();
		assignedStudents = new ArrayList<Student>();
		unassignedStudents = new ArrayList<Student>();
		emptyProject = new Project("empty");
		projectlessStudents = new ArrayList<Student>();
		untouchedStudents = new ArrayList<Student>();
	}

	protected void assignProjectsToStudents() {

		//could use random value to randomise which student in unassignedStudents we use
		Project studentsFirstProject;
		Lecturer firstProjectsLecturer;
		Project lecturersWorstNonEmptyProject;
		Student currentStudent;
		Project redundantProject;
		int currentIndex; // used to locate students favourite 	project
		Random randomStudent = new Random();
		while (!unassignedStudents.isEmpty()) {
			currentStudent = unassignedStudents.get(randomStudent.nextInt(unassignedStudents.size()));
			currentIndex = currentStudent.rankingListTracker;
			studentsFirstProject = currentStudent.preferenceList.get(currentIndex);
			firstProjectsLecturer = studentsFirstProject.lecturer;
			lecturersWorstNonEmptyProject = firstProjectsLecturer.projectList.get(firstProjectsLecturer.projectList.size() - 1); //initially set it to worst project
			if (firstProjectsLecturer.numberOfAssignees != 0) {
				//iterate over all lecturers projects backwards to find worst nonEmptyProject
				lecturersWorstNonEmptyProject = lecturersWorstNonEmptyProject(firstProjectsLecturer, lecturersWorstNonEmptyProject);
			}

			// if project is full || lecturer is full and this is lecturers worst project
			if (studentsFirstProject.currentlyAssignedStudents.size() == studentsFirstProject.capacity || (firstProjectsLecturer.numberOfAssignees == firstProjectsLecturer.capacity && lecturersWorstNonEmptyProject == studentsFirstProject)) {
				currentStudent.preferenceList.set(currentIndex, emptyProject);
				findNextFavouriteProject(currentStudent);
			} else {
				// get lecturersWorstNonEmptyProject and remove random student from this
				int indexOfWorstNonEmptyProject= firstProjectsLecturer.projectList.size() - 1;
				lecturersWorstNonEmptyProject = lecturersWorstNonEmptyProject(firstProjectsLecturer, lecturersWorstNonEmptyProject);

				// temporarily set project as students assigned project
				currentStudent.currentlyAssignedProject = studentsFirstProject;
				studentsFirstProject.currentlyAssignedStudents.add(currentStudent);
				assignedStudents.add(currentStudent);
				unassignedStudents.remove(currentStudent);
				firstProjectsLecturer.numberOfAssignees++;

				if (firstProjectsLecturer.numberOfAssignees > firstProjectsLecturer.capacity) {
					Random random = new Random();
					int removeInt = random.nextInt((lecturersWorstNonEmptyProject.currentlyAssignedStudents.size()));
					if (removeInt != 0) {
						removeInt--; // allows access to each student
					}
					// remove a random student from the lecturersWorstNonEmptyProject
					Student removeStudent = lecturersWorstNonEmptyProject.currentlyAssignedStudents.get(removeInt);
					lecturersWorstNonEmptyProject.currentlyAssignedStudents.remove(removeStudent);
					removeStudent.currentlyAssignedProject = null;

					removeStudent.preferenceList.set(removeStudent.preferenceList.indexOf(lecturersWorstNonEmptyProject), emptyProject);

					findNextFavouriteProject(removeStudent);

					if (removeStudent.rankingListTracker != -1){	//if they dont only have rejected projects
						unassignedStudents.add(removeStudent);
					}

					assignedStudents.remove(removeStudent);
					firstProjectsLecturer.numberOfAssignees--;
				}

				if (firstProjectsLecturer.capacity == firstProjectsLecturer.numberOfAssignees) {
					//every project that lecturer prefers worstnonemptyproject to
					//delete project from all students list

					for (int i = (firstProjectsLecturer.projectList.indexOf(lecturersWorstNonEmptyProject)+1); i < firstProjectsLecturer.projectList.size(); i++){
						redundantProject = firstProjectsLecturer.projectList.get(i);
						// for each student remove from their preferenceList if they have it
						for (Student s:unassignedStudents) {
								// remove this and use ranking lists
								// causing concurrent modification access error
								// so have to track location of redundant project and remove it after
								int location = -1;
								for (int j = 0; j < s.preferenceList.size()-1; j++) {
									if(s.preferenceList.get(j)==redundantProject)
										j=s.preferenceList.size();
								}
								if (location!= -1) {
									s.preferenceList.set(location, emptyProject);
									findNextFavouriteProject(s);
								}
						}
						for (Student s:unassignedStudents) {
								// causing concurrent modification access error
								// so have to track location of redundant project and remove it after
								int location = -1;
								for (int j = 0; j < s.preferenceList.size()-1; j++) {
									if(s.preferenceList.get(j)==redundantProject)
										j=s.preferenceList.size();
								}
								if (location!= -1) {
									s.preferenceList.set(location, emptyProject);
									findNextFavouriteProject(s);
								}
						}
					}
				}
			}
		}
	}

	protected void spaPApproxPromotion() {
		// while there exists an unassigned student that has a non empty list of is unpromoted.
		// Simply check when adding back to unassigned if their list is non empty or if they are unpromoted. If unpromoted, promote them and re-add all items to their list
		// otherwise add them to projectless

		Project studentsFirstProject;
		Lecturer firstProjectsLecturer;
		Student currentStudent;
		int currentIndex; // used to locate students favourite 	project
		boolean wasStudentPromoted;
		Project lecturersWorstNonEmptyProject;
		Random randomStudent = new Random();
		while (!unassignedStudents.isEmpty()) {
			lecturersWorstNonEmptyProject = emptyProject;
			currentStudent = unassignedStudents.get(randomStudent.nextInt(unassignedStudents.size()));
			wasStudentPromoted = false;
			if (currentStudent.rankingListTracker == -1) { // if currentstudent has empty preference list and is not promoted
				if (!currentStudent.promoted){
					currentStudent.promote();
				} else {
					unassignedStudents.remove(currentStudent);
					projectlessStudents.add(currentStudent);
					wasStudentPromoted = true;
				}
			}
			if (!wasStudentPromoted) { // used to ignore function runthrough if we promote the student
				currentIndex = currentStudent.rankingListTracker;
				studentsFirstProject = currentStudent.preferenceList.get(currentIndex);
				firstProjectsLecturer = studentsFirstProject.lecturer;
				if (firstProjectsLecturer.numberOfAssignees != 0) {
					lecturersWorstNonEmptyProject = firstProjectsLecturer.projectList.get(firstProjectsLecturer.projectList.size() - 1); //initially set it to worst project
				}
				if (lecturersWorstNonEmptyProject!= emptyProject && ((studentsFirstProject.currentlyAssignedStudents.size() +studentsFirstProject.currentlyAssignedPromotedStudents.size()) == studentsFirstProject.capacity || (firstProjectsLecturer.numberOfAssignees == firstProjectsLecturer.capacity && lecturersWorstNonEmptyProject == studentsFirstProject))){  //do check for if empty project, if empty project then lecturer has no worst non empty project
					// if student is unpromoted or there is no unpromoted student assigned to studentsFirstProject
					if (!currentStudent.promoted || studentsFirstProject.currentlyAssignedStudents.size()==0){
						// reject student
					} else {
						// get random unpromoted student from currently assigned students
						Student removeStudent = studentsFirstProject.currentlyAssignedStudents.get(randomStudent.nextInt(studentsFirstProject.currentlyAssignedStudents.size()));
						assignedStudents.remove(removeStudent);
						unassignedStudents.add(removeStudent);
						studentsFirstProject.currentlyAssignedPromotedStudents.add(currentStudent);
					}
				}
			} else {   // line 16 in pseudo code. Start from here tomorrow

			}
		}
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
			unassignedStudents.remove(currentStudent);
			projectlessStudents.add(currentStudent);
			currentStudent.rankingListTracker = -1;
		} else {
			currentStudent.rankingListTracker = max;
		}
	}

	// Returns lecturersWorstNonEmptyProject
	public static Project lecturersWorstNonEmptyProject(Lecturer firstProjectsLecturer, Project lecturersWorstNonEmptyProject) {
		for (int i = firstProjectsLecturer.projectList.size()-1;i>-1; i--){
			if (firstProjectsLecturer.projectList.get(i).currentlyAssignedStudents.size()>0){
					lecturersWorstNonEmptyProject = firstProjectsLecturer.projectList.get(i);
					i=-1;
			}
		}
		return lecturersWorstNonEmptyProject;
	}

	public void printInstance() {
		int numberOfStudents = unassignedStudents.size() + assignedStudents.size() + projectlessStudents.size();
		System.out.println(testProjects.size() + " " + numberOfStudents + " " + testLecturers.size());
		this.printProjects();
		this.printStudents();
		this.printLecturers();
		this.printMatching();
	}

	void printMatching() {
		System.out.println("PRINTING MATCHING");
		for (Student s:assignedStudents) {
			System.out.println(s.name + " " + s.currentlyAssignedProject.name);
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
			for (Project p: l.projectList) {
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
}
