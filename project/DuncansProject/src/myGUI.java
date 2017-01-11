

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class myGUI {

	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_5;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			myGUI window = new myGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		Button btnSpapapproxpromotion_1 = new Button(shell, SWT.CHECK);
		btnSpapapproxpromotion_1.setBounds(126, 124, 178, 16);
		btnSpapapproxpromotion_1.setText("SPA-P-APPROX-PROMOTION");
		
		Label lblNumberOfStudents = new Label(shell, SWT.NONE);
		lblNumberOfStudents.setBounds(10, 10, 144, 15);
		lblNumberOfStudents.setText("Number of Students");
		
		Label lblNumberOfProjects = new Label(shell, SWT.NONE);
		lblNumberOfProjects.setBounds(151, 10, 109, 15);
		lblNumberOfProjects.setText("Number of Projects");
		
		Label lblNumberOfLecturers = new Label(shell, SWT.NONE);
		lblNumberOfLecturers.setBounds(283, 10, 163, 15);
		lblNumberOfLecturers.setText("Number of Lecturers");
		
		Label lblAdditionalCapacityFor = new Label(shell, SWT.NONE);
		lblAdditionalCapacityFor.setBounds(10, 58, 190, 15);
		lblAdditionalCapacityFor.setText("Additional Capacity for Lecturers");
		
		Label lblAdditionalCapacityFor_1 = new Label(shell, SWT.NONE);
		lblAdditionalCapacityFor_1.setBounds(206, 58, 190, 15);
		lblAdditionalCapacityFor_1.setText("Additional Capacity for Projects");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(30, 31, 76, 21);
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(172, 31, 76, 21);
		
		text_2 = new Text(shell, SWT.BORDER);
		text_2.setBounds(293, 31, 76, 21);
		
		text_3 = new Text(shell, SWT.BORDER);
		text_3.setBounds(48, 79, 76, 21);
		
		text_4 = new Text(shell, SWT.BORDER);
		text_4.setBounds(253, 79, 76, 21);
		
		Button btnRunAlgorithm = new Button(shell, SWT.NONE);
		btnRunAlgorithm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
	
		      int numberOfStudents = Integer.parseInt(text.getText());
		      
		      int numberOfProjects = Integer.parseInt(text_1.getText());
		      
		      int numberOfLecturers = Integer.parseInt(text_2.getText());
		      
		      int lecturerCapacity = Integer.parseInt(text_3.getText());
		      
		      int projectCapacity = Integer.parseInt(text_4.getText());
		      
		      int[] arguments = new int[] {numberOfProjects, numberOfStudents, numberOfLecturers, lecturerCapacity, projectCapacity};
		      
		      int numberOfInstances = Integer.parseInt(text_5.getText());
				
		      Main main = new Main();

	          int i = 0;
	          
		      if (btnSpapapproxpromotion_1.getSelection())
		    	while(i<numberOfInstances){
		          main.go(arguments, true);
		          i++;
		    	}
		      else 
		    	  while(i<numberOfInstances){
			          main.go(arguments, true);
			          i++;
			    	}
			}
		});
		btnRunAlgorithm.setBounds(158, 226, 102, 25);
		btnRunAlgorithm.setText("Run Algorithm");
		
		Label lblHowManyTimes = new Label(shell, SWT.NONE);
		lblHowManyTimes.setBounds(93, 167, 285, 16);
		lblHowManyTimes.setText("How many times would you like the algorithm to run?");
		
		text_5 = new Text(shell, SWT.BORDER);
		text_5.setBounds(172, 189, 76, 21);
		
	}
}
