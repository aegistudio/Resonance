#ifndef __VST_PROTOCOL_CPP__
#define __VST_PROTOCOL_CPP__

#include "vstprotocol.h"

typedef int (*ProtocolHandle)(AEffect* effect);	// the handler.
ProtocolHandle handles2x[PROTOCOL_MAX];

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
		effect -> processDoubleReplacing(effect, 
			doubleInputChannel, doubleOutputChannel, 
			metadata.sampleFrames);
	else effect -> processReplacing(effect, 
			floatInputChannel, floatOutputChannel, 
			metadata.sampleFrames);

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

// The four states.
int effectOpen(AEffect* effect) {
	effect -> dispatcher(effect, effOpen, 0, 0, NULL, 0);
	return ready();
}

int effectClose(AEffect* effect) {
	effect -> dispatcher(effect, effClose, 0, 0, NULL, 0);
	return ready();
}

int effectSuspend(AEffect* effect) {
	effect -> dispatcher(effect, effMainsChanged, 0, 0, NULL, 0);
	return ready();
}

int effectResume(AEffect* effect) {
	effect -> dispatcher(effect, effMainsChanged, 0, 1, NULL, 0);
	return ready();
}

int listParams(AEffect* effect) {
	ready();
	writeInt(effect -> numParams);

	char buffer[kVstMaxParamStrLen + 1];
	buffer[kVstMaxParamStrLen] = 0;

	int i = 0; for(; i < effect -> numParams; i ++) {
		// Label
		effect -> dispatcher(effect, effGetParamLabel, i, 0, buffer, 0);
		char* label = buffer;	writeString(label);

		// Display
		effect -> dispatcher(effect, effGetParamDisplay, i, 0, buffer, 0);
		char* display = buffer;	writeString(display);

		// Name
		effect -> dispatcher(effect, effGetParamName, i, 0, buffer, 0);
		char* name = buffer;	writeString(name);

		// Current Value
		writeFloat(effect -> getParameter(effect, i));
	}
	fflush(stdout);

	return ERROR_NONE;
}

void loadHandles2x() {
	handles2x[PROTOCOL_IN_METADATA] = readMetadata;

	handles2x[PROTOCOL_IN_PROCESS] = processInput;
	handles2x[PROTOCOL_IN_EMPTY_PROCESS] = processCore;

	handles2x[PROTOCOL_IN_OPEN] = effectOpen;
	handles2x[PROTOCOL_IN_CLOSE] = effectClose;
	handles2x[PROTOCOL_IN_SUSPEND] = effectSuspend;
	handles2x[PROTOCOL_IN_RESUME] = effectSuspend;

	handles2x[PROTOCOL_IN_LISTPARAMS] = listParams;
}

#endif
