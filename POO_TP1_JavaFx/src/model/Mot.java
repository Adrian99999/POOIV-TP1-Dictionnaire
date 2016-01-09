package model;

import java.time.LocalDate;

/**
 * Classe qui gère un mot
 * @author François Lefebvre & Adrian Pinzaru
 *
 */
public class Mot
{
	private String mot;
	private String definition;
	private String nomImageAssocie;
	private LocalDate dateSaisieMot;
	private LocalDate dateModificationMot;
	
	/**
	 * Constructeur
	 * @param pMot Libellé du mot
	 */
	public Mot(String pMot) {
		this(pMot, "", "");
	}
	
	/**
	 * Constructeur principal.
	 * @param pMot Libellé du mot
	 * @param definition Définition du mot
	 * @param cheminImage Chemin de l'image associé au mot
	 */
	public Mot(String pMot, String definition, String cheminImage) {
		this.mot = pMot.toLowerCase();
		this.definition = definition;
		this.nomImageAssocie = cheminImage;
		this.dateSaisieMot = null;
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
	
	/**
	 * Définie la définition du mot. La date de modification est mise à jour 
	 * automatiquement
	 * @param definition Définition du mot
	 */
	public void setDefinition(String definition) {
		if (!definition.equals(this.definition)){
			this.definition = definition;
			this.setDateModificationMot(LocalDate.now());
		}
	}
	
	public String getNomFichier() {
		return nomImageAssocie;
	}
	
	/**
	 * Définie l'image du mot. La date de modification est mise à jour 
	 * automatiquement
	 * @param nomFichier nom du chemin d'accès à la ressource
	 */
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
	
	/**
	 * Vérifie si le mot a une image.
	 * @return
	 */
	public boolean hasImage() {
		return !this.nomImageAssocie.isEmpty();
	}
	
	/**
	 * Vérifie si un mot a le même contenu.
	 * @param motCompare Mot à comparer
	 * @return
	 */
	public boolean equals(Mot motCompare) {
		return this.mot.equals(motCompare.getMot()) &&
				this.definition.equals(motCompare.getDefinition()) &&
				this.nomImageAssocie.equals(motCompare.getNomFichier());
	}
	
	/**
	 * Copie le contenu d'un mot (définition, chemin de l'image)
	 * @param motReference Mot qui sert de référence
	 * @return Mot modifié
	 */
	public Mot updateInfoAPartirDe(Mot motReference) {
		this.definition = motReference.getDefinition();
		this.nomImageAssocie = motReference.getNomFichier();
		this.dateModificationMot = LocalDate.now();
		return this;
	}
	
	/**
	 * Retourne le nombre de mots dans la définition. Prend en compte
	 * les espaces et les apostrophes.
	 * @return Nombre de mots.
	 */
	public int getNombreMotsDansDefinition() {
		return this.getDefinition().split("[ ']").length;
	}
	
	public String toString() {
		return Mot.capitalize(this.mot);
	}
	
	/**
	 * Retourne une chaîne de caractère dont la première lettre est masjucule.
	 * @param original Expression originale
	 * @return Expression avec lettre majuscule.
	 */
	public static String capitalize(String original) {
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}
}
