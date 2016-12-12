/*import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;*/
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

  static Algorithm algorithm;

  public static void main(String[] args) {

    Student student;
    Project project;
    Lecturer lecturer;
    Lecturer lecturer2;
    Project project2;

		Scanner standardInput = new Scanner(System.in);
		System.out.print("Enter a file name or x to generate random instances: ");
		String choice = standardInput.nextLine();

    // #NOTE for all cases project students wants to be assigned is undersubscribed
    // pass in a 1 as an argument if u want to run this version

    if (!choice.equals("x")) {
      algorithm = instanceGenerator(choice);
      createRankingLists();
      algorithm.s.stabilityChecker(algorithm.assignedStudents, algorithm.unassignedStudents, algorithm.emptyProject);
      algorithm.s.checkAssignedStudentsForBlockingPairs(algorithm.assignedStudents);
      algorithm.s.checkUnassignedStudentsForBlockingPairs(algorithm.unassignedStudents);
    } else {
      // Done so we know what the arguments actually do
      System.out.print("Enter how many instances you would like to generate ");
      int numberOfInstances = Integer.parseInt(standardInput.nextLine());

      System.out.print("Enter how many projects you would like to generate ");
      int numberOfProjects = Integer.parseInt(standardInput.nextLine());

      System.out.print("Enter how many students you would like to generate ");
      int numberOfStudents = Integer.parseInt(standardInput.nextLine());

      System.out.print("Enter how many lecturers you would like to generate ");
      int numberOfLecturers = Integer.parseInt(standardInput.nextLine());

      System.out.print("Enter how much extra capacity for lecturers you would like ");
      int lecturerCapacity = Integer.parseInt(standardInput.nextLine());

      System.out.print("Enter how much extra capacity for projects you would like ");
      int projectCapacity = Integer.parseInt(standardInput.nextLine());

      int[] arguments = new int[] {numberOfProjects, numberOfStudents, numberOfLecturers, lecturerCapacity, projectCapacity};

      if (numberOfLecturers + lecturerCapacity < numberOfProjects) {
        while (numberOfLecturers + lecturerCapacity < numberOfProjects) {
          System.out.println("Lecturer + lecturer capacity must be larger than number of projects");

          System.out.print("Enter how many projects you would like to generate ");
          numberOfProjects = Integer.parseInt(standardInput.nextLine());

          System.out.print("Enter how many lecturers you would like to generate ");
          numberOfLecturers = Integer.parseInt(standardInput.nextLine());

          System.out.print("Enter how much extra capacity for lecturers you would like ");
          lecturerCapacity = Integer.parseInt(standardInput.nextLine());
        }
      }

      int i = 0;
      while (i<numberOfInstances) {
        go(arguments);
        i++;
      }
    }
  }

  static Algorithm instanceGenerator(String fileName) {
    Algorithm algorithm = new Algorithm();
    try {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      String[] splitted = br.readLine().split(" ");
      int numProjects = Integer.parseInt(splitted[0]);
      int numStudents = Integer.parseInt(splitted[1]);
      int numLecturers = Integer.parseInt(splitted[2]);
      Student currentStudent;
      Student untouchedStudent;
      // Do projects first, track their lecturer and assign at the end
      Project currentProject;

      for (int i = 0; i < numProjects; i++) {
        splitted = br.readLine().split(" ");
        currentProject = new Project(splitted[0], Integer.parseInt(splitted[1]));
        algorithm.testProjects.add(currentProject);
      }

      // now create students
      for(int i = 0; i < numStudents; i++) {
        splitted = br.readLine().split(" "); // get each student
        currentStudent = new Student(splitted[0].substring(0, splitted[0].length()-1));
        algorithm.unassignedStudents.add(currentStudent); // creates student with new name
        untouchedStudent =  new Student(splitted[0].substring(0, splitted[0].length()-1));
        algorithm.untouchedStudents.add(untouchedStudent); // creates student with new name

        for (int j = 1; j < splitted.length; j++) {
          currentStudent.preferenceList.add(algorithm.testProjects.get(Integer.parseInt(splitted[j])-1));
          untouchedStudent.preferenceList.add(algorithm.testProjects.get(Integer.parseInt(splitted[j])-1));
        }
        currentStudent.rankingList = new int[currentStudent.preferenceList.size()];
		    for (int k = 0; k < currentStudent.preferenceList.size(); k++) {

				  currentStudent.rankingList[k] = k;  // Initially set rankings so index 0 is favourite, 1 is second favourite etc..
			  }
      }

      Lecturer currentLecturer;
      // now create lecturers
      for (int i = 0; i < numLecturers; i++) {
        splitted = br.readLine().split(" ");
        currentLecturer = new Lecturer(splitted[0].substring(0, splitted[0].length()-1), Integer.parseInt(splitted[1].substring(0, splitted[0].length()-1)));
        algorithm.testLecturers.add(currentLecturer);
        for (int j = 2; j < splitted.length; j++) {
          currentLecturer.projectList.add(algorithm.testProjects.get(Integer.parseInt(splitted[j].replace(":", ""))-1));
          algorithm.testProjects.get(Integer.parseInt(splitted[j])-1).lecturer = currentLecturer;
        }
      }

      String line;
      // add matchings
      ArrayList<Student> toBeRemoved = new ArrayList<Student>();
      Project matchingProject;
      int projectNumber;
      // #TODO if there are matches edit currentstudents rankinglIst
      while ((line = br.readLine()) != null) {
        splitted = line.split(",");
        currentStudent = algorithm.unassignedStudents.get(Integer.parseInt(splitted[0].substring(1, splitted[0].length())) - 1);
        toBeRemoved.add(currentStudent);
        projectNumber = Integer.parseInt(splitted[1].substring(0, splitted[0].length()-1))-1;
        projectNumber = currentStudent.preferenceList.indexOf(algorithm.testProjects.get(projectNumber)); // get index of project in students pref list
        matchingProject = algorithm.testProjects.get(projectNumber);
        currentStudent.currentlyAssignedProject = matchingProject;
        currentStudent.rankingListTracker = projectNumber;    // for stability checking purposes
        matchingProject.currentlyAssignedStudents.add(currentStudent);
        matchingProject.lecturer.numberOfAssignees++;
      }
      algorithm.assignedStudents.addAll(toBeRemoved);
      algorithm.unassignedStudents.removeAll(toBeRemoved);

    } catch (Exception e) {
      System.out.println("Type of error: " + e.getClass().getName() + " Error message: " + e.getMessage());
    }
    return algorithm;
  }

  static void go(int[] arguments) {
      Approx algorithm = new Approx();

      populate(arguments); // args0 is number of students to generate
      assignCapacity(arguments[3], arguments[4]);	//assigns capacity to the projects, args are lecturer capacity and project capacity
      assignProjectsToLecturers(); // Associate project with a lecturer
      algorithm.printInstance();
      algorithm.assignProjectsToStudents();
      algorithm.printInstance();
      algorithm.s.stabilityChecker(algorithm.assignedStudents, algorithm.unassignedStudents, algorithm.emptyProject);
  }

  static void assignCapacity(int lecturerCapacity, int projectCapacity) {
		//currently hardcode capacity as 20 but can change to parameter
		Random random = new Random();
		for (int i = 0; i< projectCapacity; i++) {
			algorithm.testProjects.get(random.nextInt(algorithm.testProjects.size())).capacity++;
		}
		for (int i = 0; i< lecturerCapacity; i++) {
		    algorithm.testLecturers.get(random.nextInt(algorithm.testLecturers.size())).capacity++;
		}
	}

	static void populate(int[] args) {
		populateProjects(args[0]);
		populateStudents(args[1]);
		populateLecturers(args[2]);
	}

	static void populateProjects(int numberOfProjects) {
		for (int i = 0; i < numberOfProjects; i++){
			algorithm.testProjects.add(new Project(Integer.toString(i)));
		}
	}


	static void populateStudents(int numberOfStudents) {
		for (int i = 0; i < numberOfStudents; i++){
			algorithm.unassignedStudents.add(new Student(Integer.toString(i)));
      algorithm.untouchedStudents.add(new Student(Integer.toString(i)));
		}
  		// populates student preference lists
  		double random;
  		Random randomProjectIndex = new Random();;
  		ArrayList<Project> duplicateList;
  		int rPI;
  		// need to re-add projects after a student has been assigned all his projects
  		for (int j = 0; j <algorithm.unassignedStudents.size(); j++){ //for each projects
  			duplicateList = new ArrayList<Project>(algorithm.testProjects);
  			random = Math.random()*5;

  			// need to ensure we havent removed last item from duplicate list
  			for (int i = 0; i < (random + 5) && !duplicateList.isEmpty(); i++) {
  				rPI = randomProjectIndex.nextInt(duplicateList.size());
  				algorithm.unassignedStudents.get(j).preferenceList.add(duplicateList.get(rPI));
  				algorithm.untouchedStudents.get(j).preferenceList.add(duplicateList.get(rPI));
  				duplicateList.remove(rPI);
  			}

  			// create ranking list for student
  		}
      createRankingLists();
  	}

    static void populateLecturers(int numberOfLecturers) {
      for (int i = 0; i < numberOfLecturers; i++){
        algorithm.testLecturers.add(new Lecturer(Integer.toString(i)));
      }
    }


  	// for each project: assign random lecturer to project and assign project to lecturer
  	static void assignProjectsToLecturers () {
  		ArrayList<Project> proj = new ArrayList<Project>(algorithm.testProjects);

  		// first assign each lecturer one project
  		Random randomProjectIndex = new Random();
  		for (int i = 0; i < algorithm.testLecturers.size() && proj.size()>0; i++) {
  			int randomInt = randomProjectIndex.nextInt(proj.size());
  			algorithm.testLecturers.get(i).projectList.add(proj.get(randomInt));
  			proj.get(randomInt).lecturer = algorithm.testLecturers.get(i);
  			proj.remove(randomInt);
  		}

  		//currently each lecturer will get 3 each because the project will simply be assigned to whoevers left
  		// until we run out of projects
  		Project chosenProject;
  		Lecturer chosenLecturer;
  		while (proj.size()>0) {
  			int randomProjInt = randomProjectIndex.nextInt(proj.size());
  			int randomLectInt = randomProjectIndex.nextInt(algorithm.testLecturers.size());
  			chosenProject = proj.get(randomProjInt);
  			chosenLecturer = algorithm.testLecturers.get(randomLectInt);
  			chosenLecturer.projectList.add(chosenProject);
  			chosenProject.lecturer = chosenLecturer;
  			proj.remove(randomProjInt);
  		}
    }

    static void createRankingLists() {

      for (Student s: algorithm.unassignedStudents) {
        s.rankingList = new int[s.preferenceList.size()];
        for (int i = 0; i < s.rankingList.length-1; i++) {
          s.rankingList[i] = i;  // Initially set rankings so index 0 is favourite, 1 is second favourite etc..
        }
      }
      for (Student s: algorithm.assignedStudents) {
        s.rankingList = new int[s.preferenceList.size()];
        for (int i = 0; i < s.rankingList.length; i++) {
          s.rankingList[i] = i;  // Initially set rankings so index 0 is favourite, 1 is second favourite etc..
        }
      }
    }
}