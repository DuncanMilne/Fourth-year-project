import java.util.ArrayList;

public class StabilityChecker {

  Algorithm algorithm;

  public StabilityChecker(Algorithm algorithm) {
    this.algorithm = algorithm;
  }

  protected void stabilityChecker(ArrayList<Student> assignedStudents, Project emptyProject) {
    /* BLOCKING COALITIONS */
    Digraph digraph = new Digraph();
    for (Student s:assignedStudents) {
      digraph.add(s);			// add al students as nodes
    }
    for (Student s:assignedStudents) {			// for every student add edges to other students who have a preferable project
      for (int p = 0; p < s.rankingList.length; p++) {	// for every project they have
          if (s.rankingList[p] < s.untouchedPreferenceList.indexOf(s.proj)) { //if student prefers this project and the project is not empty and
            for (Student x:assignedStudents) {	 // make di edge from current student to all currently assigned
              if (x.proj == s.untouchedPreferenceList.get(p) && x.proj != emptyProject) {
            	  digraph.add(s, x);
              }
            }
        }
      }
    }
    //	System.out.println("current graph " + digraph);
    if (digraph.isDag()){
      //System.out.println("The graph is a dag");
      if (algorithm.instances == 1){ // if there is only one instance requested, print the instance
    	  algorithm.printInstance(0);
      }
      //algorithm.printInstance(0);
      //System.out.println("dag");
    } else {
      System.out.println("The graph is not a dag");
      algorithm.printInstance(0);
      System.exit(0);
    }
  }

  void checkAssignedStudentsForBlockingPairs(ArrayList<Student> assignedStudents){
    Project currentProj;
    Project lecturersWorstNonEmptyProject=null;
    int rLT; // finds rating of current project
    /* BLOCKING PAIRS */
    for (Student s:assignedStudents) {
      rLT = s.rankingList[s.rankingListTracker];
      for (int p = 0; p < s.rankingList.length; p++) {
        if (s.rankingList[p] < rLT &&  s.rankingListTracker != p) { //finds all preferred projects by student
          currentProj = s.untouchedPreferenceList.get(p);
          if (currentProj.capacity != currentProj.unpromoted.size()){
            if (currentProj.lecturer.capacity == currentProj.lecturer.assigned) {
              //currently just use location in lecturers list of projects to determine which they prefers #TODO FIX
              lecturersWorstNonEmptyProject = Algorithm.lecturersWorstNonEmptyProject(currentProj.lecturer, currentProj);
              if (currentProj.lecturer.projects.indexOf(currentProj) < currentProj.lecturer.projects.indexOf(lecturersWorstNonEmptyProject)) {
                System.out.println("ERROR: Assigned student with full teacher who prefers this project");
                algorithm.printInstance(0);
              }
            } else {
              //System.out.println("ERROR: Assigned student with under capacity teacher");
              algorithm.printInstance(0);

            }
          }

        }
      }
    }
  }


	void checkUnassignedStudentsForBlockingPairs(ArrayList<Student> unassignedStudents){
	  Project currentProj;
	  Project lecturersWorstNonEmptyProject=null;

		for (Student s:unassignedStudents) {
			for (int p = 0; p<s.untouchedPreferenceList.size(); p++)
				if (s.untouchedPreferenceList.get(p) != algorithm.emptyProject) {
					currentProj = s.untouchedPreferenceList.get(p);
					if (currentProj.lecturer.capacity == currentProj.lecturer.assigned) {
						//currently just use location in lecturers list of projects to determine which they prefers #TODO FIX
						lecturersWorstNonEmptyProject = Algorithm.lecturersWorstNonEmptyProject(currentProj.lecturer, currentProj);
						if (currentProj.lecturer.projects.indexOf(currentProj) < currentProj.lecturer.projects.indexOf(lecturersWorstNonEmptyProject)) {
							System.out.println("ERROR: unassigned student with full teacher who prefers this project");
							algorithm.printInstance(0);
						}
					} else {
						System.out.println("ERROR: unassigned student with under capacity teacher");
						algorithm.printInstance(0);
					}
					// check if lecturer prefers this project to any non empty proects or if lecturer is not full
				}
			}
  }
	
	void IProgrammingBlockingPairs(ArrayList<Student> students) {
		Project currentProj;

		for (Student s:students) {
			if (s.proj!=algorithm.emptyProject){
				for (int p = 0; p < s.untouchedPreferenceList.indexOf(s.proj); p++){
					currentProj = s.untouchedPreferenceList.get(p);
					if (currentProj.capacity > currentProj.unpromoted.size()){
						if (s.proj.lecturer.projects.indexOf(s.proj) > currentProj.lecturer.projects.indexOf(currentProj) && s.proj.lecturer == currentProj.lecturer){		// if the lecturer supervises both projects, and prefers one that the student also prefers
							algorithm.printInstance(1);
							System.out.println("DOES NOT COMPUTE, blocking pair condition 3a has been TRIGGERED"); // 3a fails
							System.exit(1);
						}
					}
					
				}
			} else {
				//if unassigned iterate through them all
				// irrelevant atm cos we're just looking @ 3a.
				/*for (Project p:s.preferenceList) {
					if (p.capacity > p.unpromoted.size()){
						System.out.println(s.proj.lecturer.name);
						if (s.proj.lecturer.projects.indexOf(s.proj) < p.lecturer.projects.indexOf(p)){
							System.out.println("DOES NOT COMPUTE"); // 3a fails
						}
					}
				}*/
			}
		}
	}
}

/*if (currentProj.lecturer.capacity == currentProj.lecturer.assigned) {
					//currently just use location in lecturers list of projects to determine which they prefers #TODO FIX
					lecturersWorstNonEmptyProject = Algorithm.lecturersWorstNonEmptyProject(currentProj.lecturer, currentProj);
					if (currentProj.lecturer.projects.indexOf(currentProj) < currentProj.lecturer.projects.indexOf(lecturersWorstNonEmptyProject)) {
						System.out.println("ERROR: unassigned student with full teacher who prefers this project");
						algorithm.printInstance(0);
					}
				} else {
					System.out.println("ERROR: unassigned student with under capacity teacher");
					algorithm.printInstance(0);
				*/
