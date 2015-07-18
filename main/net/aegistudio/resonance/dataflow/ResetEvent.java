package net.aegistudio.resonance.dataflow;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.plugin.Event;

public class ResetEvent implements Event
{
	public final Environment environment;
	
	public ResetEvent(Environment environment)
	{
		this.environment = environment;
	}
}
