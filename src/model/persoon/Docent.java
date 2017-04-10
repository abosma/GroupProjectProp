package model.persoon;

import java.util.ArrayList;

import model.les.Les;
import model.vak.Vak;

public class Docent extends Persoon {

	private int docentNummer;
	private ArrayList<Vak> vakken;
	/**
	 * Docent haalt meeste attributen van het Persoon interface
	 * maar heeft ook nog een extra attribuut docentNummer
	 * en creeert zelf een leeg arraylist vakken aan
	 * @param voornaam : voornaam
	 * @param tussenvoegsel : tussenvoegsel
	 * @param achternaam : achternaam
	 * @param wachtwoord : wachtwoord
	 * @param gebruikersnaam : gebruikersnaam
	 * @param docentNummer : docentnummer
	 */
	public Docent(String voornaam, String tussenvoegsel, String achternaam, String wachtwoord, String gebruikersnaam, int docentNummer) {
		super(voornaam, tussenvoegsel, achternaam, wachtwoord, gebruikersnaam);
		this.docentNummer = docentNummer;
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

	/**
	 * Haalt vak op gebaseerd op de ingevoerde String
	 * 
	 * @param naam : Vaknaam
	 * @return Vak
	 */
	public Vak getVak(String naam) {
		for(Vak vak : vakken){
			if(vak.getNaam().equals(naam)){
				return vak;
			}
		}
		return null;
	}
	/**
	 * Haalt alle lessen op dat de docent geeft
	 * @return ArrayList<Les>
	 */
	public ArrayList<Les> getLessen() {
		ArrayList<Les> lessen = new ArrayList<Les>();
		for(Vak vak : vakken){
			lessen.addAll(vak.getLessen());
		}
		return lessen;
	}	
}
