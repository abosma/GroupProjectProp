package model.vak;

import java.time.LocalDate;
import java.util.ArrayList;

import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;
import model.persoon.Student;
import model.presentie.PresentieLijst;

public class Vak {
	private String naam; 
	private String code; // word op dit moment niet gebruikt
	private Docent docent;
	private Klas klas;
	private ArrayList<Les> lessen;
	private ArrayList<PresentieLijst> presentieLijsten;

	/**
	 * Constructor voor het vak object
	 * 
	 * @param naam - de naam van het vak
	 * @param code - de code vak het vak
	 * @param docent - de docent die het vak geeft
	 * @param klas - de klas die het vak volgt
	 */
	public Vak(String naam, String code, Docent docent, Klas klas) {
		this.naam = naam;
		this.code = code;
		this.klas = klas;
		this.docent = docent;
		this.lessen = new ArrayList<Les>();
		this.presentieLijsten = new ArrayList<PresentieLijst>();
		
		this.docent.addVak(this);
		this.klas.addVak(this);
		
		for(Student s : this.klas.getStudenten()){
			this.presentieLijsten.add(new PresentieLijst(s));
		}
	}
	
	/**
	 * Voeg een les toe aan het Vak
	 * 
	 * @param les - het toe te voegen Les-object
	 */
	public void addLes(Les les){
		this.lessen.add(les);
		les.setVak(this);
		
		for(PresentieLijst p : presentieLijsten){
			p.addPresentie(les, -1);
		}
	}
	
	/**
	 * Geeft alle lessen terug van dit Vak
	 * 
	 * @return ArrayList<Les>-object emt alle lessen
	 */
	public ArrayList<Les> getLessen(){
		return this.lessen;
	}

	/**
	 * Geeft de naam van het Vak terug
	 * 
	 * @return (String) de naam
	 */
	public String getNaam() {
		return naam;
	}

	/**
	 * Geeft de code van het vak terug
	 * 
	 * @return (String) de vakcode
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Geeft de docent van het vak terug
	 * 
	 * @return (Docent) de docent
	 */
	public Docent getDocent() {
		return docent;
	}

	/**
	 * Haalt de klas op die dit Vak volgt
	 * 
	 * @return (Klas) de klas
	 */
	public Klas getKlas() {
		return klas;
	}
	
	/**
	 * Voeg een nieuwe presentielijst toe aan de lijst met preentielijsten
	 * 
	 * @param pStudent - student waarvoor een presentielijst meot worden teogevoegd
	 */
	public void addPresentieLijst(Student pStudent) {
		this.presentieLijsten.add(new PresentieLijst(pStudent));
	}

	/**
	 * haalt alle presentielijst op vor dit Vak
	 * 
	 * @return (ArrayList<PresentieLijst>) de lijsten
	 */
	public ArrayList<PresentieLijst> getPresentieLijsten() {
		return this.presentieLijsten;
	}

	/**
	 * Controleert of de vak het gegeven Les-object bevat
	 * 
	 * @param l - de les waarop gecontroleerd moet worden
	 * @return true als het Vak de Les heeft
	 */
	public boolean hasLes(Les l) {
		return this.lessen.contains(l);
	}

	/**
	 * het voor een specifieke student de presentielijst op
	 * 
	 * @param lStudent - Student-object 
	 * @return een PresentieLijst als deze voor de gegeven Student bestaat, anders null
	 */
	public PresentieLijst getPresentieLijstForStudent(Student lStudent) {
		for(PresentieLijst p : this.presentieLijsten){
			if(p.getStudent().equals(lStudent)){
				return p;
			}
		}
		return null;
	}

	/**
	 * Haalt de les op voor het vak die op het gegeven moment begint
	 * 
	 * @param datum - LocalDate-bject emt de datum
	 * @param begin - String met de begintijd
	 * @return (Les) les object als deze gevonden is, anders null
	 */
	public Les getLes(LocalDate datum, String begin) {
		for(Les les : this.lessen){
			if(les.getDatum().equals(datum)
				&& les.getBegin().equals(begin)){
				return les;
			}
		}
		return null;
	}
}