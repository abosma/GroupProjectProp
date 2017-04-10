//checked
package model.persoon;

import model.klas.Klas;
import model.vak.Vak;

public class Student extends Persoon {
	private int studentNummer;
	private Klas klas;
	private String groepId;
	/**
	 * Student haalt meeste attributen van het Persoon interface
	 * maar heeft ook nog een extra attribuut studentNummer
	 * en maakt zelf een attribuut aan genaamd groepID
	 * @param voornaam : voornaam
	 * @param tussenvoegsel : tussenvoegsel
	 * @param achternaam : achternaam
	 * @param wachtwoord : wachtwoord
	 * @param gebruikersnaam : gebruikersnaam
	 * @param pStudentNummer : studentnummer
	 */
	public Student(String pVoornaam, String pTussenvoegsel, String pAchternaam, String pWachtwoord,
			String pGebruikersnaam, int pStudentNummer) {
		super(pVoornaam, pTussenvoegsel, pAchternaam, pWachtwoord, pGebruikersnaam);
		this.setStudentNummer(pStudentNummer);
		this.setGroepId("");
	}

	public String getGroepId() {
		return this.groepId;
	}

	public void setGroepId(String pGroepId) {
		this.groepId = pGroepId;
	}

	public int getStudentNummer() {
		return this.studentNummer;
	}

	private void setStudentNummer(int pStudentNummer) {
		this.studentNummer = pStudentNummer;
	}

	public void setKlas(Klas klas) {
		this.klas = klas;
	}

	public Klas getKlas(){
		return this.klas;
	}
	
	public Vak getVak(String naam) {
		return this.klas.getVak(naam);
	}
	
	public boolean equals(Object o){
		if(o instanceof Student){
			Student tStudent = (Student)o;
			return tStudent.getGebruikersnaam().equals(this.getGebruikersnaam());
		}
		return false;
	}
}
