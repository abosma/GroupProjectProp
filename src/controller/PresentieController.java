
package controller;


import java.util.ArrayList;
import javax.json.Json;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.persoon.Student;
import model.klas.Klas;

import server.Conversation;
import server.Handler;


public class PresentieController implements Handler   {
	
	

	
	private PrIS informatieSysteem;

	public PresentieController(PrIS infoSys) {
		informatieSysteem = infoSys;
	}

	public void handle(Conversation conversation) {
		System.out.println(conversation.getRequestedURI());
		if (conversation.getRequestedURI().startsWith("/student/presentie")) {
			ophalen(conversation);
		} else {
			System.out.println("ssd");
		}
	}
	
	private void ophalen(Conversation conversation)  {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		//mail student via polymer
		
		Student lStudentZelf = informatieSysteem.getStudent(lGebruikersnaam);
		//Bijbehorende student object
		
		lStudentZelf.getVakken(lGebruikersnaam);
		
		
		
		
		
		
		
		
		System.out.println(lStudentZelf.getGebruikersnaam());
		
		String  lGroepIdZelf = lStudentZelf.getGroepId();
		
		Klas lKlas = informatieSysteem.getKlasVanStudent(lStudentZelf);		// klas van de student opzoeken

    ArrayList<Student> lStudentenVanKlas = lKlas.getStudenten();	// medestudenten opzoeken
		
		JsonArrayBuilder lJsonArrayBuilder = Json.createArrayBuilder();						// Uiteindelijk gaat er een array...
		
		for (Student lMedeStudent : lStudentenVanKlas) {									        // met daarin voor elke medestudent een JSON-object... 
			if (lMedeStudent == lStudentZelf) 																			// behalve de student zelf...
				continue;
			else {
				String lGroepIdAnder = lMedeStudent.getGroepId();
				boolean lZelfdeGroep = ((lGroepIdZelf != "") && (lGroepIdAnder==lGroepIdZelf));
				JsonObjectBuilder lJsonObjectBuilderVoorStudent = Json.createObjectBuilder(); // maak het JsonObject voor een student
				String lLastName = lMedeStudent.getVolledigeAchternaam();
				lJsonObjectBuilderVoorStudent
					.add("id", lMedeStudent.getStudentNummer())																	//vul het JsonObject		     
					.add("firstName", lMedeStudent.getVoornaam())	
					.add("lastName", lLastName)				     
				  .add("sameGroup", lZelfdeGroep);					     
			  
			  lJsonArrayBuilder.add(lJsonObjectBuilderVoorStudent);													//voeg het JsonObject aan het array toe				     
			}
		}
    String lJsonOutStr = lJsonArrayBuilder.build().toString();		
    // maak er een string van
    
		conversation.sendJSONMessage(lJsonOutStr);																				// string gaat terug naar de Polymer-GUI!
	}
}
