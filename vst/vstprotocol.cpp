#ifndef __VST_PROTOCOL_CPP__
#define __VST_PROTOCOL_CPP__

#include "vstprotocol.h"

// PROTOCOL_IN_METADATA
int readMetadata(AEffect*) {
	fread(&metadata, sizeof(metadata), 1, stdin);
	updateMetadata();
	return ready();
}

// PROTOCOL_IN_EMPTY_PROCESS
int processCore(AEffect* effect) {
	// Call the actual effect plugin.
	if(metadata.processDouble) 
		effect->processDoubleReplacing(effect, doubleInputChannel, doubleOutputChannel, metadata.sampleFrames);
	else effect->processReplacing(effect, floatInputChannel, floatOutputChannel, metadata.sampleFrames);

	// Write output buffer to standard output.
	return write(outputBuffer, bufferSize, 1);
}

// PROTOCOL_IN_PROCESS
int processInput(AEffect* effect) {
	// Read input buffer from standard input.
	fread(inputBuffer, bufferSize, 1, stdin);

	// Now process input.
	return processCore(effect);
}

// Loading handles.
void loadHandle() {
	handles[PROTOCOL_IN_METADATA] = readMetadata;
	handles[PROTOCOL_IN_PROCESS] = processInput;
	handles[PROTOCOL_IN_EMPTY_PROCESS] = processCore;
}

#endif
