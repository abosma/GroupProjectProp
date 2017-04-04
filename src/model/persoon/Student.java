//checked
package model.persoon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import model.klas.Klas;
import model.les.Les;
import model.vak.Vak;

public class Student extends Persoon {
	private ArrayList<String> vakken = new ArrayList<String>();

	private int studentNummer;
	private Klas klas;
	private String groepId;

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

	public ArrayList<Vak> getVakken() {
		return this.klas.getVakken();
	}
	
	public ArrayList<Les> getLessen(){
		ArrayList<Les> tList = new ArrayList<Les>();
		for(Vak vak : this.getVakken()){
			tList.addAll(vak.getLessen());
		}
		return tList;
	}

	private void setStudentNummer(int pStudentNummer) {
		this.studentNummer = pStudentNummer;
	}

	public void setKlas(Klas klas) {
		this.klas = klas;
	}

	public Vak getVak(String naam) {
		return this.klas.getVak(naam);
	}
}
