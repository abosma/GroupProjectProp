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
		
		System.out.println("DEBUG[1]: -");
		System.out.println("DEBUG[2]: currentPresentieVoorLes verzoek: " + verzoek.toString());
		
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
		System.out.println(ja.toString());
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

	private void veranderPresentieStudent(Conversation conversation) {
		JsonObject verzoek = (JsonObject) conversation.getRequestBodyAsJSON();
		System.out.println("veranderPresentieStudent "+verzoek.toString());
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
		
		System.out.println("veranderPresentieStudent "+les.getDatum().toString());
		
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
