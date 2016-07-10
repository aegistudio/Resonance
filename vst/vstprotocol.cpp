#ifndef __VST_PROTOCOL_CPP__
#define __VST_PROTOCOL_CPP__

#include "vstprotocol.h"
#define MAX_VST_PARAMSIZE 128

#pragma comment(lib, "ole32.lib")

HWND windowHandle = NULL;
HINSTANCE handle = NULL;
CRITICAL_SECTION threadCritSection;

// PROTOCOL_IN_METADATA
int readMetadata(AEffect*) {
	fread(&metadata, sizeof(metadata), 1, stdin);
	updateMetadata();
	return ready();
}

// PROTOCOL_IN_EMPTY_PROCESS
int processCore(AEffect* effect) {
	int i = 0; //Iterable variant.

	// Call the actual effect plugin.
	if(metadata.processDouble) {
		for(i = 0; i < bufferSize; i ++) 
			reverseBitOrder(doubleInputChannel + i, sizeof(double));
		effect -> processDoubleReplacing(effect, 
			doubleInputChannel, doubleOutputChannel, 
			metadata.sampleFrames);
		for(i = 0; i < bufferSize; i ++) 
			reverseBitOrder(doubleOutputChannel + i, sizeof(double));
	}
	else {
		for(i = 0; i < bufferSize; i ++) 
			reverseBitOrder(floatInputChannel + i, sizeof(float));
		effect -> processReplacing(effect, 
			floatInputChannel, floatOutputChannel, 
			metadata.sampleFrames);
		for(i = 0; i < bufferSize; i ++) 
			reverseBitOrder(floatOutputChannel + i, sizeof(float));
	}

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

// PROTOCOL_IN_*_PARAM
int listParams(AEffect* effect) {
	ready();
	writeInt(effect -> numParams);

	char buffer[MAX_VST_PARAMSIZE];
	buffer[MAX_VST_PARAMSIZE - 1] = 0;

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


int setParam(AEffect* effect) {
	int paramId;		readInt(&paramId);
	float paramValue;	readFloat(&paramValue);	

	effect -> setParameter(effect, paramId, paramValue);
	
	ready();
	char display[kVstMaxParamStrLen + 1];
	display[kVstMaxParamStrLen] = 0;

	effect -> dispatcher(effect, effGetParamDisplay, paramId, 0, display, 0);
	writeString(display);
	fflush(stdout);

	return ERROR_NONE;
}

int getParam(AEffect* effect) {
	int paramId;		readInt(&paramId);

	ready();

	float paramValue = effect -> getParameter(effect, paramId);
	writeFloat(paramValue);

	char display[kVstMaxParamStrLen + 1];
	display[kVstMaxParamStrLen] = 0;

	effect -> dispatcher(effect, effGetParamDisplay, paramId, 0, display, 0);
	writeString(display);
	fflush(stdout);

	return ERROR_NONE;
}

// PROTOCOL_IN_GET_FLAGS
int getFlags(AEffect* effect) {
	ready();

	writeInt(effect -> flags);
	fflush(stdout);

	return ERROR_NONE;
}

// PROTOCOL_IN_EDITOR_*
LRESULT CALLBACK windowMessageHandle(HWND whandle, UINT message, WPARAM w, LPARAM l) {
	switch(message) {
		case WM_DESTROY:
			// Destroy reference.
			DestroyWindow(whandle);

			EnterCriticalSection(&threadCritSection);
			windowHandle = NULL;
			LeaveCriticalSection(&threadCritSection);

			return 0;
		break;

		default:			
			return DefWindowProc(whandle, message, w, l);
		break;
	}
}

DWORD WINAPI windowThread(LPVOID param) {
	// Enter CriticalSection.

	EnterCriticalSection(&threadCritSection);
	if(windowHandle == NULL) {
		// Initialize COM components.
		CoInitialize(NULL);

		AEffect* aeffect = (AEffect*)param;
		// Get the required size.
		ERect* size = NULL;	aeffect -> dispatcher(aeffect, effEditGetRect, 0, 0, &size, 0);
		int left = size -> left;		int right = size -> right;
		int top = size -> top;			int bottom = size -> bottom;
		int width = right - left;		int height = bottom - top;

		// Open window.
		int properties = WS_TILED | WS_CAPTION | WS_SYSMENU;
		windowHandle = CreateWindow("VSTLOADER", "VSTLOADER", properties,
			CW_USEDEFAULT, CW_USEDEFAULT, width, height, NULL, NULL, NULL, NULL);

		// Set window and display.
		aeffect -> dispatcher(aeffect, effEditOpen, 0, 0, windowHandle, 0);
		ShowWindow(windowHandle, SW_SHOWNORMAL);
		UpdateWindow(windowHandle);

		// Release lock.
		LeaveCriticalSection(&threadCritSection);

		// Return value.
		ready();
		
		// Enter refresh thread.
		MSG msg;
		while(GetMessage(&msg, 0, 0, 0) > 0) {
			DispatchMessage(&msg);
			TranslateMessage(&msg);
		}
	}
	else {
		LeaveCriticalSection(&threadCritSection);	// Release lock directly.
		ready();
	}

	return 0;
}

int openEditor(AEffect* effect) {
	if(windowHandle == NULL) {
		// Create refresh thread.
		HANDLE threadHandle = CreateThread(NULL, 0, windowThread, effect, 0, NULL);
		if(threadHandle == NULL) return ERROR_WND_CREATE;
	}
	else ready();
	return ERROR_NONE;
}


int closeEditor(AEffect* effect) {
	EnterCriticalSection(&threadCritSection);
	if(windowHandle != NULL) {
		effect -> dispatcher(effect, effEditClose, 0, 0, 0, 0);
		PostMessage(windowHandle, WM_DESTROY, NULL, NULL);
	}
	LeaveCriticalSection(&threadCritSection);

	ready();
	return ERROR_NONE;
}

int isEditorOpen(AEffect* effect) {
	ready();
	putchar(windowHandle != NULL? 1 : 0);
	fflush(stdout);
	return ERROR_NONE;
}

int loadHandles2x(AEffect* effect) {
	// Load virtual function table.
	handles[PROTOCOL_IN_METADATA] = readMetadata;

	handles[PROTOCOL_IN_PROCESS] = processInput;
	handles[PROTOCOL_IN_EMPTY_PROCESS] = processCore;

	handles[PROTOCOL_IN_OPEN] = effectOpen;
	handles[PROTOCOL_IN_CLOSE] = effectClose;
	handles[PROTOCOL_IN_SUSPEND] = effectSuspend;
	handles[PROTOCOL_IN_RESUME] = effectSuspend;

	handles[PROTOCOL_IN_LISTPARAMS] = listParams;
	handles[PROTOCOL_IN_SETPARAM] = setParam;
	handles[PROTOCOL_IN_GETPARAM] = getParam;

	handles[PROTOCOL_IN_FLAGS] = getFlags;

	handles[PROTOCOL_IN_OPEN_EDITOR] = openEditor;
	handles[PROTOCOL_IN_CLOSE_EDITOR] = closeEditor;
	handles[PROTOCOL_IN_IS_EDITOR_OPEN] = isEditorOpen;

	// Get the module handle
	handle = GetModuleHandle(NULL);
	
	// Register window class
	if((effect -> flags & effFlagsHasEditor) != 0) {
		WNDCLASS windowClass = {0};
		windowClass.lpszClassName = "VSTLOADER";
		windowClass.style = CS_HREDRAW | CS_VREDRAW;
		windowClass.lpfnWndProc = (WNDPROC)windowMessageHandle;
		windowClass.hbrBackground = (HBRUSH)GetStockObject(WHITE_BRUSH);
		windowClass.hInstance = handle;
		windowClass.hCursor = LoadCursor(NULL, IDC_ARROW);
		windowClass.hIcon = NULL;
		windowClass.lpszMenuName = NULL;
		if(!RegisterClass(&windowClass))
			return ERROR_WND_REGISTER;
	}

	// Initialize critical section.
	InitializeCriticalSection(&threadCritSection);

	return ERROR_NONE;
}

void unloadHandles2x(AEffect* effect) {
	// Close window.
	if(windowHandle != NULL) 
		DestroyWindow(windowHandle);

	// De-Initalize.
	DeleteCriticalSection(&threadCritSection);
}

#endif
