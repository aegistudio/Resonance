package net.aegistudio.resonance.music;

public class Note {
	public static final Note C = new Note(0);
	
	public static final Note C_SHARP = new Note(1);
	public static final Note D_FLAT = C_SHARP;
	
	public static final Note D = new Note(2);
	
	public static final Note D_SHARP = new Note(3);
	public static final Note E_FLAT = D_SHARP;
	
	public static final Note E = new Note(4);
	public static final Note F_FLAT = E;
	
	public static final Note F = new Note(5);
	public static final Note E_SHARP = F;
	
	public static final Note F_SHARP = new Note(6);
	public static final Note G_FLAT = F_SHARP;
	
	public static final Note G = new Note(7);
	
	public static final Note G_SHARP = new Note(8);
	public static final Note A_FLAT = G_SHARP;
	
	public static final Note A = new Note(9);
	
	public static final Note A_SHARP = new Note(10);
	public static final Note B_FLAT = A_SHARP;
	
	public static final Note B = new Note(11);
	
	public static final Note B_SHARP = C;
	public static final Note C_FLAT = B;
	
	public final int scaleOffset;
	
	public static final Note[] scaleNotes = new Note[12];
	
	private Note(int scaleOffset)
	{
		this.scaleOffset = scaleOffset;
		scaleNotes[scaleOffset] = this;
	}
	
	public boolean equals(Object note)
	{
		if(!(note instanceof Note)) return false;
		return this.scaleOffset == ((Note)note).scaleOffset;
	}
	
	public int hashCode()
	{
		return this.scaleOffset;
	}
}
