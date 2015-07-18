package net.aegistudio.resonance.dataflow;

import net.aegistudio.resonance.Frame;

public class BlendStripNode extends StripNode
{
	double dryRatio = 1.0;
	double wetRatio = 0.0;
	
	public void setRatio(double dry, double wet)
	{
		this.dryRatio = dry;
		this.wetRatio = wet;
	}
	
	@Override
	public void pass(Frame input) {
		if(containedPlugin != null)
		{
			this.containedPlugin.process(input, writeFrame);
			writeFrame.mix(input, dryRatio, wetRatio);
			this.outputNode.pass(writeFrame);
		}
		else outputNode.pass(input);
	}
}
