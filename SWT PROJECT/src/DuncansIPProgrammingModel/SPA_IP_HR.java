package DuncansIPProgrammingModel;


import gurobi.*;
import java.io.*;
import java.util.*;

/**
* <p> This class contains the SPA_IP_HR_Alt IP for solving SPA instances with student and lecturer preferences and finds a
* stable matching. </p>
*
* @author Duncan Milne, based on a model unit generously gifted by Frances Cooper
*/
public class SPA_IP_HR {
    /** <p>The model representing the SPA instance.</p> */
    Model m;
    /** <p>The number of students in the instance.</p> */                                       
    int numStudents;
    /** <p>The number of projects in the instance.</p> */
    int numProjects;
    /** <p>The number of lecturers in the instance.</p> */
    int numLecturers;

    /** <p>The gurobi environment.</p> */
    GRBEnv env;
    /** <p>The gurobi model.</p> */
    GRBModel grbmodel;

    /** <p>This array takes the form of the students preference list but has several attributes for
    each student project pair. This is an array of IP_model_unit arrays. A student project pair is when a student has the project in their preference list. </p> */
    IP_model_unit[][] unitArray;
    /** <p>projectLists stores list of vars that correspond to a given project.</p> */
    ArrayList<ArrayList<GRBVar>> projectLists;
    /** <p>projectLists stores list of vars that correspond to a given lecturer.</p> */
    ArrayList<ArrayList<IP_model_unit>> lecturerLists;

    /** <p>rankList stores list of vars that correspond to a given rank.</p> */
    ArrayList<ArrayList<GRBVar>> rankLists;

    /** <p>The cost of assignments.</p> */
    double minSumRank;
    /** <p>The number of assignments.</p> */
    double maxSize;


    /**
    * <p>The SPA_IP_HR_Alt constructor - this constructor will eventually replace the other as it allows optimisations to
    * occur in different orders.</p>
    * @param m      the model SPA instance
    */
    public SPA_IP_HR(Model m) {
        // instance variables are saved
        this.m = m;
        numStudents = m.numStudents;
        numProjects = m.numProjects;
        numLecturers = m.numLecturers;


        try {
            // the GRB model is set up and variables are added to model
            setUpGRBmodel();
            grbmodel.getEnv().set(GRB.IntParam.OutputFlag, 0);

            // add upper and lower quota constraints for projects and lecturers, and student upper quota (of 1)
            upperLowerConstraints();

            String infoString = "# Information:\n";

            // Duncan's comment to explain what I think is happening, the most important part of the matching is to find the largest possible matching, we then compare matchings of this size to find the one 
            // that the students prefer the most with "minSumStudentRanks();", we then compare any of these (there may be multiple matchings with saame size and same minsumstudentranks) to find the matching
            // the lecturers prefer the most.
            infoString += "# - Optimisation: finds a maximum sized matching\n";
            addMaxSizeConstraint();
             
            infoString += "# - Optimisation: finds a minimum sum of student ranks\n";
            minSumStudentRanks();        

            infoString += "# - Optimisation: finds a minimum sum of lecturer ranks\n";
            minSumLecturerRanks();

            grbmodel.optimize();
            int status = grbmodel.get(GRB.IntAttr.Status);
            if (status != GRB.Status.OPTIMAL) {
                m.feasible = false;
                System.out.println("no solution found in the following instance:");
            }
            else {
                setStudentAssignments();
            }
            

            // write model then dispose of model and environment
            m.setInfoString(infoString);
            //grbmodel.write("SPA_IP_HR.lp");
            grbmodel.dispose();
            env.dispose();
        }

        catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
        }  
    }


    /**
    * <p>Sets up the current GRB model.</p>
    */
    private void setUpGRBmodel() throws GRBException {
        // create the GRBmodel
        env = new GRBEnv();
        grbmodel = new GRBModel(env);

        // lecturerLists stores list of vars that correspond to a given lecturer 
        lecturerLists = new ArrayList<ArrayList<IP_model_unit>>();
        for (int z = 0; z < numLecturers; z++) {
            lecturerLists.add(new ArrayList<IP_model_unit>());
        }

        // projectLists stores list of vars that correspond to a given project 
        projectLists = new ArrayList<ArrayList<GRBVar>>(); 
        for (int x = 0; x < numProjects; x++) {
            projectLists.add(new ArrayList<GRBVar>());
        }

        // rankList stores list of vars that correspond to a given rank 
        rankLists = new ArrayList<ArrayList<GRBVar>>(); 
        for (int x = 0; x < numProjects+1; x++) {
            rankLists.add(new ArrayList<GRBVar>());
        }

        // set up the unitArray and add to project and lecturer lists as go along
        unitArray = new IP_model_unit[numStudents][];

        // for each student
        for (int x = 0; x < numStudents; x++) {
            int[] prefList = m.studentPrefArray[x];
            
            // unitArrayRow is a list of IP_model_units for every project in students preference list
            IP_model_unit[] unitArrayRow = new IP_model_unit[prefList.length];

            // for each student project pair
            for (int i = 0; i < prefList.length; i++) {

                // create a new unit for each student-project pair
                unitArrayRow[i] = new IP_model_unit(grbmodel, m.studentPrefArray[x][i], m.studentPrefRankArray[x][i], "[" + x + "][" + i + "]");

                // add this GRB var to the arraylist which holds variables of this rank
                int rank = m.studentPrefRankArray[x][i];
                ArrayList<GRBVar> rankList = rankLists.get(rank - 1);
                rankList.add(unitArrayRow[i].studentPrefVar);

                // add the variable to the appropriate project list 
                int projNo = prefList[i];
                ArrayList<GRBVar> projList = projectLists.get(projNo);
                projList.add(unitArrayRow[i].studentPrefVar);

                // add the variable to the appropriate lecturer list 
                int currentLec = m.projLecturers[projNo];
                ArrayList<IP_model_unit> lecList = lecturerLists.get(currentLec - 1);
                lecList.add(unitArrayRow[i]);
            }
            unitArray[x] = unitArrayRow;
        }

        // Integrate variables into model
        grbmodel.update();
    }


    /**
    * <p>Adds upper constraints to projects, lecturers and students.</p>
    */
    private void upperLowerConstraints() throws GRBException {
        // ----------------------------------------------------------------------------------------
        // each student is matched to 1 or less projects
        for (int x = 0; x < numStudents; x++) {
            // get linear expression for the sum of all variables for a student
            GRBLinExpr sumVarsForStudent = new GRBLinExpr();
            for (int y = 0; y < unitArray[x].length; y++) {
                sumVarsForStudent.addTerm(1, unitArray[x][y].studentPrefVar);
            }
            // each student is matched to 1 or less projects
            grbmodel.addConstr(sumVarsForStudent, GRB.LESS_EQUAL, 1.0, "ConstraintStudent" + x);
        }

        // ----------------------------------------------------------------------------------------
        // for each project
        for (int y = 0; y < numProjects; y++) {
            // get linear expressions for the sum of variables for this project
            ArrayList<GRBVar> projList = projectLists.get(y);
            GRBLinExpr numStudentsForProj = new GRBLinExpr();
            for (int p = 0; p < projList.size(); p++) {
                numStudentsForProj.addTerm(1, projList.get(p));
            }

            // The number of students a project has must be less than or equal to the max capacity
            grbmodel.addConstr(numStudentsForProj, GRB.LESS_EQUAL, (double) m.projUpperQuotas[y], "ConstraintProjectUQ" + y);
            
        }

        // ----------------------------------------------------------------------------------------
        // for each lecturer 
        for (int z = 0; z < numLecturers; z++) {
            // get a linear expression for the sum of variables for this lecturer
            ArrayList<IP_model_unit> lecList = lecturerLists.get(z);
            GRBLinExpr numStudentsForLect = new GRBLinExpr();
            for (int var = 0; var < lecList.size(); var++) {  
                numStudentsForLect.addTerm(1, lecList.get(var).studentPrefVar);
            }

            // The number of students a lecturer has must be less than or equal to the max capacity
            grbmodel.addConstr(numStudentsForLect, GRB.LESS_EQUAL, (double) m.lecturerUpperQuotas[z], "ConstraintLecturerUQ" + z);
        }
    }


    /**
    * <p>Optimises on the maximum size and adds relevant constraint.</p>
    */
    public void addMaxSizeConstraint() throws GRBException {
        GRBLinExpr sumAllVariables = new GRBLinExpr();
        for (int x = 0; x < numStudents; x++) {
            for (int projInd = 0; projInd < unitArray[x].length; projInd++) {
                sumAllVariables.addTerm(1, unitArray[x][projInd].studentPrefVar);
            }
        }

        grbmodel.setObjective(sumAllVariables, GRB.MAXIMIZE);
        grbmodel.optimize();
        int status = grbmodel.get(GRB.IntAttr.Status); 

        // if there is a feasible solution
        if (status == GRB.Status.OPTIMAL) {
            maxSize = grbmodel.get(GRB.DoubleAttr.ObjVal);

            // add a new constraint into the model to say that the size of matching must be at least the bound found above
            grbmodel.addConstr(sumAllVariables, GRB.GREATER_EQUAL, (double) maxSize, "ConstraintSize");

        }
    }


    /**
    * <p>Optimises on the minimum student cost and adds relevant constraint.</p>
    */
    public void minSumStudentRanks() throws GRBException {
        GRBLinExpr sumAllRanks = new GRBLinExpr();
        //iterate through students
        for (int x = 0; x < numStudents; x++) {
        	//iterate through student project pairs
            for (int projInd = 0; projInd < unitArray[x].length; projInd++) {
            	// m.studentPrefRankArray[x][projInd] will give the ranking the student gives to any project. unitArray[x][projInd].studentPrefVar will either be 0 or 1. 1 if they have proj in pref list, 0 otherwise.
                sumAllRanks.addTerm(m.studentPrefRankArray[x][projInd], unitArray[x][projInd].studentPrefVar);
            }
        }

        grbmodel.setObjective(sumAllRanks, GRB.MINIMIZE);
        grbmodel.optimize();
        int status = grbmodel.get(GRB.IntAttr.Status); 

        // if there is a feasible solution
        if (status == GRB.Status.OPTIMAL) {
            minSumRank = grbmodel.get(GRB.DoubleAttr.ObjVal);

            // add a new constraint into the model to say that the sum of ranks must be at least as small as the bound found above
            // you only need to add a constraint if you are doing further optimisations so it's not strictly 
            // necessary here.
            grbmodel.addConstr(sumAllRanks, GRB.LESS_EQUAL, (double) minSumRank, "ConstraintSumRanks");
        }
    }
    
    /**
     * <p>Optimises on the minimum lecturer cost and adds relevant constraint.</p>
     */
    public void minSumLecturerRanks() throws GRBException {
    	GRBLinExpr sumAllRanks = new GRBLinExpr();
    	
    	 //iterate through students
        for (int x = 0; x < numStudents; x++) {
        	//iterate through student project pairs
            for (int projInd = 0; projInd < unitArray[x].length; projInd++) {
            	// m.studentPrefRankArray[x][projInd] will give the ranking the student gives to any project. unitArray[x][projInd].studentPrefVar will either be 0 or 1. 1 if they have proj in pref list, 0 otherwise.
            	// the whole next two for loops are just to find the lecturers rank for the project. 
            	for (int lecturer = 0; lecturer < m.lecturerPrefArray.length; lecturer++){
            		for (int lecturersProject = 0; lecturersProject< m.lecturerPrefArray[lecturer].length; lecturersProject++){
            				if (m.lecturerPrefArray[lecturer][lecturersProject] == unitArray[x][projInd].proj) {
            					// lecturers rank for this project + either 0 or 1 depending on whether or not the student has this project in their preference list
            					sumAllRanks.addTerm(m.lecturerPrefRankArray[lecturer][lecturersProject], unitArray[x][projInd].studentPrefVar);
            				}
            		}
            	}
                //sumAllRanks.addTerm(m.lecturerPrefRankArray[][]/*lecturers rank for this project*/, unitArray[x][projInd].studentPrefVar);
                //can get project number and then find the lecturer who has that project
            }
        }
        
        grbmodel.setObjective(sumAllRanks, GRB.MINIMIZE);
        grbmodel.optimize();
        int status = grbmodel.get(GRB.IntAttr.Status); 

        // if there is a feasible solution
        if (status == GRB.Status.OPTIMAL) {
            minSumRank = grbmodel.get(GRB.DoubleAttr.ObjVal);

            // add a new constraint into the model to say that the sum of ranks must be at least as small as the bound found above
            // you only need to add a constraint if you are doing further optimisations so it's not strictly 
            // necessary here.
            grbmodel.addConstr(sumAllRanks, GRB.LESS_EQUAL, (double) minSumRank, "ConstraintSumRanks");
        }
    }
    
    /**
    * <p>Set the current student assignments in the model.</p>
    */
    public void setStudentAssignments() throws GRBException {
        // ready to save the assigned students to the studentAssignments array in the model
        int[] studentAssignments = m.studentAssignments;
        for (int x = 0; x < numStudents; x++) {
            studentAssignments[x] = -1;
        }

        // set the student assignments
        for (int x = 0; x < numStudents; x++) {
            int prefLength = unitArray[x].length;
            boolean matched = false;
            for (int prefInd = 0; prefInd < prefLength; prefInd++) {
                double resultPref = unitArray[x][prefInd].studentPrefVar.get(GRB.DoubleAttr.X);
                if (resultPref > 0.5) {
                    studentAssignments[x] = m.studentPrefArray[x][prefInd] + 1;   
                    matched = true;   
                }
            }
            if (!matched) {
                studentAssignments[x] = -1;
            }
        }
    }
}