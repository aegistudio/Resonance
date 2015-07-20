package net.aegistudio.resonance.music;

public class ScaleNote {

	public static final ScaleNote[] scaleNotes = new ScaleNote[12];
	
	public static final ScaleNote C = new ScaleNote(0);
	
	public static final ScaleNote C_SHARP = new ScaleNote(1);
	public static final ScaleNote D_FLAT = C_SHARP;
	
	public static final ScaleNote D = new ScaleNote(2);
	
	public static final ScaleNote D_SHARP = new ScaleNote(3);
	public static final ScaleNote E_FLAT = D_SHARP;
	
	public static final ScaleNote E = new ScaleNote(4);
	public static final ScaleNote F_FLAT = E;
	
	public static final ScaleNote F = new ScaleNote(5);
	public static final ScaleNote E_SHARP = F;
	
	public static final ScaleNote F_SHARP = new ScaleNote(6);
	public static final ScaleNote G_FLAT = F_SHARP;
	
	public static final ScaleNote G = new ScaleNote(7);
	
	public static final ScaleNote G_SHARP = new ScaleNote(8);
	public static final ScaleNote A_FLAT = G_SHARP;
	
	public static final ScaleNote A = new ScaleNote(9);
	
	public static final ScaleNote A_SHARP = new ScaleNote(10);
	public static final ScaleNote B_FLAT = A_SHARP;
	
	public static final ScaleNote B = new ScaleNote(11);
	
	public static final ScaleNote B_SHARP = C;
	public static final ScaleNote C_FLAT = B;
	
	public final int scaleOffset;
	
	private ScaleNote(int scaleOffset)
	{
		this.scaleOffset = scaleOffset;
		scaleNotes[scaleOffset] = this;
	}
	
	public boolean equals(Object note)
	{
		if(!(note instanceof ScaleNote)) return false;
		return this.scaleOffset == ((ScaleNote)note).scaleOffset;
	}
	
	public int hashCode()
	{
		return this.scaleOffset;
	}
}
