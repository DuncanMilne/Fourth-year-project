import java.util.ArrayList;

public class StabilityChecker {

  Algorithm algorithm;

  public StabilityChecker(Algorithm algorithm) {
    this.algorithm = algorithm;
  }

  protected void stabilityChecker(ArrayList<Student> assignedStudents, ArrayList<Student> unassignedStudents, Project emptyProject) {
    /* BLOCKING COALITIONS */
    Digraph<Student> digraph = new Digraph<Student>();
    int rLT; // finds rating of current project
    for (Student s:assignedStudents) {
      digraph.add(s);			// add al students as nodes
    }
    for (Student s:assignedStudents) {			// for every student add edges to other students who have a preferable project
      rLT = s.rankingList[s.rankingListTracker];
      for (int p = 0; p < s.rankingList.length; p++) {
          if (s.rankingList[p] < rLT && s.preferenceList.get(p) != emptyProject) { //if student prefers this project and the project is not empty and
            for (Student x:s.preferenceList.get(p).currentlyAssignedStudents) {	 // make di edge from current student to all currently assigned
              digraph.add(s, x);
            }
        }
      }
    }
    //	System.out.println("current graph " + digraph);
    if (digraph.isDag()){
      System.out.println("The graph is a dag");
      //algorithm.printInstance();
    } else {
      System.out.println("The graph is not a dag");
      algorithm.printInstance();
    }
    // unassignedStudents prefer all of their projects
  }

  void checkAssignedStudentsForBlockingPairs(ArrayList<Student> assignedStudents){
    Project currentProj;
    Project lecturersWorstNonEmptyProject=null;
    int rLT; // finds rating of current project
    Project emptyProject= new Project("empty");
    /* BLOCKING PAIRS */
    for (Student s:assignedStudents) {
      rLT = s.rankingList[s.rankingListTracker];
      for (int p = 0; p < s.rankingList.length; p++) {
        if (s.rankingList[p] < rLT && s.preferenceList.get(p) != emptyProject && s.rankingListTracker != p) { //finds all preferred projects by student
          currentProj = s.preferenceList.get(p);
          if (currentProj.capacity != currentProj.currentlyAssignedStudents.size()){
            if (currentProj.lecturer.capacity == currentProj.lecturer.numberOfAssignees) {
              //currently just use location in lecturers list of projects to determine which they prefers #TODO FIX
              lecturersWorstNonEmptyProject = Algorithm.lecturersWorstNonEmptyProject(currentProj.lecturer, currentProj);
              if (currentProj.lecturer.projectList.indexOf(currentProj) < currentProj.lecturer.projectList.indexOf(lecturersWorstNonEmptyProject)) {
                System.out.println("ERROR: Assigned student with full teacher who prefers this project");
                algorithm.printInstance();
              }
            } else {
              System.out.println("ERROR: Assigned student with under capacity teacher");
              algorithm.printInstance();

            }
          }

        }
      }
    }
  }


    void checkUnassignedStudentsForBlockingPairs(ArrayList<Student> unassignedStudents){
      Project currentProj;
      Project lecturersWorstNonEmptyProject=null;

      int rLT; // finds rating of current project
      Project emptyProject= new Project("empty");
  		for (Student s:unassignedStudents) {
  			for (int p = 0; p<s.preferenceList.size(); p++)
  				if (s.preferenceList.get(p) != emptyProject) {
  					currentProj = s.preferenceList.get(p);
  					if (currentProj.lecturer.capacity == currentProj.lecturer.numberOfAssignees) {
  						//currently just use location in lecturers list of projects to determine which they prefers #TODO FIX
              lecturersWorstNonEmptyProject = Algorithm.lecturersWorstNonEmptyProject(currentProj.lecturer, currentProj);

  						if (currentProj.lecturer.projectList.indexOf(currentProj) < currentProj.lecturer.projectList.indexOf(lecturersWorstNonEmptyProject)) {

  							System.out.println("ERROR: unassigned student with full teacher who prefers this project");
                algorithm.printInstance();
  						}
  					} else {
  						System.out.println("ERROR: unassigned student with under capacity teacher");
              algorithm.printInstance();
  					}
  					// check if lecturer prefers this project to any non empty proects or if lecturer is not full
  				}
  			}
      }
}