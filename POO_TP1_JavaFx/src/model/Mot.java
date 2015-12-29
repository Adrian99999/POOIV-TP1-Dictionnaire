package model;

import java.time.LocalDate;

public class Mot
{
	private String mot;
	private String definition;
	private String nomFichier;
	private LocalDate dateSaisieMot;
	private LocalDate dateModificationMot;
	
	public Mot(String pMot) {
		this.mot = pMot;
		this.dateSaisieMot = LocalDate.now();
		this.definition = "";
		this.nomFichier = "";
		this.dateModificationMot = null;
	}
	
	public String getMot() {
		return mot;
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
		return nomFichier;
	}
	public void setNomFichier(String nomFichier) {
		this.nomFichier = nomFichier;
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
}
