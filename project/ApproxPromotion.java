import java.util.Random;

public class ApproxPromotion extends Algorithm{

  public ApproxPromotion() {
    super();
  }

  protected void spaPApproxPromotion() {
    // while there exists an unassigned student that has a non empty list of is unpromoted.
    // Simply check when adding back to unassigned if their list is non empty or if they are unpromoted. If unpromoted, promote them and re-add all items to their list
    // otherwise add them to projectless

    Project firstProj;
    Lecturer fPL; //first projects lecturer
    Student stud;
    int currentIndex; // used to locate students favourite 	project
    boolean wasStudentPromoted;
    Project wNEP;
    Random randomStudent = new Random();
    while (!unassigned.isEmpty()) {
      wNEP = emptyProject;
      stud = unassigned.get(randomStudent.nextInt(unassigned.size()));
      wasStudentPromoted = false;

      if (stud.rankingListTracker == -1) { // if stud has empty preference list and is not promoted
        if (!stud.promoted){
          stud.promote();
        } else {
          unassigned.remove(stud);
          projectlessStudents.add(stud);
          wasStudentPromoted = true;
        }
      }

      if (!wasStudentPromoted) { // used to ignore function runthrough if we promote the student
        currentIndex = stud.rankingListTracker;
        firstProj = stud.preferenceList.get(currentIndex);
        fPL = firstProj.lecturer;
        int wNEPIndex;
        if (fPL.assigned!= 0) {
          wNEP = lecturersWorstNonEmptyProject(fPL, wNEP);
        }
        if (((firstProj.unpromoted.size() +firstProj.promoted.size()) == firstProj.capacity || (fPL.assigned == fPL.capacity && wNEP == firstProj))){  //do check for if empty project, if empty project then lecturer has no worst non empty project
          // if student is unpromoted or there is no unpromoted student assigned to firstProj
          if (!stud.promoted || firstProj.unpromoted.size()==0){
            // reject student
            stud.preferenceList.set(currentIndex, emptyProject);
            findNextFavouriteProject(stud);
          } else {
            // get random unpromoted student from currently assigned students
            Student removeStudent = firstProj.unpromoted.get(randomStudent.nextInt(firstProj.unpromoted.size()));
            assignedStudents.remove(removeStudent);
            unassigned.add(removeStudent);
            removeStudent.preferenceList.set(removeStudent.rankingListTracker,emptyProject); //#TODO this is probably not right, not meant to change ranking lists
            findNextFavouriteProject(removeStudent);
            firstProj.promoted.add(stud);
          }
        } else {
          if (fPL.assigned != 0) {//if wNEP is not emptyProject pseudocode line16 first condition
            System.out.println(wNEP.name);
            System.out.println(fPL.projects.indexOf(wNEP));
           if (fPL.assigned == fPL.capacity && fPL.rankingList[fPL.projects.indexOf(wNEP)] < fPL.rankingList[fPL.projects.indexOf(firstProj)]){    // line 16 in pseudo code. Start from here tomorrow also try fix git.
               // lecturer is full and prefers the worst non empty project to firstProj. reject sj line 17 pseudocode
              stud.preferenceList.set(currentIndex, emptyProject);
              findNextFavouriteProject(stud);
            } else {
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
          } else { // if wNEP is emptyProject
            if (fPL.assigned==fPL.capacity) {
              // reject sj line 17 pseudocode
              stud.preferenceList.set(currentIndex, emptyProject);
              findNextFavouriteProject(stud);
            } else {
                stud.proj = firstProj;    //#TODO abstract this out
                firstProj.unpromoted.add(stud);
                assignedStudents.add(stud);
                unassigned.remove(stud);
                fPL.assigned++;
                if (fPL.assigned > fPL.capacity) {    // #TODO work out if this is needed
                  removeStudentFromArrayList(fPL, wNEP);
                }
            }
          }
    }}}
  }
}
