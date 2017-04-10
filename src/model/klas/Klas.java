package model.klas;

import java.util.ArrayList;

import model.les.Les;
import model.persoon.Student;
import model.presentie.PresentieLijst;
import model.vak.Vak;

public class Klas {

	private String klasCode;
	private String naam;
	private ArrayList<Student> deStudenten;
	private ArrayList<Vak> deVakken;

	/**
	 * Constructor voor klasse Klas
	 * 
	 * @param klasCode - de klascode 
	 * @param naam - de naam van de klas
	 */
	public Klas(String klasCode, String naam) {
		this.klasCode = klasCode;
		this.naam = naam;
		this.deStudenten = new ArrayList<Student>();
		this.deVakken = new ArrayList<Vak>();
	}
	
	/**
	 * Haalt de klascode op
	 * 
	 * @return de klascode
	 */
	public String getKlasCode() {
		return klasCode;
	}
	
	/**
	 * Haalt alle vakken van de klas op
	 * 
	 * @return ArrayList<Vak> object met alle vakken
	 */
	public ArrayList<Vak> getVakken(){
		return this.deVakken;
	}
	
	/**
	 * Controleert of de klas een gegeven vak heeft
	 * 
	 * @param vakNaam - naam van het vak
	 * @return true wanner de klas het vak heeft, anders false
	 */
	public boolean hasVak(String vakNaam){
		for(Vak vak : deVakken){
			if(vak.getNaam().equals(vakNaam)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * geeft de naam van de klas terug
	 * 
	 * @return String object emt de naam van de klas
	 */
	public String getNaam() {
		return naam;
	}

	/**
	 * Haalt alle studenten op die in de klas zitten
	 * 
	 * @return ArrayList<Student> object met alle studenten
	 */
	public ArrayList<Student> getStudenten() {
		return this.deStudenten;
	}
	
	/**
	 * Controleert of een gegeven Student object tot de klas behoort
	 * 
	 * @param pStudent - het te controleren Student object
	 * @return true wanneer het student object to de klas behoort, anders false
	 */
	public boolean bevatStudent(Student pStudent) {
		for (Student lStudent : this.getStudenten()) {
			if (lStudent==pStudent) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Voegt een student object toe aan de studentenlijst van de klas
	 * 
	 * @param pStudent - het Student object dat toegevoegt moet worden
	 */
	public void voegStudentToe(Student pStudent) {
		if (!this.getStudenten().contains(pStudent)) {
			this.getStudenten().add(pStudent);
			pStudent.setKlas(this);
			for(Vak v : this.deVakken){
				v.addPresentieLijst(pStudent);
			}
		}
	}

	/**
	 * Haal een vak object op, op basis van een vak naam
	 * 
	 * @param vakNaam -  de naam van het vak in String-formaat
	 * @return een Vak-object wanneer het vak gevonden is, anders null
	 */
	public Vak getVak(String vakNaam) {
		for(Vak vak : deVakken){
			if(vak.getNaam().equals(vakNaam)){
				return vak;
			}
		}
		return null;
	}

	/**
	 * Voeg een Vak-object toe aan de vakkenlijst
	 * 
	 * @param tVak - Vak-object dat teogevoegd moet worden
	 */
	public void addVak(Vak tVak) {
		if(this.getVak(tVak.getNaam()) == null){
			this.deVakken.add(tVak);
		}
	}

	/**
	 * Voeg een een nieuw Presentie-object toe aan de presentielijst
	 * 
	 * @param student - de student waarvoor de presentie vast meot worden gelegd
	 * @param les - de les waarom het gaat
	 * @param presentiecode - de presntiecode als integer
	 */
	public void addPresentieToPresentieLijst(Student student, Les les, int presentiecode){
		Vak tV = null;		
		for( Vak v : this.deVakken){
			if(v.hasLes(les)){
				tV = v;
			}
		}
		PresentieLijst tP = this.getPresentieLijstForStudent(student, tV);
		if(tP != null){
			tP.addPresentie(les, presentiecode);
		}
	}
	
	/**
	 * verander de presentie van de student voor een les
	 * 
	 * @param s - de Student
	 * @param l - de Les
	 * @param i - de presentiecode als integer
	 */
	public void updatePresentieLijstForStudent(Student s, Les l, int i){
		Vak tV = null;
		for(Vak v : this.deVakken){
			if(v.hasLes(l)){
				tV = v;
			}
		}
		if(tV != null){
  		PresentieLijst tP = this.getPresentieLijstForStudent(s, tV);
  		if(tP != null){
  			tP.updatePresentieLijstForLes(l, i);
  		}
		}
	}
	
	/**
	 * Geeft de presentielijst van een student voor een vak terug
	 * 
	 * @param s - de Student
	 * @param vak - het Vak 
	 * @return de PresentieLijst met de presenties
	 */
	public PresentieLijst getPresentieLijstForStudent(Student s, Vak vak){
		return vak.getPresentieLijstForStudent(s);
	}

	/**
	 * Haalt alle lessen van de klas op
	 * 
	 * @return ArrayList<Les> object met alle lessen
	 */
	public ArrayList<Les> getAllLessen() {
		ArrayList<Les> tmp = new ArrayList<Les>();
		for(Vak v : this.deVakken){
			tmp.addAll(v.getLessen());
		}
		return tmp;
	}
}
