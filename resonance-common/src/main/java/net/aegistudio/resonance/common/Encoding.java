package net.aegistudio.resonance.common;

import javax.sound.sampled.AudioFormat;

public class Encoding
{
	public static final int BITDEPTH_MASK  = 0x0000ffff;
	public static final int BITDEPTH_BIT8  = 0x00000008;
	public static final int BITDEPTH_BIT16 = 0x00000010;
	public static final int BITDEPTH_BIT24 = 0x00000018;
	public static final int BITDEPTH_BIT32 = 0x00000020;
	
	public static final int ENDIAN_MASK   = 0x00010000;
	public static final int ENDIAN_BIG    = 0x00010000;
	public static final int ENDIAN_LITTLE = 0x00000000;
	
	public static final int WORDTYPE_MASK  = 0xfffe0000;
	public static final int WORDTYPE_INT   = 0x00020000;
	public static final int WORDTYPE_UINT  = 0x00040000;
	public static final int WORDTYPE_FLOAT = 0x00060000;
	public static final int WORDTYPE_ALAW = 0x00080000;
	private static final int WORDTYPE_MAX   = 0x00080000;
	
	public static final String[] wordTypeDescription = new String[]{"integer", "unsigned integer", "float", "a-law"};
	public static final AudioFormat.Encoding[] wordTypeInstance = new AudioFormat.Encoding[]
			{AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT, AudioFormat.Encoding.ALAW};
	
	public final int encoding;
	
	public Encoding(int encoding)
	{
		this.encoding = encoding;
		if((this.encoding & 0x00000007) != 0)
			throw new IllegalArgumentException("The bit depth should not be less than a byte.");
		if((this.encoding & WORDTYPE_MASK) > WORDTYPE_MAX)
			throw new IllegalArgumentException("Unknowned word type!");
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof Encoding)
			return ((Encoding) obj).encoding == this.encoding;
		return false;
	}
	
	public int hashCode()
	{
		return this.encoding;
	}
	
	public String toString()
	{
		return String.format("%d-bit %s%s", this.getWordLength(),
				wordTypeDescription[(this.getWordType() >> 17) - 1],
				this.isBigEndian()? ", big-endian" : "");
	}
	
	public int getWordLength()
	{
		return this.encoding & BITDEPTH_MASK;
	}
	
	public boolean isBigEndian()
	{
		return (this.encoding & ENDIAN_MASK) == ENDIAN_BIG;
	}
	
	public int getWordType()
	{
		return this.encoding & WORDTYPE_MASK;
	}
	
	public AudioFormat.Encoding getWordTypeInstance()
	{
		return wordTypeInstance[(this.getWordType() >> 17) - 1];
	}
}
