package IPProgrammingExamples;

import gurobi.*;
import java.io.*;
import java.util.*;

/**
* <p> This class contains the model unit for the SPA_IP_HR_Alt IP for solving SPA instances with student 
* and lecturer preferences. </p>
*
* @author Frances Cooper
*/
public class IP_model_unit {
	int proj, rank;
	public GRBVar studentPrefVar;



	public IP_model_unit(GRBModel grbmodel, int proj, int rank, String name) throws GRBException {
		this.proj = proj;
		this.rank = rank;
		
		studentPrefVar = grbmodel.addVar(0.0, 1.0, 0.0, GRB.BINARY, "pref" + name);  
	}
}