package net.aegistudio.resonance.serial;

public class Type<T>
{
	public static final Type<Integer> INTEGER = new Type<Integer>(0, "integer");
	public static final Type<int[]> INT_ARRAY = new Type<int[]>(1, "integer[]");
	public static final Type<Float> FLOAT = new Type<Float>(2, "float");
	public static final Type<float[]> FLOAT_ARRAY = new Type<float[]>(3, "float[]");
	public static final Type<Double> DOUBLE = new Type<Double>(4, "double");
	public static final Type<double[]> DOUBLE_ARRAY = new Type<double[]>(5, "double[]");
	public static final Type<String> STRING = new Type<String>(6, "String");
	public static final Type<String[]> STRING_ARRAY = new Type<String[]>(7, "String[]");
	public static final Type<Structure> STRUCTURE = new Type<Structure>(8, "Structure[]");
	public static final Type<Structure[]> STRUCTURE_ARRAY = new Type<Structure[]>(9, "Structure[]");
	public static final Type<Class<?>> CLASS = new Type<Class<?>>(10, "Class");
	public static final Type<Boolean> BOOLEAN = new Type<Boolean>(11, "boolean");
	
	protected Type(int ordinal, String name){
		this.ordinal = ordinal;
		this.name = name;
		types[ordinal] = this;
	}
	
	private static final Type<?>[] types = new Type<?>[128];
	private final int ordinal;
	private final String name;
	
	public int ordinal()
	{
		return this.ordinal;
	}
	
	public static Type<?>[] values()
	{
		return types;
	}
	
	public String name()
	{
		return this.name;
	}
	
	public int hashCode()
	{
		return this.ordinal;
	}
}