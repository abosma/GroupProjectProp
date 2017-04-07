package model.presentie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.les.Les;
import model.persoon.Student;

public class PresentieLijst {
	private Student student;
	private ArrayList<Presentie> presenties;
	
	public PresentieLijst(Student student) {
		this.student = student;
		this.presenties = new ArrayList<Presentie>();
	}
	public void addPresentie(Les les, int type){
		Presentie tmp = new Presentie(les, type);
		if(!this.presenties.contains(tmp)){
			this.presenties.add(tmp);
		}
	}
	
	public Student getStudent(){
		return this.student;
	}
	
	public Presentie getPresentieObjectForLes(Les les){
		for(Presentie p : this.presenties){
			if(p.getLes().equals(les)){
				return p;
			}
		}
		return null;
	}
	
	public int getPresentieForLes(Les les){
		Presentie tmp = this.getPresentieObjectForLes(les);
		if(tmp!=null){
			return tmp.getCode();
		}
		return -1;
	}
	
	public boolean hasPresentieForLes(Les les){
		return this.presenties.contains(new Presentie(les,0));
	}
	
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
	
	public ArrayList<Presentie> getPresenties(){
		return this.presenties;
	}
}
