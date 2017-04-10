package model.les;

import java.time.LocalDate;
import model.vak.Vak;

public class Les {
	
  private LocalDate datum;
  private String begin, eind;
  private String lokaal;
  private Vak vak;
  
  /**
   * Contrstructor voor het Les object
   * 
   * @param dag - datum als LocalDate
   * @param begin - begintijd als String
   * @param eind - eindtijd als String
   * @param lkl - lokaal als String
   */
  public Les(LocalDate dag, String begin, String eind, String lkl){
  	this.datum = dag;
  	this.begin = begin;
  	this.eind = eind;
  	this.lokaal = lkl;
  }
  
  /**
   * Haal de datum van de les op
   * 
   * @return datum als LocalDate
   */
  public LocalDate getDatum(){
  	return this.datum;
  }
  
  /**
   * Haal het lokaal op
   * 
   * @return String met het lokaal
   */
  public String getLokaal(){
  	return this.lokaal;
  }

  /**
   * Haal de begintijd van de Les op
   * 
   * @return (String) begintijd
   */
	public String getBegin() {
		return begin;
	}

	/**
	 * Haald de eindtijd van het vak op
	 * 
	 * @return (String) eindtijd
	 */
	public String getEind() {
		return eind;
	}

	/**
	 * Stel het vak in waar de les aan toebehoort
	 * 
	 * @param vak - Vak-object
	 */
	public void setVak(Vak vak) {
		this.vak = vak;
	}

	/**
	 * Haal het vak op waar de les bij hoort
	 * 
	 * @return (Vak) het vak
	 */
	public Vak getVak() {
		return this.vak;
	}
	
	/**
	 * Controleert of het gegeven object gelijk is aan dit object
	 * 
	 * @return true of false
	 */
	public boolean equals(Object object){
		if(object instanceof Les){
			Les tmp = (Les) object;
			return tmp.getDatum().isEqual(this.datum) 
					&& tmp.getBegin().equals(this.begin)
					&& tmp.getLokaal().equals(this.lokaal);
		}
		return false;
	}
}
