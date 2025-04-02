package at.fontain.bahnhof.data;

import java.util.ArrayList;
import java.util.List;

public class Bahnhof {
	private String name;
	private List<Zug> zugList;

	// getter & setter
	public List<Zug> getZugList() {
		if (zugList == null) {
			setZugList(new ArrayList<>());
		}
		return zugList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setZugList(List<Zug> zugList) {
		this.zugList = zugList;
	}
}
