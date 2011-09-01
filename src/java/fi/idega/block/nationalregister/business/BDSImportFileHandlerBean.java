package fi.idega.block.nationalregister.business;

import is.idega.block.family.business.FamilyLogic;
import is.idega.block.family.data.FamilyMember;
import is.idega.block.family.data.FamilyMemberHome;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import se.idega.idegaweb.commune.business.CommuneUserBusiness;

import com.idega.block.importer.business.ImportFileHandler;
import com.idega.block.importer.data.ImportFile;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.business.CommuneBusiness;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.presentation.IWContext;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.Timer;

public class BDSImportFileHandlerBean extends IBOServiceBean implements
		BDSImportFileHandler, ImportFileHandler {

	private static final long serialVersionUID = 8495696370168495712L;

	private ImportFile file;
	private List failed = null;
	private List success = null;
	private User performer = null;

	private FamilyLogic famLog = null;

	private static Gender MALE = null;

	private static Gender FEMALE = null;

	public List getFailedRecords() throws RemoteException {
		return this.failed;
	}

	public List getSuccessRecords() throws RemoteException {
		return this.success;
	}

	public boolean handleRecords() throws RemoteException {
		failed = new ArrayList();
		try {
			performer = IWContext.getInstance().getCurrentUser();
		} catch (Exception ex) {
			performer = null;
		}
		Timer clock = new Timer();
		clock.start();
		try {
			log("BDSImportFileHandler [STARTING] time: "
					+ IWTimestamp.getTimestampRightNow().toString());

			// iterate through the records and process them
			String item;
			int count = 0;
			while (!(item = (String) this.file.getNextRecord()).equals("")) {
				count++;
				if (!processRecord(item)) {
					failed.add(item);
				}
				if ((count % 250) == 0) {
					log("BDSImportFileHandler processing RECORD [" + count
							+ "] time: "
							+ IWTimestamp.getTimestampRightNow().toString());
				}
				item = null;
			}
			clock.stop();
			log("Time to handleRecords: " + clock.getTime() + " ms  OR "
					+ ((int) (clock.getTime() / 1000)) + " s.");

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private boolean processRecord(String record) {
		ArrayList<BDSEntryHolder> arrayList = file
				.getValuesFromRecordString(record);
		if (arrayList == null) {
			return true;
		}
		for (BDSEntryHolder bdsEntryHolder : arrayList) {
			processEntry(bdsEntryHolder);
		}

		return true;
	}

	private boolean handleDeceased(User user) {
		try {
			getCommuneUserBusiness().setUserAsDeceased(
					(Integer) user.getPrimaryKey(),
					IWTimestamp.RightNow().getDate());

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return true;
	}

	private User handlePinChanged(BDSEntryHolder entry) throws RemoteException,
			FinderException {
		User emptyUser = getEmptyNewUser(entry.getNewPersonalId());
		boolean returnEmptyUser = false;

		if (emptyUser != null) {
			IWTimestamp creationDate = new IWTimestamp(emptyUser.getCreated());
			creationDate.setAsDate();
			IWTimestamp now = IWTimestamp.RightNow();
			now.setAsDate();
			if (now.equals(creationDate)) {
				try {
					getCommuneUserBusiness().getUser(entry.getOldPersonalId());
					deleteEmptyUser(emptyUser);
				} catch (FinderException e) {
					returnEmptyUser = true;
				}
			} else {
				returnEmptyUser = true;
				try {
					User newUser = getCommuneUserBusiness().getUser(
							entry.getOldPersonalId());
					deleteEmptyUser(newUser);
				} catch (FinderException e) {
				}
			}
		}

		User user = null;
		if (returnEmptyUser) {
			user = emptyUser;
		} else {
			user = getCommuneUserBusiness().getUser(entry.getOldPersonalId());

		}
		user.setPersonalID(entry.getNewPersonalId());
		user.store();

		return user;
	}

	private User getEmptyNewUser(String pin) {
		User user = null;
		try {
			user = getCommuneUserBusiness().getUser(pin);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (FinderException e) {
		}

		return user;
	}

	private void deleteEmptyUser(User user) {
		try {
			getFamilyLogic().removeAllFamilyRelationsForUser(user);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		user.setPersonalID(user.getPersonalID().substring(0, 8) + "REPL");
		user.setDeleted(true);
		if (this.performer != null) {
			user.setDeletedBy(((Integer) performer.getPrimaryKey()).intValue());
		}
		user.setDeletedWhen(IWTimestamp.getTimestampRightNow());
		user.store();
	}

	private FamilyLogic getFamilyLogic() {
		try {
			return (FamilyLogic) getServiceInstance(FamilyLogic.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private CommuneUserBusiness getCommuneUserBusiness() {
		try {
			return (CommuneUserBusiness) getServiceInstance(CommuneUserBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private Gender getGender(BDSEntryHolder entry) {
		try {
			GenderHome home = (GenderHome) this.getIDOHome(Gender.class);
			if (entry.isFemale()) {
				if (FEMALE == null) {
					FEMALE = home.getFemaleGender();
				}
				return FEMALE;
			} else {
				if (MALE == null) {
					MALE = home.getMaleGender();
				}
				return MALE;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null; // if something happened
		}
	}

	private boolean processEntry(BDSEntryHolder entry) {
		boolean movingFromCommune = false;
		boolean movingFromCountry = false;
		boolean isDisabled = false;
		boolean deceased = false;
		boolean pinChanged = false;
		boolean newPerson = false;

		User user = null;

		if (entry.getDataOfDeath() != null) {
			deceased = true;
		}

		if (entry.getNewPersonalId() != null) {
			pinChanged = true;
		}

		try {
			if (pinChanged) {
				user = handlePinChanged(entry);
			} else {
				user = getCommuneUserBusiness().getUser(entry.getPersonalId());
			}
		} catch (RemoteException e) {
			e.printStackTrace();

			return false;
		} catch (FinderException e) {
			newPerson = true;
		}

		if (deceased) {
			if (newPerson) {
				return true;
			}

			return handleDeceased(user);
		}

		/*
		 * if (isDisabled) { if (newPerson) { return true; }
		 * 
		 * handleCitizenGroup(user, movingFromCommune, entry);
		 * 
		 * return handleDisabled(user); }
		 */

		Gender gender = getGender(entry);
		IWTimestamp dateOfBirth = new IWTimestamp(entry.getDay(),
				entry.getMonth(), entry.getYear());
		try {
			user = getCommuneUserBusiness().createOrUpdateCitizenByPersonalID(
					entry.getFirstName(), entry.getMiddleName(),
					entry.getLastName(), entry.getPersonalId(), gender,
					dateOfBirth);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		/*
		 * if (!movingFromCommune) { movingFromCommune = !isHomeCommune(entry);
		 * }
		 */

		// handleCitizenGroup(user, movingFromCommune, entry);

		if (entry.getStreetName() != null) {
			if (!handleAddress(user, entry)) {
				return false;
			}
		}

		if (!handleRelations(user, entry, deceased)) {
			return false;
		}

		/*
		 * if (!handleExtraInfo(user, entry)) { return false; }
		 */

		return true;
	}

	private boolean handleRelations(User user, BDSEntryHolder entry,
			boolean isDeceased) {
		try {
			getFamilyLogic().removeAllFamilyRelationsForUser(user);
			if (entry.getSpouse() != null) {
				User relative = null;

				try {
					relative = getCommuneUserBusiness().getUser(
							entry.getSpouse());
					getFamilyLogic().setAsSpouseFor(user, relative);
				} catch (Exception e) {
				}
			}

			if (entry.getFather() != null) {
				User relative = null;

				try {
					relative = getCommuneUserBusiness().getUser(
							entry.getFather());
					getFamilyLogic().setAsChildFor(user, relative);
				} catch (Exception e) {
				}
			}

			if (entry.getMother() != null) {
				User relative = null;

				try {
					relative = getCommuneUserBusiness().getUser(
							entry.getMother());
					getFamilyLogic().setAsChildFor(user, relative);
				} catch (Exception e) {
				}
			}

			if (entry.getChildren() != null && !entry.getChildren().isEmpty()) {
				Iterator it = entry.getChildren().iterator();
				while (it.hasNext()) {
					String pin = (String) it.next();
					User relative = null;

					if (pin != null && !"".equals(pin.trim())) {
						try {
							relative = getCommuneUserBusiness().getUser(pin);
						} catch (Exception e) {
						}

						if (!isDeceased) {
								if (relative != null) {
									getFamilyLogic().setAsParentFor(user,
											relative);
									getFamilyLogic().setAsCustodianFor(relative,
											user);
							} 
						} else {
							if (relative != null) {
								handleDeceased(relative);
							}
						}
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean handleAddress(User user, BDSEntryHolder entry) {
		String communeCode = entry.getMunicipality();
		Commune commune = null;
		try {
			commune = getCommuneBusiness().getCommuneByCode(communeCode);
		} catch (RemoteException e1) {
			logDebug("Commune with code:" + communeCode
					+ " (countyNumber+communeNumber) not found in database");
		}

		// country id 187 name Sweden isoabr: SE
		Country finland = null;
		try {
			finland = ((CountryHome) getIDOHome(Country.class))
					.findByIsoAbbreviation("FI");
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (FinderException e1) {
			e1.printStackTrace();
		}

		try {
			String streetName = entry.getStreetName();
			String streetNumber = entry.getStreetNumber();
			Address address = getCommuneUserBusiness()
					.getUsersMainAddress(user);

			if (address != null) {
				Collection addresses = user.getAddresses();
				log("count = " + (addresses == null ? 0 : addresses.size()));
				if (!ListUtil.isEmpty(addresses)) {
					try {
						user.removeAllAddresses();
					} catch (IDORemoveRelationshipException e1) {
						e1.printStackTrace();
					}
					address = null;
				}

				/*
				 * if count == 0 then something strange is happening. Should
				 * never happen...
				 */
			}

			PostalCode code = null;
			if (entry.getPostCode() != null) {
				code = getAddressBusiness()
						.getPostalCodeAndCreateIfDoesNotExist(
								entry.getPostCode(), "", finland);
			}
			boolean addAddress = false;
			if (address == null) {
				AddressHome addressHome = getAddressBusiness().getAddressHome();
				address = addressHome.create();
				AddressType mainAddressType = addressHome.getAddressType1();
				address.setAddressType(mainAddressType);
				addAddress = true;
			}
			address.setCountry(finland);
			if (code != null) {
				address.setPostalCode(code);
			}
			// address.setProvince(entry.getCountyCode());
			if (commune != null) {
				address.setCity(commune.getCommuneName());
				address.setCommune(commune);
			}
			address.setStreetName(streetName);
			address.setStreetNumber(streetNumber);
			address.store();
			if (addAddress) {
				user.addAddress(address);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void setImportFile(ImportFile file) throws RemoteException {
		this.file = file;
	}

	public void setRootGroup(Group arg0) throws RemoteException {
	}

	public FamilyLogic getMemberFamilyLogic() throws RemoteException {
		if (this.famLog == null) {
			this.famLog = (FamilyLogic) IBOLookup.getServiceInstance(
					getIWApplicationContext(), FamilyLogic.class);
		}
		return this.famLog;
	}

	protected FamilyMemberHome getFamilyMemberHome() {
		try {
			return (FamilyMemberHome) this.getIDOHome(FamilyMember.class);
		} catch (RemoteException e) {
			throw new EJBException(e.getMessage());
		}
	}

	private CommuneBusiness getCommuneBusiness() {
		try {
			return (CommuneBusiness) getServiceInstance(CommuneBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private AddressBusiness getAddressBusiness() {
		try {
			return (AddressBusiness) getServiceInstance(AddressBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

}