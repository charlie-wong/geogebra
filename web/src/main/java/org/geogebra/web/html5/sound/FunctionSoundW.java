package org.geogebra.web.html5.sound;


import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.sound.FunctionSound;
import org.geogebra.web.html5.sound.WebAudioWrapper.FunctionAudioListener;

/**
 * Class for playing function-generated sounds.
 * 
 * @author Laszlo Gal
 *
 */
public final class FunctionSoundW extends FunctionSound implements
		FunctionAudioListener {

	public static final FunctionSoundW INSTANCE = new FunctionSoundW();
	private WebAudioWrapper waw = WebAudioWrapper.INSTANCE;

	/**
	 * Constructs instance of FunctionSound
	 * 
	 */
	public FunctionSoundW() {
		super();
		if (WebAudioWrapper.INSTANCE.init()) {
			App.debug("[WEB AUDIO] Initialization is OK.");
		} else {
			App.debug("[WEB AUDIO] Initialization has FAILED.");
		}

		if (!initStreamingAudio(getSampleRate(), getBitDepth())) {
			App.error("Cannot initialize streaming audio");
		}

	}
	/**
	 * Initializes instances of AudioFormat and SourceDataLine
	 * 
	 * @param sampleRate
	 *            = 8000, 16000, 11025, 16000, 22050, or 44100
	 * @param bitDepth
	 *            = 8 or 16
	 * @return
	 */
	protected boolean initStreamingAudio(int sampleRate, int bitDepth) {
		if (super.initStreamingAudio(sampleRate, bitDepth) == false) {
			return false;
		}

		waw.setListener(this);


		return true;
	}

	/**
	 * Plays a sound generated by the time valued GeoFunction f(t), from t = min
	 * to t = max in seconds. The function is assumed to have range [-1,1] and
	 * will be clipped to this range otherwise.
	 * 
	 * @param geoFunction
	 * @param min
	 * @param max
	 * @param sampleRate
	 * @param bitDepth
	 */

	@Override
	public void playFunction(final GeoFunction geoFunction, final double min,
			final double max, final int sampleRate, final int bitDepth) {
		if (!checkFunction(geoFunction, min, max, sampleRate, bitDepth)) {
			return;
		}
		App.debug("FunctionSound");
		waw.setListener(this);
		generateFunctionSound();
	}

	/**
	 * Pauses/resumes sound generation
	 * 
	 * @param doPause
	 */
	public void pause(boolean doPause) {

		if (doPause) {
			setMin(getT());
			stopSound();
		} else {
			playFunction(getF(), getMin(), getMax(), getSampleRate(),
					getBitDepth());
		}
	}

	private boolean stopped = false;



	private void generateFunctionSound() {

		stopped = false;

		// time between samples
		setSamplePeriod(1.0 / getSampleRate());

		// create internal buffer for mathematically generated sound data
		// a small buffer minimizes latency when the function changes
		// dynamically
		// TODO: find optimal buffer size
		waw.start(getSampleRate());

		int frameSetSize = getSampleRate() / 5; // 20ms ok?
		if (getBitDepth() == 8) {
			setBuf(new byte[frameSetSize]);
		} else {
			setBuf(new byte[2 * frameSetSize]);
		}


		setT(getMin());
		loadBuffer();
		doFade(getBuf()[0], false);
		// waw.write(getBuf(), frameSetSize);
		waw.write(getBuf(), getBufLength());

	}

	private void loadBuffer() {
		if (getBitDepth() == 16) {
			loadBuffer16(getT());
		} else {
			loadBuffer8(getT());
		}

	}

	public void fillBuffer() {
		int frameSetSize = getSampleRate() / 5;

		if (getT() < getMax() && !stopped) {
			setT(getT() + getSamplePeriod() * frameSetSize);
			loadBuffer();
			// waw.write(getBuf(), frameSetSize);
			waw.write(getBuf(), getBufLength());
		} else if (getBitDepth() == 16 || (getBitDepth() == 8 && !stopped)) {

			doFade(getBuf()[getBufLength() - 1], true);
		}

	}

	private void doFade(short peakValue, boolean isFadeOut) {
		byte[] fadeBuf = getFadeBuffer(peakValue, isFadeOut);
		waw.write(fadeBuf, fadeBuf.length);
	}

	/**
	 * Stops function sound
	 */
	public void stopSound() {
		stopped = true;
		waw.stop();
	}

	}
