package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.les.Les;
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
		LocalDateTime beginDatum = les.getBeginDatum();
		LocalDateTime eindDatum = les.getEindDatum();
		
		// Stel de formatter in op een datum met de yyyy-MM-dd specificatie
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String stringDatum = beginDatum.format(formatter);
		
		// Stel de formatter in op uren en minutes teruggeven in het formaat HH:mm
		formatter = DateTimeFormatter.ofPattern("HH:mm");
		String stringBegin = beginDatum.format(formatter);
    String StringEind = eindDatum.format(formatter);
		
		JsonObjectBuilder lJsonObjectBuilder = Json.createObjectBuilder();
		
		// genereer het JsonObject
		lJsonObjectBuilder
			.add("datum", stringDatum)
			.add("begin", stringBegin)
			.add("eind", StringEind)
			.add("vak", les.getNaam())
			.add("docent", les.getDocent().getGebruikersnaam())
			.add("lokaal", les.getLokaal())
			.add("klas", les.getKlas().getNaam());
		
		// Geef het JsonObject terug
		return lJsonObjectBuilder.build();
	}
	
	private void veranderPresentieStudent(Conversation conversation){
		JsonObject JsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		ArrayList<Les> lessen = informatieSysteem.getAlleLessen();
		
		String beginDatum = JsonObjectIn.getString("datum") + " " + JsonObjectIn.getString("begin");
		String eindDatum = JsonObjectIn.getString("datum") + " " + JsonObjectIn.getString("eind");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		
		LocalDateTime bDatum = LocalDateTime.parse(beginDatum, formatter);
		LocalDateTime eDatum = LocalDateTime.parse(eindDatum, formatter);
		
		String gNaam = JsonObjectIn.getString("username");
		String vNaam = JsonObjectIn.getString("vak");
		String pNummer = JsonObjectIn.getString("redenoptie");
		
		int presentieID = 0;
		
		if(pNummer.equals("Ziek")){
			presentieID = 2;
		}else{
			presentieID = 3;
		}
		
		for(Les l : lessen){
			if(l.getBeginDatum().equals(bDatum) && l.getEindDatum().equals(eDatum) && l.getNaam().equals(vNaam)){
				l.veranderPresentie(gNaam, presentieID);
				System.out.println(l.getPresentie());
			}
		}
		
		conversation.sendJSONMessage("true");
	}
	
	private void ophalenPresentieStudenten(Conversation conversation){
		JsonObject JsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		ArrayList<Les> lessen = informatieSysteem.getAlleLessen();
		
		String beginDatum = JsonObjectIn.getString("datum") + " " + JsonObjectIn.getString("begin");
		String eindDatum = JsonObjectIn.getString("datum") + " " + JsonObjectIn.getString("eind");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		
		LocalDateTime bDatum = LocalDateTime.parse(beginDatum, formatter);
		LocalDateTime eDatum = LocalDateTime.parse(eindDatum, formatter);
		
		String vNaam = JsonObjectIn.getString("vak");
		
		JsonArrayBuilder JsonArrayVoorDocent = Json.createArrayBuilder();
		
		for(Les l : lessen){
			if(l.getBeginDatum().equals(bDatum) && l.getEindDatum().equals(eDatum) && l.getNaam().equals(vNaam)){
				for(Map.Entry<String, Integer> pres : l.getPresentie().entrySet()){
					JsonObjectBuilder JsonObjectVoorDocent = Json.createObjectBuilder();
					
					boolean tempBool = false;
					
					if(pres.getValue() == 1){
						tempBool = true;
					}else{
						tempBool = false;
					}
					
					JsonObjectVoorDocent.add("naam", pres.getKey());
					JsonObjectVoorDocent.add("aanwezig", tempBool);
					
	  			JsonArrayVoorDocent.add(JsonObjectVoorDocent);
	    	}
			}
		}
		conversation.sendJSONMessage(JsonArrayVoorDocent.build().toString());
	}
}
