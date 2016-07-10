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
	ERROR_WND_REGISTER,
	ERROR_WND_CREATE,
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
	// 	numParams : int {
	//		paramValue: float,
	//		paramLabel: char buffer,
	//		paramDisplay: char buffer,
	//		paramName: char buffer.
	//	} x numParams

	PROTOCOL_IN_SETPARAM,		// set a parameter.
	// Input: 
	//	paramId : int
	// 	value: float
	// Output: 
	// 	paramDisplay : char buffer

	PROTOCOL_IN_GETPARAM,		// get a parameter.
	// Input:
	//	paramId: int
	// Output:
	//	value: float
	// 	paramDisplay: char buffer.

	PROTOCOL_IN_FLAGS,		// get the flag.
	// Input: None
	// Output:
	//	flags: int

	PROTOCOL_IN_OPEN_EDITOR,	// open an editor.
	// Input: None
	// Output: None

	PROTOCOL_IN_CLOSE_EDITOR,	// close an editor.
	// Input: None
	// Output: None

	PROTOCOL_IN_IS_EDITOR_OPEN,	// is editor opened.
	// Input: None
	// Output: byte
	// 	0 for false and 1 for true.

	PROTOCOL_MAX
};
//@endsection


typedef int (*ProtocolHandle)(AEffect* effect);	// the handler.
ProtocolHandle handles[PROTOCOL_MAX];

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

void writeShort(short value) {
	putchar((value >>  8) & 0x0ff);
	putchar((value >>  0) & 0x0ff);
}

void readInt(int* value) {
	*value = 0;
	*value |= (getchar() & 0x0ff) << 24;
	*value |= (getchar() & 0x0ff) << 16;
	*value |= (getchar() & 0x0ff) <<  8;
	*value |= (getchar() & 0x0ff) <<  0;
}

void writeString(char* buffer) {
	short length = strlen(buffer);
	writeShort(length);
	fwrite(buffer, length, 1, stdout);
	fflush(stdout);
}

void writeFloat(float value) {
	float* address = &value;
	int* converted = (int*)address;
	writeInt(*converted);
}

void readFloat(float* target) {
	int* address = (int*)target;
	readInt(address);
}

#define MAX_BIT_LENGTH 16
void reverseBitOrder(void* address, int length) {
	char* converted = (char*)address;
	char buffer[MAX_BIT_LENGTH];
	int i = 0; for(; i < length; i ++) 
		buffer[i] = converted[i];
	int j = 0; for(; j < length; j ++)
		converted[j] = buffer[length - j - 1];
}

#endif
