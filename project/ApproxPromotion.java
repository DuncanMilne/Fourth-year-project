import java.util.Random;

public class ApproxPromotion extends Algorithm{

  public ApproxPromotion() {
    super();
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
        } else {
          lecturersWorstNonEmptyProject = lecturersWorstNonEmptyProject(firstProjectsLecturer, lecturersWorstNonEmptyProject);
        }
        if (lecturersWorstNonEmptyProject!= emptyProject && ((studentsFirstProject.currentlyAssignedStudents.size() +studentsFirstProject.currentlyAssignedPromotedStudents.size()) == studentsFirstProject.capacity || (firstProjectsLecturer.numberOfAssignees == firstProjectsLecturer.capacity && lecturersWorstNonEmptyProject == studentsFirstProject))){  //do check for if empty project, if empty project then lecturer has no worst non empty project
          // if student is unpromoted or there is no unpromoted student assigned to studentsFirstProject
          if (!currentStudent.promoted || studentsFirstProject.currentlyAssignedStudents.size()==0){
            // reject student
            currentStudent.preferenceList.set(currentIndex, emptyProject);
            findNextFavouriteProject(currentStudent);
          } else {
            // get random unpromoted student from currently assigned students
            Student removeStudent = studentsFirstProject.currentlyAssignedStudents.get(randomStudent.nextInt(studentsFirstProject.currentlyAssignedStudents.size()));
            assignedStudents.remove(removeStudent);
            unassignedStudents.add(removeStudent);
            studentsFirstProject.currentlyAssignedPromotedStudents.add(currentStudent);
          }
        } else if (firstProjectsLecturer.numberOfAssignees == firstProjectsLecturer.capacity && firstProjectsLecturer.rankingList[firstProjectsLecturer.projectList.indexOf(lecturersWorstNonEmptyProject)] < firstProjectsLecturer.rankingList[firstProjectsLecturer.projectList.indexOf(studentsFirstProject)]){   // line 16 in pseudo code. Start from here tomorrow also try fix git.
          // lecturer is full and prefers the worst non empty project to studentsFirstProject
          currentStudent.preferenceList.set(currentIndex, emptyProject);
          findNextFavouriteProject(currentStudent);
        } else {
          currentStudent.currentlyAssignedProject = studentsFirstProject;
          studentsFirstProject.currentlyAssignedStudents.add(currentStudent);
          assignedStudents.add(currentStudent);
          unassignedStudents.remove(currentStudent);
          firstProjectsLecturer.numberOfAssignees++;
          Project worstNonEmptyProject; //new variable because it says in pseudo cod pz != pj, dont understand why yet
          Student removeStudent;
          // if lecturer is oversubscribed
          if (firstProjectsLecturer.numberOfAssignees > firstProjectsLecturer.capacity) {
            worstNonEmptyProject = lecturersWorstNonEmptyProject(firstProjectsLecturer, lecturersWorstNonEmptyProject);
            Random random = new Random();
            if (worstNonEmptyProject.currentlyAssignedStudents.size() > 0) {

              int removeInt = random.nextInt((lecturersWorstNonEmptyProject.currentlyAssignedStudents.size()));
              if (removeInt != 0) {
                removeInt--; // allows access to each student
              }
              // remove a random student from the lecturersWorstNonEmptyProject
              removeStudent = worstNonEmptyProject.currentlyAssignedStudents.get(removeInt);
           }else {
             int removeInt = random.nextInt((lecturersWorstNonEmptyProject.currentlyAssignedPromotedStudents.size()));
             if (removeInt != 0) {
               removeInt--; // allows access to each student
             }
             // remove a random student from the lecturersWorstNonEmptyProject
             removeStudent = worstNonEmptyProject.currentlyAssignedPromotedStudents.get(removeInt);
           }
            worstNonEmptyProject.currentlyAssignedStudents.remove(removeStudent);
            removeStudent.currentlyAssignedProject = null;

            removeStudent.preferenceList.set(removeStudent.preferenceList.indexOf(worstNonEmptyProject), emptyProject);

            findNextFavouriteProject(removeStudent);

            if (removeStudent.rankingListTracker != -1){	//if they dont only have rejected projects
              unassignedStudents.add(removeStudent);
            }

            assignedStudents.remove(removeStudent);
            firstProjectsLecturer.numberOfAssignees--;
          }
        }
      }
    }
  }
}
