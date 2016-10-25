import java.util.ArrayList;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Random;

// Currently creating projects and students and adding projects to students preference lists.
// Lecturers are also created
public class Algorithm {

	// various lists of randomised items
	static ArrayList<Project> testProjects = new ArrayList<Project>();
	static ArrayList<Lecturer> testLecturers = new ArrayList<Lecturer>();
	static ArrayList<Student> testStudents = new ArrayList<Student>();

	public static void main(String[] args) {
		populate(args); // args0 is number of students to generate
		assignCapacity();	//assigns capacity to the projects
		assignProjectsToLecturers(); // Associate project with a lecturer

	}


	static void assignCapacity() {
		//currently hardcode capacity as 20 but can change to parameter
			Random random = new Random();
		for (int i = 0; i< 20; i++) {
			testProjects.get(random.nextInt(testProjects.size())).capacity++;
		}
		for (int i = 0; i< 5; i++) {
		testLecturers.get(random.nextInt(testLecturers.size())).capacity++;
		}
	}

	static void populate(String[] args) {
		populateProjects(Integer.parseInt(args[0]));
		populateStudents(Integer.parseInt(args[1]));
		populateLecturers(Integer.parseInt(args[2]));
	}


	private static void populateProjects(int numberOfProjects) {
		//String[] projectNames = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o"};
		for (int i = 0; i < numberOfProjects; i++){
			testProjects.add(new Project("project " + i /*projectNames[i]*/));
		}
	}


	private static void populateStudents(int numberOfStudents) {
		for (int i = 0; i < numberOfStudents; i++){
			testStudents.add(new Student(Integer.toString(i)));
		}

		// populates student preference lists
		double random;
		Random randomProjectIndex = new Random();;
		ArrayList<Project> duplicateList;
		int rPI;
		// need to re-add projects after a student has been assigned all his projects
		for (int j = 0; j <testStudents.size(); j++){ //for each projects
			duplicateList = new ArrayList<Project>(testProjects);
			random = Math.random()*5;

			for (int i = 0; i < (random + 5) && i<10; i++) {
				rPI =  randomProjectIndex.nextInt(duplicateList.size());
				testStudents.get(j).preferenceList.add(duplicateList.get(rPI));
				duplicateList.remove(rPI);
			}
		}
	}


	private static void populateLecturers(int numberOfLecturers) {
		for (int i = 0; i < numberOfLecturers; i++){
			testLecturers.add(new Lecturer("Lecturer " + i));
		}
	}

	// for each project: assign random lecturer to project and assign project to lecturer
	private static void assignProjectsToLecturers () {
		ArrayList<Project> proj = new ArrayList<Project>(testProjects);

		// first assign each lecturer one project
		Random randomProjectIndex = new Random();
		for (int i = 0; i < testLecturers.size(); i++) {
			int randomInt = randomProjectIndex.nextInt(proj.size());
			testLecturers.get(i).projectList.add(proj.get(randomInt));
			proj.remove(randomInt);
		}

		//currently each lecturer will get 3 each because the project will simply be assigned to whoevers left
		// until we run out of projects
		Lecturer chosenLecturer;
		ArrayList<Lecturer> duplicateLecturerList = new ArrayList<Lecturer>(testLecturers);
		for (int i =0; i< proj.size() && duplicateLecturerList.size() > 0; i++){
			int randomInt = randomProjectIndex.nextInt(duplicateLecturerList.size());
			chosenLecturer = duplicateLecturerList.get(randomInt);
			chosenLecturer.projectList.add(proj.get(i));
			chosenLecturer.numberOfAssignees++;
			if (chosenLecturer.capacity == chosenLecturer.numberOfAssignees) {
				duplicateLecturerList.remove(randomInt);
			}
		}
		//print the list of lecturers and projects here
		for (Lecturer l:testLecturers) {
			System.out.println(l.name);
			for (Project p:l.projectList){
				System.out.println(p.name);
			}
		}
	}
}
