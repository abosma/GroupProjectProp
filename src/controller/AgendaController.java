package controller;

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
		Student lStudentZelf = informatieSysteem.getStudent(lGebruikersnaam);
		
		JsonObjectBuilder lJsonObjectBuilderForReply = Json.createObjectBuilder();
		lJsonObjectBuilderForReply
			.add("firstName", lStudentZelf.getVoornaam())
			.add("lastName", lStudentZelf.getVolledigeAchternaam());
		
		String out = lJsonObjectBuilderForReply.build().toString();
		
		System.out.println("unbuilt: " + lJsonObjectBuilderForReply.toString());
		System.out.println("  built: " + out);
		
		conversation.sendJSONMessage(out);
	}
}
