package it.unipi.ing.mim.project5;

import java.util.ArrayList;

public class GroundTruthLists {
	
	
	private ArrayList<String> relevantList;
	private ArrayList<String> ambiguousList;
	
	public GroundTruthLists(ArrayList<String> relevantList, ArrayList<String> ambiguousList){
		this.setRelevantList(relevantList);
		this.setAmbiguousList(ambiguousList);
	}

	public ArrayList<String> getAmbiguousList() {
		return ambiguousList;
	}

	public void setAmbiguousList(ArrayList<String> ambiguousList) {
		this.ambiguousList = ambiguousList;
	}

	public ArrayList<String> getRelevantList() {
		return relevantList;
	}

	public void setRelevantList(ArrayList<String> relevantList) {
		this.relevantList = relevantList;
	}
	
}
