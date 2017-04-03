
package controller;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import javax.json.Json;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.les.Les;
import server.Conversation;
import server.Handler;


public class PresentieController implements Handler   {
	private PrIS informatieSysteem;

	public PresentieController(PrIS infoSys) {
		informatieSysteem = infoSys;
	}

	public void handle(Conversation conversation) {
		if(conversation.getRequestedURI().startsWith("/student/presentie")) {
			studentOphalen(conversation);
		}if (conversation.getRequestedURI().startsWith("/docent/presentie")) {
			docentOphalen(conversation);
		}
	}
	
	private void studentOphalen(Conversation conversation)  {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		ArrayList<Les> lessenStudent = informatieSysteem.getLessenStudent(lGebruikersnaam);
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		
		for (Les l : lessenStudent) {
			JsonObjectBuilder lJsonObjectBuilderVoorStudent = Json.createObjectBuilder();
			JsonObjectBuilder lJsonObjectBuilderVoorLes = Json.createObjectBuilder();
			
			
			LocalDateTime beginDatum = l.getBeginData();
			LocalDateTime eindDatum = l.getEindData();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    
			String beginDatumString = beginDatum.format(formatter);
	    String eindDatumString = eindDatum.format(formatter);
			String docentNaam = l.getDocent().getGebruikersnaam();
			String lokaal = l.getLokaal();
			
			lJsonObjectBuilderVoorStudent
				.add(l.getNaam(), lJsonObjectBuilderVoorLes)
  				.add("beginDatum", beginDatumString)
  				.add("eindDatum", eindDatumString)
  				.add("docentNaam", docentNaam)
  				.add("lokaal", lokaal);
			for(Map.Entry<String, Integer> pres : l.getPresentie().entrySet()){
    		if(pres.getKey().equals(lGebruikersnaam)){
    			String s = null;
    			switch(pres.getValue()){
    				case 0:
    					s = "Afwezig";
    					break;
    				case 1:
    					s = "Aanwezig";
    					break;
    				case 2:
    					s = "Ziek";
    					break;
    				case 3:
    					s = "Afgemeld";
    					break;
    				case 4:
    					s = "Afgemeld (Niet geaccepteerd)";
    					break;
    			}
    			lJsonObjectBuilderVoorLes.add(pres.getKey(), s);
    		}
    	}
		  lJsonArrayBuilder.add(lJsonObjectBuilderVoorStudent);
		}
    String lJsonOutStr = lJsonArrayBuilder.build().toString();		
    
		conversation.sendJSONMessage(lJsonOutStr);
	}
	
	private void docentOphalen(Conversation conversation)  {
	JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		String lVaknaam = lJsonObjectIn.getString("vaknaam");
		
		ArrayList<Les> lessenStudent = informatieSysteem.getVakDocent(lVaknaam, lGebruikersnaam);
		
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		
		for (Les l : lessenStudent) {
			JsonObjectBuilder lJsonObjectBuilderVoorStudent = Json.createObjectBuilder();
			
			LocalDateTime beginDatum = l.getBeginData();
			LocalDateTime eindDatum = l.getEindData();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    
			String beginDatumString = beginDatum.format(formatter);
	    String eindDatumString = eindDatum.format(formatter);
			String docentNaam = l.getDocent().getGebruikersnaam();
			String lokaal = l.getLokaal();
			
			lJsonObjectBuilderVoorStudent
				.add("beginDatum", beginDatumString)
				.add("eindDatum", eindDatumString)
				.add("docentNaam", docentNaam)
				.add("lokaal", lokaal);
			for(Map.Entry<String, Integer> pres : l.getPresentie().entrySet()){
  			lJsonObjectBuilderVoorStudent.add(pres.getKey(), pres.getValue());
    	}
		  lJsonArrayBuilder.add(lJsonObjectBuilderVoorStudent);
		}
    String lJsonOutStr = lJsonArrayBuilder.build().toString();		
    
		conversation.sendJSONMessage(lJsonOutStr);
	}
}
