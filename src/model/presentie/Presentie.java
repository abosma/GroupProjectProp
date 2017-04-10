package model.presentie;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;

import model.les.Les;

public class Presentie {
	private Les les;
	private int code;
	private String reden;

	/**
	 * Constructor voor het Presentie object 
	 * 
	 * @param les - de les waar deze Presentie bij hoort
	 * @param code - de presentiecode
	 */
	public Presentie(Les les, int code) {
		this.les = les;
		this.code = code;
		this.reden = "";
	}

	/**
	 * Constructor voor het Presentie object 
	 * 
	 * @param les - de les waar deze Presentie bij hoort
	 * @param code - de presentiecode
	 * @param reden - de reden voor de presentie
	 */
	public Presentie(Les les, int code, String reden) {
		this(les, code);
		this.reden = reden;
	}

	/**
	 * geeft de reden terug
	 * 
	 * @return (String) de reden
	 */
	public String getReden() {
		return reden;
	}

	/**
	 * Pas de reden aan
	 * 
	 * @param reden - de reden voor een bepaalde presentiecode
	 */
	public void setReden(String reden) {
		this.reden = reden;
	}
	
	/**
	 * pas de presentiecode voor het Presentie-object aan
	 * 
	 * @param code - de nieuwe presentiecode
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Haal de les op, waar dit Presentie-object bij hoort
	 * 
	 * @return (Les) de les
	 */
	public Les getLes() {
		return les;
	}

	/**
	 * Haal de preentiecode op
	 * 
	 * @return (int) de presentiecode
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Controleer of het gegeven object gelijk is aan dit object
	 * 
	 * @param object - het object waarmee dit object vergeleken moet worden
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof Presentie) {
			Presentie tmp = (Presentie) object;
			return tmp.getLes().equals(this.les);
		}
		return false;
	}

	/**
	 * Pas de presentie aan
	 * 
	 * @param code - de nieuwe presentiecode
	 * @param reden - de reden die bij dez presentiecode hoort
	 */
	public void update(int code, String reden) {
		
		if(this.getCode() < 2){
			this.code = code;
			this.reden = reden;
		}
		
	}

	/**
	 * Hiermee kunnen Presentie-objecten in een lijst gesorteerd worden.
	 * De Presenties worden gesorteerd op basis van de datum van het Les-object dat een PResentie-object bezit.
	 * Hiermee word chronologisch gesorteerd.
	 */
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
