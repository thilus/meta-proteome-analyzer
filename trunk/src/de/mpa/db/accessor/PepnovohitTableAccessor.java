/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 01/12/2011
 * Time: 14:55:09
 */
package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.compomics.util.db.interfaces.Deleteable;
import com.compomics.util.db.interfaces.Persistable;
import com.compomics.util.db.interfaces.Retrievable;
import com.compomics.util.db.interfaces.Updateable;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2007/07/06 09:41:53 $
 */

/**
 * This class is a generated accessor for the Pepnovohit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class PepnovohitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'pepnovohitid' column.
	 */
	protected long iPepnovohitid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_spectrumid' column.
	 */
	protected long iFk_spectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_peptideid' column.
	 */
	protected long iFk_peptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'indexid' column.
	 */
	protected long iIndexid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'rankscore' column.
	 */
	protected Number iRankscore = null;


	/**
	 * This variable represents the contents for the 'pnvscore' column.
	 */
	protected Number iPnvscore = null;


	/**
	 * This variable represents the contents for the 'n_gap' column.
	 */
	protected Number iN_gap = null;


	/**
	 * This variable represents the contents for the 'c_gap' column.
	 */
	protected Number iC_gap = null;


	/**
	 * This variable represents the contents for the 'precursor_mh' column.
	 */
	protected Number iPrecursor_mh = null;


	/**
	 * This variable represents the contents for the 'charge' column.
	 */
	protected long iCharge = Long.MIN_VALUE;


	/**
	 * This variable represents the key for the 'pepnovohitid' column.
	 */
	public static final String PEPNOVOHITID = "PEPNOVOHITID";

	/**
	 * This variable represents the key for the 'fk_spectrumid' column.
	 */
	public static final String FK_SPECTRUMID = "FK_SPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_peptideid' column.
	 */
	public static final String FK_PEPTIDEID = "FK_PEPTIDEID";

	/**
	 * This variable represents the key for the 'indexid' column.
	 */
	public static final String INDEXID = "INDEXID";

	/**
	 * This variable represents the key for the 'rankscore' column.
	 */
	public static final String RANKSCORE = "RANKSCORE";

	/**
	 * This variable represents the key for the 'pnvscore' column.
	 */
	public static final String PNVSCORE = "PNVSCORE";

	/**
	 * This variable represents the key for the 'n_gap' column.
	 */
	public static final String N_GAP = "N_GAP";

	/**
	 * This variable represents the key for the 'c_gap' column.
	 */
	public static final String C_GAP = "C_GAP";

	/**
	 * This variable represents the key for the 'precursor_mh' column.
	 */
	public static final String PRECURSOR_MH = "PRECURSOR_MH";

	/**
	 * This variable represents the key for the 'charge' column.
	 */
	public static final String CHARGE = "CHARGE";




	/**
	 * Default constructor.
	 */
	public PepnovohitTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'PepnovohitTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public PepnovohitTableAccessor(HashMap aParams) {
		if(aParams.containsKey(PEPNOVOHITID)) {
			this.iPepnovohitid = ((Long)aParams.get(PEPNOVOHITID)).longValue();
		}
		if(aParams.containsKey(FK_SPECTRUMID)) {
			this.iFk_spectrumid = ((Long)aParams.get(FK_SPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_PEPTIDEID)) {
			this.iFk_peptideid = ((Long)aParams.get(FK_PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(INDEXID)) {
			this.iIndexid = ((Long)aParams.get(INDEXID)).longValue();
		}
		if(aParams.containsKey(RANKSCORE)) {
			this.iRankscore = (Number)aParams.get(RANKSCORE);
		}
		if(aParams.containsKey(PNVSCORE)) {
			this.iPnvscore = (Number)aParams.get(PNVSCORE);
		}
		if(aParams.containsKey(N_GAP)) {
			this.iN_gap = (Number)aParams.get(N_GAP);
		}
		if(aParams.containsKey(C_GAP)) {
			this.iC_gap = (Number)aParams.get(C_GAP);
		}
		if(aParams.containsKey(PRECURSOR_MH)) {
			this.iPrecursor_mh = (Number)aParams.get(PRECURSOR_MH);
		}
		if(aParams.containsKey(CHARGE)) {
			this.iCharge = ((Long)aParams.get(CHARGE)).longValue();
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'PepnovohitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Pepnovohit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public PepnovohitTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iPepnovohitid = aResultSet.getLong("pepnovohitid");
		this.iFk_spectrumid = aResultSet.getLong("fk_spectrumid");
		this.iFk_peptideid = aResultSet.getLong("fk_peptideid");
		this.iIndexid = aResultSet.getLong("indexid");
		this.iRankscore = (Number)aResultSet.getObject("rankscore");
		this.iPnvscore = (Number)aResultSet.getObject("pnvscore");
		this.iN_gap = (Number)aResultSet.getObject("n_gap");
		this.iC_gap = (Number)aResultSet.getObject("c_gap");
		this.iPrecursor_mh = (Number)aResultSet.getObject("precursor_mh");
		this.iCharge = aResultSet.getLong("charge");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Pepnovohitid' column
	 * 
	 * @return	long	with the value for the Pepnovohitid column.
	 */
	public long getPepnovohitid() {
		return this.iPepnovohitid;
	}

	/**
	 * This method returns the value for the 'Fk_spectrumid' column
	 * 
	 * @return	long	with the value for the Fk_spectrumid column.
	 */
	public long getFk_spectrumid() {
		return this.iFk_spectrumid;
	}

	/**
	 * This method returns the value for the 'Fk_peptideid' column
	 * 
	 * @return	long	with the value for the Fk_peptideid column.
	 */
	public long getFk_peptideid() {
		return this.iFk_peptideid;
	}

	/**
	 * This method returns the value for the 'Indexid' column
	 * 
	 * @return	long	with the value for the Indexid column.
	 */
	public long getIndexid() {
		return this.iIndexid;
	}

	/**
	 * This method returns the value for the 'Rankscore' column
	 * 
	 * @return	Number	with the value for the Rankscore column.
	 */
	public Number getRankscore() {
		return this.iRankscore;
	}

	/**
	 * This method returns the value for the 'Pnvscore' column
	 * 
	 * @return	Number	with the value for the Pnvscore column.
	 */
	public Number getPnvscore() {
		return this.iPnvscore;
	}

	/**
	 * This method returns the value for the 'N_gap' column
	 * 
	 * @return	Number	with the value for the N_gap column.
	 */
	public Number getN_gap() {
		return this.iN_gap;
	}

	/**
	 * This method returns the value for the 'C_gap' column
	 * 
	 * @return	Number	with the value for the C_gap column.
	 */
	public Number getC_gap() {
		return this.iC_gap;
	}

	/**
	 * This method returns the value for the 'Precursor_mh' column
	 * 
	 * @return	Number	with the value for the Precursor_mh column.
	 */
	public Number getPrecursor_mh() {
		return this.iPrecursor_mh;
	}

	/**
	 * This method returns the value for the 'Charge' column
	 * 
	 * @return	long	with the value for the Charge column.
	 */
	public long getCharge() {
		return this.iCharge;
	}

	/**
	 * This method sets the value for the 'Pepnovohitid' column
	 * 
	 * @param	aPepnovohitid	long with the value for the Pepnovohitid column.
	 */
	public void setPepnovohitid(long aPepnovohitid) {
		this.iPepnovohitid = aPepnovohitid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_spectrumid' column
	 * 
	 * @param	aFk_spectrumid	long with the value for the Fk_spectrumid column.
	 */
	public void setFk_spectrumid(long aFk_spectrumid) {
		this.iFk_spectrumid = aFk_spectrumid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_peptideid' column
	 * 
	 * @param	aFk_peptideid	long with the value for the Fk_peptideid column.
	 */
	public void setFk_peptideid(long aFk_peptideid) {
		this.iFk_peptideid = aFk_peptideid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Indexid' column
	 * 
	 * @param	aIndexid	long with the value for the Indexid column.
	 */
	public void setIndexid(long aIndexid) {
		this.iIndexid = aIndexid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Rankscore' column
	 * 
	 * @param	aRankscore	Number with the value for the Rankscore column.
	 */
	public void setRankscore(Number aRankscore) {
		this.iRankscore = aRankscore;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Pnvscore' column
	 * 
	 * @param	aPnvscore	Number with the value for the Pnvscore column.
	 */
	public void setPnvscore(Number aPnvscore) {
		this.iPnvscore = aPnvscore;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'N_gap' column
	 * 
	 * @param	aN_gap	Number with the value for the N_gap column.
	 */
	public void setN_gap(Number aN_gap) {
		this.iN_gap = aN_gap;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'C_gap' column
	 * 
	 * @param	aC_gap	Number with the value for the C_gap column.
	 */
	public void setC_gap(Number aC_gap) {
		this.iC_gap = aC_gap;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Precursor_mh' column
	 * 
	 * @param	aPrecursor_mh	Number with the value for the Precursor_mh column.
	 */
	public void setPrecursor_mh(Number aPrecursor_mh) {
		this.iPrecursor_mh = aPrecursor_mh;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Charge' column
	 * 
	 * @param	aCharge	long with the value for the Charge column.
	 */
	public void setCharge(long aCharge) {
		this.iCharge = aCharge;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM pepnovohit WHERE pepnovohitid = ?");
		lStat.setLong(1, iPepnovohitid);
		int result = lStat.executeUpdate();
		lStat.close();
		return result;
	}


	/**
	 * This method allows the caller to read data for this
	 * object from a persistent store based on the specified keys.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public void retrieve(Connection aConn, HashMap aKeys) throws SQLException {
		// First check to see whether all PK fields are present.
		if(!aKeys.containsKey(PEPNOVOHITID)) {
			throw new IllegalArgumentException("Primary key field 'PEPNOVOHITID' is missing in HashMap!");
		} else {
			iPepnovohitid = ((Long)aKeys.get(PEPNOVOHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM pepnovohit WHERE pepnovohitid = ?");
		lStat.setLong(1, iPepnovohitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iPepnovohitid = lRS.getLong("pepnovohitid");
			iFk_spectrumid = lRS.getLong("fk_spectrumid");
			iFk_peptideid = lRS.getLong("fk_peptideid");
			iIndexid = lRS.getLong("indexid");
			iRankscore = (Number)lRS.getObject("rankscore");
			iPnvscore = (Number)lRS.getObject("pnvscore");
			iN_gap = (Number)lRS.getObject("n_gap");
			iC_gap = (Number)lRS.getObject("c_gap");
			iPrecursor_mh = (Number)lRS.getObject("precursor_mh");
			iCharge = lRS.getLong("charge");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'pepnovohit' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'pepnovohit' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from pepnovohit";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<PepnovohitTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<PepnovohitTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<PepnovohitTableAccessor>  entities = new ArrayList<PepnovohitTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new PepnovohitTableAccessor(rs));
		}
		rs.close();
		stat.close();
		return entities;
	}



	/**
	 * This method allows the caller to update the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int update(Connection aConn) throws SQLException {
		if(!this.iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE pepnovohit SET pepnovohitid = ?, fk_spectrumid = ?, fk_peptideid = ?, indexid = ?, rankscore = ?, pnvscore = ?, n_gap = ?, c_gap = ?, precursor_mh = ?, charge = ? WHERE pepnovohitid = ?");
		lStat.setLong(1, iPepnovohitid);
		lStat.setLong(2, iFk_spectrumid);
		lStat.setLong(3, iFk_peptideid);
		lStat.setLong(4, iIndexid);
		lStat.setObject(5, iRankscore);
		lStat.setObject(6, iPnvscore);
		lStat.setObject(7, iN_gap);
		lStat.setObject(8, iC_gap);
		lStat.setObject(9, iPrecursor_mh);
		lStat.setLong(10, iCharge);
		lStat.setLong(11, iPepnovohitid);
		int result = lStat.executeUpdate();
		lStat.close();
		this.iUpdated = false;
		return result;
	}


	/**
	 * This method allows the caller to insert the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int persist(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO pepnovohit (pepnovohitid, fk_spectrumid, fk_peptideid, indexid, rankscore, pnvscore, n_gap, c_gap, precursor_mh, charge) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		if(iPepnovohitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iPepnovohitid);
		}
		if(iFk_spectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_spectrumid);
		}
		if(iFk_peptideid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_peptideid);
		}
		if(iIndexid == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, iIndexid);
		}
		if(iRankscore == null) {
			lStat.setNull(5, 3);
		} else {
			lStat.setObject(5, iRankscore);
		}
		if(iPnvscore == null) {
			lStat.setNull(6, 3);
		} else {
			lStat.setObject(6, iPnvscore);
		}
		if(iN_gap == null) {
			lStat.setNull(7, 3);
		} else {
			lStat.setObject(7, iN_gap);
		}
		if(iC_gap == null) {
			lStat.setNull(8, 3);
		} else {
			lStat.setObject(8, iC_gap);
		}
		if(iPrecursor_mh == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, iPrecursor_mh);
		}
		if(iCharge == Long.MIN_VALUE) {
			lStat.setNull(10, 4);
		} else {
			lStat.setLong(10, iCharge);
		}
		int result = lStat.executeUpdate();

		// Retrieving the generated keys (if any).
		ResultSet lrsKeys = lStat.getGeneratedKeys();
		ResultSetMetaData lrsmKeys = lrsKeys.getMetaData();
		int colCount = lrsmKeys.getColumnCount();
		iKeys = new Object[colCount];
		while(lrsKeys.next()) {
			for(int i=0;i<iKeys.length;i++) {
				iKeys[i] = lrsKeys.getObject(i+1);
			}
		}
		lrsKeys.close();
		lStat.close();
		// Verify that we have a single, generated key.
		if(iKeys != null && iKeys.length == 1 && iKeys[0] != null) {
			// Since we have exactly one key specified, and only
			// one Primary Key column, we can infer that this was the
			// generated column, and we can therefore initialize it here.
			iPepnovohitid = ((Number) iKeys[0]).longValue();
		}
		this.iUpdated = false;
		return result;
	}

	/**
	 * This method will return the automatically generated key for the insert if 
	 * one was triggered, or 'null' otherwise.
	 *
	 * @return	Object[]	with the generated keys.
	 */
	public Object[] getGeneratedKeys() {
		return this.iKeys;
	}

}