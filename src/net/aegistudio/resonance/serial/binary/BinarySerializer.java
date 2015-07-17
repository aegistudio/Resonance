package net.aegistudio.resonance.serial.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import net.aegistudio.resonance.serial.Serializer;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

import static net.aegistudio.resonance.serial.Type.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BinarySerializer implements BinaryParser<Structure>, Serializer
{
	private final HashMap<Type, BinaryParser> parserMapper
		= new HashMap<Type, BinaryParser>();
	
	public BinarySerializer()
	{
		BinaryParser<Integer> intParser = new BinaryParser<Integer>()
		{
			@Override
			public Integer parse(DataInputStream input) throws Exception {
				return input.readInt();
			}

			@Override
			public void write(DataOutputStream output, Integer type) throws Exception {
				output.writeInt(type);
			}
		};
		parserMapper.put(INTEGER, intParser);
		
		parserMapper.put(INT_ARRAY, new ArrayBinaryParser<Integer>(intParser)
		{
			@Override
			protected Integer[] createArray(int length) {
				return new Integer[length];
			}
		});
		
		BinaryParser<Float> floatParser = new BinaryParser<Float>()
		{

			@Override
			public Float parse(DataInputStream input) throws Exception {
				return input.readFloat();
			}

			@Override
			public void write(DataOutputStream output, Float type) throws Exception {
				output.writeFloat(type);
			}
		};
		parserMapper.put(FLOAT, floatParser);
		
		parserMapper.put(FLOAT_ARRAY, new ArrayBinaryParser<Float>(floatParser)
		{
			@Override
			protected Float[] createArray(int length) {
				return new Float[length];
			}
		});
		
		BinaryParser<Double> doubleParser = new BinaryParser<Double>()
		{

			@Override
			public Double parse(DataInputStream input) throws Exception {
				return input.readDouble();
			}

			@Override
			public void write(DataOutputStream output, Double type) throws Exception {
				output.writeDouble(type);
			}
		};
		parserMapper.put(DOUBLE, doubleParser);
		
		parserMapper.put(DOUBLE_ARRAY, new ArrayBinaryParser<Double>(doubleParser)
		{
			@Override
			protected Double[] createArray(int length) {
				return new Double[length];
			}
		});
		
		parserMapper.put(STRUCTURE, this);
		
		parserMapper.put(STRUCTURE_ARRAY, new ArrayBinaryParser<Structure>(this)
		{
			@Override
			protected Structure[] createArray(int length) {
				return new Structure[length];
			}
		});
		
		BinaryParser<String> stringParser = new BinaryParser<String>()
		{
			@Override
			public String parse(DataInputStream input) throws Exception {
				return input.readUTF();
			}

			@Override
			public void write(DataOutputStream output, String type) throws Exception {
				output.writeUTF(type);
			}
		};
		
		parserMapper.put(STRING, stringParser);
		
		parserMapper.put(STRING_ARRAY, new ArrayBinaryParser<String>(stringParser)
		{
			@Override
			protected String[] createArray(int length) {
				return new String[length];
			}
		});
		
		parserMapper.put(CLASS, new BinaryParser<Class>()
		{
			@Override
			public Class parse(DataInputStream input) throws Exception {
				String clazz = input.readUTF();
				if(clazz.equals(""))
					return null;
				else return Class.forName(clazz);
			}

			@Override
			public void write(DataOutputStream output, Class type) throws Exception {
				if(type == null)
					output.writeUTF("");
				else output.writeUTF(type.getName());
			}
		});
		
		parserMapper.put(BOOLEAN, new BinaryParser<Boolean>()
		{
			@Override
			public Boolean parse(DataInputStream input) throws Exception {
				return input.readBoolean();
			}

			@Override
			public void write(DataOutputStream output, Boolean type) throws Exception {
				output.writeBoolean(type);
			}
		});
		
		parserMapper.put(BYTE, new BinaryParser<Byte>()
		{

			@Override
			public Byte parse(DataInputStream input) throws Exception {
				return input.readByte();
			}

			@Override
			public void write(DataOutputStream output, Byte type) throws Exception {
				output.writeByte(type);
			}
		});
	}
	
	@Override
	public Structure parse(DataInputStream input) throws Exception {
		Structure returnValue = new Structure();
		int entryLength = input.readInt();
		for(int i = 0; i < entryLength; i ++)
		{
			int typeId = input.readByte();
			Type type;
			if(typeId < 0) type = Type.values()[-typeId];
			else type = Type.values()[typeId];
			
			String keyName = input.readUTF();
			returnValue.declare(keyName, type);
			BinaryParser parser = this.parserMapper.get(type);
			Object value;
			if(typeId < 0) value = null;
			else value = parser.parse(input);
			returnValue.set(keyName, type, value);
		}
		return returnValue;
	}

	@Override
	public void write(DataOutputStream output, Structure structure) throws Exception {
		int entryLength = structure.typeMap.keySet().size();
		output.writeInt(entryLength);
		for(String key : structure.typeMap.keySet())
		{
			Object value = structure.valueMap.get(key);
			Type type = structure.typeMap.get(key);
			if(value == null) output.writeByte(- type.ordinal());
			else output.writeByte(type.ordinal());
			
			output.writeUTF(key);
			if(value != null)
			{
				BinaryParser parser = this.parserMapper.get(type);
				parser.write(output, value);
			}
		}
	}

	@Override
	public Structure parse(InputStream inputStream) throws Exception {
		return this.parse(new DataInputStream(inputStream));
	}

	@Override
	public void write(OutputStream outputStream, Structure target) throws Exception {
		this.write(new DataOutputStream(outputStream), target);
	}
}
