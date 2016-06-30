package net.aegistudio.resonance.plugin.vst;

/**
 * see also vst/vstprotocol.h.
 * 
 * @author aegistudio
 *
 */
public enum EnumErrorCode {
	NONE,
	
	VST_UNSPECIFIED,
	VST_LOADFAIL("Specified file is not a valid DLL."),
	VST_NOENTRY("Specified file is not a vst plugin."),
	VST_NOINSTANCE("Cannot create an effect instance."),
	
	PROTOCOL_UNDEFINED;
	
	public final String description;
	private EnumErrorCode() {
		this.description = "Please contact developer with the error code.";
	}
	private EnumErrorCode(String description) {
		this.description = description;
	}
	
	public String toString() {
		return "Error #" + ordinal() + ": " + this.description;
	}
}
