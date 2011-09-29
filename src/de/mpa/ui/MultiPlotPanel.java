package de.mpa.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JPanel;

import de.mpa.io.MascotGenericFile;

public class MultiPlotPanel extends JPanel {


	private ArrayList<MascotGenericFile> spectra;
	private int padX = 5, padY = 5;
	private Color bgColor = Color.WHITE;
	private ArrayList<Color> lineColors;
	
	
	public ArrayList<MascotGenericFile> getSpectra() {
		return spectra;
	}
	public void setSpectra(ArrayList<MascotGenericFile> spectra) {
		this.spectra = spectra;
	}

	public int getPadX() {
		return padX;
	}
	public void setPadX(int padX) {
		this.padX = padX;
	}

	public int getPadY() {
		return padY;
	}
	public void setPadY(int padY) {
		this.padY = padY;
	}

	public Color getBgColor() {
		return bgColor;
	}
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public ArrayList<Color> getLineColors() {
		return lineColors;
	}
	public void setLineColors(ArrayList<Color> lineColors) {
		this.lineColors = lineColors;
	}
	

	@Override
	protected void paintComponent( Graphics g )
	{
//		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent( g );
		
		g.setColor(bgColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// determine axis limits
		double minX = Double.MAX_VALUE, maxX = 0, maxY = 0;
		if (this.spectra != null) {
			for (MascotGenericFile spectrum : this.spectra) {
				HashMap<Double, Double> peaks = spectrum.getPeaks();
				TreeSet<Double> sortedPeaks = new TreeSet<Double>(peaks.keySet());
				if (spectrum != null) {
					minX = Math.min(minX, sortedPeaks.first());
					maxX = Math.max(maxX, sortedPeaks.last());
					maxY = Math.max(maxY, spectrum.getHighestIntensity());
				}
			}
		}

		// plot spectra
		if (this.spectra != null) {
			int i = 0;
			for (MascotGenericFile spectrum : this.spectra) {
				HashMap<Double, Double> peaks = spectrum.getPeaks();
				TreeSet<Double> sortedPeaks = new TreeSet<Double>(peaks.keySet());
				if ((spectrum != null) && (lineColors.get(i) != null)) {
					// grab peaks and draw stuff
					g.setColor(lineColors.get(i));
//					lineColors.get(i).
					for (Double mz : sortedPeaks) {
						g.drawLine((int)((mz-minX)/(maxX-minX)*(this.getWidth()-2*padX)+padX),
									    (this.getHeight()-padY),
								   (int)((mz-minX)/(maxX-minX)*(this.getWidth()-2*padX)+padX),
								   (int)(this.getHeight()-padY-peaks.get(mz)/maxY*(this.getHeight()-2*padY)));
					}
				}
				i++;
			}
		}
	}
}