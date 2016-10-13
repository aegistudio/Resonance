package net.aegistudio.resonance.format;

import java.util.TreeMap;

import net.aegistudio.resonance.common.Encoding;

public abstract class Format implements InputFormat, OutputFormat{
	public static final TreeMap<Integer, Format> formats = new TreeMap<Integer, Format>();
	public static final int WORDTYPE_INVERSE = (~Encoding.WORDTYPE_MASK);
	
	static
	{
		Format signedByteFormat = new ByteFormat(true);
		formats.put(Encoding.BITDEPTH_BIT8 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_LITTLE, signedByteFormat);
		formats.put(Encoding.BITDEPTH_BIT8 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG, signedByteFormat);
		
		Format unsignedByteFormat = new ByteFormat(false);
		formats.put(Encoding.BITDEPTH_BIT8 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_LITTLE, unsignedByteFormat);
		formats.put(Encoding.BITDEPTH_BIT8 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_BIG, unsignedByteFormat);
		
		formats.put(Encoding.BITDEPTH_BIT16 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_LITTLE, new WordFormat(false, false));
		formats.put(Encoding.BITDEPTH_BIT16 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_LITTLE, new WordFormat(false, true));
		formats.put(Encoding.BITDEPTH_BIT16 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_BIG, new WordFormat(true, false));
		formats.put(Encoding.BITDEPTH_BIT16 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG, new WordFormat(true, true));

		formats.put(Encoding.BITDEPTH_BIT24 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_LITTLE, new Bit24Format(false, false));
		formats.put(Encoding.BITDEPTH_BIT24 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_LITTLE, new Bit24Format(false, true));
		formats.put(Encoding.BITDEPTH_BIT24 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_BIG, new Bit24Format(true, false));
		formats.put(Encoding.BITDEPTH_BIT24 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG, new Bit24Format(true, true));
		
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_LITTLE, new DoubleWordFormat(false, false));
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_LITTLE, new DoubleWordFormat(false, true));
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_BIG, new DoubleWordFormat(true, false));
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG, new DoubleWordFormat(true, true));
		
		for(Integer key : formats.keySet().toArray(new Integer[0]))
			formats.put(key & WORDTYPE_INVERSE | Encoding.WORDTYPE_ALAW, new AlawNonlinearFormat(formats.get(key)));
	};
}
