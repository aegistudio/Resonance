package net.aegistudio.resonance.io;

import java.util.TreeMap;

import net.aegistudio.resonance.Encoding;

public abstract class Format implements InputFormat, OutputFormat{
	public static final TreeMap<Integer, Format> formats = new TreeMap<Integer, Format>();
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
		
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_LITTLE, new DoubleWordFormat(false, false));
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_LITTLE, new DoubleWordFormat(false, true));
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_UINT | Encoding.ENDIAN_BIG, new DoubleWordFormat(true, false));
		formats.put(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG, new DoubleWordFormat(true, true));
	};
}
