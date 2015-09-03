package de.mpa.job.instances;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpa.io.GenericContainer;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.job.Job;

public class SpectraJob extends Job {
	
	private MascotGenericFileReader reader;
	private List<File> files;
	
    /**
     * Constructor with experiment id as additional parameter:
     * Used for storing the search spectra.
     * 
     * @param files The spectrum search files.
     */
    public SpectraJob(List<File> files) {
    	this.files = files;
    }

	@Override
	public void run() {
		
		log = Logger.getLogger(getClass());
    	setDescription("INITIALIZE SPECTRA");
    	int totalSpectra = 0;
    	
		// Iterate the MGF files.
		for (File file : files) {
			try {
				long spectrumCounter = 0;
				reader = new MascotGenericFileReader(file);
				// Get all spectra from the reader.
				List<MascotGenericFile> spectra = reader.getSpectrumFiles(false);

				// Iterate over all spectra.
				for (MascotGenericFile mgf : spectra) {
					// The filename, remove leading and trailing whitespace.
					String title = mgf.getTitle().trim();
					
					// Fill the cache maps
					GenericContainer.SpectrumTitle2IdMap.put(title, ++spectrumCounter);
					GenericContainer.SpectrumTitle2FilenameMap.put(title, file.getAbsolutePath());
					totalSpectra++;
				}
				GenericContainer.MGFReaders.put(file.getAbsolutePath(), reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		GenericContainer.numberTotalSpectra = totalSpectra;
		
	}
}
