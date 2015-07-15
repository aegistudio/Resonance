package net.aegistudio.resonance.output;

import java.util.TreeMap;

import net.aegistudio.resonance.Encoding;
import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.device.OutputDevice;
import net.aegistudio.resonance.serial.Structure;

public class OutputController implements OutputFacade
{
	protected TreeMap<Integer, Format> formats = new TreeMap<Integer, Format>();
	
	public OutputController()
	{
		Format signedByteFormat = new ByteFormat(true);
		formats.put(Encoding.BITDEPTH_BIT8 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_LITTLE, signedByteFormat);
		formats.put(Encoding.BITDEPTH_BIT8 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG, signedByteFormat);
		
		Format unsignedByteFormat = new ByteFormat(false);
		formats.put(Encoding.BITDEPTH_BIT8 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_LITTLE, unsignedByteFormat);
		formats.put(Encoding.BITDEPTH_BIT8 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_BIG, unsignedByteFormat);
		
		formats.put(Encoding.BITDEPTH_BIT16 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_LITTLE, new WordFormat(false, false));
		formats.put(Encoding.BITDEPTH_BIT16 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_LITTLE, new WordFormat(false, true));
		formats.put(Encoding.BITDEPTH_BIT16 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_BIG, new WordFormat(true, false));
		formats.put(Encoding.BITDEPTH_BIT16 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG, new WordFormat(true, true));
		
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_LITTLE, new DoubleWordFormat(false, false));
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_LITTLE, new DoubleWordFormat(false, true));
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_BIG, new DoubleWordFormat(true, false));
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG, new DoubleWordFormat(true, true));
	}

	@Override
	public void load(Structure output) {
		// No Data To Load.
	}

	@Override
	public void save(Structure output) {
		// No Data To Write.
	}
	
	OutputDevice outputDevice;
	Format format;
	byte[] buffer;
	
	@Override
	public void reset(Environment environment, OutputDevice outputDevice) {
		if(this.outputDevice != null)
			this.outputDevice.destroy();
		this.outputDevice = outputDevice;
		this.outputDevice.create(environment);
		
		format = formats.get(environment.wordEncoding.encoding);
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
