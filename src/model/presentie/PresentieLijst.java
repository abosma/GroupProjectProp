package model.presentie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.les.Les;
import model.persoon.Student;

public class PresentieLijst {
	private Student student;
	private ArrayList<Presentie> presenties;
	
	/**
	 * Constrcutor voor PresentieLijst
	 * 
	 * @param student - Student-object van de bijbehorende student
	 */
	public PresentieLijst(Student student) {
		this.student = student;
		this.presenties = new ArrayList<Presentie>();
	}
	
	/**
	 * Voeg een presentie aan de lijst toe
	 * 
	 * @param les - les waar de presentie bij hoort
	 * @param type - presentiecode
	 */
	public void addPresentie(Les les, int type){
		Presentie tmp = new Presentie(les, type);
		if(!this.presenties.contains(tmp)){
			this.presenties.add(tmp);
		}
	}
	
	/**
	 * Haalt de student op
	 * 
	 * @return (Student) de student
	 */
	public Student getStudent(){
		return this.student;
	}
	
	/**
	 * Haalt de presentie voor een gegeven les op
	 * 
	 * @param les - de les waarvoor de presentie moet worden opgehaald
	 * @return geeft een Presentieobject terug als er een match is, anders null
	 */
	public Presentie getPresentieObjectForLes(Les les){
		for(Presentie p : this.presenties){
			if(p.getLes().equals(les)){
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Haalt de presentiecode voor een les op.
	 * 
	 * @param les - de les waarvoor de rpesentie moet worden gevonden
	 * @return de presentiecode, deze is -1 wanneer er gen preentiecode is gevonden.
	 */
	public int getPresentieForLes(Les les){
		Presentie tmp = this.getPresentieObjectForLes(les);
		if(tmp!=null){
			return tmp.getCode();
		}
		return -1;
	}
	
	/**
	 * Controleer of de PresentieLijst voor de les een Presentie-object heeft.
	 * 
	 * @param les - de les waavoor de Presentie moet worden gezocht
	 * @return true wanneer de PresentieLijst een PResentie heeft voor de gegeven les
	 */
	public boolean hasPresentieForLes(Les les){
		return this.presenties.contains(new Presentie(les,0));
	}
	
	/**
	 * Pas de presentie voor een gegeven les aan, en voeg een reden toe.
	 * 
	 * @param l - de les waarvoor de Presentie moet worden aangepast
	 * @param i - de presentiecode
	 * @param opm - de reden voor de presentiecode
	 */
	public void updatePresentieLijstForLes(Les l, int i, String opm) {
		if(this.hasPresentieForLes(l)){
			for(Presentie p : this.presenties){
				if(p.getLes().equals(l)){
					p.update(i, opm);
					break;
				}
			}
		} else {
			this.presenties.add(new Presentie(l,i,opm));
		}
	}
	
	/**
	 * Pas de presentiecode aan voor een gegeven les in de lijst
	 * 
	 * @param l - les waarvoor de presentiecode meot worden aangepast
	 * @param i - de nieuwe presentiecode
	 */
	public void updatePresentieLijstForLes(Les l, int i) {
		if(this.hasPresentieForLes(l)){
			for(Presentie p : this.presenties){
				if(p.getLes().equals(l)){
					p.update(i, "");
					break;
				}
			}
		} else {
			this.addPresentie(l, i);
		}
	}
	
	/**
	 * Geeft alle geregistreerde Presentie-objecten terug
	 * 
	 * @return ArrayList<Presentie>
	 */
	public ArrayList<Presentie> getPresenties(){
		return this.presenties;
	}
}
 