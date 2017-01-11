import java.util.Random;

public class ApproxPromotion extends Algorithm{

  public ApproxPromotion() {
    super();
  }

  protected void spaPApproxPromotion() {
    // while there exists an unassigned student that has a non empty list of is unpromoted.
    // Simply check when adding back to unassigned if their list is non empty or if they are unpromoted. If unpromoted, promote them and re-add all items to their list
    // otherwise add them to projectless


    Lecturer fPL; //first projects lecturer

    Student stud; // random student chosen from list of unassigned students

    Project firstProj; // the random students first project

    int currentIndex; // used to locate students favourite project

    boolean wasStudentPromoted; // boolean value determining whether or not the student was promoted on this pass through the while loop

    Project wNEP; // the lecturers worst non empty project

    Random randomStudent = new Random();  // used to chose a random student

    while (!unassigned.isEmpty()) {

      wNEP = emptyProject;

      stud = unassigned.get(randomStudent.nextInt(unassigned.size()));

      wasStudentPromoted = false;

      /* if stud has empty preference list and is not promoted then promote them.
       * Otherwise if student has empty preference list and is promoted, add them to a list of projectless students
      */

      if (stud.rankingListTracker == -1) {
        if (!stud.promoted){
          stud.promote();
        } else {
          unassigned.remove(stud);
          projectlessStudents.add(stud);
          wasStudentPromoted = true;
        }
      }


      if (!wasStudentPromoted) { // used to ignore function runthrough if the student was promoted

        // get the index of the students favourite "available" project
        currentIndex = stud.rankingListTracker;

        // get that project
        firstProj = stud.preferenceList.get(currentIndex);

        fPL = firstProj.lecturer;


        // if the student's favourite project's lecturer has a worst non empty project, find it
        if (fPL.assigned!= 0) {
          wNEP = lecturersWorstNonEmptyProject(fPL, wNEP);
        }

        // Checks to see if project is full OR lecturer is full and the favourite project is the lecturer's worst non empty project
        if (((firstProj.unpromoted.size() +firstProj.promoted.size()) == firstProj.capacity || (fPL.assigned == fPL.capacity && wNEP == firstProj))){

          // if student is unpromoted or there is no unpromoted student assigned to firstProj
          if (!stud.promoted || firstProj.unpromoted.size()==0){

            // reject student and find their next favourite project
            stud.preferenceList.set(currentIndex, emptyProject);
            findNextFavouriteProject(stud);

          } else {
            // get random unpromoted student from the project's currently assigned students
            Student removeStudent = firstProj.unpromoted.get(randomStudent.nextInt(firstProj.unpromoted.size()));
            assignedStudents.remove(removeStudent);
            unassigned.add(removeStudent);

            // set the project is the remove students list to be essentially -1
            removeStudent.preferenceList.set(removeStudent.rankingListTracker,emptyProject);
            findNextFavouriteProject(removeStudent);

            // add the student to the list of promoted students assigned to this project
            firstProj.promoted.add(stud);
          }
        } else {
          // if the first projects lecturer has a worst non empty project
          if (fPL.assigned != 0) {

           // if the first projects lecturer is full and they prefer the worst non empty project the students favourite
           if (fPL.assigned == fPL.capacity && fPL.rankingList[fPL.projects.indexOf(wNEP)] < fPL.rankingList[fPL.projects.indexOf(firstProj)]){

               // Reject student
               stud.preferenceList.set(currentIndex, emptyProject);
              findNextFavouriteProject(stud);
            } else {

              // otherwise assign the student to the project
              assignStudentToProj(stud, firstProj, fPL, wNEP);
            }

          } else { // if the lecturer has no project with an assigned student

            if (fPL.assigned==fPL.capacity) {

              // reject student
              stud.preferenceList.set(currentIndex, emptyProject);
              findNextFavouriteProject(stud);
            } else {

              //assign student
              assignStudentToProj(stud, firstProj, fPL, wNEP);
            }
          }
        }
      }
    }
  }

  void assignStudentToProj(Student stud, Project firstProj, Lecturer fPL, Project wNEP){
    stud.proj = firstProj;
    firstProj.unpromoted.add(stud);
    assignedStudents.add(stud);
    unassigned.remove(stud);
    fPL.assigned++;
    // if lecturer is oversubscribed
    if (fPL.assigned > fPL.capacity) {
      removeStudentFromArrayList(fPL, wNEP);
    }
  }

}