
package controller;


import java.text.DecimalFormat;
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
 
	/**
	 * De PresentieController klasse moet alle presentie-gerelateerde aanvragen
	 * afhandelen. Methode handle() kijkt welke URI is opgevraagd en laat
	 * dan de juiste methode het werk doen. Je kunt voor elke nieuwe URI
	 * een nieuwe methode schrijven.
	 * 
	 * @param infoSys - het toegangspunt tot het domeinmodel
	 */
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
	
	/**
	 * Functie StudentOphalen gebruikt de opgestuurde aanvraag om data op te halen
	 * 
	 * Uit de request wordt de gebruikersnaam gehaald en wordt er een JSON-object
	 * gecreeerd en teruggestuurd.
	 * 
	 * In dit JSON-Object zit er een Les en Vak object in samen met de presenties.
	 * 
	 * @param conversation - De request door de Polymer frontend
	 */
	
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
			
			ArrayList<Presentie> presenties = lPresentie.getPresenties();
			
			int lessenAanwezig = 0;
			int totaalAantalLessen = 0;
			
			Collections.sort(presenties, Presentie.presentieDateComparator);
			
			
			for(Presentie p: presenties){
				JsonObjectBuilder lBuilder = Json.createObjectBuilder();
				String s = this.informatieSysteem.translatePresentieIntToString(p.getCode());
				
				String lesnummer = "Les "+ (++count);
				lBuilder
					.add("les", lesnummer)
					.add("presentie", s)
					.add("datum", p.getLes().getDatum().toString())
					.add("reden", p.getReden());
				lJsonArrayBuilderVoorLes.add(lBuilder);

				totaalAantalLessen++;
				if(p.getCode() == 1){
					lessenAanwezig++;
				}
			}
			DecimalFormat formatter = new DecimalFormat("###.##");
			lJsonObjectBuilderVoorVak
				.add("vak",vak.getNaam())
				.add("lessen", lJsonArrayBuilderVoorLes)
			  
				.add("percentage", formatter.format(((double)lessenAanwezig/(double)totaalAantalLessen)*100).toString());
			
			lJsonArrayBuilderVoorPresentie.add(lJsonObjectBuilderVoorVak);
			
		}
		String lJsonOutStr = lJsonArrayBuilderVoorPresentie.build().toString();	
		conversation.sendJSONMessage(lJsonOutStr);
	}
	
	/**
	 * Functie DocentOphalen gebruikt de opgestuurde aanvraag om data op te halen
	 * 
	 * Uit de request wordt de gebruikersnaam gehaald en wordt er een JSON-object
	 * gecreeerd en teruggestuurd.
	 * 
	 * In dit JSON-Object zit er een Klas object met daaronder een Vak object 
	 * en daaronder een Les Object in samen met de presenties.
	 * 
	 * @param conversation - De request door de Polymer frontend
	 */	
	public void docentOphalen(Conversation conversation)  {
		JsonObject lJsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		
		String lGebruikersnaam = lJsonObjectIn.getString("username");
		
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
  				int totaalAantalLessen = 0;
  				int lessenAanwezig = 0;
  				//ArrayBuilder voor presenties
  				JsonArrayBuilder jabPresenties = Json.createArrayBuilder();
  				
  				for(Presentie presentie : presenties){
  					if(presentie.getCode() == 1)lessenAanwezig++; // '1' betekent aanwezig
  					
  					JsonObjectBuilder jobEnkelePresentie = Json.createObjectBuilder();
  					
  					jobEnkelePresentie
  						.add("les", ++totaalAantalLessen)
  						.add("presentie", informatieSysteem.translatePresentieIntToString(presentie.getCode()))
  						.add("code", presentie.getCode())
  						.add("datum", presentie.getLes().getDatum().toString())
  						.add("reden", presentie.getReden());
  					
  					jabPresenties.add(jobEnkelePresentie);
  				}
  				//aanmeken json object builder
  				JsonObjectBuilder jobStudent = Json.createObjectBuilder();
  				DecimalFormat formatter = new DecimalFormat("##0.#");
  				//Toevoegen presentielijst aan student object
  				jobStudent
  					.add("naam", student.getVoornaam() + " " + student.getVolledigeAchternaam())
  					.add("email", student.getGebruikersnaam())
						.add("lessen", jabPresenties)
  					.add("percentage", formatter.format(((double)lessenAanwezig/(double)totaalAantalLessen)*100).toString());  				//Toevoegen studentobject aan de array met alle studentpresenties vor het vak
  				jabStudentPresentiesVoorKlas
  					.add(jobStudent);
  			}
				JsonObjectBuilder jobPresentieVoorKlas = Json.createObjectBuilder();
				
				jobPresentieVoorKlas
					.add("klas", klas.getNaam())
					.add("studenten", jabStudentPresentiesVoorKlas);
				
				jabGroepVakken.add(jobPresentieVoorKlas);
			}
			JsonObjectBuilder jobGroepVakken = Json.createObjectBuilder();
			
			jobGroepVakken
				.add("vak", vakkenLijst.get(0).getNaam())
				.add("klassen", jabGroepVakken);
			
			jabAlleGroepen.add(jobGroepVakken);
		}
		
		String lJsonOutStr = jabAlleGroepen.build().toString();	
		conversation.sendJSONMessage(lJsonOutStr);
	}
}
