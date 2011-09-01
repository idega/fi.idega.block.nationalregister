package fi.idega.block.nationalregister.business;


import java.rmi.RemoteException;
import java.util.List;
import com.idega.block.importer.data.ImportFile;
import com.idega.block.importer.business.ImportFileHandler;
import com.idega.business.IBOService;
import com.idega.user.data.Group;

public interface BDSImportFileHandler extends IBOService, ImportFileHandler {
	/**
	 * @see fi.idega.block.nationalregister.business.FinlandImportFileHandlerBean#getFailedRecords
	 */
	public List getFailedRecords() throws RemoteException, RemoteException;

	/**
	 * @see fi.idega.block.nationalregister.business.FinlandImportFileHandlerBean#getSuccessRecords
	 */
	public List getSuccessRecords() throws RemoteException, RemoteException;

	/**
	 * @see fi.idega.block.nationalregister.business.FinlandImportFileHandlerBean#handleRecords
	 */
	public boolean handleRecords() throws RemoteException, RemoteException;

	/**
	 * @see fi.idega.block.nationalregister.business.FinlandImportFileHandlerBean#setImportFile
	 */
	public void setImportFile(ImportFile file) throws RemoteException,
			RemoteException;

	/**
	 * @see fi.idega.block.nationalregister.business.FinlandImportFileHandlerBean#setRootGroup
	 */
	public void setRootGroup(Group arg0) throws RemoteException,
			RemoteException;
}