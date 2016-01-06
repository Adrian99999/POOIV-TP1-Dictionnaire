package model;

import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Mot
{
	private String mot;
	private String definition;
	private String nomImageAssocie;
	private LocalDate dateSaisieMot;
	private LocalDate dateModificationMot;
	
	public Mot(String pMot) {
		this.mot = pMot.toLowerCase();
		this.dateSaisieMot = LocalDate.now();
		this.definition = "";
		this.nomImageAssocie = "";
		this.dateModificationMot = null;
	}
	
	public String getMot() {
		return mot;
	}
	
	public String toString() {
		return Mot.capitalize(this.mot);
	}
	
	public void setMot(String mot) {
		this.mot = mot;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public String getNomFichier() {
		return nomImageAssocie;
	}
	public void setImageAssocieAuMot(String nomFichier) {
		//this.nomImageAssocie = this.getClass().getClassLoader().getResource(nomFichier).toString();
		this.nomImageAssocie = nomFichier;
	}
	public LocalDate getDateSaisieMot() {
		return dateSaisieMot;
	}
	public void setDateSaisieMot(LocalDate dateSaisieMot) {
		this.dateSaisieMot = dateSaisieMot;
	}
	public LocalDate getDateModificationMot() {
		return dateModificationMot;
	}
	public void setDateModificationMot(LocalDate dateModificationMot) {
		this.dateModificationMot = dateModificationMot;
	}

	public boolean hasImage() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private static String capitalize(String original) {
		return original.substring(0, 1).toUpperCase() + original.substring(1);

	}
}
