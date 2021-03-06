package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

import de.mpa.client.settings.Parameter.NumberParameter;

/**
 * Parameter settings for spectrum filtering in File Panel.
 * 
 * @author T.Muth
 */
public class SpectrumFilterParameters extends ParameterMap {
	
	@Override
	public void initDefaults() {
		this.put("minpeaks", new NumberParameter(10, 1, null, "Min. Significant Peaks", "The minimum number of peaks in the spectrum with intensity above the noise level.", "Spectrum Filtering"));
		this.put("mintic", new NumberParameter(10000, 1, null, "Min. Total Ion Current", "The minimum total ion current of the spectrum.", "Spectrum Filtering"));
		this.put("minsnr", new NumberParameter(1.0, 0.0, null, "Min. Signal/Noise Ratio", "The minimum signal-to-noise ratio.", "Spectrum Filtering"));
		this.put("noiselvl", new NumberParameter(2.5, 0.0, null, "Noise level", "The ion intensity below which peaks are considered noise.", "Spectrum Filtering"));
	}

	@Override
	public File toFile(String path) throws IOException {
		// do nothing, not needed
		return null;
	}
}