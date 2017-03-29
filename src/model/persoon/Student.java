//checked
package model.persoon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Student extends Persoon {
	private ArrayList<String> vakken = new ArrayList<String>();

	private int studentNummer;
	private String groepId;

	public Student(
		String pVoornaam, 
		String pTussenvoegsel, 
		String pAchternaam, 
		String pWachtwoord, 
		String pGebruikersnaam,
		int pStudentNummer) {
		super(
			pVoornaam, 
			pTussenvoegsel, 
			pAchternaam, 
			pWachtwoord, 
			pGebruikersnaam);
		this.setStudentNummer(pStudentNummer);
		this.setGroepId("");
	}

 public String getGroepId() {
    return this.groepId;
  }
 
  public void setGroepId(String pGroepId) {
    this.groepId= pGroepId;	
  }
 
	public int getStudentNummer() {
		return this.studentNummer;
	}
	
	public ArrayList<String> getVakken(String mail_Leerling){
		String csvFile = "././CSV/rooster.csv";
    BufferedReader br = null;
    String line = "";
    String cvsSplitBy = ",";

	    try {
	        br = new BufferedReader(new FileReader(csvFile));
	        while ((line = br.readLine()) != null) {
	        	

	            // use comma as separator
	            String[] vak = line.split(cvsSplitBy);
	            if(mail_Leerling.equals(vak[4])){
	            	 	String nvak = vak[3];
	 	            if (!vakken.contains(nvak)){
	 	            	vakken.add(nvak);
	 	            	System.out.println(nvak);
	 	            }
            }
	            
	            
	            
  }
} 
	    
	    catch (FileNotFoundException e) {
	    	e.printStackTrace();}
	    catch (IOException e) {
	      e.printStackTrace();}
	    
	    return vakken;
		
	}

	private void setStudentNummer(int pStudentNummer) {
		this.studentNummer = pStudentNummer;
	}
	
}
