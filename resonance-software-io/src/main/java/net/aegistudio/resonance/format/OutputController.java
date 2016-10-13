package net.aegistudio.resonance.format;

import net.aegistudio.resonance.common.Environment;
import net.aegistudio.resonance.common.Frame;
import net.aegistudio.resonance.common.OutputDevice;
import net.aegistudio.resonance.common.OutputFacade;
import net.aegistudio.resonance.serial.Structure;

public class OutputController implements OutputFacade
{
	@Override
	public void load(Structure output) {
		// No Data To Load.
	}

	@Override
	public void save(Structure output) {
		// No Data To Write.
	}
	
	OutputDevice outputDevice;
	OutputFormat format;
	byte[] buffer;
	
	@Override
	public void reset(Environment environment, OutputDevice outputDevice) {
		if(this.outputDevice != null)
			this.outputDevice.destroy();
		this.outputDevice = outputDevice;
		this.outputDevice.create(environment);
		
		format = Format.formats.get(environment.wordEncoding.encoding);
		if(format == null) throw new IllegalArgumentException("No matching format for the given word type.");
		buffer = new byte[environment.getByteBufferSize()];
	}

	@Override
	public synchronized void output(Frame frame) {
		format.write(frame, buffer);
		outputDevice.write(buffer);
	}

	@Override
	public void stop() {
		if(this.outputDevice != null)
			this.outputDevice.stop();
	}

	@Override
	public OutputDevice getOutputDevice() {
		return outputDevice;
	}
}
