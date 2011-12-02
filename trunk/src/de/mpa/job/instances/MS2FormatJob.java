package de.mpa.job.instances;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import de.mpa.job.Job;

public class MS2FormatJob extends Job{
	
	private String ms2file;
	private String outputfile;
	private File mgfFile;
	
	public MS2FormatJob(File mgfFile) {
		this.mgfFile = mgfFile;
	}
	
	public void run() {
			ms2file = JobConstants.DATASET_PATH + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + ".ms2";;
			outputfile = mgfFile.getAbsolutePath().substring(0, mgfFile.getAbsolutePath().indexOf(".mgf")) + "_format.ms2";;
			setDescription("MS2 FORMAT JOB");
			BufferedReader reader = null;
			BufferedWriter writer = null;
			int scan = 1;
			String formatted = "";
			try {
				reader = new BufferedReader(new FileReader(ms2file));
				writer = new BufferedWriter(new FileWriter(outputfile));
				String nextLine;			
				String[] splits;
				
				// Iterate over all the lines of the file.
				while ((nextLine = reader.readLine()) != null) {					
					if(nextLine.charAt(0) == 'S'){
						splits = nextLine.split("\\s+");
						formatted = splits[0] + "\t" + scan + "\t" + scan + "\t" + splits[3];
						writer.write(formatted  + "\n");
						scan++;
					} else {
						writer.write(nextLine  + "\n");
					}					
				}			
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
	}
}
