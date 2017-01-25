import java.util.ArrayList;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class GurobiModel extends Algorithm {

    /** <p>The gurobi environment.</p> */
    GRBEnv env;
    /** <p>The gurobi model.</p> */
    GRBModel grbmodel;
    
    boolean feasible = true;
	
    public GurobiModel() {
		super();
        try {
			env = new GRBEnv();
	        grbmodel = new GRBModel(env);
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	public void assignConstraints(Algorithm a) throws GRBException {

	    //grbmodel.getEnv().set(GRB.IntParam.OutputFlag, 0);
	        
		upperLowerConstraints(a);
			
		addMaxSizeConstraint(a);
		
		blockingCoalitionConstraints(a);
		
        grbmodel.optimize();

	   	 for (Student s:a.untouchedStudents) {
	   		 for (GRBVar var: s.envyList) {
	   			 System.out.println(var.get(GRB.DoubleAttr.X));
	   		 }
	   	 }
	   	 
        int status = grbmodel.get(GRB.IntAttr.Status);
        
        if (status != GRB.Status.OPTIMAL) {
            feasible = false;
            System.out.println("no solution found in the following instance:");
        }
        else {
            setStudentAssignments(a);
        }
        

        // write model then dispose of model and environment
        //m.setInfoString(infoString);
        //grbmodel.write("SPA_IP_HR.lp");
        grbmodel.dispose();
        env.dispose();    
	}
	
	/**
    * <p>Adds upper and lower quota constraints to projects and lecturers, and student upper quota.</p>
    */
    private void upperLowerConstraints(Algorithm a) throws GRBException {
        // ----------------------------------------------------------------------------------------
        // each student is matched to 1 or less projects
    	
        for (Student s:a.untouchedStudents) {
            GRBLinExpr sumVarsForStudent = new GRBLinExpr();
            // could do for each student
        	for (Project p: s.preferenceList) {
        		GRBVar v = grbmodel.addVar(0.0, 1.0, 0.0, GRB.BINARY, "pref" + p.name);
        		s.grbvars.add(v);
        		sumVarsForStudent.addTerm(1, v);
        	}
            // each student is matched to 1 or less projects
            grbmodel.addConstr(sumVarsForStudent, GRB.LESS_EQUAL, 1.0, "ConstraintStudent " + s.name); //can change this to be less than or equal to the capacity of each project
        }
     
        // ----------------------------------------------------------------------------------------
        // for each project 
        for (Project p: a.projects) {
            GRBLinExpr numStudentsForProj = new GRBLinExpr();
            // for every student, if this project is in their pref list, add term
            for (Student s: a.untouchedStudents) {
            	if (s.preferenceList.contains(p)){
            		numStudentsForProj.addTerm(1,s.grbvars.get(s.preferenceList.indexOf(p)));
            	}
            }
            // The number of students a project has must be less than or equal to the max capacity
            grbmodel.addConstr(numStudentsForProj, GRB.LESS_EQUAL, (double) p.capacity, "ConstraintProjectUQ" + p.name); //need to set this to be the capacity of each project   
        }
        
        
        // ----------------------------------------------------------------------------------------
        // for each lecturer
        int x = 0;
        for (Lecturer l: a.testLecturers) {
            GRBLinExpr numStudentsForLect = new GRBLinExpr();    
            for (Student s:a.untouchedStudents) {
            	for (Project p: s.preferenceList) {
            		if (l.projects.contains(p)){
                		numStudentsForLect.addTerm(1, s.grbvars.get(x));
            		}
            		x++;
            	}
           	 	x=0;
            }
            grbmodel.addConstr(numStudentsForLect, GRB.LESS_EQUAL, (double) l.capacity, "ConstraintLecturerUQ" + l.name);
        }

    }

    /**
     * <p>Optimises on the maximum size and adds relevant constraint.</p>
     */
     public void addMaxSizeConstraint(Algorithm a) throws GRBException {
     	 GRBLinExpr sumAllVariables = new GRBLinExpr();
          for (Student s: a.untouchedStudents) {
         	 for (GRBVar var: s.grbvars)
                sumAllVariables.addTerm(1, var);
          }

          grbmodel.setObjective(sumAllVariables, GRB.MAXIMIZE);
          
          /*
          grbmodel.optimize();
         
          int status = grbmodel.get(GRB.IntAttr.Status); 
          
          // if there is a feasible solution
          if (status == GRB.Status.OPTIMAL) {
              double maxSize = grbmodel.get(GRB.DoubleAttr.ObjVal);
              // add a new constraint into the model to say that the size of matching must be at least the bound found above
              grbmodel.addConstr(sumAllVariables, GRB.GREATER_EQUAL, (double) maxSize, "ConstraintSize");
          }*/
     }

     private void blockingCoalitionConstraints(Algorithm a) throws GRBException {
    	 // First we create an envy graph
    	 
    	 // all of the v variables are being set to 1 which shouldn't happen because everyone shouldnt envy everyone
    	 
    	 for (Student i1:a.untouchedStudents){
    		 for (Student i2: a.untouchedStudents) {
    			 if (i1!=i2){
    				 GRBVar v = grbmodel.addVar(0.0, 1.0, 0.0, GRB.BINARY, "envy " + i2.name);  
    				 v.set(GRB.DoubleAttr.Start, 0.0);
    				 i1.envyList.add(v);
    				 for (Project j1:i1.preferenceList) {
    					 // for every project i1 prefers to j1
    					 for (int x = 0; x < i1.preferenceList.indexOf(j1); x++) {
        					 Project j2 = i1.preferenceList.get(x);
    						 if (i2.preferenceList.contains(j2)) { //if i2 likes project j2
    					     	 GRBLinExpr lhs = new GRBLinExpr();
    							 GRBLinExpr rhs = new GRBLinExpr();
    					     	 lhs.addConstant(1.0);
    					     	 lhs.addTerm(1, v);
    					     	 rhs.addTerm(1, i1.grbvars.get(i1.preferenceList.indexOf(j1)));
    					     	 rhs.addTerm(1, i2.grbvars.get(i2.preferenceList.indexOf(j2)));	
    					     	 grbmodel.addConstr(lhs, GRB.GREATER_EQUAL, rhs, "myconstraint"); // this will set v to be 1 for any student that envies any other
    						 }
    					 }
    				 }
    			 }
    		 }	
    	 }
     }
     
    // duncans comment - the assignments have already been chosen by the constraints, this is just setting them
    public void setStudentAssignments(Algorithm a) throws GRBException {
    	// ready to save the assigned students to the studentAssignments array in the model


        // set the student assignments
        for (int x = 0; x < a.untouchedStudents.size(); x++) {
        	Student s = a.untouchedStudents.get(x);
            int prefLength = s.preferenceList.size();
            boolean matched = false;
            // for every preference of current student
            for (int projInd = 0; projInd < prefLength; projInd++) {
                double resultPref = s.grbvars.get(projInd).get(GRB.DoubleAttr.X);
                if (resultPref > 0.5) {
                    s.proj = s.preferenceList.get(projInd); 
                    matched = true;
                }
            }
            if (!matched){
                s.proj = emptyProject;
            }
        }
    }
}


















