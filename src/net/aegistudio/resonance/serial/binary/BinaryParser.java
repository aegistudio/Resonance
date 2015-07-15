package net.aegistudio.resonance.serial.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface BinaryParser<T>
{
	public T parse(DataInputStream input) throws Exception;
	
	public void write(DataOutputStream output, T type) throws Exception;
}
