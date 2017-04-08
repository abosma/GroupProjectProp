package model.presentie;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;

import model.les.Les;

public class Presentie {
	private Les les;
	private int code;
	private String reden;

	public Presentie(Les les, int code) {
		this.les = les;
		this.code = code;
		this.reden = "";
	}

	public Presentie(Les l, int i, String reden) {
		this(l, i);
		this.reden = reden;
	}

	public String getReden() {
		return reden;
	}

	public void setReden(String reden) {
		this.reden = reden;
	}
	
	public void setCode(int code) {
		this.code = code;
	}

	public Les getLes() {
		return les;
	}

	public int getCode() {
		return code;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Presentie) {
			Presentie tmp = (Presentie) object;
			return tmp.getLes().equals(this.les);
		}
		return false;
	}

	public void update(int code, String reden) {
		this.code = code;
		this.reden = reden;
	}

	public static Comparator<Presentie> presentieDateComparator = new Comparator<Presentie>() {

		public int compare(Presentie s1, Presentie s2) {
			LocalDate myDate = s1.getLes().getDatum();
			LocalDate extDate = s2.getLes().getDatum();

			ZoneId zoneId = ZoneId.systemDefault();

			long epochA = myDate.atStartOfDay(zoneId).toEpochSecond();
			long epochB = extDate.atStartOfDay(zoneId).toEpochSecond();
			int answer = (int) (epochA - epochB);
			return answer;
		}
	};
}
