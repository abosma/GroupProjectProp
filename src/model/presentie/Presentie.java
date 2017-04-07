package model.presentie;

import model.les.Les;

public class Presentie {
	private Les les;
	private int code;
	private String reden;
	
	public Presentie(Les les, int code){
		this.les = les;
		this.code = code;
		this.reden = "";
	}

	public Presentie(Les l, int i, String reden) {
		this(l,i);
		this.reden = reden;
	}

	public String getReden() {
		return reden;
	}

	public void setReden(String reden) {
		this.reden = reden;
	}

	public Les getLes() {
		return les;
	}

	public int getCode() {
		return code;
	}
	
	@Override
	public boolean equals(Object object){
		if(object instanceof Presentie){
			Presentie tmp = (Presentie) object;
			return tmp.getLes().equals(this.les);
		}
		return false;
	}

	public void update(int code, String reden) {
		this.code = code;
		this.reden = reden;		
	}
}
