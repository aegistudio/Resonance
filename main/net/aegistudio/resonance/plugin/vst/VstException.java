package net.aegistudio.resonance.plugin.vst;

@SuppressWarnings("serial")
public class VstException extends RuntimeException {
	public VstException(String message) {
		super(message);
	}
	
	public VstException(EnumErrorCode code) {
		super(code.toString());
	}
	
	public VstException(Throwable cause) {
		super(cause);
	}
}
