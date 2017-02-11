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

	ArrayList<GRBLinExpr> rhsvars = new ArrayList<GRBLinExpr>();
	ArrayList<GRBLinExpr> lhsvars = new ArrayList<GRBLinExpr>();
	ArrayList<GRBVar> UCPTracker = new ArrayList<GRBVar>();
	ArrayList<Integer> vtracker = new ArrayList<Integer>();
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
	
	public GurobiModel(Algorithm algorithm) {
  		this.projects = new ArrayList<Project>(algorithm.projects);
  		this.lecturers = new ArrayList<Lecturer>(algorithm.lecturers);
  		this.emptyProject = new Project("empty");
  		this.assignedStudents = new ArrayList<Student>(algorithm.unassigned);
  		this.untouchedStudents = new ArrayList<Student>(algorithm.untouchedStudents);
        try {
			env = new GRBEnv();
	        grbmodel = new GRBModel(env);
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	public void assignConstraints(Algorithm a) throws GRBException {

	    grbmodel.getEnv().set(GRB.IntParam.OutputFlag, 0);
	        
		upperLowerConstraints(a);
			
		addMaxSizeConstraint(a);
		
		blockingCoalitionConstraints(a);
		
		assign3aConstraints(a);
		
        grbmodel.optimize();
	   	 
        int status = grbmodel.get(GRB.IntAttr.Status);
        
        if (status != GRB.Status.OPTIMAL) {
            feasible = false;
            System.out.println("no solution found in the following instance:");
            a.printInstance(1);
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
	
	// this condition asserts that 4/4 conditions are not true, if they are that is bad so we stop that from happening
	private void assign3aConstraints(Algorithm a) throws GRBException {
		
		// first condition Si is unassigned or Si prefers Pj to M(Si)
		ArrayList<GRBVar> underCapacityProjects = new ArrayList<GRBVar>();
		
		for (Project p: a.projects) {
			GRBVar v = grbmodel.addVar(0.0, 1.0, 0.0, GRB.BINARY, p.name + " under capacity");
			v.set(GRB.DoubleAttr.Start, 0.0);
			underCapacityProjects.add(v); // Ei,i'
		}
		
		//need to make variables a b c and d for each student/project/lecturer combo. projects + lecturers will obv be matched
		

		for (Student s:assignedStudents){
			
			int i = 0; //tracks current project location in pref list
			
			for (Project p:s.preferenceList){
				
				GRBLinExpr Aijk = new GRBLinExpr();
				 	
				Aijk.addConstant(1.0);
				
				GRBLinExpr sumOf = new GRBLinExpr();
				
				// sumOf is 1 if a student has a worse project than p or student is unassigned
				// #NOTE i believe this to be working as intended... further testing needed??
				for (int j = 0; j < i; j ++) {
					sumOf.addTerm(1.0, s.grbvars.get(j));
				}
				
				Aijk.multAdd(-1.0, sumOf);
				
				GRBLinExpr BijkRHS = new GRBLinExpr();
				
				GRBLinExpr BijkLHS = new GRBLinExpr();
				
				BijkLHS.addTerm(p.capacity, underCapacityProjects.get(a.projects.indexOf(p)));
				
				BijkRHS.addConstant(p.capacity); // instead of dividing Eijk by capacity of project, times everything else by capacity of project
				
				// 1- (students assigned to projects/capacity of project
				GRBLinExpr Eijk = new GRBLinExpr();
				
				// Eijk is used to count how many students are subscribed to p
				for (Student t:assignedStudents) {
					if (t.preferenceList.contains(p)) {
						Eijk.addTerm(1.0, t.grbvars.get(t.preferenceList.indexOf(p)));
					}
				}
					
				BijkRHS.multAdd(-1.0, Eijk);
					
				grbmodel.addConstr(BijkLHS, GRB.GREATER_EQUAL, BijkRHS, "constraintname");
				
				 
				GRBLinExpr Cijk = new GRBLinExpr();
				
				// this was causing errors at some point, it shouldnt. seems to be causing errors when # lects < # projs < # studs. 6,4,2 fails
				//believe this works, not put too much thought in but seems to make sense
				for (Project q:p.lecturer.projects) {
					if (s.preferenceList.contains(q) && p!=q) {
						Cijk.addTerm(1.0, s.grbvars.get(s.preferenceList.indexOf(q)));
					}
				}

				GRBLinExpr Dijk = new GRBLinExpr();
				
				for (int j = p.lecturer.projects.indexOf(p)+1; j < p.lecturer.projects.size(); j ++) {
					Project curr = p.lecturer.projects.get(j);
					if (s.preferenceList.contains(curr)) {
						Dijk.addTerm(1.0, s.grbvars.get(s.preferenceList.indexOf(curr)));
					}
				}

				
				GRBLinExpr threeA = new GRBLinExpr();
				
				threeA.multAdd(1.0, Aijk);
				
				threeA.addTerm(1.0, underCapacityProjects.get(a.projects.indexOf(p)));
				
				threeA.multAdd(1.0, Cijk);
				
				threeA.multAdd(1.0, Dijk);

				lhsvars.add(Aijk);
				//rhsvars.add(BijkRHS);
				
				//rhsvars.add(Dijk);
				UCPTracker.add(underCapacityProjects.get(a.projects.indexOf(p)));
				grbmodel.addConstr(threeA, GRB.LESS_EQUAL, 3, "constraint 3a");
				i++;
			}
		}
	}

	/**
    * <p>Adds upper and lower quota constraints to projects and lecturers, and student upper quota.</p>
    */
    private void upperLowerConstraints(Algorithm a) throws GRBException {
        // ----------------------------------------------------------------------------------------
        // each student is matched to 1 or less projects
    	
        for (Student s:a.assignedStudents) {
            GRBLinExpr sumVarsForStudent = new GRBLinExpr();
            // could do for each student
        	for (Project p: s.preferenceList) {
        		GRBVar v = grbmodel.addVar(0.0, 1.0, 0.0, GRB.BINARY, "pref" + p.name);
        		s.grbvars.add(v);
        		sumVarsForStudent.addTerm(1, v);
        	}
            // each student is matched to 1 or less projects
            grbmodel.addConstr(sumVarsForStudent, GRB.LESS_EQUAL, 1.0, "ConstraintStudent " + s.name); 
        }
     
        // ----------------------------------------------------------------------------------------
        // for each project 
        for (Project p: a.projects) {
            GRBLinExpr numStudentsForProj = new GRBLinExpr();
            // for every student, if this project is in their pref list, add term
            for (Student s: a.assignedStudents) {
            	if (s.preferenceList.contains(p)){
            		numStudentsForProj.addTerm(1,s.grbvars.get(s.preferenceList.indexOf(p)));
            	}
            }
            // The number of students a project has must be less than or equal to the max capacity
            grbmodel.addConstr(numStudentsForProj, GRB.LESS_EQUAL, (double) p.capacity, "ConstraintProjectUQ" + p.name);
        }
        
        
        // ----------------------------------------------------------------------------------------
        // for each lecturer
        int x = 0;
        for (Lecturer l: a.lecturers) {
            GRBLinExpr numStudentsForLect = new GRBLinExpr();    
            for (Student s:a.assignedStudents) {
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
          for (Student s: a.assignedStudents) {
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
     	 
    	 for (Student i1:a.assignedStudents){
    		 for (Student i2: a.assignedStudents) {
    			 if (i1!=i2){
    				 GRBVar v = grbmodel.addVar(0.0, 1.0, 0.0, GRB.BINARY, i1 + " envies " + i2.name + " if this is 1.0");  
    				 v.set(GRB.DoubleAttr.Start, 0.0); // Ei,i'
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
    					     	 grbmodel.addConstr(lhs, GRB.GREATER_EQUAL, rhs, "creates envygraph"); // this will set v to be 1 for any student that envies any other
    					     }
    					 }
    				 }
    			 }
    		 }	
    	 }
    	 
    	 ArrayList<Integer> vertexLabels = new ArrayList<Integer>();
    	 
    	 for (int i = 0; i< a.assignedStudents.size(); i++) {
    		 //GRBVar v = grbmodel.addVar(0, assignedStudents.size(), 0, GRB.INTEGER, "label " + i1.name);
    		 vertexLabels.add(i);
    	 }
    	 
    	 int i1Index = 0;
    	 int i2Index = 0;
    	 
    	 // checks for topological ordering
    	 
    	 for (Student i1:a.assignedStudents) {
    		 for (Student i2:a.assignedStudents) { // can find vi' by getting grbvar at certain indexes
    			 if (i1 != i2){
    				 
	    			 GRBLinExpr lhs = new GRBLinExpr();
	    			 GRBLinExpr rhs = new GRBLinExpr();
	    			 
	    			 GRBLinExpr bracketedExpression = new GRBLinExpr();
	    			 bracketedExpression.addTerm(-1, i1.envyList.get(i2Index));
	    			 bracketedExpression.addConstant(1.0); 
	    			 rhs.addConstant(vertexLabels.get(a.assignedStudents.indexOf(i2)));
	    			 rhs.multAdd(a.assignedStudents.size(), bracketedExpression);

	    			 lhs.addConstant(vertexLabels.get(i1Index));
	    			 lhs.addConstant(1.0);
	    			 grbmodel.addConstr(lhs, GRB.LESS_EQUAL, rhs, "myconstraint2");
	    			 i2Index++;
    			 }
    		 }
    		 i2Index = 0;
    		 i1Index++;
    	 }
     }
     
    // duncans comment - the assignments have already been chosen by the constraints, this is just setting them
    public void setStudentAssignments(Algorithm a) throws GRBException {
    	// ready to save the assigned students to the studentAssignments array in the model

        // set the student assignments
        for (int x = 0; x < a.assignedStudents.size(); x++) {
        	Student s = a.assignedStudents.get(x);
            int prefLength = s.preferenceList.size();
            boolean matched = false;
            // for every preference of current student
            for (int projInd = 0; projInd < prefLength; projInd++) {
                double resultPref = s.grbvars.get(projInd).get(GRB.DoubleAttr.X);
                if (resultPref > 0.5) {
                    s.proj = s.preferenceList.get(projInd); 
                    matched = true;
                    s.proj.unpromoted.add(s);
                }
            }
            if (!matched){
                s.proj = emptyProject;
            }
        }
    }
}