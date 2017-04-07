
package controller;


import java.util.ArrayList;
import java.util.Collections;

import javax.json.Json;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.klas.Klas;
import model.persoon.Docent;
import model.persoon.Student;
import model.presentie.Presentie;
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
		System.out.println(conversation.getRequestedURI());
		if(conversation.getRequestedURI().startsWith("/student/presentie")) {
			studentOphalen(conversation);
		}if (conversation.getRequestedURI().startsWith("/docent/presentie")) {
			docentOphalen(conversation);
		}
	}
	
	private void studentOphalen(Conversation conversation)  {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		System.out.println(lGebruikersnaam);
		
		Student lStudent = informatieSysteem.getStudent(lGebruikersnaam);

		ArrayList<Vak> vakken = lStudent.getKlas().getVakken();

		JsonArrayBuilder lJsonArrayBuilderVoorPresentie = Json.createArrayBuilder();
		for(Vak vak : vakken){
			int aui = 0;
			
			JsonObjectBuilder lJsonObjectBuilderVoorVak = Json.createObjectBuilder();
			
			//lesnummer(als string bijv:les1) en de aanwezigheid als string
			JsonArrayBuilder lJsonArrayBuilderVoorLes = Json.createArrayBuilder();

			PresentieLijst lPresentie = vak.getPresentieLijstForStudent(lStudent);
			
			for(Presentie p: lPresentie.getPresenties()){
				JsonObjectBuilder lBuilder = Json.createObjectBuilder();
				String s = this.informatieSysteem.translatePresentieIntToString(p.getCode());
				
				String lesnummer = "Les "+ (++aui);
				lBuilder
					.add("les", lesnummer)
					.add("presentie", s)
					.add("datum", p.getLes().getDatum().toString())
					.add("reden", p.getReden());
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
		System.out.println(lGebruikersnaam);
		
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
  				
  				ArrayList<Presentie> presenties = lPresentie.getPresenties();
  				
  				Collections.sort(presenties, Presentie.presentieDateComparator);
  				
  				for(Presentie p : lPresentie.getPresenties()){
  					JsonObjectBuilder lBuilder = Json.createObjectBuilder();
  					String s = this.informatieSysteem.translatePresentieIntToString(p.getCode());
  					
  					lBuilder
  						.add("les", ++count)
  						.add("presentie", s)
  						.add("datum", p.getLes().getDatum().toString())
  						.add("reden", p.getReden());
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
