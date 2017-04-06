
package controller;


import java.util.ArrayList;
import java.util.Map;

import javax.json.Json;


import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.les.Les;
import model.persoon.Student;
import model.presentie.PresentieLijst;
import model.vak.Vak;
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
			//docentOphalen(conversation);
		}
	}
	
	private void studentOphalen(Conversation conversation)  {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		
		Student lStudent = informatieSysteem.getStudent(lGebruikersnaam);

		ArrayList<Vak> vakken = lStudent.getKlas().getVakken();

		for(Vak vak : vakken){
			int count = 0;
			//Key die het hele JSON bestand in een lijst heeft staan
			
			//Een naam van het vak die een lijst bevat met de lesgegevens
			JsonObjectBuilder lJsonObjectBuilderVoorVak = Json.createObjectBuilder();
			
			//lesnummer(als string bijv:les1) en de aanwezigheid als string
			JsonArrayBuilder lJsonArrayBuilderVoorLes = Json.createArrayBuilder();

			PresentieLijst lPresentie = vak.getPresentieLijstForStudent(lStudent);
			
			for(Map.Entry<Les, Integer> entry : lPresentie.getPresentieMap().entrySet()){
				JsonObjectBuilder lBuilder = Json.createObjectBuilder();
				String s = this.informatieSysteem.translatePresentieIntToString(entry.getValue());
				
				String lesnummer = "Les "+ String.valueOf(++count);
				lBuilder.add(lesnummer, s);
				lJsonArrayBuilderVoorLes.add(lBuilder);
			}
					
			lJsonObjectBuilderVoorVak.add(vak.getNaam(), lJsonArrayBuilderVoorLes);
			//hier toevoegen aan json voor de ene vak
			
			String lJsonOutStr = lJsonObjectBuilderVoorVak.build().toString();	
			
			conversation.sendJSONMessage(lJsonOutStr);
		}
	}
	
	/*
	
	private void docentOphalen(Conversation conversation)  {
	JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		String lVaknaam = lJsonObjectIn.getString("vaknaam");
		
		ArrayList<Les> lessenStudent = informatieSysteem.getVakDocent(lVaknaam, lGebruikersnaam);
		
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		
		for (Les l : lessenStudent) {
			JsonObjectBuilder lJsonObjectBuilderVoorStudent = Json.createObjectBuilder();
			
			LocalDateTime beginDatum = l.getBeginDatum();
			LocalDateTime eindDatum = l.getEindDatum();
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
//    System.out.println(lJsonOutStr);
    
		conversation.sendJSONMessage(lJsonOutStr);
	}*/
}
