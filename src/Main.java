


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
* Copyright - This code contains a watermark for the author
* @author Khalid Belharbi
*/
public class Main {	
	
	static int LIMITE = 50 ; // the number of instances
	static double probabilty = 0.9; // if you want a random value just make it equal = -1
	final static int SCOPE = 10; // the max value of multiplicity * 
	public static void main(String[] args) {
		for(int i=0;i<LIMITE;i++)
			                 // write here the path to your xsd file (you can generate it from your ecore file, and specify the folder of output for xmi files (in my case is 'ClassDataSet') 
			write(new Parser("D:\\WORKSPACE\\DSL_workspace\\Selection_Project\\models\\Class.xsd"),new File("ClassDataSet/Class_input"+i+".xmi"));	
	}	
	public static void write(Parser p,File path) {
		 try {
		      FileWriter myWriter = new FileWriter(path);
		      myWriter.write(new String(p.txt));
		      myWriter.close();
		    } catch (IOException e) {e.printStackTrace();}		
	}
}

