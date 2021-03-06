package controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import model.PrIS;
import model.klas.Klas;
import model.les.Les;
import server.Conversation;
import server.Handler;

public class SysteemDatumController implements Handler {
	private PrIS informatieSysteem;
	/**
	 * De SysteemDatumController klasse moet alle systeem (en test)-gerelateerde aanvragen
	 * afhandelen. Methode handle() kijkt welke URI is opgevraagd en laat
	 * dan de juiste methode het werk doen. Je kunt voor elke nieuwe URI
	 * een nieuwe methode schrijven.
	 * 
	 * @param infoSys - het toegangspunt tot het domeinmodel
	 */
	public SysteemDatumController(PrIS infoSys) {
		this.informatieSysteem = infoSys;
	}

	public void handle(Conversation conversation) {
	  if (conversation.getRequestedURI().startsWith("/systeemdatum/lesinfo")) {
			ophalenLesInfo(conversation);
		}
	}
	
  private void ophalenLesInfo(Conversation conversation) {
  	//<to do> begin
  	//De volgende statements moeten gewijzigd worden zodat daadwerkelijk de eerste en laatste lesdatum wordt bepaald
  	Calendar lEersteLesDatum = Calendar.getInstance();
		Calendar lLaatsteLesDatum = Calendar.getInstance();
		
		ArrayList<Klas> alleKlassen = informatieSysteem.getAlleKlassen();
		ArrayList<Les> alleLessen = new ArrayList<Les>();
		
		for(Klas k : alleKlassen){
			alleLessen.addAll(k.getAllLessen());
		}
		
		for(Les l : alleLessen){
			LocalDateTime beginDatum = l.getDatum().atTime(LocalTime.parse(l.getBegin()));
			
			Calendar cal = Calendar.getInstance();
			Instant instant = beginDatum.toInstant(ZoneOffset.UTC);
			Date date = Date.from(instant);
			cal.setTime(date);
			
			if(cal.compareTo(lEersteLesDatum) == 0){
				lEersteLesDatum = cal;
			}
			if(cal.compareTo(lEersteLesDatum) == 0){
				lLaatsteLesDatum = cal;
			}
		}
    //<to do> end
		
		JsonObjectBuilder lJsonObjectBuilder = Json.createObjectBuilder();
		//Deze volgorde mag niet worden gewijzigd i.v.m. JS. (Hier mag dus ook geen andere JSON voor komen.)
		lJsonObjectBuilder 
			.add("eerste_lesdatum", PrIS.calToStandaardDatumString(lEersteLesDatum))
			.add("laatste_lesdatum", PrIS.calToStandaardDatumString(lLaatsteLesDatum));

		String lJsonOut = lJsonObjectBuilder.build().toString();
		
		conversation.sendJSONMessage(lJsonOut);										// terugsturen naar de Polymer-GUI!	}
  }
}
