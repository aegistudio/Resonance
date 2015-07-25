package net.aegistudio.resonance.mixer;

import net.aegistudio.resonance.OrderedNamedHolder;
import net.aegistudio.resonance.dataflow.DataflowNode;
import net.aegistudio.resonance.dataflow.DrainNode;
import net.aegistudio.resonance.dataflow.SourceNode;
import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;

public class Track extends OrderedNamedHolder<Effect> implements SerializedObject
{
	public final DataflowNode trackSourceNode;
	public final DataflowNode trackDrainNode;
	
	public Track()
	{
		super("effect", false);
		this.trackSourceNode = new DrainNode();
		this.trackDrainNode = new SourceNode();
		
		this.trackSourceNode.addOutputNode(trackDrainNode);
		this.trackDrainNode.addInputNode(trackSourceNode);
	}
	
	@Override
	public void load(Structure input) {
		super.load(input);
	}

	@Override
	public void save(Structure output) {
		super.save(output);
	}

	@Override
	protected Effect newObject(Class<?> clazz) {
		return new Effect();
	}
	
	/**
	 * Get the input port of a effect or the output port of this track.
	 * @param index the index of the plugin
	 * @return the flow node.
	 */
	protected DataflowNode getInputNode(int index)
	{
		if(index < 0 || index > super.orderList.size()) return null;
		if(index == super.orderList.size()) return this.trackDrainNode;
		return super.get(orderList.get(index)).sourceNode;
	}
	
	/**
	 * Get the output port of a effect or the input port of this track.
	 * @param index the index of the plugin
	 * @return the flow node.
	 */
	protected DataflowNode getOutputNode(int index)
	{
		if(index == -1) return this.trackSourceNode;
		if(index >= super.orderList.size()) return null;
		return super.get(orderList.get(index)).drainNode;
	}

	public Effect create(String name, Class<? extends Effect> clazz)
	{
		DataflowNode inputNode = getInputNode(this.orderList.size());
		DataflowNode outputNode = getOutputNode(this.orderList.size() - 1);
		
		Effect creation = super.create(name);
		
		outputNode.removeOutputNode(inputNode);
		inputNode.removeInputNode(outputNode);
		
		outputNode.addOutputNode(creation.sourceNode);
		creation.sourceNode.addInputNode(creation.sourceNode);
		
		inputNode.addInputNode(creation.drainNode);
		creation.drainNode.addOutputNode(inputNode);
		
		return creation;
	}
	
	public void remove(String name)
	{
		
	}
}
