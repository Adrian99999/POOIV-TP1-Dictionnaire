package model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Classe qui gère le filtre des mots lors de la recherche.
 * L'expressioni de recherche accepte les joker "*" et "?" pour remplacer un groupe
 * de caractères ou un seul caractère respectivement.
 * Après la construction, utiliser les méthodes appropriées pour ajouter
 * un filtre de date et le filtre par image.
 * Plusieurs attributs sont gérés automatiquement par binding.
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
	
	/**
	 * Nombre minimal de caractère dans l'expression de recherche pour 
	 * la recherche dans le contenu
	 */
	public static final int MIN_CHAR_RECH_CONTENU = 3;
	
	/**
	 * Constructeur. 
	 */
	public FiltreDeRecherche () {
		this.filtreParDate = FiltreParDate.getDefault();
		this.doitContenirUneImage = false;
		this.filtreParDateActif = false;
		this.expressionDeDepart.set("");
		this.rechercheDansLeContenuDemande.set(false);
		this.rechercheOuverte.set(false);
		
		// Permet la mise à jour de l'expression rationnelle (regex)
		this.expressionDeDepart.addListener(this);
		this.rechercheDansLeContenuDemande.addListener(this);
		this.rechercheOuverte.addListener(this);
		
		// Recherche dans le contenu seulement si l'expression
		// est de taille supérieure à 3.
		recherchePermiseDansContenu.bind(
				expressionDeDepart.length().greaterThanOrEqualTo(
						MIN_CHAR_RECH_CONTENU
						)
				);
	}
	
	/**
	 * Retourne le BooleanProperty lié à la possibilité de faire une recherche
	 * dans le contenu.
	 * @return
	 */
	public BooleanProperty recherchePermiseDansContenuProperty() {
		return recherchePermiseDansContenu;
	}
	
	/**
	 * Retourne l'expression textuelle du filtre.
	 * @return
	 */
	public String getExpression() {
		return this.expressionDeDepart.get();
	}
	
	/**
	 * Définie si le filtre par date est considéré dans la recherche.
	 * @param valeur Valeur d'activation
	 */
	public void setFiltreParDateActif(boolean valeur) {
		this.filtreParDateActif = valeur;
	}
	
	/**
	 * Retourne l'état d'activité du filtre par date.
	 * @return
	 */
	public boolean filtreParDateEstActif() {
		return this.filtreParDateActif;
	}
	
	/**
	 * Ajoute un filtre pour conserver seulement les mot qui contiennent 
	 * une image.
	 * @param doit Indication si le mot doit contenir une image (true/false)
	 */
	public void setDoitContenirImage(boolean doit) {
		this.doitContenirUneImage = doit;
	}
	
	/**
	 * Retourne l'information si les résultats de recherche doivent contenir
	 * une image.
	 * @return
	 */
	public boolean getDoitContenirImage() {
		return this.doitContenirUneImage;
	}
	
	/**
	 * Retourne le BooleanProperty en lien avec la demande de l'utilisateur
	 * quant à la recherche dans le contenu.
	 * @return
	 */
	public BooleanProperty rechercheDansContenuDemandeProperty() {
		return this.rechercheDansLeContenuDemande;
	}
	
	/**
	 * Retourne l'expression régulière générée sous forme de chaîne de caractères
	 * @return
	 */
	public String getRegex() {
		return regex.pattern();
	}
	
	/**
	 * Définie la valeur pour une recherche ouverte (recherche par préfixe)
	 * @param b
	 */
	public void setRechercheOuverte(boolean b) {
		this.rechercheOuverte.set(b);
	}
	
	/**
	 * Retourne la propriété de l'expression.
	 * @return
	 */
	public StringProperty expressionProperty() {
		return this.expressionDeDepart;
	}
	
	/**
	 * Retourne si la recherche se fait dans le contenu au final.
	 * Peut ne pas être la même que ce qui est demandé par l'utilisateur.
	 * @return
	 */
	public boolean rechercheDansLeContenuEffectif() {
		return this.chercherDansLeContenuEffectif;
	}
	
	/**
	 * Retourne le filtre par date
	 * @return
	 */
	public FiltreParDate getFiltreParDate() {
		return this.filtreParDate;
	}
	
	/**
	 * Définie le filtre par date.
	 * @param filtreParDate
	 */
	public void setFiltreParDate(FiltreParDate filtreParDate) {
		this.filtreParDate = filtreParDate;
	}
	
	/**
	 * Retourne la valeur de recherche dans le contenu tel que demandé par
	 * l'utilisateur.
	 * @return
	 */
	public boolean demandeRechercheDansContenu() {
		return this.rechercheDansLeContenuDemande.get();
	}
	
	/**
	 * Met a jour l'expression rationnelle selon les données du filtre.
	 */
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
	
	/**
	 * Transforme une expression contenant des jokers en expression rationnelle.
	 * @param wcString Expression contenant les jokers.
	 * @return Expression rationnelle.
	 */
	private String convertWildcardsToRegex(String wcString) {
		String regex = wcString;
		regex = regex.replace("*", ".*");
		regex = regex.replace("?", ".");
		return regex;
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
	 * Vérifie si le filtre est inutile dans les faits.
	 * @return
	 */
	public boolean estNull() {
		return (this.expressionDeDepart.get().isEmpty() ||
				this.expressionDeDepart.get().equals("*") 
				) && 
				!this.hasFiltreParDateOuParImage();
	}
	
	/**
	 * Retourne si le filtre a un filtre par date ou par image ou seulement
	 * selon une expression.
	 * @return
	 */
	public boolean hasFiltreParDateOuParImage() {
		return this.doitContenirUneImage || this.filtreParDateEstActif();
	}
	
	/**
	 * Vérifie si une chaîne de caractères possède des jokers pris en compte
	 * par le filtre.
	 * @param text Expression à évaluer
	 * @return
	 */
	public static boolean contientDesJokers(String text) {
		return text.contains("*") || text.contains("?");
	}
	
	/**
	 * Vérifie si le filtre fait sa recherche par mot exacte ou pas.
	 * @return
	 */
	public boolean rechercheLeMotExacte() {
		return !this.rechercheDansLeContenuEffectif() && !this.getExpression().isEmpty();
	}

	/**
	 * Retourne la valeur si la recherche dans le contenu est possible.
	 * @return
	 */
	public boolean validePourLaRechercheDansLeContenu() {
		return this.recherchePermiseDansContenu.get();
	}
	
	public String toString() {
		if (this.chercherDansLeContenuEffectif) {
			return regex.toString() + " dans le contenu";
		} else {
			return this.expressionDeDepart + " expression exacte";
		}
	}
	
	/**
	 * Retourne la définition du filtre.
	 * @return
	 */
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
	
	/**
	 * Méthode de l'interface ChangeListener
	 */
	@Override
	public void changed(ObservableValue observable, Object oldValue,
			Object newValue) {
		updateRegex();
	}

	/**
	 * Retourne le filtre de recherche par défaut.
	 * @return
	 */
	public static FiltreDeRecherche getDefault() {
		return new FiltreDeRecherche();
	}
}
