package de.mpa.algorithms.similarity;

import java.util.Map;


public class PearsonCorrelation implements SpectrumComparator {

	/**
	 * The input vectorization method.
	 */
	private final Vectorization vect;
	
	/**
	 * The input transformation method.
	 */
	private final Transformation trafo;

	/**
	 * The peak map of the source spectrum.
	 */
	private Map<Double, Double> peaksSrc; 
	
	/**
	 * The similarity score between source spectrum and target spectrum.
	 * Ranges between 0.0 and 1.0.
	 */
	private double similarity;

	/**
	 * The squared magnitude of the source intensity vector.
	 */
	private double denom1;
	
	/**
	 * Class constructor specifying vectorization and data transformation methods.
	 * @param vect
	 * @param trafo
	 */
	public PearsonCorrelation(Vectorization vect, Transformation trafo) {
		this.vect = vect;
		this.trafo = trafo;
	}
	
	// TODO: remove normalization, seems to have no effect here
	
	@Override
	public void prepare(Map<Double, Double> inputPeaksSrc) {
		
		// bin source spectrum
        this.peaksSrc = this.vect.vectorize(inputPeaksSrc, this.trafo);
		
		// calculate magnitude and mean intensity
		double magSrc = 0.0;
		double meanSrc = 0.0;
		for (double intenSrc : this.peaksSrc.values()) {
			magSrc += intenSrc * intenSrc;
			meanSrc += intenSrc;
		}
		magSrc = Math.sqrt(magSrc);
		meanSrc /= this.peaksSrc.size();
		meanSrc /= magSrc;
		
		// normalize and center source spectrum peaks
        this.denom1 = 0.0;
		for (Map.Entry<Double, Double> peakSrc : peaksSrc.entrySet()) {
			double intenSrc = peakSrc.getValue()/magSrc - meanSrc;
			peaksSrc.put(peakSrc.getKey(), intenSrc);
			denom1 += intenSrc * intenSrc;
		}
	}

	@Override
	public void compareTo(Map<Double, Double> inputPeaksTrg) {

		// bin target spectrum
		Map<Double, Double> peaksTrg = vect.vectorize(inputPeaksTrg, trafo);

		// calculate magnitude and mean intensity
		double magTrg = 0.0;
		double meanTrg = 0.0;
		for (double intenTrg : peaksTrg.values()) {
			magTrg += intenTrg * intenTrg;
			meanTrg += intenTrg;
		}
		magTrg = Math.sqrt(magTrg);
		meanTrg /= peaksTrg.size();
		meanTrg /= magTrg;

		// calculate dot product
		double numer = 0.0, denom2 = 0.0;
		for (Map.Entry<Double, Double> peakTrg : peaksTrg.entrySet()) {
			double intenTrg = peakTrg.getValue()/magTrg - meanTrg;	// normalize and center
			Double intenSrc = this.peaksSrc.get(peakTrg.getKey());
			if (intenSrc != null) {
				numer += intenSrc * intenTrg;
			}
			denom2 += intenTrg * intenTrg;
		}
		
		// normalize score
        similarity = numer / Math.sqrt(this.denom1 * denom2);
        similarity = (this.similarity > 0.0) ? this.similarity : 0.0;	// cut off negative scores
	}

	@Override
	public double getSimilarity() {
		return this.similarity;
	}

	@Override
	public Map<Double, Double> getSourcePeaks() {
		return this.peaksSrc;
	}

	@Override
	public Vectorization getVectorization() {
		return this.vect;
	}

}
