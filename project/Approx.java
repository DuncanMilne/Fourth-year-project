import java.util.Random;

public class Approx extends Algorithm{

  public Approx() {
    super();
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
}
