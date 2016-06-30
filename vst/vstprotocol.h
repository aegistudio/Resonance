#ifndef __VST_PROTOCOL_H__
#define __VST_PROTOCOL_H__

#include "pluginterfaces/vst2.x/aeffect.h"
#include "pluginterfaces/vst2.x/aeffectx.h"
#include "pluginterfaces/vst2.x/vstfxstore.h"

typedef AEffect* (*PluginMain) (audioMasterCallback);

//@section error return code
#define ERROR_NONE 			0
#define ERROR_VST_UNSPECIFIED 		1
#define ERROR_VST_LOADFAIL		2
#define ERROR_VST_NOENTRY		3
#define ERROR_VST_NOINSTANCE		4
#define ERROR_PROTOCOL_UNDEFINED	5
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
#define PROTOCOL_IN_BYE 		0	// terminate the current program.
// Input: None
// Output: None

#define PROTOCOL_IN_METADATA		1	// we will update the metadata.

#define PROTOCOL_IN_PROCESS		2	// start processing.
// Input: The Input Buffer
#define PROTOCOL_IN_EMPTY_PROCESS	3	// the input will be empty.
// Input: None
// Output: The Output Buffer

#define PROTOCOL_MAX			4	

typedef int (*ProtocolHandle)(AEffect* effect);	// the handler.
ProtocolHandle handles[PROTOCOL_MAX];
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

#endif
