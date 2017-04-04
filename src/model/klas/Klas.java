package model.klas;

import java.util.ArrayList;

import model.persoon.Docent;
import model.persoon.Student;
import model.vak.Vak;

public class Klas {

	private String klasCode;
	private String naam;
	private ArrayList<Student> deStudenten;
	private ArrayList<Vak> deVakken;

	public Klas(String klasCode, String naam) {
		this.klasCode = klasCode;
		this.naam = naam;
		this.deStudenten = new ArrayList<Student>();
		this.deVakken = new ArrayList<Vak>();
	}
	
	public String getKlasCode() {
		return klasCode;
	}
	
	public ArrayList<Vak> getVakken(){
		return this.deVakken;
	}
	
	public boolean hasVak(String vakNaam){
		for(Vak vak : deVakken){
			if(vak.getNaam().equals(vakNaam)){
				return true;
			}
		}
		return false;
	}
	
	public String getNaam() {
		return naam;
	}

	public ArrayList<Student> getStudenten() {
		return this.deStudenten;
	}
	
	public boolean bevatStudent(Student pStudent) {
		for (Student lStudent : this.getStudenten()) {
			if (lStudent==pStudent) {
				return true;
			}
		}
		return false;
	}

	public void voegStudentToe(Student pStudent) {
		if (!this.getStudenten().contains(pStudent)) {
			this.getStudenten().add(pStudent);
			pStudent.setKlas(this);
		}
	}

	public Vak getVak(String vakNaam) {
		for(Vak vak : deVakken){
			if(vak.getNaam().equals(vakNaam)){
				return vak;
			}
		}
		return null;
	}

	public void addVak(Vak tVak) {
		if(this.getVak(tVak.getNaam()) == null){
			this.deVakken.add(tVak);
		}
	}

}
