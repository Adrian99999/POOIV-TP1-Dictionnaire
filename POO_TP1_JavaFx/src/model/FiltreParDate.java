package model;

import java.time.LocalDate;
import java.util.function.Predicate;

/**
 * Classe interne qui gère le filtre par date.
 * @author François Lefebvre et Adrian Pinzaru
 *
 */
public class FiltreParDate implements Predicate<Mot>{
	
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
	
	public String toString() {
		String s = "";
		
		switch (this.typeDeFiltre) {
		case MODIFICATION:
			s += "Modifié";
			break;
		case SAISIE:
			s+= "Saisi";
			break;
		}
		
		switch (this.moment) {
		case AVANT:
			s += " avant le ";
			break;
		case APRES:
			s += " après le ";
			break;
		}
		
		s += this.date;
		
		return s;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public FILTRE_PAR_DATE_DE getTypeDate() {
		return this.typeDeFiltre;
	}

	public MOMENT_PAR_RAPPORT_A_DATE getMoment() {
		return this.moment;
	}
}