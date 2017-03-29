package controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.json.Json;
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
		if (conversation.getRequestedURI().startsWith("/student/rooster/ophalen")) {
			ophalenStudent(conversation);
		} else if (conversation.getRequestedURI().startsWith("/docent/rooster/ophalen")){
			ophalenDocent(conversation);
		}
	}

	private void ophalenDocent(Conversation conversation) {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		//Geeft alle lessen terug van docent met username van lJsonObjectIn
		ArrayList<Les> lessenDocent = informatieSysteem.getLessenDocent(lGebruikersnaam);
		
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		
		for (Les l : lessenDocent) {
			//Dit is om het begin en eindtijd op te halen
			ArrayList<LocalDateTime> tempArray = l.getLesData();
			
			//0 is begintijd, 1 is eindtijd in l.getLesData()
			LocalDateTime beginDatum = tempArray.get(0);
			LocalDateTime eindDatum = tempArray.get(1);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      
			//Hier wordt de LocalDateTime naar een string omgezet, dus van 01-01-2000T00:00:00 naar 2000-01-01 00:00:00
			String beginDataString = beginDatum.format(formatter);
      String eindDataString = eindDatum.format(formatter);
			String lesNaam = l.getNaam();
			String docentNaam = l.getDocent().getGebruikersnaam();
			String lokaal = l.getLokaal();
			String klasNaam = l.getKlas().getNaam();
			
			//Hier voeg ik alle strings toe aan de JsonObjectBuilder zodat Polymer alles kan gebruiken
			JsonObjectBuilder lJsonObjectBuilderVoorDocent = Json.createObjectBuilder();
			lJsonObjectBuilderVoorDocent
				.add("beginDatum", beginDataString)
				.add("eindDatum", eindDataString)
				.add("lesNaam", lesNaam)
				.add("docentNaam", docentNaam)
				.add("lokaal", lokaal)
				.add("klasNaam", klasNaam);
		  
		  lJsonArrayBuilder.add(lJsonObjectBuilderVoorDocent);
		}
    String lJsonOutStr = lJsonArrayBuilder.build().toString();
		conversation.sendJSONMessage(lJsonOutStr);
	}

	private void ophalenStudent(Conversation conversation) {
		//Hetzelfde als ophalenDocent behalve dat het getLessenStudent gebruikt in plaats van getLessenDocent
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		int lWeekNummer = lJsonObjectIn.getInt("weeknummer");
		int lJaarNummer = lJsonObjectIn.getInt("jaarnummer");
		ArrayList<Les> lessenStudent = informatieSysteem.getLessenStudentWeek(lGebruikersnaam, lWeekNummer, lJaarNummer);
		
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		
		for (Les l : lessenStudent) {
			ArrayList<LocalDateTime> tempArray = l.getLesData();
			
			LocalDateTime beginDatum = tempArray.get(0);
			LocalDateTime eindDatum = tempArray.get(1);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    
			String beginDatumString = beginDatum.format(formatter);
      String eindDatumString = eindDatum.format(formatter);
			String lesNaam = l.getNaam();
			String docentNaam = l.getDocent().getGebruikersnaam();
			String lokaal = l.getLokaal();
			String klasNaam = l.getKlas().getNaam();
			
			JsonObjectBuilder lJsonObjectBuilderVoorStudent = Json.createObjectBuilder();
			lJsonObjectBuilderVoorStudent
				.add("beginDatum", beginDatumString)
				.add("eindDatum", eindDatumString)
				.add("lesNaam", lesNaam)
				.add("docentNaam", docentNaam)
				.add("lokaal", lokaal)
				.add("klasNaam", klasNaam);
		  
		  lJsonArrayBuilder.add(lJsonObjectBuilderVoorStudent);
		}
    String lJsonOutStr = lJsonArrayBuilder.build().toString();
		conversation.sendJSONMessage(lJsonOutStr);
	}
}
