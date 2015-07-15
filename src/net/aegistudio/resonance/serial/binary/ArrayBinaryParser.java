package net.aegistudio.resonance.serial.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class ArrayBinaryParser<T> implements BinaryParser<T[]>
{
	protected final BinaryParser<T> singleParser;
	
	protected ArrayBinaryParser(BinaryParser<T> singleParser)
	{
		this.singleParser = singleParser;
	}
	
	@Override
	public T[] parse(DataInputStream input) throws Exception {
		int length = input.readInt();
		T[] value = this.createArray(length);
		for(int i = 0; i < length; i ++)
			value[i] = singleParser.parse(input);
		return value;
	}

	@Override
	public void write(DataOutputStream output, T[] type) throws Exception {
		output.writeInt(type.length);
		for(int i = 0; i < type.length; i ++)
			singleParser.write(output, type[i]);
	}
	
	protected abstract T[] createArray(int length);
}
