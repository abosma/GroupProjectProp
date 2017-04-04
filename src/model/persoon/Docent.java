package model.persoon;

import java.util.ArrayList;

import model.les.Les;
import model.vak.Vak;

public class Docent extends Persoon {

	private int docentNummer;
	private ArrayList<Vak> vakken;
	
	public Docent(String voornaam, String tussenvoegsel, String achternaam, String wachtwoord, String gebruikersnaam, int docentNummer) {
		super(voornaam, tussenvoegsel, achternaam, wachtwoord, gebruikersnaam);
		docentNummer = 0;
		this.vakken = new ArrayList<Vak>();
	}

	public void addVak(Vak vak){
		this.vakken.add(vak);
	}
	
	public int getDocentNummer() {
		return docentNummer;
	}

	public void setDocentNummer(int docentNummer) {
		this.docentNummer = docentNummer;
	}

	public boolean equals(Docent docent){
		return this.getGebruikersnaam().equals(docent.getGebruikersnaam());
	}

	public ArrayList<Vak> getVakken() {
		return this.vakken;
	}

	public Vak getVak(String naam) {
		for(Vak vak : vakken){
			if(vak.getNaam().equals(naam)){
				return vak;
			}
		}
		return null;
	}

	public ArrayList<Les> getLessen() {
		ArrayList<Les> lessen = new ArrayList<Les>();
		for(Vak vak : vakken){
			lessen.addAll(vak.getLessen());
		}
		return lessen;
	}	
}
