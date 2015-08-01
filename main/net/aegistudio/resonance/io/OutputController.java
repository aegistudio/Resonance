package net.aegistudio.resonance.io;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.device.OutputDevice;
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
}
