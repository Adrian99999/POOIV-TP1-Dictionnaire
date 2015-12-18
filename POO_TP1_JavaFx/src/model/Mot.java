package model;

import java.time.LocalDate;

public class Mot 
{
	private String mot;
	private String definition;
	private String nomFichier;
	private LocalDate dateSaisieMot;
	private LocalDate dateModificationMot;
	/**
	 * Constructeur de la classe
	 * @param pMot
	 * @param pDefinition
	 * @param pNomfichier
	 * @param pDateSaisieMot
	 * @param pDateModificationMot
	 */
	public Mot(String pMot, String pDefinition, String pNomfichier, LocalDate pDateSaisieMot, LocalDate pDateModificationMot){
		setMot(pMot);
		setDefinition(pDefinition);
		setNomFichier(pNomfichier);
		setDateSaisieMot(pDateSaisieMot);
		setDateModificationMot(pDateModificationMot);
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
	
	
}
