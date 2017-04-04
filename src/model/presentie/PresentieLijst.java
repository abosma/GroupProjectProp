package model.presentie;

import java.util.HashMap;
import java.util.Map;

import model.les.Les;
import model.persoon.Student;

public class PresentieLijst {
	private Student student;
	private Map<Les, Integer> presentie;
	
	public PresentieLijst(Student student) {
		this.student = student;
		this.presentie = new HashMap<Les, Integer>();
		
		//Debug line
		//System.out.println("-Presentie-Constructor: " + student.getGebruikersnaam());
	}
	public void addPresentie(Les les, int type){
		if(!hasPresentieForLes(les)){
			this.presentie.put(les, type);
		}
	}
	
	public Student getStudent(){
		return this.student;
	}
	
	public int getPresentieForLes(Les les){
		if(this.presentie.containsKey(les)){
			return (int) this.presentie.get(les);
		}
		return -1;
	}
	
	public boolean hasPresentieForLes(Les les){
		return this.presentie.containsKey(les);
	}
	public void updatePresentieLijstForLes(Les l, int i) {
		if(this.hasPresentieForLes(l)){
			this.presentie.replace(l, i);
		} else {
			this.addPresentie(l, i);
		}
	}
	
	public Map<Les, Integer> getPresentieMap(){
		return this.presentie;
	}
}
