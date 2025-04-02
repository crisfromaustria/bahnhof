package at.fontain.bahnhof.data;

import java.util.ArrayList;
import java.util.List;

public class Zug {
	private String name;
	private Lokomotive lokomotive;
	private List<Wagoon> wagoonList;

	// getter & setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Lokomotive getLokomotive() {
		return lokomotive;
	}

	public void setLokomotive(Lokomotive lokomotive) {
		this.lokomotive = lokomotive;
	}

	public List<Wagoon> getWagoonList() {
		if (wagoonList == null) {
			setWagoonList(new ArrayList<>());
		}
		return wagoonList;
	}

	public void setWagoonList(List<Wagoon> wagoonList) {
		this.wagoonList = wagoonList;
	}
}
