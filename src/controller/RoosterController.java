package controller;

import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.klas.Klas;
import model.persoon.Student;
import server.Conversation;

public class RoosterController {
	private PrIS informatieSysteem;
	
	public RoosterController(PrIS infoSys) {
		informatieSysteem = infoSys;
	}
	
	public void handle(Conversation conversation) {
		if (conversation.getRequestedURI().startsWith("/student/rooster")) {
			ophalenStudent(conversation);
		} else if (conversation.getRequestedURI().startsWith("/docent/rooster")){
			ophalenDocent(conversation);
		}
	}

	private void ophalenDocent(Conversation conversation) {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		Student lStudentZelf = informatieSysteem.getStudent(lGebruikersnaam);
		String  lGroepIdZelf = lStudentZelf.getGroepId();
		
		Klas lKlas = informatieSysteem.getKlasVanStudent(lStudentZelf);

    ArrayList<Student> lStudentenVanKlas = lKlas.getStudenten();
		
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();
		
		for (Student lMedeStudent : lStudentenVanKlas) {
			if (lMedeStudent == lStudentZelf)
				continue;
			else {
				String lGroepIdAnder = lMedeStudent.getGroepId();
				boolean lZelfdeGroep = ((lGroepIdZelf != "") && (lGroepIdAnder==lGroepIdZelf));
				JsonObjectBuilder lJsonObjectBuilderVoorStudent = Json.createObjectBuilder();
				String lLastName = lMedeStudent.getVolledigeAchternaam();
				lJsonObjectBuilderVoorStudent
					.add("id", lMedeStudent.getStudentNummer())
					.add("firstName", lMedeStudent.getVoornaam())
					.add("lastName", lLastName)
				  .add("sameGroup", lZelfdeGroep);
			  
			  lJsonArrayBuilder.add(lJsonObjectBuilderVoorStudent);
			}
		}
    String lJsonOutStr = lJsonArrayBuilder.build().toString();
		conversation.sendJSONMessage(lJsonOutStr);
	}

	private void ophalenStudent(Conversation conversation) {
		
	}
	
}
