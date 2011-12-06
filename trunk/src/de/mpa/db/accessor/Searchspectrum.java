package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mpa.db.MapContainer;

public class Searchspectrum extends SearchspectrumTableAccessor{
    /**
     * Calls the super class.
     * @param params
     */
    public Searchspectrum(HashMap params){
        super(params);
    }
    
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Searchspectrum(ResultSet aRS) throws SQLException {
        super(aRS);
    }
    
    /**
     * This method will find a spectrum file from the current connection, based on the spectrum name.
     *
     * @param spectrumName String with the spectrum name of the spectrum file to find.
     * @param aConn     Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Searchspectrum findFromSpectrumName(String spectrumName, Connection aConn, boolean omssa) throws SQLException {
    	String formatted = "";
    	if(omssa){
    		formatted = spectrumName.replace("\\\\", "/");
    	} else {
    		formatted = spectrumName.replace('\\', '/');
    	}
        Searchspectrum temp = null;
        // Only get the last 1500 records
        PreparedStatement ps = aConn.prepareStatement("select * from searchspectrum where spectrumname = ? order by creationdate LIMIT 1500");
        ps.setString(1, formatted);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Searchspectrum(rs);
        }
        rs.close();
        ps.close();
        if (counter < 1) {
            throw new SQLException("Select based on spectrum name '" + formatted + "' resulted in " + counter + " results!");
        }

        return temp;
    }
    
    public static long getSpectrumIdFromSpectrumName(String spectrumName, boolean omssa) {
    	String formatted = "";
    	if(omssa){
    		formatted = spectrumName.replace("\\\\", "/");
    	} else {
    		formatted = spectrumName.replace('\\', '/');
    	}
        return MapContainer.Spectrumname2IdMap.get(formatted);
    }
    
    public static long getSpectrumIdFromFileName(String filename) {    	
    	return MapContainer.Filename2IdMap.get(filename);
    }
    
    /**
     * This method will find a spectrum file from the current connection, based on the filename.
     *
     * @param fileName String with the filename of the spectrum file to find.
     * @param aConn     Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Searchspectrum findFromFilename(String filename, Connection aConn) throws SQLException {
    	
        Searchspectrum temp = null;
        PreparedStatement ps = aConn.prepareStatement("select * from searchspectrum where filename = ? order by creationdate LIMIT 1500");
        ps.setString(1, filename);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Searchspectrum(rs);
        }
        rs.close();
        ps.close();
        if (counter < 1) {
            throw new SQLException("Select based on filename'" + filename + "' resulted in " + counter + " results!");
        }

        return temp;
    }
    
    /**
     * This method will find a spectrum file from the current connection, based on the spectrum filename.
     *
     * @param filename String with the filename of the spectrum file to find.
     * @param aConn     Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static void checkDuplicateFile(String filename, Connection aConn) throws SQLException {
    	
        Searchspectrum temp = null;
        PreparedStatement ps = aConn.prepareStatement("select * from searchspectrum");        
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Searchspectrum(rs);
            if(temp.getFilename().equals(filename.substring(0, filename.length() - 4) + "_1.mgf")) {
            	throw new SQLException("File '" + filename + "' already exists in the DB!");
            }
        }
        rs.close();
        ps.close();        
    }


    /**
     * This method will find a spectrum file from the current connection, based on the specified spectrumid.
     *
     * @param aSpectrumID long with the spectrumid of the spectrum file to find.
     * @param aConn           Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Searchspectrum findFromID(long aSpectrumID, Connection aConn) throws SQLException {
        Searchspectrum temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where spectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Searchspectrum(rs);
        }
        rs.close();
        ps.close();
        if (counter != 1) {
            SQLException sqe = new SQLException("Select based on spectrum ID '" + aSpectrumID + "' resulted in " + counter + " results instead of 1!");
            sqe.printStackTrace();
            throw sqe;
        }
        return temp;
    }
    
    /**
     * Returns the entries within a specified precursor range.
     * @param precursorMz
     * @param tolMz
     * @param aConn
     * @return
     * @throws SQLException
     */
    public static List<Searchspectrum> getEntriesWithinPrecursorRange(double precursorMz, double tolMz, Connection aConn) throws SQLException {
    	List<Searchspectrum> temp = new ArrayList<Searchspectrum>();
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where precursor_mz >= ? and precursor_mz <= ?");
        ps.setDouble(1, precursorMz - tolMz);
        ps.setDouble(2, precursorMz + tolMz);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp.add(new Searchspectrum(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
}
