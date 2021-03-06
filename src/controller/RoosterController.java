package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import model.PrIS;
import model.les.Les;
import model.persoon.Docent;
import model.persoon.Student;
import model.vak.Vak;
import server.Conversation;
import server.Handler;

public class RoosterController implements Handler{
	private PrIS informatieSysteem;
	
	/**
	 * De RoosterController klasse moet alle presentie-gerelateerde aanvragen
	 * afhandelen. Methode handle() kijkt welke URI is opgevraagd en laat
	 * dan de juiste methode het werk doen. Je kunt voor elke nieuwe URI
	 * een nieuwe methode schrijven.
	 * 
	 * @param infoSys - het toegangspunt tot het domeinmodel
	 */
	
	public RoosterController(PrIS infoSys) {
		informatieSysteem = infoSys;
	}
	
	public void handle(Conversation conversation) {
		if (conversation.getRequestedURI().startsWith("/docent/rooster/lesdagophalen")){
			ophalenDocentLessen(conversation);
		} else if (conversation.getRequestedURI().startsWith("/student/rooster/lesdagophalen")){
			ophalenStudentLessen(conversation);
		} else if (conversation.getRequestedURI().startsWith("/student/rooster/afmelden")){
			veranderPresentieStudent(conversation);
		} else if (conversation.getRequestedURI().startsWith("/docent/rooster/les/studenten")){
			ophalenPresentieStudenten(conversation);
		} else if (conversation.getRequestedURI().startsWith("/docent/rooster/les/presentie/opslaan")){
			opslaanPresentieVoorLes(conversation);
		}else if (conversation.getRequestedURI().startsWith("/docent/rooster/les/presentie/current")){
			currentPresentieVoorLes(conversation);
		}else if (conversation.getRequestedURI().startsWith("/docent/rooster/les/presentie/afkeuren")){
			afkeurenPresentie(conversation);
		}
	}
	
	/**
	 * Functie afkeurenPresentie haalt de gebruikersnaam, vak en datum op
	 * waarvan de presentie afgekeurd moet worden.
	 * 
	 * Met deze data kunnen we de presentiecode van dit student veranderen
	 * naar een afgekeurde afwezigheidsaanvraag
	 * 
	 * @param conversation - De request door de Polymer frontend
	 */
	
	private void afkeurenPresentie(Conversation conversation){
		JsonObject verzoek = (JsonObject) conversation.getRequestBodyAsJSON();
		
		//Ophalen student
		Student s = informatieSysteem.getStudent(verzoek.getString("username"));
		
		//Ophalen vak waarvoor word afgemeld
		Vak vak = s.getVak(verzoek.getString("vak"));
		
		//Ophalen specifieke les waarvoor word afgememeld
		Les les = vak.getLes(
				LocalDate.parse(verzoek.getString("datum")), 
				verzoek.getString("begin"));
		
		vak.getPresentieLijstForStudent(s).getPresentieObjectForLes(les).setCode(4);
	}
	
	private void currentPresentieVoorLes(Conversation conversation) {
		JsonObject verzoek = (JsonObject) conversation.getRequestBodyAsJSON();
		
		//Ophalen student
		Student s = informatieSysteem.getStudent(verzoek.getString("username"));
		
		//Ophalen vak waarvoor word afgemeld
		Vak vak = s.getVak(verzoek.getString("vak"));
		
		//Ophalen specifieke les waarvoor word afgememeld
		Les les = vak.getLes(
				LocalDate.parse(verzoek.getString("datum")), 
				verzoek.getString("begin"));
		
		int presentieVoorLes = vak.getPresentieLijstForStudent(s).getPresentieForLes(les);
		
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("value", presentieVoorLes);
		conversation.sendJSONMessage(job.build().toString());
	}
	
	/**
	 * Functie opslaanPresentieVoorLes haalt een array van studenten 
	 * op met ingevoerde presenties door de docent en het vak + datum van het vak.
	 * 
	 * Deze worden dan opgeslagen voor ieder student in het informatiesysteem.
	 * 
	 * @param conversation - De request door de Polymer frontend
	 */
	
	private void opslaanPresentieVoorLes(Conversation conversation) {
		JsonObject verzoek = (JsonObject) conversation.getRequestBodyAsJSON();
		
		// Haal de array met studenten uit de Json request
		JsonArray ja = verzoek.getJsonArray("studenten");

		// vind de student
		Student s = informatieSysteem.getStudent(ja.getJsonObject(0).getString("email"));
		// haal het vak op
		Vak vak = s.getVak(verzoek.getString("vak"));
		// haalde desbetreffende les op
		Les les = vak.getLes(LocalDate.parse(verzoek.getString("datum")), verzoek.getString("begin"));
		
		// loop alle studenten af
		for(JsonValue jv : ja){
			JsonObject jo = (JsonObject) jv;
			String name = jo.getString("email");
			
			Student student = null;
			for(Student _s : vak.getKlas().getStudenten()){
				if(_s.getGebruikersnaam().equals(name)){
					student = _s;
				}
			}
			
			/* Controleren of de opgehaalde student-object een daadwerkelijk student obejct is
			 * Anders crasht het programma hier... 
			 */
			if(student != null){
				int huidigePresentie = vak.getPresentieLijstForStudent(student).getPresentieForLes(les);
				
				// Afhandelen van alle mogelijke opties
				if(jo.getBoolean("aanwezig")){
					//student aanwezig melden
					vak.getPresentieLijstForStudent(student).updatePresentieLijstForLes(les, 1);
				} 
				//ToDo maak deze code specifieker voor alle gevallen'
				// wanneer een student zich niet ziekgemeld heeft, moeten we de presentie op afwezig zetten
				else if(huidigePresentie != informatieSysteem.translatePresentieStringToInt("ziek")){
					vak.getPresentieLijstForStudent(student).updatePresentieLijstForLes(les, 0);			
				} 
			}
		}
	}
	
	/**
	 * Functie ophalenPresentieStudenten haalt het Docent, vak en datum op uit de request
	 * en gebruikt de data om de les waar de presenties van opgehaald moeten worden de
	 * presenties op te halen en terug te sturen.
	 *  
	 * @param conversation - De request door de Polymer frontend
	 */

	private void ophalenPresentieStudenten(Conversation conversation) {
		JsonObject verzoek = (JsonObject) conversation.getRequestBodyAsJSON();
		
		Docent docent = informatieSysteem.getDocent(verzoek.getString("username"));
		Vak vak = docent.getVak(verzoek.getString("vak"));
		Les les = vak.getLes(LocalDate.parse(verzoek.getString("datum")), verzoek.getString("begin"));
		
		ArrayList<Student> studenten = vak.getKlas().getStudenten();
		
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for(Student s : studenten){
			int presentieVoorLes = vak.getPresentieLijstForStudent(s).getPresentieForLes(les);
			
			String opmerking = "";
			boolean accepteerbaar = false;
			
			if(presentieVoorLes >= 2 && presentieVoorLes <= 4){
				opmerking = vak.getPresentieLijstForStudent(s).getPresentieObjectForLes(les).getReden();
				
				if(presentieVoorLes == 3){
					accepteerbaar = true;
				}
			}
			
			JsonObjectBuilder job = Json.createObjectBuilder();
			job
				.add("naam", s.getVoornaam() + " " + s.getVolledigeAchternaam())
				.add("email", s.getGebruikersnaam())
				.add("presentie", presentieVoorLes)
				.add("aanwezig", presentieVoorLes == 1)
				.add("opmerking", opmerking)
				.add("accepteerbaar", accepteerbaar);

			
			jab.add(job);
		}
		conversation.sendJSONMessage(jab.build().toString());
	}
	
	/**
	 * Functie veranderPresentieStudent haalt de reden, gebruikersnaam, vak en datum op.
	 * 
	 * Als de reden ziek is wordt er in het systeem voor de les de student voor dat les
	 * op die datum voor dat vak op ziek gezet, anders wordt er ook een reden ingevuld
	 * voor zijn afwezigheid, de leraar krijgt deze reden dan ook te zien.
	 * 
	 * @param conversation - De request door de Polymer frontend
	 */

	private void veranderPresentieStudent(Conversation conversation) {
		JsonObject verzoek = (JsonObject) conversation.getRequestBodyAsJSON();

		//Ophalen afmeldingsreden
		int reden = informatieSysteem.translatePresentieStringToInt(verzoek.getString("redenoptie"));
		
		//Ophalen student
		Student student = informatieSysteem.getStudent(verzoek.getString("username"));
		
		//Ophalen vak waarvoor word afgemeld
		Vak vak = student.getVak(verzoek.getString("vak"));
		
		//Ophalen specifieke les waarvoor word afgememeld
		Les les = vak.getLes(
				LocalDate.parse(verzoek.getString("datum")), 
				verzoek.getString("begin"));
		
		//De presentiestatus voor een les aanpassen
		if(reden == 2){
			vak.getPresentieLijstForStudent(student).updatePresentieLijstForLes(les, reden, "Ziek");
		}else{
			vak.getPresentieLijstForStudent(student).updatePresentieLijstForLes(les, reden, verzoek.getString("redenveld"));
		}
		
		
	}

	private void ophalenDocentLessen(Conversation conversation) {
		ophalenLesWeek(conversation, "docent");		
	}
	
	private void ophalenStudentLessen(Conversation conversation) {
		ophalenLesWeek(conversation, "student");
	}
	
	/**
	 * Functie ophalenLesWeek haalt de gebruikersnaam en datum op.
	 * 
	 * Met deze data haalt hij alle lessen op van de gebruiker van de week waarin de datum zit
	 * en stuurt hij deze als JSON object terug.
	 * 
	 * @param conversation - De request door de Polymer frontend
	 */
	
	private void ophalenLesWeek(Conversation conversation, String rol){
    JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		String lStringDate = lJsonObjectIn.getString("date");
		int lDays = lJsonObjectIn.getInt("days");
		
		JsonArrayBuilder lWeekBuilder = Json.createArrayBuilder();
		
		// Hieronder maken we een datum object op basis van de ontvangen datum. 
		LocalDateTime lDate = LocalDateTime.parse(lStringDate.substring(0, lStringDate.length()-2)); // remove the 'Z' from the javascript date object to make it reable for Java
		
		// Haal per dag de lessen op en stop ze in een array
		for(int i = 0; i < lDays; i++){
  		
			// voeg een offset aan de datum toe
			LocalDateTime lDateWithOffset = lDate.plusDays(i);
			
  		ArrayList<Les> lLessen;
			
  		//Alle lessen ophalen via PrIS
  		if(rol.equals("docent")){
				//de rol is docent, dus haal de lessen op via de docent methode
  			lLessen = informatieSysteem.getLessenDocentForSingleDate(lGebruikersnaam, lDateWithOffset);
			} else if (rol.equals("student")){
				//de rol is student, dus haal de lessen op via de student methode
				lLessen = informatieSysteem.getLessenStudentForSingleDate(lGebruikersnaam, lDateWithOffset);
			} else {
				//er is geen rol herkend, dus we gebruiken een lege array. 
				//dit resulteerd in een dag zonder lessen.
				lLessen = new ArrayList<Les>();
			}
			
  		// genereer de lesdag en voeg deze aan de lesweek toe
  		lWeekBuilder.add(transformLessenToJsonArray(lLessen));
		}
		// genereer het antwoord op de request
		String lJsonOutStr = lWeekBuilder.build().toString();
		// stuur het antwoord terug
		conversation.sendJSONMessage(lJsonOutStr);
	}
	
	/**
	 * Maakt van een lijst met lessen (ArrayList<Les>) een JsonArray
	 * 
	 * @param lessen - het te transformeren ArrayLisy<Les>-object
	 * @return (JsonArray) een JasonArray met de lessen
	 */
	private JsonArray transformLessenToJsonArray(ArrayList<Les> lessen){
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		// Controleren of er lessen zijn meegegeven
		if(lessen.isEmpty()){
			//geen lessen
			
			JsonObjectBuilder lJsonObjectBuilder = Json.createObjectBuilder();
			// creeer JsonObject
			lJsonObjectBuilder
				.add("leeg", true);
			
			// Voeg object toe aan de array
			lJsonArrayBuilder.add(lJsonObjectBuilder);
		} else {
			// er zijn lessen meegegeven
			
			// genereer array met JsonObjecten die lessen representeren
  		for (Les les : lessen) {
  		  lJsonArrayBuilder.add(transformLesToJsonObject(les));
  		}
		}
		// geef de JsonArray met alle lessen terug
		return lJsonArrayBuilder.build();
	}
	
	/**
	 * Transformeert een les object naar een JasonObject
	 * 
	 * @param les - Het te transformeren Les-object
	 * @return (JsonObject) het JsonObject op basis van de Les
	 */
	private JsonObject transformLesToJsonObject(Les les){
		LocalDate datum = les.getDatum();
		
		// Stel de formatter in op een datum met de yyyy-MM-dd specificatie
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String stringDatum = datum.format(formatter);
		
		JsonObjectBuilder lJsonObjectBuilder = Json.createObjectBuilder();
		
		Vak lVak = les.getVak();
		
		// genereer het JsonObject
		lJsonObjectBuilder
			.add("datum", stringDatum)
			.add("begin", les.getBegin())
			.add("eind", les.getEind())
			.add("vak", lVak.getNaam())
			.add("docent", lVak.getDocent().getGebruikersnaam())
			.add("lokaal", les.getLokaal())
			.add("klas", lVak.getKlas().getNaam());
		
		// Geef het JsonObject terug
		return lJsonObjectBuilder.build();
	}
}
