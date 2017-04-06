
package controller;


import java.util.ArrayList;
import java.util.Map;

import javax.json.Json;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.klas.Klas;
import model.les.Les;
import model.persoon.Docent;
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
			docentOphalen(conversation);
		}
	}
	
	private void studentOphalen(Conversation conversation)  {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		
		Student lStudent = informatieSysteem.getStudent(lGebruikersnaam);

		ArrayList<Vak> vakken = lStudent.getKlas().getVakken();

		JsonArrayBuilder lJsonArrayBuilderVoorPresentie = Json.createArrayBuilder();
		for(Vak vak : vakken){
			int count = 0;		
			
			JsonObjectBuilder lJsonObjectBuilderVoorVak = Json.createObjectBuilder();
			
			//lesnummer(als string bijv:les1) en de aanwezigheid als string
			JsonArrayBuilder lJsonArrayBuilderVoorLes = Json.createArrayBuilder();

			PresentieLijst lPresentie = vak.getPresentieLijstForStudent(lStudent);
			
			for(Map.Entry<Les, Integer> entry : lPresentie.getPresentieMap().entrySet()){
				JsonObjectBuilder lBuilder = Json.createObjectBuilder();
				String s = this.informatieSysteem.translatePresentieIntToString(entry.getValue());
				
				lBuilder
					.add("les", ++count)
					.add("presentie", s)
					.add("datum", entry.getKey().getDatum().toString());
				lJsonArrayBuilderVoorLes.add(lBuilder);
			}

			//hier toevoegen aan json voor de ene vak
			
			lJsonObjectBuilderVoorVak
				.add("vak",vak.getNaam())
				.add("lessen", lJsonArrayBuilderVoorLes);
			
			lJsonArrayBuilderVoorPresentie.add(lJsonObjectBuilderVoorVak);
			
		}
		String lJsonOutStr = lJsonArrayBuilderVoorPresentie.build().toString();	
		System.out.println(lJsonOutStr);
		conversation.sendJSONMessage(lJsonOutStr);
	}
	
	public void docentOphalen(Conversation conversation)  {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		
		Docent doc = informatieSysteem.getDocent(lGebruikersnaam);

		ArrayList<Vak> vakken = doc.getVakken();
		
		JsonObjectBuilder JsonObjectVak = Json.createObjectBuilder();
		
//				"Vak1" : {
//					"Studentnaam" : {
//						"Les" : 0,
//						"Presentie" : Niet geregistreerd,
//						"Datum" : 2017-03-07
//					}
//				}
		
		for(Vak vak : vakken){
			int count = 0;				
			
			Klas k = vak.getKlas();
			ArrayList<Student> studenten = k.getStudenten();
			
			JsonObjectBuilder JsonObjectStudent = Json.createObjectBuilder();
			
			JsonArrayBuilder lJsonArrayBuilderVoorLes = Json.createArrayBuilder();
			
			for(Student stu : studenten){
				
				PresentieLijst lPresentie = vak.getPresentieLijstForStudent(stu);		
				
				for(Map.Entry<Les, Integer> entry : lPresentie.getPresentieMap().entrySet()){
					
					JsonObjectBuilder lBuilder = Json.createObjectBuilder();
					
					String s = this.informatieSysteem.translatePresentieIntToString(entry.getValue());
					
					lBuilder
						.add("les", ++count)
						.add("presentie", s)
						.add("datum", entry.getKey().getDatum().toString());
					lJsonArrayBuilderVoorLes.add(lBuilder);
					
				}
				JsonObjectStudent.add(stu.getGebruikersnaam(), lJsonArrayBuilderVoorLes);
			}
			JsonObjectVak.add(vak.getNaam(), JsonObjectStudent);
		}
		conversation.sendJSONMessage(JsonObjectVak.build().toString());
	}
}
