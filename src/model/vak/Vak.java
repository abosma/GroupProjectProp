package model.vak;

import java.util.ArrayList;

import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;
import model.persoon.Student;
import model.presentie.Presentie;

public class Vak {
	private String naam, code;
	private Docent docent;
	private Klas klas;
	private ArrayList<Les> lessen;
	private ArrayList<Presentie> dePresenties;
	
	public Vak(String naam, String code, Docent docent, Klas klas) {
		this.naam = naam;
		this.code = code;
		this.klas = klas;
		this.docent = docent;
		this.lessen = new ArrayList<Les>();
		this.dePresenties = new ArrayList<Presentie>();
		
		this.docent.addVak(this);
		this.klas.addVak(this);
		
		for(Student s : this.klas.getStudenten()){
			this.dePresenties.add(new Presentie(s));
		}
	}
	
	public void addLes(Les les){
		this.lessen.add(les);
		les.setVak(this);
		
		//Debug line
		//System.out.println("-Vak-addLes: " + naam + " | " + les.getDatum().toString());
		for(Presentie p : dePresenties){
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
	
	public void addPresentie(Student pStudent) {
		this.dePresenties.add(new Presentie(pStudent));
	}

	public ArrayList<Presentie> getPresenties() {
		return this.dePresenties;
	}

	public boolean hasLes(Les l) {
		return this.lessen.contains(l);
	}

	public Presentie getPresentieForStudent(Student lStudent) {
		for(Presentie p : this.dePresenties){
			if(p.getStudent().equals(lStudent)){
				return p;
			}
		}
		return null;
	}
}
