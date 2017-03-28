package controller;

import java.time.LocalDateTime;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.persoon.Student;
import server.Conversation;
import server.Handler;

public class AgendaController implements Handler{

	private PrIS informatieSysteem;
	
	public AgendaController(PrIS infoSysteem) {
		this.informatieSysteem = infoSysteem;
	}

	@Override
	public void handle(Conversation conversation) {
		if(conversation.getRequestedURI().startsWith("/student/rooster/dagOphalen")){
			ophalen(conversation);
		}
	}

	private void ophalen(Conversation conversation) {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		String lDatum = lJsonObjectIn.getString("date");
		String[] parts = lDatum.split("-");
		
		LocalDateTime lLocalDate = LocalDateTime.of(
				Integer.parseInt(parts[2]),		//jaar
				Integer.parseInt(parts[1]),		//maand - 1, want hava telt vanaf 0 bij maanden...
				Integer.parseInt(parts[0]),		//dag - 0, want java telt vanaf 1 bij dagen.. *zucht*
				0, 0); //Rest
		
		System.out.println(lGebruikersnaam + " " + lLocalDate.toString());
		
		Student lStudentZelf = informatieSysteem.getStudent(lGebruikersnaam);
		
		/* NB deze code is nog niet af */ 
		
		JsonObjectBuilder lJsonObjectBuilderForReply = Json.createObjectBuilder();
		/*
		lJsonObjectBuilderForReply
			.add("firstName", lStudentZelf.getVoornaam())
			.add("lastName", lStudentZelf.getVolledigeAchternaam());
		*/
		String out = lJsonObjectBuilderForReply.add("firstname","Pim").build().toString();
		
		conversation.sendJSONMessage(out);
	}
}
