package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import model.FiltreParDate.FILTRE_PAR_DATE_DE;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Classe qui gère le filtre des mots lors de la recherche
 * @author François Lefebvre et Adrian Pinzaru
 *
 */
public class FiltreDeRecherche implements ChangeListener<Object>, Predicate<Mot> {
	
	private boolean filtreParDateActif;
	private boolean chercherDansLeContenuEffectif;
	private boolean doitContenirUneImage;
	private Pattern regex;
	private FiltreParDate filtreParDate;
	private StringProperty expressionDeDepart = new SimpleStringProperty("");
	private BooleanProperty rechercheDansLeContenuDemande = new SimpleBooleanProperty(false);
	private BooleanProperty recherchePermiseDansContenu = new SimpleBooleanProperty();
	private BooleanProperty rechercheOuverte = new SimpleBooleanProperty();
	
	public static final int MIN_CHAR_RECH_CONTENU = 3;
	
	/**
	 * Constructeur. Accepte les joker "*" et "?" pour remplacer un groupe
	 * de caractères ou un seul caractère respectivement.
	 * Après la construction, utiliser les méthodes appropriées pour ajouter
	 * un filtre de date et le filtre par image.
	 * @param chaine Chaîne de caractères du champ de saisie
	 * @param chercherDansLeContenuDuMot Indication si on peut chercher dans
	 * le contenu du mot ou seulement à partir du début.
	 * @throws Exception 
	 */
	public FiltreDeRecherche () {
		this.filtreParDate = FiltreParDate.getDefault();
		this.doitContenirUneImage = false;
		this.filtreParDateActif = false;
		this.expressionDeDepart.set("");
		this.rechercheDansLeContenuDemande.set(false);
		this.rechercheOuverte.set(false);
		
		this.expressionDeDepart.addListener(this);
		this.rechercheDansLeContenuDemande.addListener(this);
		this.rechercheOuverte.addListener(this);
		
		recherchePermiseDansContenu.bind(
				expressionDeDepart.length().greaterThanOrEqualTo(
						MIN_CHAR_RECH_CONTENU
						)
				);
//		expressionDeDepart.addListener((obs, old, n) -> {
//			if (n.length() >= MIN_CHAR_RECH_CONTENU) {
//				recherchePermiseDansContenu.set(true);
//			} else {
//				recherchePermiseDansContenu.set(false);
//			}
//		});
	}
	
	public void updateRegex() {
		
		String expression = this.expressionDeDepart.get().toLowerCase() + 
				(this.rechercheOuverte.get() ? "*" : "");
		
		if (FiltreDeRecherche.contientDesJokers(expression)) {
			this.chercherDansLeContenuEffectif = true;
			expression = convertWildcardsToRegex(expression);
		}
		
		if (this.chercherDansLeContenuEffectif) {
			if (!this.rechercheDansLeContenuDemande.get()) {
				expression = "^" + expression + "$";
			}
			
			this.regex = Pattern.compile(expression);
		}
	}
	
	public BooleanProperty recherchePermiseDansContenuProperty() {
		return recherchePermiseDansContenu;
	}
	
	public String getExpression() {
		return this.expressionDeDepart.get();
	}
	
	/**
	 * Ajoute un filtre de date au filtre.
	 * @param filtreDate Filtre de date.
	 */
//	public void addFiltreParDate(FiltreParDate filtreDate) {
//		this.filtreParDate = filtreDate;
//	}
	
	public void setFiltreParDateActif(boolean valeur) {
		this.filtreParDateActif = valeur;
	}
	
	public boolean filtreParDateEstActif() {
		return this.filtreParDateActif;
	}
	
	
	/**
	 * Ajoute un filtre pour conserver seulement les 
	 * @param doit indication si le mot doit contenir une image.
	 */
	public void setDoitContenirImage(boolean doit) {
		this.doitContenirUneImage = doit;
	}
	
	public boolean getDoitContenirImage() {
		return this.doitContenirUneImage;
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
		return doitContenirUneImage ? mot.hasImage() : true;
	}
	
	/**
	 * Valide le libelle du mot.
	 * @param mot Mot à valider.
	 * @return True si valide.
	 */
	private boolean validerLibelle(Mot mot) {
		if (this.chercherDansLeContenuEffectif) {
			return this.regex.matcher(mot.getMot()).find();
		} else if (!this.expressionDeDepart.get().isEmpty()) {
			return mot.getMot().equals(this.expressionDeDepart);
		} else {
			return true;
		}
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
		regex = regex.replace("*", ".*");
		regex = regex.replace("?", ".");
		return regex;
	}
	
	public String toString() {
		if (this.chercherDansLeContenuEffectif) {
			return regex.toString() + " dans le contenu";
		} else {
			return this.expressionDeDepart + " expression exacte";
		}
	}
	
	public boolean estNull() {
		return (this.expressionDeDepart.get().isEmpty() ||
				this.expressionDeDepart.get().equals("*") 
				) && 
				!this.hasFiltreParDateOuParImage();
	}
	
	public static FiltreDeRecherche getDefault() {
		return new FiltreDeRecherche();
	}

	public boolean rechercheDansLeContenu() {
		return this.chercherDansLeContenuEffectif;
	}

	public boolean hasFiltreParDateOuParImage() {
		return this.doitContenirUneImage || this.filtreParDateEstActif();
	}

	public String getDefinition() {
		List<String> filtres = new ArrayList<>();
			
		if (this.filtreParDateActif) {
			filtres.add(this.filtreParDate.toString());
		}
		
		if (this.doitContenirUneImage) {
			filtres.add("avec image");
		}
		
		if (filtres.isEmpty()) {
			filtres.add("aucun filtre");
		}
		
		String filtreString = String.join(
				", ", 
				filtres
					.stream()
					.map((s) -> {return s.toLowerCase();})
					.collect(Collectors.toList())
					);
		
		// Capitalize et point
		filtreString = filtreString.substring(0, 1).toUpperCase() + filtreString.substring(1) + ".";

		return filtreString;
	}

	public FiltreParDate getFiltreParDate() {
		return this.filtreParDate;
	}

	public void setFiltreParDate(FiltreParDate filtreParDate) {
		this.filtreParDate = filtreParDate;
	}

	public boolean demandeRechercheDansContenu() {
		return this.rechercheDansLeContenuDemande.get();
	}
	
	public static boolean contientDesJokers(String text) {
		return text.contains("*") || text.contains("?");
	}
	
	public boolean rechercheLeMotExacte() {
		return !this.rechercheDansLeContenu() && !this.getExpression().isEmpty();
	}

	public boolean validePourLaRecherche() {
		return this.recherchePermiseDansContenu.get();
	}

//	public void setExpression(String expression) {
//		this.expressionDeDepart.set(expression);
//	}
//
//	public void setRechercheDansLeContenu(Boolean b) {
//		this.rechercheDansLeContenuDemande.set(b);
//	}
//	
	public void setRechercheOuverte(boolean b) {
		this.rechercheOuverte.set(b);
	}
	
	public StringProperty expressionProperty() {
		return this.expressionDeDepart;
	}

	@Override
	public void changed(ObservableValue observable, Object oldValue,
			Object newValue) {
		updateRegex();
	}
	
	public BooleanProperty rechercheDansContenuDemandeProperty() {
		return this.rechercheDansLeContenuDemande;
	}

	public String getRegex() {
		return regex.pattern();
	}
}
