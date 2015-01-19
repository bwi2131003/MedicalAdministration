package de.rho.server.patient.boundary;

import java.util.ArrayList;

import de.rho.server.patient.entity.Patient;


/**
 * @author Andreas R�wert, Heiko Herder, Roger Ordon
 * @version 1.1
 * 
 * Interface als interne Schnittstelle (2CSV)
 * Methodenvorlage fuer CSV Dateizugriffe
 *
 */

public interface InPatientToCSV {
	
	public ArrayList<Patient> readPatientFromCSV(String list);
	
	public void writePatientToCSV(ArrayList<Patient> list);
		

}
