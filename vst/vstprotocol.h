#ifndef __VST_PROTOCOL_H__
#define __VST_PROTOCOL_H__

#include "pluginterfaces/vst2.x/aeffect.h"
#include "pluginterfaces/vst2.x/aeffectx.h"
#include "pluginterfaces/vst2.x/vstfxstore.h"

typedef AEffect* (*PluginMain) (audioMasterCallback);

//@section error return code
enum ErrorCode {
	ERROR_NONE = 0,	
	ERROR_IO_NOTBINARY,
	ERROR_VST_UNSPECIFIED, 	
	ERROR_VST_LOADFAIL,
	ERROR_VST_NOENTRY,
	ERROR_VST_NOINSTANCE,
	ERROR_PROTOCOL_UNDEFINED,
};
//@endsection

// when metadata flag set to true.
// Input:
struct Metadata {
	// (!! can only be mutated with -m)
	int masterVersion;			// the audio master version. 

	bool processDouble;			// whether we use double precision.
	int channels;				// how many channels.
	int sampleFrames;			// how many frames per sample
};
// Output: None

#define PROTOCOL_OUT_READY		0	// when the program is ready.
#define PROTOCOL_OUT_BYE		1	// when the program is terminated.

//@section inprotocol
enum ProtocolIn {
	PROTOCOL_IN_BYE = 0,		// terminate the current program.
	// Input: None
	// Output: void

	PROTOCOL_IN_METADATA,		// we will update the metadata.
	// Input: struct Metadata
	// Output: void

	PROTOCOL_IN_PROCESS,		// start processing.
	// Input: The Input Buffer
	// Output: The Output Buffer

	PROTOCOL_IN_EMPTY_PROCESS,	// the input will be empty.
	// Input: None
	// Output: The Output Buffer

	PROTOCOL_IN_OPEN,		// start the effect.
	PROTOCOL_IN_CLOSE,		// end the effect.
	PROTOCOL_IN_SUSPEND,		// suspend the effect.
	PROTOCOL_IN_RESUME,		// resume the effect.
	// Input: None
	// Output: None

	PROTOCOL_IN_LISTPARAMS,		// list the parameters.
	// Input: None
	// Output: 
	// 	numParams : Int {
	//		paramValue: float,
	//		paramLabel: char buffer,
	//		paramDisplay: char buffer,
	//		paramName: char buffer.
	//	} x numParams

	PROTOCOL_MAX
};
//@endsection

// return void
int ready() {
	putchar(PROTOCOL_OUT_READY);
	fflush(stdout);
	return ERROR_NONE;
}

// return void* / return data
int write(void* buffer, int size, int count) {
	ready();
	fwrite(buffer, size, 1, stdout);
	fflush(stdout);		// flush the buffer, or caller will be waiting...
	return ERROR_NONE;
}

// throw Exception
int wrong(int code) {
	putchar(PROTOCOL_OUT_BYE);
	putchar(code);
	return code;
}

// force little endian.
void writeInt(int value) {
	putchar((value >> 24) & 0x0ff);
	putchar((value >> 16) & 0x0ff);
	putchar((value >>  8) & 0x0ff);
	putchar((value >>  0) & 0x0ff);
}

void writeString(char* buffer) {
	int length = strlen(buffer);
	writeInt(length);
	fwrite(buffer, length, 1, stdout);
	fflush(stdout);
}

void writeFloat(float value) {
	float* address = &value;
	int* converted = (int*)address;
	writeInt(*converted);
}

#endif
