#include <windows.h>
#include <iostream>

#include "stdio.h"
#include "vstprotocol.h"
using namespace std;

Metadata metadata;

VstIntPtr loaderMasterCallback (AEffect* effect, VstIntPtr opcode, 
	VstInt32 index, VstIntPtr value, void* ptr, float opt) {

	switch(opcode) {
		case audioMasterVersion:
			return metadata.masterVersion;
		break;
		default:
			return 0;
		break;
	}
}

#include "vstbuffer.h"
void updateMetadata() {
	resetBuffer();
}

#include "vstprotocol.cpp"

int dllMain(HINSTANCE vsthandle) {
	// Find the main vst entry.
	PluginMain vstmain = (PluginMain)GetProcAddress(vsthandle, "VSTPluginMain");
	if(vstmain == NULL) return ERROR_VST_NOENTRY;

	// Create the instancce.
	AEffect* effectInstance = vstmain(loaderMasterCallback);
	if(effectInstance == NULL) return ERROR_VST_NOINSTANCE;

	// When you receive ready token, you could start.
	ready();

	// Work with protocol.
	int inputHandleCode = PROTOCOL_IN_BYE;
	int returnCode = ERROR_NONE;
	while((inputHandleCode = getchar()) != PROTOCOL_IN_BYE) {
		if(inputHandleCode < 0 || inputHandleCode >= PROTOCOL_MAX) 
			returnCode = ERROR_PROTOCOL_UNDEFINED;
		else returnCode = handles[inputHandleCode](effectInstance);

		if(returnCode != ERROR_NONE) break;
	}

	// Finish up.
	delete effectInstance;
	return returnCode;
}

int main(int argv, char** args) {
	// Check whether library specified.
	if(argv <= 1) return wrong(ERROR_VST_UNSPECIFIED);
	loadHandle();

	// Initialize metadata.
	metadata.masterVersion = 2400;
	metadata.processDouble = false;
	metadata.channels = 2;
	metadata.sampleFrames = 1024;

	// And process parameters.
	int i = 2; for(; i < argv; i ++) {
		if(strcmp("-m", args[i]) == 0) 
			fread(&metadata, sizeof(metadata), 1, stdin);
	}

	// Allocate buffer space.
	updateMetadata();

	// Load vst library First.
	HINSTANCE vsthandle = LoadLibraryA(args[1]);
	if(vsthandle == NULL) return wrong(ERROR_VST_LOADFAIL);

	// Entry the main with dll.
	int dllMainCode = dllMain(vsthandle);

	// Free the library memory.
	FreeLibrary(vsthandle);
	freeBuffer();
	return wrong(dllMainCode);
}
