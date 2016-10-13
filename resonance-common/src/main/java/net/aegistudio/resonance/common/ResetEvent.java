package net.aegistudio.resonance.common;

public class ResetEvent implements Event {
	public final Environment environment;
	
	public ResetEvent(Environment environment) {
		this.environment = environment;
	}
}
