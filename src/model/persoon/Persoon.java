package model.persoon;

public abstract class Persoon {

	private String voornaam;
	private String tussenvoegsel;
	private String achternaam;
	private String wachtwoord;
	private String gebruikersnaam;

	/**
	 * Persoon is een interface voor Student en Docent
	 * Hierin wordt aangegeven wat student en docent als attributen hebben.
	 * @param voornaam : voornaam
	 * @param tussenvoegsel : tussenvoegsel
	 * @param achternaam : achternaam
	 * @param wachtwoord : wachtwoord
	 * @param gebruikersnaam : gebruikersnaam
	 */
	
	public Persoon(String voornaam, String tussenvoegsel, String achternaam, String wachtwoord, String gebruikersnaam) {
		this.voornaam = voornaam;
		this.tussenvoegsel = tussenvoegsel;
		this.achternaam = achternaam;
		this.wachtwoord = wachtwoord;
		this.gebruikersnaam = gebruikersnaam;
	}

	public String getVoornaam() {
		return this.voornaam;
	}

	private String getAchternaam() {
		return this.achternaam;
	}

	protected String getWachtwoord() {
		return this.wachtwoord;
	}

	public String getGebruikersnaam() {
		return this.gebruikersnaam;
	}

	/**
	 * Voegt achternaam en tussenvoegsel samen om een volledig achternaam terug
	 * te geven.
	 */
	public String getVolledigeAchternaam() {
		String lVolledigeAchternaam="";
		if (this.tussenvoegsel != null && this.tussenvoegsel != "" && this.tussenvoegsel.length() > 0) {
			lVolledigeAchternaam += this.tussenvoegsel + " ";
		}
		lVolledigeAchternaam += this.getAchternaam();
		return lVolledigeAchternaam;
	}
	/**
	 * Checkt of het wachtwoord overeenkomt met het wachtwoord van
	 * de student/docent
	 * @param wachtwoord : te checken wachtwoord
	 */
	public boolean komtWachtwoordOvereen(String pWachtwoord) {
		boolean lStatus = false;
		if (this.getWachtwoord().equals(pWachtwoord)) {
			lStatus = true;
		}
		return lStatus;
	}
}
