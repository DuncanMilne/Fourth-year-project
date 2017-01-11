import java.util.Random;

public class Approx extends Algorithm{

  public Approx() {
    super();
  }

  protected void assignProjectsToStudents() {

		//could use random value to randomise which student in unassigned we use
		Project studentsFirstProject;

    Lecturer fPL;

    Project lecturersWorstNonEmptyProject;

    Student student;

    Project redundantProject;

    int currentIndex; // used to locate students favourite 	project

    Random randomStudent = new Random();

		while (!unassigned.isEmpty()) {

			student = unassigned.get(randomStudent.nextInt(unassigned.size()));

      currentIndex = student.rankingListTracker;

      studentsFirstProject = student.preferenceList.get(currentIndex);

      fPL = studentsFirstProject.lecturer;

			lecturersWorstNonEmptyProject = fPL.projects.get(fPL.projects.size() - 1); //initially set it to worst project

			if (fPL.assigned != 0) {
				//iterate over all lecturers projects backwards to find worst nonEmptyProject
				lecturersWorstNonEmptyProject = lecturersWorstNonEmptyProject(fPL, lecturersWorstNonEmptyProject);
			}

			// if project is full || lecturer is full and this is lecturers worst project
			if (studentsFirstProject.unpromoted.size() == studentsFirstProject.capacity || (fPL.assigned == fPL.capacity && lecturersWorstNonEmptyProject == studentsFirstProject)) {
				student.preferenceList.set(currentIndex, emptyProject);
				findNextFavouriteProject(student);
			} else {
				// get lecturersWorstNonEmptyProject and remove random student from this
				//int indexOfWorstNonEmptyProject= fPL.projects.size() - 1; eclipse says this isnt being used, commented out will investigate later
				lecturersWorstNonEmptyProject = lecturersWorstNonEmptyProject(fPL, lecturersWorstNonEmptyProject);

				// temporarily set project as students assigned project
				student.proj = studentsFirstProject;
				studentsFirstProject.unpromoted.add(student);
				assignedStudents.add(student);
				unassigned.remove(student);
				fPL.assigned++;

				if (fPL.assigned > fPL.capacity) {
					Random random = new Random();
					int removeInt = random.nextInt((lecturersWorstNonEmptyProject.unpromoted.size()));
					if (removeInt != 0) {
						removeInt--; // allows access to each student
					}
					// remove a random student from the lecturersWorstNonEmptyProject
					Student removeStudent = lecturersWorstNonEmptyProject.unpromoted.get(removeInt);
					lecturersWorstNonEmptyProject.unpromoted.remove(removeStudent);
					removeStudent.proj = null;

					removeStudent.preferenceList.set(removeStudent.preferenceList.indexOf(lecturersWorstNonEmptyProject), emptyProject);

					findNextFavouriteProject(removeStudent);

					if (removeStudent.rankingListTracker != -1){	//if they dont only have rejected projects
						unassigned.add(removeStudent);
					}

					assignedStudents.remove(removeStudent);
					fPL.assigned--;
				}

				if (fPL.capacity == fPL.assigned) {
					//every project that lecturer prefers worstnonemptyproject to
					//delete project from all students list

					for (int i = (fPL.projects.indexOf(lecturersWorstNonEmptyProject)+1); i < fPL.projects.size(); i++){
						redundantProject = fPL.projects.get(i);
						// for each student remove from their preferenceList if they have it
						for (Student s:unassigned) {

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
						for (Student s:unassigned) {

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
