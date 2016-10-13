#ifndef __VSTIO_H__
#define __VSTIO_H__

#define MAX_CHANNEL 2			// how many channel is allowed.

float *floatInput, *floatOutput;
float* floatInputChannel[MAX_CHANNEL];
float* floatOutputChannel[MAX_CHANNEL];

double *doubleInput, *doubleOutput;
double* doubleInputChannel[MAX_CHANNEL];
double* doubleOutputChannel[MAX_CHANNEL];

int bufferSize, sampleTotal;
void *inputBuffer, *outputBuffer;

void freeBuffer() {
	if(floatInput) delete[] floatInput;
	if(floatOutput) delete[] floatOutput;
	if(doubleInput) delete[] doubleInput;
	if(doubleOutput) delete[] doubleOutput;
}

void resetBuffer() {
	freeBuffer();
	sampleTotal = metadata.channels * metadata.sampleFrames;
	bufferSize = sampleTotal * sizeof(double);

	if(!metadata.processDouble) {	
		floatInput = new float[sampleTotal];
		floatOutput = new float[sampleTotal];
		int i = 0; for(; i < metadata.channels; i ++) {
			floatInputChannel[i] = floatInput + i * metadata.sampleFrames;
			floatOutputChannel[i] = floatOutput + i * metadata.sampleFrames;
		}
	}

	doubleInput = new double[sampleTotal];
	doubleOutput = new double[sampleTotal];
	int i = 0; for(; i < metadata.channels; i++) {
		doubleInputChannel[i] = doubleInput + i * metadata.sampleFrames;
		doubleOutputChannel[i] = doubleOutput + i * metadata.sampleFrames;
	}
	inputBuffer = doubleInput;
	outputBuffer = doubleOutput;
}

#endif
