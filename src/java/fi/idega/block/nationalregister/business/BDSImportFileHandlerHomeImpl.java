package fi.idega.block.nationalregister.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class BDSImportFileHandlerHomeImpl extends IBOHomeImpl implements
		BDSImportFileHandlerHome {
	@Override
	public Class<BDSImportFileHandler> getBeanInterfaceClass() {
		return BDSImportFileHandler.class;
	}

	public BDSImportFileHandler create() throws CreateException {
		return (BDSImportFileHandler) super.createIBO();
	}
}