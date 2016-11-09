package de.mpa.client.blast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.client.ui.dialogs.BlastDialog.BlastResultOption;

/**
 * This class holds a BLAST query.
 * @author R. Heyer
 */
public class BlastResult {
	
	/**
	 * The name of the BLAST query.
	 */
	String name;
	
	/**
	 * The length of a BLAST query
	 */
	int length;
	
	/**
	 * The BLAST hits for the BLAST query
	 */
	Map<String,BlastHit> blastHitsMap;
	
	/**
	 * The database against which the query was carried out
	 */
	String database;
	
	/**
	 * Default constructor.
	 */
	public BlastResult() {
	blastHitsMap = new HashMap<String, BlastHit>();
	}
	
	/**
	 * Gets the BLAST hits map.
	 * @return The BLAST hits map.
	 */
	public Map<String, BlastHit> getBlastHitsMap() {
		return blastHitsMap;
	}

	/**
	 * Sets the BLAST hits map with the accession as key
	 * @param blastHitsMap
	 */
	public void putBlastHitsMap(BlastHit blastHit) {
		this.blastHitsMap.put(blastHit.getAccession(), blastHit);
	}

	/**
	 * Gets the name of a BLAST query.
	 * @return. The name of the BLAST query.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the BLAST query-
	 * @param name. The name of the BLASt query
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the length of a BLAST query.
	 * @return The length of the query sequence.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Sets the length of the BLAST query.
	 * @param length. The length of the BLASt query
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Gets the database of the query.
	 * @return The name of the database.
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Sets the name of the database.
	 * @param database. The name of the database.
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
	
	/**
	 * Method to get best BLAST hit (The one with the lowest e-value).
	 * @return BlastHit. The best BLAST HIT 
	 */
	public BlastHit getBestEValueBlastHit(){
	
		BlastHit bestBlastHit = null;
		
		if (this.blastHitsMap != null & this.blastHitsMap.size() > 0 ) {
			Collection<BlastHit> blastHitColl = this.blastHitsMap.values();
			bestBlastHit = blastHitColl.iterator().next();
			for (BlastHit blastHit : blastHitColl) {
				if (bestBlastHit.geteValue() > blastHit.geteValue()) {
					bestBlastHit = blastHit;
				}
			}
		}
		return bestBlastHit ; 
	}
	
	/**
	 * Method to get best BLAST hit (The one with the lowest e-value).
	 * @return BlastHit. The best BLAST HIT 
	 */
	public BlastHit getBestBitScoreBlastHit(){
	
		BlastHit bestBlastHit = null;
		
		if (this.blastHitsMap != null & this.blastHitsMap.size() > 0 ) {
			Collection<BlastHit> blastHitColl = this.blastHitsMap.values();
			bestBlastHit = blastHitColl.iterator().next();
			for (BlastHit blastHit : blastHitColl) {
				if (bestBlastHit.getScore() < blastHit.getScore()) {
					bestBlastHit = blastHit;
				}
			}
		}
		return bestBlastHit; 
	}
	
	/**
	 * Method to get all BLAST hits that satisfy a given evalue threshold (The one with the lowest e-value).
	 * @return List<BlastHit>. The list of blast hits 
	 */
	public List<BlastHit> getBestBlastHits(double evalue){
		
		// init list
		List<BlastHit> bestBlastHits = new ArrayList<BlastHit>();
						
		// collect hits
		if (this.blastHitsMap != null & this.blastHitsMap.size() > 0 ) {
			Collection<BlastHit> blastHitColl = this.blastHitsMap.values();
			// find lowest evalue
			double evalue_new = evalue;
			for (BlastHit blastHit : blastHitColl) {
				if (blastHit.geteValue() <= evalue_new) {
					evalue_new = blastHit.geteValue();
				}
			}
			// take all hits with the lowest evalue
			for (BlastHit blastHit_2 : blastHitColl) {
				if (blastHit_2.geteValue() <= evalue_new) {
					bestBlastHits.add(blastHit_2);
				}
			}
		}
		return bestBlastHits ; 
	}
//	String[] items = new String[] {"Best E-value","Best Identity", "Best BitScore", "First E-value", "First Identity", "First BitScore", "All Hits"};
	public List<BlastHit> getSelectedBLASTHits(BlastResultOption resultOption) {
		// init list
		List<BlastHit> bestBlastHits = new ArrayList<BlastHit>();
		// collect hits
		if (this.blastHitsMap != null & this.blastHitsMap.size() > 0 ) {
			Collection<BlastHit> blastHitColl = this.blastHitsMap.values();
			switch (resultOption) {
				case BEST_BITSCORE:
				case FIRST_BITSCORE:
					// bitscore of 0 for initial comparison
					double bitscore = 0;
					for (BlastHit blastHit : blastHitColl) {
						if (blastHit.getScore() >= bitscore) {
							bitscore = blastHit.getScore();
						}
					}
					// take all hits with the highest shared bitscore
					for (BlastHit blastHit_2 : blastHitColl) {
						if (blastHit_2.getScore() == bitscore) {
							bestBlastHits.add(blastHit_2);
							if (resultOption == BlastResultOption.FIRST_BITSCORE) {
								return bestBlastHits;
							}
						}
					}
					return bestBlastHits;
				case BEST_EVALUES: 
				case FIRST_EVALUE:
					// evalue of 1 for initial comparison
					double evalue_new = 1;
					for (BlastHit blastHit : blastHitColl) {
						if (blastHit.geteValue() <= evalue_new) {
							evalue_new = blastHit.geteValue();
						}
					}
					// take all hits with the lowest evalue
					for (BlastHit blastHit_2 : blastHitColl) {
						if (blastHit_2.geteValue() == evalue_new) {
							bestBlastHits.add(blastHit_2);
							if (resultOption == BlastResultOption.FIRST_EVALUE) {
								return bestBlastHits;
							}
						}
					}
					return bestBlastHits;
				case BEST_IDENTITIES:
				case FIRST_IDENTITY:
					// initial identity of 0% for comparison
					double identities = 0;
					for (BlastHit blastHit : blastHitColl) {
						if (blastHit.getIdentities() >= identities) {
							identities = blastHit.getIdentities();
						}
					}
					// take all hits with the highest identity (max == 100%) 
					for (BlastHit blastHit_2 : blastHitColl) {
						if (blastHit_2.getIdentities() == identities) {
							bestBlastHits.add(blastHit_2);
							if (resultOption == BlastResultOption.FIRST_IDENTITY) {
								return bestBlastHits;
							}
						}
					}
					return bestBlastHits;
				case ALL_HITS:
					return (List<BlastHit>) blastHitColl;
			}
		}
		return bestBlastHits;
	}
	
}
