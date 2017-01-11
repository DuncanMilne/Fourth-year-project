package IPProgrammingExamples;
import java.util.ArrayList;
/**
* <p> This class represents the model of an SPA HR instance. </p>
* <p> *** Still ti do: functions at the bottom of the class can be sped up by creating master lists ***</p>
*
* @author Frances
*/
public class Model {   
	/** <p> The number of students. </p> */
	int numStudents;
	/** <p> The number of projects. </p> */
	int numProjects;
    /** <p> The number of lecturers. </p> */
    int numLecturers;

    /** <p> The project lower quotas. </p> */
    int[] projectLowerQuotas;
    /** <p> The project capacities. </p> */
    int[] projUpperQuotas;
    /** <p> The project lecturers. </p> */
    int[] projLecturers;

    /** <p> The lecturer lower quotas. </p> */
    int[] lecturerLowerQuotas;
    /** <p> The lecturer upper quotas. </p> */
    int[] lecturerUpperQuotas;

    /** <p> The 2D array of student pref lists. studentPrefArray[i][j] = p if student i hasproject p at position j in their preference list. </p> */
    int[][] studentPrefArray;
        /** <p> The 2D array of student pref ranks. studentPrefRankArray[i][j] = r if student i hasproject p as rank r. </p> */
    int[][] studentPrefRankArray;

    /** <p> The 2D array of lecturer pref lists. studentPrefArray[i][j] = p if student i hasproject p at position j in their preference list. </p> */
    int[][] lecturerPrefArray;
    /** <p> The 2D array of lecturer pref ranks. studentPrefRankArray[i][j] = r if student i hasproject p as rank r. </p> */
    int[][] lecturerPrefRankArray;

    ArrayList<ArrayList<Integer>> lecturersProjs;

	/** <p> The student assignments. studentAssignments[i] = a if student i is assigned to project j. </p> */
	int[] studentAssignments;
    /** <p> Number of lecturer assignments. </p> */
    int[] numLecturerAssignments;

    /** <p> Whether there is a feasible solution. </p> */
    boolean feasible;
    /** <p> String hold information to be printed from the SPA_IP inc which optimisations were run. </p> */
    String infoString;
    /** <p> Maximum size of student preference list. </p> */
    int maxPrefLength;

    String timeAndDate;

    
	/**
	* <p> Constructor for the Model class - sets the instance variables. </p>
	* @param studentPrefArray		
    * @param studentPrefRankArray 
    * @param lecturerPrefArray 
    * @param lecturerPrefRankArray 
	* @param projectLowerQuotas	
	* @param projUpperQuotas	
    * @param projLecturers 
    * @param lecturerLowerQuotas 
    * @param lecturerUpperQuotas  
    * @param lecturerTargets
	*/
	public Model(int[][] studentPrefArray, int[][] studentPrefRankArray, int[][] lecturerPrefArray, int[][] lecturerPrefRankArray,
     int[] projectLowerQuotas, int[] projUpperQuotas, int[] projLecturers, int[] lecturerLowerQuotas, int[] lecturerUpperQuotas) {
		feasible = true;  // assume this model is feasible until proven otherwise

        // set the instance variables
        this.studentPrefArray = studentPrefArray;
		this.studentPrefRankArray = studentPrefRankArray;
        this.lecturerPrefArray = lecturerPrefArray;
        this.lecturerPrefRankArray = lecturerPrefRankArray;

        this.projectLowerQuotas = projectLowerQuotas;
		this.projUpperQuotas = projUpperQuotas;
        this.projLecturers = projLecturers;

        this.lecturerLowerQuotas = lecturerLowerQuotas;
        this.lecturerUpperQuotas = lecturerUpperQuotas;

		numStudents = studentPrefArray.length;
		numProjects = projectLowerQuotas.length;
        numLecturers = lecturerLowerQuotas.length;

        // get lists of projects for each lecturer
        lecturersProjs = new ArrayList<ArrayList<Integer>>();
        for (int z = 0; z < numLecturers; z++) {
            ArrayList<Integer> lecturerProj = new ArrayList<Integer>();
            for (int plIndex = 0; plIndex < projLecturers.length; plIndex++) {
                if (projLecturers[plIndex]-1 == z) {
                    lecturerProj.add(plIndex);
                }
            }
            lecturersProjs.add(lecturerProj);
        }

        // all student assignments are initially set to -1
		studentAssignments = new int[numStudents];
		for (int i = 0; i < numStudents; i++) {
			studentAssignments[i] = -1;
		}

        // set the maximum preference list length int variable
        maxPrefLength = 0;
        for (int i = 0; i < studentPrefArray.length; i++) {
            if (studentPrefArray[i].length > maxPrefLength) {
                maxPrefLength = studentPrefArray[i].length;
            }
        }
        infoString = "";
        timeAndDate = "";
	}


    /**************************** methods to return results ****************************/
    /**
    * <p>Returns a vertical string of the assigned values.</p>
    * @return a vertical string of assigned values
    */
    public String getRawResults() {
        String returnString = "";

        // student assignments
        for (int i = 0; i < numStudents; i++) {
            int project = studentAssignments[i];
            returnString += project + " ";
        }
        return returnString;
    }

    /**
    * <p> Returns a neat and user friendly version of results for the current student assignments. </p>
    * @return results for the student assignments
    */
    public String getAllResults() {

        //////////////////////////// create the assignments string ///////////////////////////
        String assignmentsString = "";

        // saves project assignments
        ArrayList<ArrayList<Integer>> projectAssignments = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < numProjects; i++) {
            projectAssignments.add(new ArrayList<Integer>());
        }

        // saves the number of lecturer assignments
        numLecturerAssignments = new int[numLecturers];

        
        // student assignments & save the students that a project is assigned
        assignmentsString += "# details of which project each student is assigned \n";
        assignmentsString += "Student_assignments:\n";
        for (int i = 0; i < numStudents; i++) {
            int studentNum = i+1;
            assignmentsString += "s_" + studentNum + ":";
            int project = studentAssignments[i];
            if (project == -1) {
                assignmentsString += " no assignment\n";
            }
            else {
                projectAssignments.get(project - 1).add(i+1);

                int pl = projLecturers[project - 1];
                assignmentsString += " p_" + project + " (l_" + pl + ")" + "\n";
            }
        }
        assignmentsString += "\n";


        // project assignments
        assignmentsString += "# details of which students each project is assigned, and the number of students\n" ;
        assignmentsString += "# assigned compared to the projects maximum capacity \n";
        assignmentsString += "Project_assignments:\n";
        for (int i = 0; i < numProjects; i++) {
            int pl = projLecturers[i];
            int projectNum = i+1;
            int numProjectAssignments = projectAssignments.get(i).size();
            assignmentsString += "p_" + projectNum + " (l_" + pl + "): ";

            if (numProjectAssignments == 0) {
                assignmentsString += "no assignment";
            }

            for (int j : projectAssignments.get(i)) {
                assignmentsString += "s_" + j + " ";
            }
            assignmentsString += "   " + numProjectAssignments + "/" + projUpperQuotas[i] + "\n";
        }
        assignmentsString += "\n";


        // lecturer assignments
        assignmentsString += "# details of which students each lecturer is assigned, and the number of students\n"; 
        assignmentsString += "# assigned compared to the lecturers maximum capacity (target in brackets) \n";
        assignmentsString += "Lecturer_assignments:\n";
        for (int i = 0; i < numLecturers; i++) {
            int lecturerNum = i + 1;
            int numLecAssignments = 0;
            assignmentsString += "l_" + lecturerNum + ": ";

            for (int proj : lecturersProjs.get(i)) {
                numLecAssignments += projectAssignments.get(proj).size();

                for (int j : projectAssignments.get(proj)) {
                    int projNum = proj + 1;
                    assignmentsString += "s_" + j + " (p_" + projNum + ") ";
                }
            }
            if (numLecAssignments == 0) {
                assignmentsString += "no assignment";
            }
            numLecturerAssignments[i] = numLecAssignments;

            assignmentsString += "   " + numLecAssignments + "/" + lecturerUpperQuotas[i] + "\n";
        }


        //////////////////////////// create the return string ///////////////////////////
        String returnString = "";

        // add information relating to which optimisations have taken place
        returnString += timeAndDate;
        returnString += infoString + "\n";

        // add further matching statistics
        int size = getMatchingSize();
        returnString += "# the number of students matched compared with the total number of students\n";
        returnString += "Matching_size: " + size + "/" + numStudents + "\n\n";

        int matchedCost = calcMatchedCost();
        returnString += "# the sum of ranks of student assignments in the matching\n";
        returnString += "Matching_cost: " + matchedCost + "\n\n";


        // add the assignments string to the return string and return
        returnString += assignmentsString;
        return returnString;
    }


    /**************************** matching statistics helper methods ****************************/

    /**
    * <p> Returns the cost of the matching for matched students only. </p>
    * @return the cost of the matching
    */
    public int calcMatchedCost() {
        int cost = 0;
        for (int i = 0; i < numStudents; i++) {
            // only consider matched students
            if (studentAssignments[i] != -1) {
                cost += getRank(i, studentAssignments[i] - 1);
            }   
        }
        return cost;
    }


    /**
    * <p> Returns the rank for a given student index and project num. </p>
    * @return the rank of the project or -1 if the project is unacceptable to the student
    */
    public int getRank(int studentInd, int projectInd) {
        int[] studentPref = studentPrefArray[studentInd];
        for (int i = 0; i < studentPref.length; i++) {
            if (studentPref[i] == projectInd) {
                return studentPrefRankArray[studentInd][i];
            }
        }
        return -1;
    }


    /**
    * <p> Returns the size of the matching. </p>
    * @return the size of the matching
    */
    protected int getMatchingSize() {
        int numMatchedStudents = 0;
        for (int i = 0; i < studentAssignments.length; i++) {
            if (studentAssignments[i] != -1) {
                numMatchedStudents++;
            }
        }

        return numMatchedStudents;
    }


    /**
    * <p> Collects the information on which optimisations were used in the IP run.</p>
    */
    public void setInfoString(String s) {
        infoString = s;
    }

    /**
    * <p> Print all arrays of the model. </p>
    */
    public void printModel() {
        System.out.println("--------------- model ---------------");
        print(studentPrefArray, "studentPrefArray");
        print(studentPrefRankArray, "studentPrefRankArray");
        print(lecturerPrefArray, "lecturerPrefArray");
        print(lecturerPrefRankArray, "lecturerPrefRankArray");
        print(projectLowerQuotas, "projectLowerQuotas");
        print(projUpperQuotas, "projUpperQuotas");
        print(projLecturers, "projLecturers");
        print(lecturerLowerQuotas, "lecturerLowerQuotas");
        print(lecturerUpperQuotas, "lecturerUpperQuotas");
        print(studentAssignments, "studentAssignments");
    }

    /**
    * <p> Print an int[][]. </p>
    * @param intArray
    * @param message
    */
    private void print(int[][] intArray, String message) {
        String s = message + "\n";
        
        for (int[] row : intArray) {
            for (int cell : row) {
                s += cell + " ";
            }
            s += "\n";
        }
        System.out.println(s);
    }


    /**
    * <p> Print an int[]. </p>
    * @param intArray
    * @param message
    */
    public void print(int[] intArray, String message) {
        String s = message + "\n";
        
       for (int cell : intArray) {
                s += cell + " ";  
        }
        s += "\n";
        System.out.println(s);
    }
}
