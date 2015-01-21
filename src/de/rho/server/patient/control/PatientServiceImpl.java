package de.rho.server.patient.control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.rho.server.dao.boundary.InDaoToDB;
import de.rho.server.dao.boundary.InDaoToFile;
import de.rho.server.dao.control.FaDaoService;
import de.rho.server.patient.boundary.InPatientService;
import de.rho.server.patient.entity.Patient;

/*
import java.util.Date;
  
public class DateDemo {
   public static void main(String args[]) {
       // Instantiate a Date object
       Date date = new Date();
        
       // display time and date using toString()
       System.out.println(date.toString());
   }
}
 */


/**
 * @author Heiko, Roger
 * @version 1.1
 * 
 * Implementierung des Services Patient
 * Uebergabestation der Servicemethoden an konkrete Klassen 
 *
 */

public class PatientServiceImpl extends UnicastRemoteObject implements InPatientService {
	
	
	//Alternative:
	//interface variable = factory.methode...
	
	//Trennung von Methoden-Service (File, DB) und Connection-Service (File, DB)
	

	/** Methoden-Services **/
	private PatientToCSV patient2csv; 	 				//Deklaration fuer CSV
	private PatientToDB patient2db;				 		//Deklaration fuer DB
	
	
	/** Connection-Services **/
	private InDaoToDB db_service = FaDaoService.getDaoToDBService();
	private InDaoToFile file_service = FaDaoService.getDaoToFileService(); //to do: benutze file_service-connection
	private ResultSet resultSet;
	private String sql_statement;
	
	protected PatientServiceImpl() throws RemoteException {
		super();
		this.patient2csv = new PatientToCSV(); //Initialisierung/Instanziierung der "Objektverbindung" CSV (Defaultkonstruktor)
		this.patient2db = new PatientToDB();   //Initialisierung/Instanziierung der "Objektverbindung" DB (Defaultkonstruktor)

	}

	
	/**** TEST ****/
	
	public String createPatient(String test) throws RemoteException {
		return test;
	}
	
	/**************/
	/**** CRUD ****/
	/**************/
	
	// ************************
	// **** create Patient ****
	// ************************
	@Override
	public void createPatientInDB(Patient patient) throws RemoteException {
		System.out.println("PatientServiceImpl.createPatientInDB");
		
		
		// **** SQL Statement erstellen ****
		sql_statement = this.patient2db.createPatientSqlStatement(patient);
		
		
		// **** Connection zur H2 Datenbank oeffnen ****  
		Connection con = null;
		try {
			con = db_service.connect();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// **** SQL Query ausfuehren ****
		db_service.executeQuery(con, sql_statement, false);
		
		
		// **** Connection zur H2 Datenbank schliessen ****
		try {
			db_service.disconnect(con, null);		//null als Parameter, damit der zweite Parameter bestehen bleiben kann.
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	// **********************
	// **** Read Patient ****
	// **********************
	@Override
	public Patient readPatientInDB(int id) throws RemoteException {
		System.out.println("PatientServiceImpl.readPatientInDB");
		
		Patient patient = new Patient();
		
		// **** SQL Statement erstellen ****
		sql_statement = this.patient2db.readPatientSqlStatement(id);
				
		// **** Connection zur H2 Datenbank oeffnen ****  
		Connection con = null;					
		try {
			con = db_service.connect();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// **** SQL Query ausfuehren, ResultSet erwartet ****
		resultSet = db_service.executeQuery(con, sql_statement, true);
		
		
		//**** ResultSet in Patient Objekt speichern ****
		try {
			while(resultSet.next()) {
				patient.setId(Integer.parseInt(resultSet.getString("ID")));
				patient.setFirstname(resultSet.getString("FIRSTNAME"));
				patient.setLastname(resultSet.getString("LASTNAME"));	
				patient.setGender(resultSet.getString("GENDER"));
				patient.setAddressid(Integer.parseInt(resultSet.getString("ADDRESSID")));
				patient.setLastvisit(resultSet.getString("LASTVISIT"));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// **** Connection zur H2 Datenbank schliessen ****
		try {
			db_service.disconnect(con, resultSet);		//con und resultSet schlie�en
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return patient;
	}

	// **** Connection zur H2 Datenbank schliessen ****

	
	@Override
	public void updatePatientInDB(Patient patient) throws RemoteException {
		System.out.println("Impl: leite 'update' an 2DB weiter");
		this.patient2db.updatePatientDB(patient);
	}

	@Override
	public void deletePatientInDB(int id) throws RemoteException {
		System.out.println("Impl: leite 'delete' an 2DB weiter");
		this.patient2db.deletePatientDB(id);
	}

	
	/**** Search ****/
	
	@Override
	public Patient searchPatientByIdInDB(int id) throws RemoteException {
		System.out.println("Impl: leite 'searchByIdInDB' an 2DB weiter");
		return this.searchPatientByIdInDB(id);
	}
	
	@Override
	public ArrayList<Patient> searchPatientByNameInDB(String searchString) throws RemoteException {
		System.out.println("Impl: leite 'searchByNameInDB' an 2DB weiter");
		return this.searchPatientByNameInDB(searchString);
	}

	
	/**** File ****/

	@Override
	public ArrayList<Patient> readPatientListFromCSV(String list) throws RemoteException {
		System.out.println("Impl: leite 'readList' an 2CSV weiter");
		//return this.patient2csv.readPatientListFromCSV(ArrayList(list));
		return this.patient2csv.readPatientFromCSV(list);
	}

	@Override
	public void writePatientListToCSV(ArrayList<Patient> list) throws RemoteException {
		System.out.println("Impl: leite 'writeList' an 2CSV weiter");
		this.patient2csv.writePatientToCSV(list);
	}
	

	/**** Status ****/

	@Override
	public void checkDate(String searchdate) throws RemoteException {
		// TODO wo soll die MEthode implementiert werden, eigentlich im "search"....
		System.out.println("Impl: noch keine Weiterleitung implementiert");
		
	}

}
