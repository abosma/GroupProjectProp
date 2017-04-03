package model.les;

import model.klas.Klas;
import model.persoon.Docent;
import model.persoon.Student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Les {
	
  private LocalDateTime beginDatum;
  private LocalDateTime eindDatum;
  private String lesNaam;
  private Docent docent;
  private String lokaal;
  private Klas klas;
  private Map<String, Integer> presentieMap = new HashMap<String, Integer>();
  
  public Les(LocalDateTime bD, LocalDateTime bE, String lN, Docent doc, String lkl, Klas kl){
  	this.beginDatum = bD;
  	this.eindDatum = bE;
  	this.lesNaam = lN;
  	this.docent = doc;
  	this.lokaal = lkl;
  	this.klas = kl;
  	
		for(Student stu : klas.getStudenten()){
			presentieMap.put(stu.getGebruikersnaam(), 0);
		}
  }
  
  public void veranderPresentie(String gNaam, int presentieID){
  	if(this.presentieMap.containsKey(gNaam)){
  		this.presentieMap.put(gNaam, presentieID);
  	}
  }
  
  public Map<String, Integer> getPresentie(){
  	return this.presentieMap;
  }
  
  public ArrayList<LocalDateTime> getLesData(){
  	ArrayList<LocalDateTime> returnArray = new ArrayList<LocalDateTime>();
  	returnArray.add(this.beginDatum);
  	returnArray.add(this.eindDatum);
  	return returnArray;
  }
  
  public LocalDateTime getBeginDatum(){
  	return this.beginDatum;
  }
  
  public LocalDateTime getEindDatum(){
  	return this.eindDatum;
  }
  
  public String getNaam(){
  	return this.lesNaam;
  }
  
  public String getLokaal(){
  	return this.lokaal;
  }
  
  public Docent getDocent(){
  	return this.docent;
  }
  
  public Klas getKlas(){
  	return this.klas;
  }
  
  public String toString(){
  	return "Begindatum: " + this.beginDatum + "\n"
  			+ "Einddatum: " + this.eindDatum + "\n"
  			+ "Lesnaam: " + this.lesNaam + "\n"
  			+ "Docent: " + this.docent.getVoornaam() + "\n"
  			+ "Lokaal: " + this.lokaal + "\n"
  			+ "Klas: " + this.klas.getKlasCode() + "\n";
  }
}
