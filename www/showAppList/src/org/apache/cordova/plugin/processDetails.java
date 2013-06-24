package org.apache.cordova.plugin;

import java.util.LinkedList;

public class processDetails {
	
	private int importance;
	private LinkedList<usageTime> ll = new LinkedList<usageTime>();

	public LinkedList<usageTime> getLl() {
		return ll;
	}

	public void setLl(LinkedList<usageTime> ll) {
		this.ll = ll;
	}

	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}
}
