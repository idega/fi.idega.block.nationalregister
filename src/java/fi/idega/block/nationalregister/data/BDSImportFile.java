package fi.idega.block.nationalregister.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import com.idega.block.importer.data.GenericImportFile;
import com.idega.block.importer.data.ImportFile;

import fi.idega.block.nationalregister.business.BDSEntryHolder;
import fi.idega.block.nationalregister.business.BDSImportConstants;

public class BDSImportFile extends GenericImportFile implements ImportFile {
	public BDSImportFile() {
		super();
		setRecordDilimiter("\n");
	}

	public BDSImportFile(File file) {
		this();
		setFile(file);
	}

	@Override
	public Collection getRecords() {
		return super.getRecords();
	}

	@Override
	public ArrayList getValuesFromRecordString(String recordString) {

		boolean first = true;
		Scanner scanner = new Scanner(recordString);
		scanner.useDelimiter("\\|");

		String lastId = null;
		
		ArrayList<BDSEntryHolder> ret = new ArrayList<BDSEntryHolder>();
		BDSEntryHolder holder = null;
		while (scanner.hasNext()) {
			String elem = scanner.next();
			try {
				String id = elem.substring(0, 3);
				Integer.parseInt(id);
				if (first) {
					holder = new BDSEntryHolder();
					first = false;
					String ssn = elem.substring(3, 14);
					int day = Integer.parseInt(ssn.substring(0, 2));
					int month = Integer.parseInt(ssn.substring(2, 4));
					int year = Integer.parseInt(ssn.substring(4, 6));
					
					String yearSep = ssn.substring(6, 7);
					if (yearSep.equals("+")) {
						year += 1800;
					} else if (yearSep.equals("-")) {
						year += 1900;
					} else if (yearSep.equals("A")) {
						year += 2000;
					}
					
					first = false;
					
					holder.setPersonalId(ssn);
					holder.setDay(day);
					holder.setMonth(month);
					holder.setYear(year);
				} else {
					if (BDSImportConstants.DATA_GROUP_PERSONAL_INFORMATION
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String ssn = elem.substring(4, 15);
						String valid = elem.substring(15, 16);
						
						if (valid.equals("2")) {
							holder.setOldPersonalId(ssn);
						} else if (valid.equals("1")) {
							holder.setNewPersonalId(ssn);
						}
					} else if (BDSImportConstants.DATA_GROUP_MOTHER_TONGUE
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String languageCode = elem.substring(4, 6);
						
						holder.setMotherTongue(languageCode);
					} else if (BDSImportConstants.DATA_GROUP_GENDER.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String gender = elem.substring(4, 5);

						if ("1".equals(gender)) {
							holder.setMale(true);
						} else if ("2".equals(gender)) {
							holder.setFemale(true);
						}
					} else if (BDSImportConstants.DATA_GROUP_PERSONS_NAME
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String lastName = elem.substring(4, 104);
						String firstName = elem.substring(104, 204);
						//String dateOfChange = elem.substring(204, 212);
						//String info = elem.substring(212, 213);
						
						holder.setFirstName(firstName);
						holder.setLastName(lastName);
					} else if (BDSImportConstants.DATA_GROUP_NAME_CHANGE
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String name = elem.substring(4, 104);
						String type = elem.substring(104, 106);
						//String startDate = elem.substring(106, 114);
						//String endDate = elem.substring(114, 122);
						//String info = elem.substring(122, 123);
						if ("01".equals(type)) {
							holder.setLastName(name);
						} else if ("02".equals(type)) {
							holder.setFirstName(name);
						} else if ("03".equals(type)) {
							holder.setMiddleName(name);
						} else if ("10".equals(type)) {
							holder.setLastName(name);
						} else if ("11".equals(type)) {
							holder.setFirstName(name);
						}
					} else if (BDSImportConstants.DATA_GROUP_NAME_CHANGE_METHOD
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_CITIZENSHIP
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_PLACE_OF_BIRTH_LOCAL
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_PLACE_OF_BIRTH_FOREIGN
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_MARITIAL_STATUS
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String status = elem.substring(4, 5);
						//String date = elem.substring(5, 13);

						holder.setMaritalStatus(status);
					} else if (BDSImportConstants.DATA_GROUP_CURRENT_REGISTRAR
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_REGISTRAR_HISTORY
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_DECEASED_DATE
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String date = elem.substring(4, 12);

						holder.setDataOfDeath(date);
					} else if (BDSImportConstants.DATA_GROUP_DECLARED_DECEASED_DATE
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String date = elem.substring(4, 12);

						holder.setDataOfDeath(date);
					} else if (BDSImportConstants.DATA_GROUP_PROTECTION_BAN
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_OTHER_PROHIBITIONS
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_PERMANENT_ADDRESS
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String streetName = elem.substring(4, 104);
						String streetNameSV = elem.substring(104, 204);
						String streetNumber = elem.substring(204, 211);
						String stairLetter = elem.substring(211, 212);
						String apartmentNumber = elem.substring(212, 215);
						//String seperatingLetter = elem.substring(215, 216);
						String postCode = elem.substring(216, 221);
						//String dateOfMoving = elem.substring(221, 229);
						//String dateOfMoving2 = elem.substring(229, 237);
						
						holder.setStreetName(streetName);
						holder.setStreetNameSV(streetNameSV);
						holder.setStreetNumber(streetNumber);
						holder.setStairLetter(stairLetter);
						holder.setApartmentNumber(apartmentNumber);
						holder.setPostCode(postCode);
					} else if (BDSImportConstants.DATA_GROUP_TEMPORARY_ADDRESS
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_MAILING_ADDRESS
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_PERMANENT_ADDRESS_FOREIGN
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_TEMPORARY_ADDRESS_FOREIGN
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_PERMANENT_DOMICILE_CODE
							.equals(id)) {
						//System.out.println("not handling " + id); 

					} else if (BDSImportConstants.DATA_GROUP_FORMER_PERMANENT_DOMICILE_CODE
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String homeArea = elem.substring(4, 28);
						//String date = elem.substring(28, 36);
						 
						holder.setMunicipality(homeArea.substring(0, 3));
						holder.setTown(homeArea.substring(3, 6));
					} else if (BDSImportConstants.DATA_GROUP_TEMPORARY_RESIDENT_ID
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_HOME_MUNICIPALITY
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String municipality = elem.substring(4, 7);
						//String date = elem.substring(7, 15);
						 
						holder.setMunicipality(municipality);
					} else if (BDSImportConstants.DATA_GROUP_FORMER_MUNICIPALITY
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_TEMPORARY_HOME_MUNICIPALITY
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_MUNICIPALITY_SUBDIVISION
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_END_OF_YEAR_MUNICIPALITY
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_ABSENT_POPULATION
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_COUNTRY_ARRIVAL
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_MOVED_FROM_STATE
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_MOVED_TO_STATE
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_CHILD.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String childSSN = elem.substring(4, 15);
						//String rel = elem.substring(15, 16);
						//String rel2 = elem.substring(16, 17);
						//String date = elem.substring(17, 25);
						 
						holder.addChild(childSSN);
					} else if (BDSImportConstants.DATA_GROUP_PARENT.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						String parentSSN = elem.substring(4, 15);
						String rel = elem.substring(15, 16);
						//String rel2 = elem.substring(16, 17);
						//String date = elem.substring(17, 25);
						
						if ("1".equals(rel) || "3".equals(rel)) {
							holder.setFather(parentSSN);
						} else if ("2".equals(rel) || "4".equals(rel)) {
							holder.setMother(parentSSN);
						} 
					} else if (BDSImportConstants.DATA_GROUP_ADOPTION_RELATIONSHIP
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_WARD.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_GUARDIAN
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_PROTECTION_OF_INTERESTS
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_PROTECTOR_OF_INTEREST
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_CHILD_CUSTODY
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_MARRIAGE
							.equals(id)) {
						//String changeAttribute = elem.substring(3, 4);
						//String date = elem.substring(4, 12);
						String spouse = elem.substring(12, 23);
						String valid = elem.substring(23, 24);
						
						if ("1".equals(valid)) {
							holder.setSpouse(spouse);
						}
					} else if (BDSImportConstants.DATA_GROUP_WEDDING_DAY_METHOD
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_MARRIAGE_DISOLVED
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_LEGAL_SEPERATION
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_REGISTERED_PARTNERSHIP
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_PROFESSION
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_CONGREGATION
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_CONGREGATION_HISTORY
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_CHURCH_ACTIVITY
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_ORDER_LETTER
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_RELIGION_TYPE
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_PREFERRED_LANGUAGE
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_ESERVICES_ID
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_PERSON_WITHOUT_PERSONAL_ID
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_CLEAR_TEXT_INFORMATION
							.equals(id)) {
						//System.out.println("not handling " + id); 
					} else if (BDSImportConstants.DATA_GROUP_SPECIFIED_NAME
							.equals(id)) {
						//System.out.println("not handling " + id); 
					}
				}
				
				lastId = id;
			} catch (NumberFormatException e) {
				System.out.println(recordString + " not an entry");
				return null;
			}
		}

		if (holder != null) {
			ret.add(holder);
		}
		
		return ret;
	}
}