package model;

import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Classe qui gère le filtre des mots lors de la recherche
 * @author François Lefebvre et Adrian Pinzaru
 *
 */
public class FiltreDeRecherche implements Predicate<Mot>{
	
	/**
	 * Choix pour le type de filtre de date
	 *
	 */
	public enum FILTRE_PAR_DATE_DE {
		SAISIE, MODIFICATION
	}
	
	/**
	 * Choix de la période à vérifier par rapport à la date de référence.
	 *
	 */
	public enum MOMENT_PAR_RAPPORT_A_DATE {
		AVANT, APRES
	}
	
	private Pattern regex;
	private boolean doitContenirUneImage;
	private FiltreParDate filtreParDate;
	
	/**
	 * Constructeur. Accepte les joker "*" et "?" pour remplacer un groupe
	 * de caractères ou un seul caractère respectivement.
	 * Après la construction, utiliser les méthodes appropriées pour ajouter
	 * un filtre de date et le filtre par image.
	 * @param chaine Chaîne de caractères du champ de saisie
	 * @param chercherDansLeContenuDuMot Indication si on peut chercher dans
	 * le contenu du mot ou seulement à partir du début.
	 */
	public FiltreDeRecherche (String chaine, boolean chercherDansLeContenuDuMot) {
		String regexString = convertWildcardsToRegex(chaine);
		if (!chercherDansLeContenuDuMot) {
			regexString = "^" + regexString;
		}
		this.regex = Pattern.compile(regexString);
		this.filtreParDate = null;
		this.doitContenirUneImage = false;
	}

	
	/**
	 * Ajoute un filtre de date au filtre.
	 * @param filtreDate Filtre de date.
	 */
	public void addFiltreParDate(FiltreParDate filtreDate) {
		this.filtreParDate = filtreDate;
	}
	
	/**
	 * Ajoute un filtre pour conserver seulement les 
	 * @param doit indication si le mot doit contenir une image.
	 */
	public void setDoitContenirImage(boolean doit) {
		this.doitContenirUneImage = doit;
	}
	
	/**
	 * Vérifie le mot.
	 */
	@Override
	public boolean test(Mot mot) {
		
		if (!validerImage(mot)) {
			return false;
		}
		
		if (!validerLibelle(mot)) {
			return false;
		}
		
		return validerDate(mot);
	}
	
	/**
	 * 
	 * Valide en fonction de la présence de l'image.
	 * @param mot Mot à vérifier.
	 * @return True si valide.
	 */
	private boolean validerImage(Mot mot) {
		return doitContenirUneImage && mot.hasImage();
	}
	
	/**
	 * Valide le libelle du mot.
	 * @param mot Mot à valider.
	 * @return True si valide.
	 */
	private boolean validerLibelle(Mot mot) {
		return this.regex.matcher(mot.getMot()).matches();
	}
	
	/**
	 * Valide en fonction du filtre de date
	 * @param mot Mot à vérifier.
	 * @return True si sans filtre de date ou si date valide.
	 */
	private boolean validerDate(Mot mot) {
		return filtreParDate == null || filtreParDate.test(mot);
	}
	
	/**
	 * Transforme une expression contenant des jokers en expression régulière.
	 * @param wcString Expression contenant les jokers.
	 * @return Expression régulière.
	 */
	private String convertWildcardsToRegex(String wcString) {
		String regex = wcString;
		regex.replace("*", ".*");
		regex.replace("?", ".");
		regex += "$";
		return regex;
	}
	
	/**
	 * Classe interne qui gère le filtre par date.
	 * @author François Lefebvre et Adrian Pinzaru
	 *
	 */
	public class FiltreParDate implements Predicate<Mot>{
		private MOMENT_PAR_RAPPORT_A_DATE moment;
		private FILTRE_PAR_DATE_DE typeDeFiltre;
		private LocalDate date;
		
		/**
		 * Constructeur.
		 * @param momentDate Avant ou après la date de référence.
		 * @param typeDeFiltre Sur la date de saisie ou sur la date de modification.
		 * @param date Date de référence
		 */
		public FiltreParDate(MOMENT_PAR_RAPPORT_A_DATE momentDate, FILTRE_PAR_DATE_DE typeDeFiltre, LocalDate date) {
			this.moment = momentDate;
			this.typeDeFiltre = typeDeFiltre;
			this.date = date;
		}
		
		/*
		 * Vérifie la validité du mot selon la date.
		 */
		@Override
		public boolean test(Mot mot) {
			LocalDate dateMot = getDateCible(mot);
			
			switch (this.moment) {
			case AVANT:
				return dateMot.isBefore(this.date);
			case APRES:
				return dateMot.isAfter(this.date) || dateMot.isEqual(this.date);
			}
			return false;
		}
		
		/**
		 * Récupère la date ciblée par le filtre.
		 * @param mot Mot à vérifier.
		 * @return Date ciblée.
		 */
		private LocalDate getDateCible(Mot mot) {
			
			LocalDate date = null;
			
			switch (this.typeDeFiltre)
			{
			case SAISIE:
				date = mot.getDateSaisieMot();
				break;
			
			case MODIFICATION:
				date = mot.getDateModificationMot();
				break;
			}
			
			return date;
		}
	}
}
