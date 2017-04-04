package model.vak;

import java.util.ArrayList;

import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;
import model.persoon.Student;
import model.presentie.PresentieLijst;

public class Vak {
	private String naam, code;
	private Docent docent;
	private Klas klas;
	private ArrayList<Les> lessen;
	private ArrayList<PresentieLijst> presentieLijsten;
	
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
	
	public void addLes(Les les){
		this.lessen.add(les);
		les.setVak(this);
		
		for(PresentieLijst p : presentieLijsten){
			p.addPresentie(les, -1);
		}
	}
	
	public ArrayList<Les> getLessen(){
		return this.lessen;
	}

	public String getNaam() {
		return naam;
	}

	public String getCode() {
		return code;
	}

	public Docent getDocent() {
		return docent;
	}

	public Klas getKlas() {
		return klas;
	}
	
	public void addPresentieLijst(Student pStudent) {
		this.presentieLijsten.add(new PresentieLijst(pStudent));
	}

	public ArrayList<PresentieLijst> getPresentieLijsten() {
		return this.presentieLijsten;
	}

	public boolean hasLes(Les l) {
		return this.lessen.contains(l);
	}

	public PresentieLijst getPresentieLijstForStudent(Student lStudent) {
		for(PresentieLijst p : this.presentieLijsten){
			if(p.getStudent().equals(lStudent)){
				return p;
			}
		}
		return null;
	}
}
