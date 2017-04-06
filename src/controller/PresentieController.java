
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
			//docentOphalen(conversation);
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
	
	public void docentOphalen(String gebruikersnaam)  {
		//JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		
		//String lGebruikersnaam = lJsonObjectIn.getString("username");
		
		String lGebruikersnaam = gebruikersnaam;
		
		Docent doc = informatieSysteem.getDocent(lGebruikersnaam);

		ArrayList<Vak> vakken = doc.getVakken();
		
		JsonArrayBuilder JsonArrayDocentVakken = Json.createArrayBuilder();
		ArrayList<ArrayList<Vak>> list = new ArrayList<ArrayList<Vak>>();
		
		for(Vak vak : vakken){
			Boolean flag = true;
			for(ArrayList<Vak> sublist : list){
				if(sublist.get(0).getNaam().equals(vak.getNaam())){
					sublist.add(vak);
					flag = false;
					break;
				}
			}
			if(flag){
				ArrayList<Vak> tmp = new ArrayList<Vak>();
				tmp.add(vak);
				list.add(tmp);
			}
		}
		
		for(ArrayList<Vak> vakkenLijst : list){
			
			JsonArrayBuilder lJsonArrayBuilderVoorPresentie = Json.createArrayBuilder();
			
			for(Vak vak : vakkenLijst){
				int count = 0;
				
				JsonObjectBuilder lJsonObjectBuilderVoorVak = Json.createObjectBuilder();
				
				Klas k = vak.getKlas();
				ArrayList<Student> studenten = k.getStudenten();
				
				JsonObjectBuilder lJsonObjectBuilderVoorStudent = Json.createObjectBuilder();
				
				for(Student stu : studenten){
					JsonArrayBuilder lJsonArrayBuilderVoorLes = Json.createArrayBuilder();
					
  				PresentieLijst lPresentie = vak.getPresentieLijstForStudent(stu);
  				
  				for(Map.Entry<Les, Integer> entry : lPresentie.getPresentieMap().entrySet()){
  					JsonObjectBuilder lBuilder = Json.createObjectBuilder();
  					String s = this.informatieSysteem.translatePresentieIntToString(entry.getValue());
  					
  					lBuilder
  						.add("les", ++count)
  						.add("presentie", s)
  						.add("datum", entry.getKey().getDatum().toString());
  					lJsonArrayBuilderVoorLes.add(lBuilder);
  					lJsonObjectBuilderVoorStudent.add("naam", stu.getGebruikersnaam());
  					lJsonObjectBuilderVoorStudent.add("les", lJsonArrayBuilderVoorLes);
  				}
  
  				//hier toevoegen aan json voor de ene vak
  				
  				lJsonObjectBuilderVoorVak
  					.add("student", lJsonObjectBuilderVoorStudent);
  				
  				lJsonArrayBuilderVoorPresentie.add(lJsonObjectBuilderVoorVak);
  			}
				JsonArrayDocentVakken.add(vak.getNaam());
			}
			JsonArrayDocentVakken.add(lJsonArrayBuilderVoorPresentie);
		}
		String lJsonOutStr = JsonArrayDocentVakken.build().toString();	
		System.out.println(lJsonOutStr);
		//conversation.sendJSONMessage(lJsonOutStr);
	}
}
