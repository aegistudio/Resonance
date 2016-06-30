#ifndef __VSTIO_H__
#define __VSTIO_H__

#define MAX_CHANNEL 2			// how many channel is allowed.

float *floatInput, *floatOutput;
float* floatInputChannel[MAX_CHANNEL];
float* floatOutputChannel[MAX_CHANNEL];

double *doubleInput, *doubleOutput;
double* doubleInputChannel[MAX_CHANNEL];
double* doubleOutputChannel[MAX_CHANNEL];

int bufferSize;
void* inputBuffer;
void* outputBuffer;

void freeBuffer() {
	if(floatInput == NULL) delete[] floatInput;
	if(floatOutput == NULL) delete[] floatOutput;
	if(doubleInput == NULL) delete[] doubleInput;
	if(doubleOutput == NULL) delete[] doubleOutput;
}

void resetBuffer() {
	freeBuffer();
	int sampleTotal = metadata.channels * metadata.sampleFrames;
	if(metadata.processDouble) {	
		doubleInput = new double[sampleTotal];
		doubleOutput = new double[sampleTotal];
		int i = 0; for(; i < metadata.channels; i++) {
			doubleInputChannel[i] = doubleInput + i * metadata.sampleFrames;
			doubleOutputChannel[i] = doubleOutput + i * metadata.sampleFrames;
		}
		bufferSize = sampleTotal * sizeof(double);
		inputBuffer = doubleInput;
		outputBuffer = doubleOutput;
	}
	else {
		floatInput = new float[sampleTotal];
		floatOutput = new float[sampleTotal];
		int i = 0; for(; i < metadata.channels; i ++) {
			floatInputChannel[i] = floatInput + i * metadata.sampleFrames;
			floatOutputChannel[i] = floatOutput + i * metadata.sampleFrames;
		}
		bufferSize = sampleTotal * sizeof(float);
		inputBuffer = floatInput;
		outputBuffer = floatOutput;
	}
}

#endif
