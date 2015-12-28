package com.nerdery.smartecarte.dcb;

public class DcbDevice {
	public enum State {
		ON, OFF
	}
	
	public final Integer id;
	public State state = State.OFF;
	
	public DcbDevice(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Device [id=" + id + ", state=" + state + "]";
	}
}
