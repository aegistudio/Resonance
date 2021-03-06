package net.aegistudio.resonance.plugin.vst;

/**
 * see also vst/vstprotocol.h.
 * 
 * @author aegistudio
 *
 */
public enum EnumErrorCode {
	NONE,
	
	IO_NOT_BINARY,
	
	VST_UNSPECIFIED,
	VST_LOADFAIL("Specified file is not a valid DLL."),
	VST_NOENTRY("Specified file is not a vst plugin."),
	VST_NOINSTANCE("Cannot create an effect instance."),
	
	WINDOW_REGISTER,
	WINDOW_CREATE,
	
	PROTOCOL_UNDEFINED;
	
	public final String description;
	private EnumErrorCode() {
		this.description = "Internal error. Please contact developer with the error code.";
	}
	private EnumErrorCode(String description) {
		this.description = description;
	}
	
	public String toString() {
		return "Error #" + ordinal() + ": " + this.description;
	}
}
