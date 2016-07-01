package net.aegistudio.resonance.plugin.vst;

import java.io.IOException;
import java.io.OutputStream;

/**
 * see also vst/vstprotocol.h.
 * 
 * @author aegistudio
 *
 */
public enum EnumOperation {
	TERMINATE,
	METADATA,
	
	PROCESS,
	PROCESS_EMPTY,
	
	OPEN,
	CLOSE,
	SUSPEND,
	RESUME,
	
	LIST_PARAMS;
	
	public void write(OutputStream output) throws IOException {
		output.write(ordinal());
		output.flush();
	}
}
