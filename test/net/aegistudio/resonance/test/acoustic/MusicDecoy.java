package net.aegistudio.resonance.test.acoustic;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.music.MusicFacade;
import net.aegistudio.resonance.serial.Structure;

/**
 * Acoustic layer test doesn't need a complete music layer.
 * So I've written a music decoy with all empty methods in it.
 * @author aegistudio
 */

public class MusicDecoy implements MusicFacade
{
	@Override
	public void load(Structure output)	{		}

	@Override
	public void save(Structure output) {		}

	@Override
	public void tick() {			}

	@Override
	public void setBeatsPerMinute(float bpm) {			}

	@Override
	public float getBeatsPerMinute() {		return 0;	}

	@Override
	public void reset(Environment environment) {			}

	@Override
	public void setBeatPosition(double position) {			}

	@Override
	public double getBeatPosition() {				return 0;			}
}
