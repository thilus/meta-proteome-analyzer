package de.mpa.io.parser.comet;

import java.io.Serializable;

import de.mpa.client.model.SearchHit;
import de.mpa.client.model.dbsearch.SearchEngineType;

public class CometHit implements SearchHit, Serializable {
	
	/**
	 * Default serialization ID.
	 */
	private static final long serialVersionUID = 1L;
	private SearchEngineType type;
	private long spectrumId;
	private int charge;
	private double expNeutralMass;
	private double calcNeutralMass;
	private double eValue;
	private double xCorr;
	private double deltaCn;
	private double spScore;
	private int ionsMatches;
	private int totalIons;
	
	private String peptideSequence;
	private String accession;
	private String proteinSequence;
	private String proteinDescription;
	private double qValue;
	private String spectrumFilename;
	private String spectrumTitle;
	
	public CometHit() {}
	
	public CometHit(SearchEngineType type, long spectrumId, int charge, double expNeutralMass, double calcNeutralMass,
			double eValue, double xCorr, double deltaCn, double spScore, int ionsMatches, int totalIons,
			String peptideSequence, String accession, String proteinSequence, String proteinDescription, double qValue,
			String spectrumFilename, String spectrumTitle) {
		this.type = type;
		this.spectrumId = spectrumId;
		this.charge = charge;
		this.expNeutralMass = expNeutralMass;
		this.calcNeutralMass = calcNeutralMass;
		this.eValue = eValue;
		this.xCorr = xCorr;
		this.deltaCn = deltaCn;
		this.spScore = spScore;
		this.ionsMatches = ionsMatches;
		this.totalIons = totalIons;
		this.peptideSequence = peptideSequence;
		this.accession = accession;
		this.proteinSequence = proteinSequence;
		this.proteinDescription = proteinDescription;
		this.qValue = qValue;
		this.spectrumFilename = spectrumFilename;
		this.spectrumTitle = spectrumTitle;
	}
	
	/**
	 * Copy constructor.
	 */
	public CometHit(CometHit hit) {
		this(hit.getType(), hit.getSpectrumId(), hit.getCharge(), hit.getExpNeutralMass(), hit.getCalcNeutralMass(),
				hit.getEValue(), hit.getXCorr(), hit.getDeltaCn(), hit.getSpScore(), hit.getIonsMatches(),
				hit.getTotalIons(), hit.getPeptideSequence(), hit.getAccession(), hit.getProteinSequence(),
				hit.getProteinDescription(), hit.getQvalue(), hit.getSpectrumFilename(), hit.getSpectrumTitle());
	}
	
	@Override
	public SearchEngineType getType() {
		return type;
	}

	@Override
	public String getPeptideSequence() {
		return peptideSequence;
	}

	@Override
	public String getAccession() {
		return accession;
	}

	@Override
	public double getQvalue() {
		return qValue;
	}

	@Override
	public double getScore() {
		return xCorr;
	}

	@Override
	public String getProteinSequence() {
		return proteinSequence;
	}

	public double getExpNeutralMass() {
		return expNeutralMass;
	}

	public double getCalcNeutralMass() {
		return calcNeutralMass;
	}

	@Override
	public long getSpectrumId() {
		return spectrumId;
	}

	@Override
	public int getCharge() {
		return charge;
	}

	public void setType(SearchEngineType type) {
		this.type = type;
	}

	public void setPeptideSequence(String peptideSequence) {
		this.peptideSequence = peptideSequence;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public void setProteinSequence(String proteinSequence) {
		this.proteinSequence = proteinSequence;
	}

	public void setSpectrumId(long spectrumId) {
		this.spectrumId = spectrumId;
	}

	public void setQValue(double qValue) {
		this.qValue = qValue;
	}

	public void setXCorr(double xCorr) {
		this.xCorr = xCorr;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public double getEValue() {
		return eValue;
	}

	public double getXCorr() {
		return xCorr;
	}

	public double getDeltaCn() {
		return deltaCn;
	}

	public double getSpScore() {
		return spScore;
	}

	public int getIonsMatches() {
		return ionsMatches;
	}

	public int getTotalIons() {
		return totalIons;
	}

	@Override
	public String getProteinDescription() {
		return proteinDescription;
	}

	public void setProteinDescription(String proteinDescription) {
		this.proteinDescription = proteinDescription;
	}
	
	
	@Override
	public String getSpectrumFilename() {
		return spectrumFilename;
	}

	public void setSpectrumFilename(String spectrumFilename) {
		this.spectrumFilename = spectrumFilename;
	}

	@Override
	public String getSpectrumTitle() {
		return spectrumTitle;
	}

	public void setSpectrumTitle(String spectrumTitle) {
		this.spectrumTitle = spectrumTitle;
	}

	public void setExpNeutralMass(double expNeutralMass) {
		this.expNeutralMass = expNeutralMass;
	}

	public void setCalcNeutralMass(double calcNeutralMass) {
		this.calcNeutralMass = calcNeutralMass;
	}

	public void setEValue(double eValue) {
		this.eValue = eValue;
	}

	public void setDeltaCn(double deltaCn) {
		this.deltaCn = deltaCn;
	}

	public void setSpScore(double spScore) {
		this.spScore = spScore;
	}

	public void setIonsMatches(int ionsMatches) {
		this.ionsMatches = ionsMatches;
	}

	public void setTotalIons(int totalIons) {
		this.totalIons = totalIons;
	}
}