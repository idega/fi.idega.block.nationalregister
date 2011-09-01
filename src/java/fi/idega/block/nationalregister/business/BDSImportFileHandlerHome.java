package fi.idega.block.nationalregister.business;


import javax.ejb.CreateException;
import java.rmi.RemoteException;
import com.idega.business.IBOHome;

public interface BDSImportFileHandlerHome extends IBOHome {
	public BDSImportFileHandler create() throws CreateException,
			RemoteException;
}