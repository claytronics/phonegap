package org.apache.cordova.plugin;

import java.util.LinkedList;

public class processDetails {
	
	private int importance;
	private LinkedList<usageTime> llTime = new LinkedList<usageTime>();
	private LinkedList<locationTrack> llLoc = new LinkedList<locationTrack>();
	private LinkedList<accelerometerTrack> llAcc = new LinkedList<accelerometerTrack>();

	public LinkedList<accelerometerTrack> getLlAcc() {
		return llAcc;
	}

	public void setLlAcc(LinkedList<accelerometerTrack> llAcc) {
		this.llAcc = llAcc;
	}

	public LinkedList<locationTrack> getLlLoc() {
		return llLoc;
	}

	public void setLlLoc(LinkedList<locationTrack> llLoc) {
		this.llLoc = llLoc;
	}

	public LinkedList<usageTime> getLlTime() {
		return llTime;
	}

	public void setLlTime(LinkedList<usageTime> llTime) {
		this.llTime = llTime;
	}

	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}
}
