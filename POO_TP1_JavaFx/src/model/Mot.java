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
		if (!definition.equals(this.definition)){
			this.definition = definition;
			this.setDateModificationMot(LocalDate.now());
		}
	}
	public String getNomFichier() {
		return nomImageAssocie;
	}
	public void setImageAssocieAuMot(String nomFichier) {
		if (!nomFichier.equals(this.nomImageAssocie)) {
			this.nomImageAssocie = nomFichier;
			this.setDateModificationMot(LocalDate.now());
		}
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
		return !this.nomImageAssocie.isEmpty();
	}
	
	public static String capitalize(String original) {
		return original.substring(0, 1).toUpperCase() + original.substring(1);

	}
}
