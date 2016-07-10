package net.aegistudio.resonance.plugin.vst;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.plugin.Event;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.serial.Structure;

public class VstPlugin implements Plugin {
	public final String[] cmd;
	public final String vst;
	
	public VstPlugin(String[] cmd, String vst) {
		this.cmd = cmd;
		this.vst = vst;
	}

	public Process process;
	public InputStream processOutput, processError;
	public OutputStream processInput;
	
	public VstParameter[] paramList;
	
	// The flags.
	public boolean hasEditor;			public static final int HAS_EDITOR_MASK = 1 << 0;
	public boolean canReplacing;		public static final int CAN_REPLACINg_MASK = 1 << 4;
	public boolean programChunks;		public static final int PROGRAM_CHUNKS_MASK = 1 << 5;
	public boolean isSynth;				public static final int IS_SYNTH = 1 << 8;
	public boolean canDoubleReplacing;	public static final int CAN_DOUBLE_REPLACING_MASK = 1 << 12;
	
	@Override
	public void create(Structure parameter) throws VstException {
		try {
			String[] arguments = new String[cmd.length + 1];
			System.arraycopy(cmd, 0, arguments, 0, cmd.length);
			arguments[cmd.length] = vst;
			this.process = Runtime.getRuntime().exec(arguments);
			
			this.processOutput = this.process.getInputStream();
			this.processError = this.process.getErrorStream();
			this.processInput = this.process.getOutputStream();
		}
		catch(IOException e) {
			throw new VstException(e.getMessage());
		}
		
		operate((i, o, e) -> ensureInput());
		
		operate((i, o, e) -> {
			EnumOperation.FLAGS.write(o);
			ensureInput();
			int flags = new DataInputStream(i).readInt();
			
			hasEditor = (flags & HAS_EDITOR_MASK) != 0;
			canReplacing = (flags & CAN_REPLACINg_MASK) != 0;
			programChunks = (flags & PROGRAM_CHUNKS_MASK) != 0;
			isSynth = (flags & IS_SYNTH) != 0;
			canDoubleReplacing = (flags & CAN_DOUBLE_REPLACING_MASK) != 0;			
		});

		operate((i, o, e) -> {
			EnumOperation.OPEN.write(o);
			ensureInput();
		});
		
		operate((i, o, e) -> {
			EnumOperation.LIST_PARAMS.write(o);
			ensureInput();
			
			DataInputStream din = new DataInputStream(i);
			int paramCount = din.readInt();
			paramList = new VstParameter[paramCount];
			
			for(int j = 0; j < paramCount; j ++) 
				paramList[j] = new VstParameter(j, this, i);
		});
	}
	
	@Override
	public void trigger(Event event) {
		
	}

	@Override
	public void process(Frame input, Frame output) {
		
	}
	
	public void openEditor() throws VstException {
		if(hasEditor)
			operate((i, o, e) -> {
				EnumOperation.OPEN_EDITOR.write(o);
				ensureInput();
			});
	}

	public void closeEditor() throws VstException {
		if(hasEditor)
			operate((i, o, e) -> {
				EnumOperation.CLOSE_EDITOR.write(o);
				ensureInput();
			});
	}
	
	public boolean isEditorOpen() throws VstException {
		if(hasEditor) {
			return operateFunc((i, o, e) -> {
				EnumOperation.IS_EDITOR_OPEN.write(o);
				ensureInput();
				return i.read() == 1;
			});
		}
		return false;
	}
	
	@Override
	public void destroy() throws VstException {
		if(isEditorOpen()) closeEditor();
		
		operate((i, o, e) -> {
			EnumOperation.CLOSE.write(o);
			ensureInput();
		});
		
		operate((i, o, e) -> EnumOperation.TERMINATE.write(o));
		abandon();

	}
	
	protected void abandon() {
		this.process.destroy();
		this.process = null;
	}
	
	public static interface Operation<T> {
		public T communicate(InputStream input, OutputStream output, 
				InputStream error) throws VstException, IOException;
	}
	
	public static interface OperationVoid {
		public void communicate(InputStream input, OutputStream output, 
				InputStream error) throws VstException, IOException;
	}
	
	public synchronized <T> T operateFunc(Operation<T> todo) throws VstException {
		try {
			if(process == null) return null;	// already terminated.
			if(!process.isAlive()) throw new IOException();

			synchronized(this) {
				return todo.communicate(processOutput, processInput, processError);
			}
		}
		catch(IOException io) {
			abandon();
			throw new VstException(io);
		}
		catch(VstException ex) {
			abandon();
			throw ex;
		}
	}
	
	public void operate(OperationVoid todo) throws VstException {
		operateFunc((i, o, e) -> {
			todo.communicate(i, o, e);
			return null;
		});
	}
	
	public void ensureInput() throws VstException{
		if(process == null) throw new VstException("Not running!");
		try {
			int available = processOutput.read();
			if(available < 0) throw new IOException("Stream closed.");
			if(available == 0) return;
		
			int exitCode = processOutput.read();
			if(exitCode < 0) throw new IOException();
			throw new VstException(EnumErrorCode.values()[exitCode]);
		}
		catch(IOException e) {
			throw new VstException(e);
		}
	}
}
