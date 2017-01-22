package IPProgrammingExamples;
import java.io.*;
import java.util.*;

 /**
 *	This class runs the SPA gurobi program over all files in the given directory and outputs results to file. </p>
 *
 * @author Frances
 */

public class Main2 {


 	/**
 	* <p> Main method. Processes all files. </p>
 	*/
 	public static void main(String args[]) {
 		String input = System.getProperty("user.dir") + "\\" + args[0];
 		File f = new File(input);
 		processFile(f);	
 	}

 	
 	/**
 	* <p> Processes a file. Creates a model object and processes it, saving the results to file. </p>
 	* @param file 		the file to process
 	* @param the directory to save results to
 	*/
 	public static void processFile(File file) {

 		// create the model letting the readFile method know whether to expect lower quotas in the input file
 		Model model = UtilCHAT_FileIO.readFile(file);
 		if (model == null) {
 			System.out.println("** error: the file " + file.getName() + " is incompatable");
 		}

 		model.printModel();

 		// run the IP 
 		SPA_IP_HR MIP = new SPA_IP_HR(model);
 		String raw = model.getRawResults();
 		String all = model.getAllResults();
 		System.out.println(raw);
 		System.out.println(all);
 	}	
}
