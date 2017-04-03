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
			ophalenDocentLesWeek(conversation);
		} else if (conversation.getRequestedURI().startsWith("/student/rooster/lesdagophalen")){
			ophalenStudentLesdag(conversation);
		}
	}
	
	private void ophalenDocentLesWeek(Conversation conversation) {
		ophalenLesWeek(conversation, "docent");		
	}
	
	private void ophalenLesWeek(Conversation conversation, String rol){
    JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		String lStringDate = lJsonObjectIn.getString("date");
		int lDays = lJsonObjectIn.getInt("days");
		
		JsonArrayBuilder lWeekBuilder = Json.createArrayBuilder();
		
		LocalDateTime lDate = LocalDateTime.parse(lStringDate.substring(0, lStringDate.length()-2)); // remove the 'Z' from the javascript date object to make it reable for Java
		
		for(int i = 0; i < lDays; i++){
  		
			if(i>0)lDate = lDate.plusDays(1);
			
  		//Alle lessen ophalen via PrIS
			ArrayList<Les> lLessen;
			if(rol.equals("docent")){
				lLessen = informatieSysteem.getLessenDocentForSingleDate(lGebruikersnaam, lDate);
			} else if (rol.equals("student")){
				lLessen = informatieSysteem.getLessenStudentForSingleDate(lGebruikersnaam, lDate);
			} else {
				lLessen = new ArrayList<Les>();
			}
			
  		lWeekBuilder.add(transformLessenToJsonArray(lLessen));
		}
		String lJsonOutStr = lWeekBuilder.build().toString();
		conversation.sendJSONMessage(lJsonOutStr);
	}
	
	private void ophalenStudentLesdag(Conversation conversation) {
		ophalenLesWeek(conversation, "student");
	}
	
	private JsonArray transformLessenToJsonArray(ArrayList<Les> lessen){
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		if(lessen.isEmpty()){
			JsonObjectBuilder lJsonObjectBuilderVoorDocent = Json.createObjectBuilder();
			lJsonObjectBuilderVoorDocent.add("vak", "Geen Lessen");
			lJsonArrayBuilder.add(lJsonObjectBuilderVoorDocent);
		} else {
  		for (Les les : lessen) {
  		  lJsonArrayBuilder.add(transformLesToJsonObject(les));
  		}
		}
		return lJsonArrayBuilder.build();
	}
	
	private JsonObject transformLesToJsonObject(Les les){
		LocalDateTime beginDatum = les.getBeginDatum();
		LocalDateTime eindDatum = les.getEindDatum();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
		String stringDatum = beginDatum.format(formatter);
		formatter = DateTimeFormatter.ofPattern("HH:mm");
		String stringBegin = beginDatum.format(formatter);
    String StringEind = eindDatum.format(formatter);
		String lesNaam = les.getNaam();
		String docentNaam = les.getDocent().getGebruikersnaam();
		String lokaal = les.getLokaal();
		String klasNaam = les.getKlas().getNaam();
		
		JsonObjectBuilder lJsonObjectBuilder = Json.createObjectBuilder();
		
		lJsonObjectBuilder
			.add("datum", stringDatum)
			.add("begin", stringBegin)
			.add("eind", StringEind)
			.add("vak", lesNaam)
			.add("docent", docentNaam)
			.add("lokaal", lokaal)
			.add("klas", klasNaam);
		
		return lJsonObjectBuilder.build();
	}
}
