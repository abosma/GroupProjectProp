
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
			
			ArrayList<Presentie> presenties = lPresentie.getPresenties();
			
			Collections.sort(presenties, Presentie.presentieDateComparator);
			
			
			for(Presentie p: presenties){
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

		ArrayList<ArrayList<Vak>> list = new ArrayList<ArrayList<Vak>>();
		
		for(Vak vak : vakken){
			boolean gesorteerd = false;
			for(ArrayList<Vak> sublist : list){
				if(sublist.get(0).getNaam().equals(vak.getNaam())){
					sublist.add(vak);
					gesorteerd = true;
					break;
				}
			}
			if(!gesorteerd){
				ArrayList<Vak> tmp = new ArrayList<Vak>();
				tmp.add(vak);
				list.add(tmp);
			}
		}
		JsonArrayBuilder jabAlleGroepen = Json.createArrayBuilder();
		for(ArrayList<Vak> vakkenLijst : list){
			
			JsonArrayBuilder jabGroepVakken = Json.createArrayBuilder();
			
			for(Vak vak : vakkenLijst){				
				JsonArrayBuilder jabStudentPresentiesVoorKlas = Json.createArrayBuilder();
				
				Klas klas = vak.getKlas();
				
				for(Student student : klas.getStudenten()){
					//presentie array
  				ArrayList<Presentie> presenties = vak.getPresentieLijstForStudent(student).getPresenties();
  				// sorteren
  				Collections.sort(presenties, Presentie.presentieDateComparator);
  				// lessen counter
  				int count = 0;
  				
  				//ArrayBuilder voor presenties
  				JsonArrayBuilder jabPresenties = Json.createArrayBuilder();
  				
  				for(Presentie p : presenties){
  					JsonObjectBuilder jobEnkelePresentie = Json.createObjectBuilder();
  					
  					jobEnkelePresentie
  						.add("les", ++count)
  						.add("presentie", informatieSysteem.translatePresentieIntToString(p.getCode()))
  						.add("datum", p.getLes().getDatum().toString())
  						.add("reden", p.getReden());
  					
  					jabPresenties.add(jobEnkelePresentie);
  				}
  				//aanmeken json object builder
  				JsonObjectBuilder jobStudent = Json.createObjectBuilder();
  				//Toevoegen presentielijst aan student object
  				jobStudent
  					.add("naam", student.getGebruikersnaam())
						.add("lessen", jabPresenties);
  
  				//Toevoegen studentobject aan de array met alle studentpresenties vor het vak
  				jabStudentPresentiesVoorKlas
  					.add(jobStudent);
  			}
				JsonObjectBuilder jobPresentieVoorKlas = Json.createObjectBuilder();
				
				jobPresentieVoorKlas
					.add("klas", klas.getNaam())
					.add("presentie", jabStudentPresentiesVoorKlas);
				
				jabGroepVakken.add(jobPresentieVoorKlas);
			}
			JsonObjectBuilder jobGroepVakken = Json.createObjectBuilder();
			
			jobGroepVakken
				.add("vak", vakkenLijst.get(0).getNaam())
				.add("klassen", jabGroepVakken);
			
			jabAlleGroepen.add(jobGroepVakken);
		}
		
		String lJsonOutStr = jabAlleGroepen.build().toString();	
		//System.out.println(lJsonOutStr);
		conversation.sendJSONMessage(lJsonOutStr);
	}
}
