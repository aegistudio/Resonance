package net.aegistudio.resonance.serial;

import java.io.InputStream;
import java.io.OutputStream;

public interface Serializer
{
	public Structure parse(InputStream inputStream) throws Exception;
	
	public void write(OutputStream outputStream, Structure target) throws Exception;
}
