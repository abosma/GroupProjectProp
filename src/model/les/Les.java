package model.les;

import java.time.LocalDate;
import java.time.LocalDateTime;

import model.vak.Vak;

public class Les {
	
  private LocalDate datum;
  private String begin, eind;
  private String lokaal;
  private Vak vak;
  
  public Les(LocalDate dag, String begin, String eind, String lkl){
  	this.datum = dag;
  	this.begin = begin;
  	this.eind = eind;
  	this.lokaal = lkl;
  }
  
  public LocalDate getDatum(){
  	return this.datum;
  }
  
  public String getLokaal(){
  	return this.lokaal;
  }

	public String getBegin() {
		return begin;
	}

	public String getEind() {
		return eind;
	}

	public void setVak(Vak vak) {
		this.vak = vak;
	}

	public Vak getVak() {
		return this.vak;
	}  
}
