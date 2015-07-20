package net.aegistudio.resonance.mixer;

import net.aegistudio.resonance.dataflow.DataflowNode;
import net.aegistudio.resonance.dataflow.DrainNode;
import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;

public class Track implements SerializedObject
{
	public final DataflowNode trackSourceNode;
	public final DataflowNode trackDrainNode;
	
	public Track()
	{
		this.trackSourceNode = new DrainNode();
		this.trackDrainNode = trackSourceNode;
	}
	
	@Override
	public void load(Structure input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Structure output) {
		// TODO Auto-generated method stub
		
	}
	
}
