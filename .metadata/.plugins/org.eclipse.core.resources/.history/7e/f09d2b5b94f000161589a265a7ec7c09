import java.util.ArrayList;
import java.util.Random;

public class Approx extends Algorithm{

	public Approx() {
		super();
	}
	
  	public Approx(Algorithm algorithm) {
  		this.assignedStudents = new ArrayList<Student>(algorithm.assignedStudents);
  		this.projects = new ArrayList<Project>(algorithm.projects);
  		this.lecturers = new ArrayList<Lecturer>(algorithm.lecturers);
  		this.unassigned = new ArrayList<Student>(algorithm.unassigned);
  		this.emptyProject = new Project("empty");
  		this.projectlessStudents = new ArrayList<Student>(algorithm.projectlessStudents);
  		this.untouchedStudents = new ArrayList<Student>(algorithm.untouchedStudents);
  		this.s = new StabilityChecker(this);
  		//testing variables
  		
	}
  	public Approx(Algorithm algorithm, boolean cloning) throws CloneNotSupportedException {
  		this.assignedStudents = new ArrayList<Student>();
  	  for (Student s : algorithm.assignedStudents) {
  		  Student new1 = (Student) s.clone();
  		  this.assignedStudents.add(new1);
  		  new1.preferenceList = new ArrayList<Project>();
  		  new1.untouchedPreferenceList = new ArrayList<Project>();
  		  for (Project p : s.preferenceList)
  			  new1.preferenceList.add((Project) p.clone());
  		  for (Project p : s.untouchedPreferenceList)
  			  new1.untouchedPreferenceList.add((Project) p.clone());
  	  }
  	  
  	  this.unassigned = new ArrayList<Student>();
  	  for (Student s : algorithm.unassigned) {
  		  Student new1 = (Student) s.clone();
  		  this.unassigned.add(new1);
  		  new1.preferenceList = new ArrayList<Project>();
  		  new1.untouchedPreferenceList = new ArrayList<Project>();
  		  for (Project p : s.preferenceList)
  			  new1.preferenceList.add((Project) p.clone());
  		  for (Project p : s.untouchedPreferenceList)
  			  new1.untouchedPreferenceList.add((Project) p.clone());
  	  }
  	  
  	  this.projectlessStudents = new ArrayList<Student>();
  	  for (Student s : algorithm.projectlessStudents) {
  		  Student new1 = (Student) s.clone();
  		  this.projectlessStudents.add(new1);
  		  new1.preferenceList = new ArrayList<Project>();
  		  new1.untouchedPreferenceList = new ArrayList<Project>();
  		  for (Project p : s.preferenceList)
  			  new1.preferenceList.add((Project) p.clone());
  		  for (Project p : s.untouchedPreferenceList)
  			  new1.untouchedPreferenceList.add((Project) p.clone());
  	  }
  	  
  	  this.untouchedStudents = new ArrayList<Student>();
  	  for (Student s : algorithm.untouchedStudents) {
  		  Student new1 = (Student) s.clone();
  		  this.untouchedStudents.add(new1);
  		  new1.preferenceList = new ArrayList<Project>();
  		  new1.untouchedPreferenceList = new ArrayList<Project>();
  		  for (Project p : s.preferenceList)
  			  new1.preferenceList.add((Project) p.clone());
  		  for (Project p : s.untouchedPreferenceList)
  			  new1.untouchedPreferenceList.add((Project) p.clone());
  	  }
  	  
  	  this.projects = new ArrayList<Project>();
  	  for (Project s : algorithm.projects){
  		  Project new1 = (Project) s.clone();
  		  this.projects.add(new1);
  		  new1.unpromoted = new ArrayList<Student>();
  		  new1.promoted = new ArrayList<Student>();
  		  for (Student p: s.unpromoted) 
  			  new1.unpromoted.add((Student) p.clone());
  		  
  		  for (Student p: s.unpromoted) 
  			  new1.promoted.add((Student) p.clone());
  		  
  	  }
  	  
  	  this.lecturers = new ArrayList<Lecturer>();
  	  for (Lecturer s : algorithm.lecturers) {
  		  Lecturer new1 = (Lecturer) s.clone();
  		  this.lecturers.add(new1);
  		  new1.projects = new ArrayList<Project>();
  		  for (Project p : s.projects)
  			  new1.projects.add((Project) p.clone());
		  for (int i = 0; i < s.rankingList.length; i++) 
			  new1.rankingList[i] = s.rankingList[i];
  	  }

  	  this.emptyProject = new Project("empty");
  	  this.s = new StabilityChecker(this);
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
			//System.out.println(student.rankingListTracker);
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

				// temporarily set project as students assigned project
				
				student.proj = studentsFirstProject;
				studentsFirstProject.unpromoted.add(student);
				assignedStudents.add(student);
				unassigned.remove(student);
				fPL.assigned++;

				lecturersWorstNonEmptyProject = lecturersWorstNonEmptyProject(fPL, lecturersWorstNonEmptyProject);
				
				if (fPL.assigned > fPL.capacity) { // if lecturer is over subscribed
					
					Random random = new Random();
					int removeInt = random.nextInt((lecturersWorstNonEmptyProject.unpromoted.size()));
				
					if (removeInt != 0) {
						removeInt--; // allows access to each student
					}
					
					// remove a random student from the lecturersWorstNonEmptyProject
					Student removeStudent = lecturersWorstNonEmptyProject.unpromoted.get(removeInt);
					lecturersWorstNonEmptyProject.unpromoted.remove(removeStudent);
					removeStudent.proj = null;
					System.out.println("remove studewnt name " + removeStudent.name);
					removeStudent.preferenceList.set(removeStudent.preferenceList.indexOf(lecturersWorstNonEmptyProject), emptyProject);

					findNextFavouriteProject(removeStudent);

					if (removeStudent.rankingListTracker != -1){	//if they dont only have rejected projects
						unassigned.add(removeStudent);
					}

					assignedStudents.remove(removeStudent);
					fPL.assigned--;
				}
		
				if (fPL.capacity == fPL.assigned) {
					
					for (int i = (fPL.projects.indexOf(lecturersWorstNonEmptyProject)+1); i < fPL.projects.size(); i++){
						redundantProject = fPL.projects.get(i);
						// for each student remove from their preferenceList if they have it
						for (Student s:unassigned) {

							// causing concurrent modification access error
							// so have to track location of redundant project and remove it after
							int location = -1;
	
							for (int j = 0; j < s.preferenceList.size(); j++) {
								if(s.preferenceList.get(j)==redundantProject)
									location = j;
									j=s.preferenceList.size();
							}

							if (location!= -1) {
								s.preferenceList.set(location, emptyProject);
								findNextFavouriteProject(s);
							}
					}
						
					for (Student s:assignedStudents) {

						// causing concurrent modification access error
						// so have to track location of redundant project and remove it after
						int location = -1;

						for (int j = 0; j < s.preferenceList.size(); j++) {
							if(s.preferenceList.get(j)==redundantProject)
								location = j;
								j=s.preferenceList.size();
						}

						if (location!= -1) {
							if (s.preferenceList.get(location)!= s.proj){
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
}
