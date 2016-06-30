package net.aegistudio.resonance.plugin.vst;

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
	}

	@Override
	public void trigger(Event event) {
		
	}

	@Override
	public void process(Frame input, Frame output) {
		
	}

	@Override
	public void destroy() throws VstException {
		operate((i, o, e) -> o.write(EnumOperation.TERMINATE.ordinal()));
		abandon();
	}
	
	protected void abandon() {
		this.process.destroy();
		this.process = null;
	}
	
	public static interface Operation {
		public void communicate(InputStream input, OutputStream output, 
				InputStream error) throws VstException, IOException;
	}
	
	public void operate(Operation todo) throws VstException {
		try {
			if(process == null) return;	// already terminated.
			if(!process.isAlive()) throw new IOException();
			todo.communicate(processOutput, processInput, processError);
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
