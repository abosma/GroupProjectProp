package model.vak;

import java.util.ArrayList;

import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;

public class Vak {
	private String naam, code;
	private Docent docent;
	private Klas klas;
	private ArrayList<Les> lessen;
	
	public Vak(String naam, String code, Docent docent, Klas klas) {
		this.naam = naam;
		this.code = code;
		this.klas = klas;
		this.docent = docent;
		this.lessen = new ArrayList<Les>();
		
		this.docent.addVak(this);
		this.klas.addVak(this);
	}
	
	public void addLes(Les les){
		this.lessen.add(les);
		les.setVak(this);
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
}
