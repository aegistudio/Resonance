package net.aegistudio.resonance.device;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.io.Format;

public class WaveDevice implements InputDevice
{
	Frame bufferedWave;
	
	public WaveDevice(AudioInputStream waveInputStream) throws Exception
	{
		int totalBytes = waveInputStream.available();
		byte[] buffer = new byte[totalBytes];
		waveInputStream.read(buffer);
		
		Environment environment = new Environment(waveInputStream.getFormat(), (int)waveInputStream.getFrameLength(), 1);
		bufferedWave = new Frame(environment.channels, environment.samplesPerFrame);
		
		Format.formats.get(environment.wordEncoding.encoding).read(bufferedWave, buffer);
	}
	
	@Override
	public void create(Environment environment) {
		
	}

	@Override
	public void read(Frame target) {
		
	}

	public static void main(String[] arguments) throws Exception
	{
		double[] dbl = new WaveDevice(AudioSystem.getAudioInputStream(new File("/home/luohaoran/age.of.empire.2.conqueror/Sound/terrain/Cricket.wav")))
						.bufferedWave.getSamples(0);
		for(double dblEntry : dbl) System.out.println(dblEntry);
	}
	
}
