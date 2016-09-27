#define VST_2_4_EXTENSIONS 1

#include "windows.h"
#pragma comment(lib, "user32")
#pragma comment(lib, "gdi32")

#include "stdio.h"
#include "fcntl.h"
#include "io.h"
#include "vstprotocol.h"

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
void updateMetadata(AEffect* effect) {
	resetBuffer();
	effect -> dispatcher(effect, effSetSampleRate, 0, 0, 0, metadata.sampleRate);
}

#include "vstprotocol.cpp"

int dllMain(HINSTANCE vsthandle) {
	// Find the main vst entry.
	PluginMain vstmain = (PluginMain)GetProcAddress(vsthandle, "VSTPluginMain");
	if(vstmain == NULL) return ERROR_VST_NOENTRY;

	// Create the instancce.
	AEffect* effectInstance = vstmain(loaderMasterCallback);
	if(effectInstance == NULL) return ERROR_VST_NOINSTANCE;

	// Judge and load handle.
	int loadResult = loadHandles2x(effectInstance);
	if(loadResult != ERROR_NONE) return loadResult;

	// Allocate buffer space.
	updateMetadata(effectInstance);

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
	unloadHandles2x(effectInstance);
	delete effectInstance;

	return returnCode;
}

int main(int argv, char** args) {
	// Set stdin, stdout to binary mode.
	if((_setmode(_fileno(stdin), _O_BINARY)) == -1)
		return wrong(ERROR_IO_NOTBINARY);
	if((_setmode(_fileno(stdout), _O_BINARY)) == -1)
		return wrong(ERROR_IO_NOTBINARY);

	// Check whether library specified.
	if(argv <= 1) return wrong(ERROR_VST_UNSPECIFIED);

	// Initialize metadata.
	metadata.masterVersion = 3100;
	metadata.processDouble = false;
	metadata.sampleRate = 44100.f;
	metadata.channels = 2;
	metadata.sampleFrames = 1024;

	// And process parameters.
	int i = 2; for(; i < argv; i ++) {
		if(strcmp("-m", args[i]) == 0) 
			fread(&metadata, sizeof(metadata), 1, stdin);
	}

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
