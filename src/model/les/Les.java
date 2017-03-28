package model.les;

import model.klas.Klas;
import model.persoon.Docent;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Les {
	
  private LocalDateTime beginData;
  private LocalDateTime eindData;
  private String lesNaam;
  private Docent docent;
  private String lokaal;
  private Klas klas;
  
  public Les(LocalDateTime bD, LocalDateTime bE, String lN, Docent doc, String lkl, Klas kl){
  	this.beginData = bD;
  	this.eindData = bE;
  	this.lesNaam = lN;
  	this.docent = doc;
  	this.lokaal = lkl;
  	this.klas = kl;
  }
  
  public ArrayList<LocalDateTime> getLesData(){
  	ArrayList<LocalDateTime> returnArray = new ArrayList<LocalDateTime>();
  	returnArray.add(this.beginData);
  	returnArray.add(this.eindData);
  	return returnArray;
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
  	return "Begindatum: " + this.beginData + "\n"
  			+ "Einddatum: " + this.eindData + "\n"
  			+ "Lesnaam: " + this.lesNaam + "\n"
  			+ "Docent: " + this.docent.getVoornaam() + "\n"
  			+ "Lokaal: " + this.lokaal + "\n"
  			+ "Klas: " + this.klas.getKlasCode() + "\n";
  }
}