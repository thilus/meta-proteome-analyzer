package de.mpa.jobs;


/**
 * Possible values for the job status are:
 * 
 * 	WAITING - job is waiting for processing thread.
 * 
 * 	RUNNING - job is running.
 * 
 * 	FINISHED - job is finished.
 * 
 *  ERROR - job gave an error.
 * 
 * @author Thilo Muth
 *
 */
public enum JobStatus {
	
	WAITING, RUNNING, FINISHED, ERROR

}

