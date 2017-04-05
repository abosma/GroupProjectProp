package model;

import java.util.ArrayList;

import model.klas.Klas;
import model.persoon.Docent;
import model.persoon.Student;
import model.vak.Vak;
import model.les.Les;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

public class PrIS {
	private ArrayList<Docent> deDocenten;
	private ArrayList<Student> deStudenten;
	private ArrayList<Klas> deKlassen;
	private ArrayList<Les> deLessen;

	/**
	 * De constructor maakt een set met standaard-data aan. Deze data moet nog
	 * uitgebreidt worden met rooster gegevens die uit een bestand worden
	 * ingelezen, maar dat is geen onderdeel van deze demo-applicatie!
	 * 
	 * De klasse PrIS (PresentieInformatieSysteem) heeft nu een meervoudige
	 * associatie met de klassen Docent, Student, Vakken en Klassen Uiteraard kan
	 * dit nog veel verder uitgebreid en aangepast worden!
	 * 
	 * De klasse fungeert min of meer als ingangspunt voor het domeinmodel. Op dit
	 * moment zijn de volgende methoden aanroepbaar:
	 * 
	 * String login(String gebruikersnaam, String wachtwoord) Docent
	 * getDocent(String gebruikersnaam) Student getStudent(String gebruikersnaam)
	 * ArrayList<Student> getStudentenVanKlas(String klasCode)
	 * 
	 * Methode login geeft de rol van de gebruiker die probeert in te loggen, dat
	 * kan 'student', 'docent' of 'undefined' zijn! Die informatie kan gebruikt
	 * worden om in de Polymer-GUI te bepalen wat het volgende scherm is dat
	 * getoond moet worden.
	 * 
	 */
	public PrIS() {
		deDocenten = new ArrayList<Docent>();
		deStudenten = new ArrayList<Student>();
		deKlassen = new ArrayList<Klas>();

		// Inladen klassen
		vulKlassen(deKlassen);

		// Inladen studenten in klassen
		vulStudenten(deStudenten, deKlassen);

		// Inladen docenten
		vulDocenten(deDocenten);

		// Inladen lessen
		vulLessen(deKlassen, deLessen);

	} // Einde Pris constructor

	// deze method is static onderdeel van PrIS omdat hij als hulp methode
	// in veel controllers gebruikt wordt
	// een standaardDatumString heeft formaat YYYY-MM-DD
	public static Calendar standaardDatumStringToCal(String pStadaardDatumString) {
		Calendar lCal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			lCal.setTime(sdf.parse(pStadaardDatumString));
		} catch (ParseException e) {
			e.printStackTrace();
			lCal = null;
		}
		return lCal;
	}

	// deze method is static onderdeel van PrIS omdat hij als hulp methode
	// in veel controllers gebruikt wordt
	// een standaardDatumString heeft formaat YYYY-MM-DD
	public static String calToStandaardDatumString(Calendar pCalendar) {
		int lJaar = pCalendar.get(Calendar.YEAR);
		int lMaand = pCalendar.get(Calendar.MONTH) + 1;
		int lDag = pCalendar.get(Calendar.DAY_OF_MONTH);

		String lMaandStr = Integer.toString(lMaand);
		if (lMaandStr.length() == 1) {
			lMaandStr = "0" + lMaandStr;
		}
		String lDagStr = Integer.toString(lDag);
		if (lDagStr.length() == 1) {
			lDagStr = "0" + lDagStr;
		}
		String lString = Integer.toString(lJaar) + "-" + lMaandStr + "-" + lDagStr;
		return lString;
	}

	public Docent getDocent(String gebruikersnaam) {
		Docent resultaat = null;

		for (Docent d : deDocenten) {
			if (d.getGebruikersnaam().equals(gebruikersnaam)) {
				resultaat = d;
				break;
			}
		}

		return resultaat;
	}

	public Klas getKlasVanStudent(Student pStudent) {
		// used
		for (Klas lKlas : deKlassen) {
			if (lKlas.bevatStudent(pStudent)) {
				return (lKlas);
			}
		}
		return null;
	}

	public Student getStudent(String pGebruikersnaam) {
		// used
		Student lGevondenStudent = null;

		for (Student lStudent : deStudenten) {
			if (lStudent.getGebruikersnaam().equals(pGebruikersnaam)) {
				lGevondenStudent = lStudent;
				break;
			}
		}

		return lGevondenStudent;
	}

	public Student getStudent(int pStudentNummer) {
		// used
		Student lGevondenStudent = null;

		for (Student lStudent : deStudenten) {
			if (lStudent.getStudentNummer() == (pStudentNummer)) {
				lGevondenStudent = lStudent;
				break;
			}
		}

		return lGevondenStudent;
	}

	public String login(String gebruikersnaam, String wachtwoord) {
		for (Docent d : deDocenten) {
			if (d.getGebruikersnaam().equals(gebruikersnaam)) {
				if (d.komtWachtwoordOvereen(wachtwoord)) {
					return "docent";
				}
			}
		}

		for (Student s : deStudenten) {
			if (s.getGebruikersnaam().equals(gebruikersnaam)) {
				if (s.komtWachtwoordOvereen(wachtwoord)) {
					return "student";
				}
			}
		}

		return "undefined";
	}

	public ArrayList<Les> getAlleLessen() {
		return this.deLessen;
	}

	public ArrayList<Les> getLessenDocent(String gebruikersnaam) {
		ArrayList<Les> returnLessen = new ArrayList<Les>();
		Docent lDocent = this.getDocent(gebruikersnaam);
		for(Vak vak : lDocent.getVakken()){
			returnLessen.addAll(vak.getLessen());
		}
		return returnLessen;
	}

	public ArrayList<Les> getVakStudent(String naam, String gebruikersnaam) {
		return this.getStudent(gebruikersnaam).getVak(naam).getLessen();
	}

	public ArrayList<Les> getVakDocent(String naam, String gebruikersnaam) {
		return this.getDocent(gebruikersnaam).getVak(naam).getLessen();
	}

	public ArrayList<Les> getLessenStudent(String stuMail) {
		return this.getStudent(stuMail).getKlas().getAllLessen();
	}
	
	public String translatePresentieIntToString(int i){
		switch(i){
  		case 0: return "afwezig";
  		case 1: return "aanwezig";
  		case 2: return "ziek";
  		case 3: return "afgemeld";
  		case 4: return "afgemeld (niet geaccepteerd)";
  		default: return "niet geregistreerd";
  	}
	}
	
	public int translatePresentieStringToInt(String s){
		s = s.toLowerCase();
		if(s.equals("afwezig")){
			return 0;
		} else if(s.equals("aanwezig")){
			return 1;
		} else if(s.equals("ziek")){
			return 2;
		} else if(s.equals("afgemeld")){
			return 3;
		} else if (s.equals("afmelden")){
			return 3;
		} else if(s.equals("afgemeld (niet geaccepteerd)")){
			return 4;
		} else if(s.equals("niet geregistreerd")){
			return -1;
		} else {
			return -1;
		}
	}

	public ArrayList<Les> getLessenDocentForSingleDate(String dct, LocalDateTime date) {
		ArrayList<Les> lesDag = new ArrayList<Les>();

		Docent docent = this.getDocent(dct);

		for (Les les : docent.getLessen()) {
			LocalDate beginDatum = les.getDatum();
			if (date.getYear() == beginDatum.getYear() 
					&& date.getMonth() == beginDatum.getMonth()
					&& date.getDayOfMonth() == beginDatum.getDayOfMonth()) {	
				lesDag.add(les);
			}
		}
		return lesDag;
	}

	public ArrayList<Les> getLessenStudentForSingleDate(String studentMail, LocalDateTime date) {
		// variabele waarin we de lessen van de gegeven dag opslaan
		ArrayList<Les> lesdag = new ArrayList<Les>();
		// haal student op
		Student theStudent = this.getStudent(studentMail);
		
		// loop door alle lessen
		for (Les les : theStudent.getKlas().getAllLessen()) {
			LocalDate beginDatum = les.getDatum();
			// controleer of de datum overeenkomt
			if (date.getYear() == beginDatum.getYear() 
					&& date.getMonth() == beginDatum.getMonth()
					&& date.getDayOfMonth() == beginDatum.getDayOfMonth()) {
				lesdag.add(les);
			}
		}
		return lesdag;
	}

	private void vulDocenten(ArrayList<Docent> pDocenten) {
		String csvFile = "././CSV/docenten.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] element = line.split(cvsSplitBy);
				String gebruikersnaam = element[0].toLowerCase();
				String voornaam = element[1];
				String tussenvoegsel = element[2];
				String achternaam = element[3];
				pDocenten.add(new Docent(voornaam, tussenvoegsel, achternaam, "geheim", gebruikersnaam, 1));

				// System.out.println(gebruikersnaam);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void vulKlassen(ArrayList<Klas> pKlassen) {
		// TICT-SIE-VIA is de klascode die ook in de rooster file voorkomt
		// V1A is de naam van de klas die ook als file naam voor de studenten van
		// die klas wordt gebruikt
		Klas k1 = new Klas("TICT-SIE-V1A", "V1A");
		Klas k2 = new Klas("TICT-SIE-V1B", "V1B");
		Klas k3 = new Klas("TICT-SIE-V1C", "V1C");
		Klas k4 = new Klas("TICT-SIE-V1D", "V1D");
		Klas k5 = new Klas("TICT-SIE-V1E", "V1E");
		Klas k6 = new Klas("TICT-SIE-V1F", "V1F");

		pKlassen.add(k1);
		pKlassen.add(k2);
		pKlassen.add(k3);
		pKlassen.add(k4);
		pKlassen.add(k5);
		pKlassen.add(k6);
	}

	private void vulLessen(ArrayList<Klas> pKlassen, ArrayList<Les> pLessen) {
		String csvFile = "././CSV/rooster.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] element = line.split(cvsSplitBy);
				
				// Split uren en minuten in excel bestand
				String beginTijd = element[1];
				String eindTijd = element[2];
				
				// Maak van excel vak 1 een localdate
				LocalDate dag = LocalDate.parse(element[0]);

				// Vak 3 is lesnaam
				String vakNaam = element[3];
				
				// getDocent geeft een docent terug met hetzelfde email als vak 4
				Docent docent = this.getDocent(element[4]);
				
				// Vak 5 is het lokaal
				String lokaal = element[5];
				
				// Maak empty Klas aan
				Klas klas = null;
				
				// Voor iedere klas in de gevulde pKlassen
				for (Klas k : pKlassen) {
					// Als de klascode hetzelfde is als de klascode in vak 6
					if (k.getKlasCode().equals(element[6])) {
						// Dan is de empty klas de klas met hetzelfde klascode
						klas = k;
					}
				}
				Les tLes = new Les(dag, beginTijd, eindTijd, lokaal);
				
				/*Controleren of de Klas het vak al heeft.  
				 * Wanneer een klas het vak heeft, hoeft enkel een nieuwe les aan het vak toegeveogd te worden.
				 * Anders moet eerst het vak voor de klas worden aangemaakt, en dan de les worden teogeveogd.
				 */
				if(klas.hasVak(vakNaam)){
					klas.getVak(vakNaam).addLes(tLes);
				} else {
					Vak tVak = new Vak(vakNaam, "", docent, klas);
					tVak.addLes(tLes);
					klas.addVak(tVak);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Om alle lessen te printen uncomment deze
			// System.out.println(pLessen.toString());
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void vulStudenten(ArrayList<Student> pStudenten, ArrayList<Klas> pKlassen) {
		Student lStudent;
		for (Klas k : pKlassen) {
			String csvFile = "././CSV/" + k.getNaam() + ".csv";
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";

			try {

				br = new BufferedReader(new FileReader(csvFile));

				while ((line = br.readLine()) != null) {

					// use comma as separator
					String[] element = line.split(cvsSplitBy);
					String gebruikersnaam = (element[3] + "." + element[2] + element[1] + "@student.hu.nl").toLowerCase();
					// verwijder spaties tussen dubbele voornamen en tussen bv van der
					gebruikersnaam = gebruikersnaam.replace(" ", "");
					String lStudentNrString = element[0];
					int lStudentNr = Integer.parseInt(lStudentNrString);
					lStudent = new Student(element[3], element[2], element[1], "geheim", gebruikersnaam, lStudentNr); // Volgorde 3-2-1  = voornaam, tussenvoegsel en achternaam
					pStudenten.add(lStudent);
					k.voegStudentToe(lStudent);

					// System.out.println(gebruikersnaam);

				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}