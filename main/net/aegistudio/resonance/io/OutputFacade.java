package net.aegistudio.resonance.io;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.device.OutputDevice;
import net.aegistudio.resonance.serial.SerializedObject;

/**
 * Output Facade serves as the facade of the output layer.
 * It initialize the output layer, start, output and stop the
 * device having received control signal from the control layer,
 * etc.
 * @author aegistudio
 */

public interface OutputFacade extends SerializedObject
{
	public void reset(Environment environment, OutputDevice outputDevice);
	
	public void output(Frame frame);
	
	public void stop();
}