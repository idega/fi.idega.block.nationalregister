package fi.idega.block.nationalregister.business;

import java.util.ArrayList;
import java.util.List;


public class BDSEntryHolder {
	private String personalId = null;
	private int day = 0;
	private int month = 0;
	private int year = 0;
	
	private String newPersonalId = null;
	private String oldPersonalId = null;
	
	private String motherTongue = null;
	
	private boolean isMale = false;
	private boolean isFemale = false;
	
	private String firstName = null;
	private String middleName = null;
	private String lastName = null;
	
	private String maritalStatus = null;
	
	private String dataOfDeath = null;
	
	private String streetName = null;
	private String streetNameSV = null;
	private String streetNumber = null;
	private String stairLetter = null;
	private String apartmentNumber = null;
	private String postCode = null;

	private String municipality = null;
	private String town = null;
	
	private String spouse = null;
	private List<String> children;
	private String father = null;
	private String mother = null;
	
	public String getNewPersonalId() {
		return newPersonalId;
	}
	public void setNewPersonalId(String newPersonalId) {
		this.newPersonalId = newPersonalId;
	}
	public String getOldPersonalId() {
		return oldPersonalId;
	}
	public void setOldPersonalId(String oldPersonalId) {
		this.oldPersonalId = oldPersonalId;
	}
	public String getPersonalId() {
		return personalId;
	}
	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getMotherTongue() {
		return motherTongue;
	}
	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}
	public boolean isFemale() {
		return isFemale;
	}
	public void setFemale(boolean isFemale) {
		this.isFemale = isFemale;
	}
	public boolean isMale() {
		return isMale;
	}
	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getDataOfDeath() {
		return dataOfDeath;
	}
	public void setDataOfDeath(String dataOfDeath) {
		this.dataOfDeath = dataOfDeath;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getStreetNameSV() {
		return streetNameSV;
	}
	public void setStreetNameSV(String streetNameSV) {
		this.streetNameSV = streetNameSV;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getStairLetter() {
		return stairLetter;
	}
	public void setStairLetter(String stairLetter) {
		this.stairLetter = stairLetter;
	}
	public String getApartmentNumber() {
		return apartmentNumber;
	}
	public void setApartmentNumber(String apartmentNumber) {
		this.apartmentNumber = apartmentNumber;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getMunicipality() {
		return municipality;
	}
	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getSpouse() {
		return spouse;
	}
	public void setSpouse(String spouse) {
		this.spouse = spouse;
	}
	public String getFather() {
		return father;
	}
	public void setFather(String father) {
		this.father = father;
	}
	public String getMother() {
		return mother;
	}
	public void setMother(String mother) {
		this.mother = mother;
	}
	
	public List<String> getChildren() {
		return this.children;
	}
	
	public void addChild(String child) {
		if (this.children == null) {
			this.children = new ArrayList<String>();
		}
		
		children.add(child);
	}
}