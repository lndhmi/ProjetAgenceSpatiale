package tp1;

import java.util.Comparator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Mission implements Comparable<Mission> {
	private String nom;
	private LocalDate dateDebut;
	private LocalDate dateFin;
	private int budget;
	private EtatMission etat = EtatMission.PLANIFIEE;
	private List<Zone> zonesObj;
	private List<Zone> zonesExplorees;
	private CorpsCeleste corps;
	private DirecteurDeMission directeur;
	private Set<Operateur> operateurs = new HashSet<>();
	private List<Engin> engins = new ArrayList<>();
	private List<Echantillon> echantillons = new ArrayList<>();

	public Mission(String nom, int budget, LocalDate dateDebut, LocalDate dateFin, List<Zone> zonesObj,
			CorpsCeleste corps, DirecteurDeMission directeur) {
		if (corps == null || directeur == null) {
			throw new IllegalArgumentException("Une mission a un corps celeste et un directeur");
		}
		if (dateDebut == null || dateFin == null) {
			throw new IllegalArgumentException("Les dates sont obligatoires");
		}
		if (dateFin.isBefore(dateDebut)) {
			throw new IllegalArgumentException("La date de fin doit être après la date de début");
		}
		this.nom = nom;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.budget = budget;
		this.zonesObj = zonesObj;
		this.zonesExplorees = new ArrayList<>();
		this.corps = corps;
		this.directeur = directeur;
		directeur.ajouterMission(this);
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public int getBudget() {
		return budget;
	}
	public void SetBudget(int budget) {
		this.budget = budget;
	}
	public LocalDate getDateDebut() {
		return dateDebut;
	}
	public LocalDate getDateFin() {
		return dateFin;
	}
	public EtatMission getEtat() {
		return etat;
	}
	public List<Zone> getZonesObj() {
		return zonesObj;
	}

	public void ajouterZoneExploree(Zone z) {
		if (!zonesObj.contains(z)) {
			throw new IllegalArgumentException("Cette zone n'est pas un objectif de la mission");
		}
		if (!zonesExplorees.contains(z)) {
			zonesExplorees.add(z);
		}
	}

	public boolean estReussie() {
		if (zonesObj.isEmpty()) {
			return false;
		}
		double pourcentage = (double) zonesExplorees.size() / zonesObj.size() * 100;
		return pourcentage >= 70;
	}

	// ---------- Composition de la mission (seulement quand elle est planifiee) ----------

	public void ajouterOperateur(Operateur op) {
		verifierPlanifiee();
		operateurs.add(op);
	}

	public void ajouterEngin(Engin e) {
		verifierPlanifiee();
		if (e instanceof Drone && !corps.aAtmosphere()) {
			throw new IllegalArgumentException("Pas de drone sur un corps sans atmosphere");
		}
		engins.add(e);
	}

	private void verifierPlanifiee() {
		if (etat != EtatMission.PLANIFIEE) {
			throw new IllegalStateException("La mission n'est plus planifiee");
		}
	}
	
	public static List<Mission> missionsPlanifiees(List<Mission> missions) {
	    List<Mission> resultat = new ArrayList<>();

	    for (Mission m : missions) {
	        if (m.getEtat() == EtatMission.PLANIFIEE) {
	            resultat.add(m);
	        }
	    }

	    Collections.sort(resultat,
	        Comparator.comparing(Mission::getDateDebut));

	    return resultat;
	}

	// ---------- Demarrage ----------

	public boolean peutDemarrer() {
		if (etat != EtatMission.PLANIFIEE || engins.isEmpty() || operateurs.isEmpty()) {
			return false;
		}
		for (Operateur op : operateurs) {
			if (op.getMission() != null && op.getMission().getEtat() == EtatMission.EN_COURS) {
				return false; // deja engage ailleurs
			}
		}
		for (Engin e : engins) {
			if (e.getetat() != EtatEngin.DISPONIBLE) {
				return false;
			}
			Competence requise = (e instanceof Drone) ? Competence.pilotege_drone : Competence.pilotage_rover;
			if (!aUnOperateurAvec(requise)) {
				return false;
			}
		}
		return true;
	}

	private boolean aUnOperateurAvec(Competence c) {
		for (Operateur op : operateurs) {
			if (op.getCompetences().contains(c)) {
				return true;
			}
		}
		return false;
	}

	public void demarrer() {
		if (!peutDemarrer()) {
			throw new IllegalStateException("Conditions de demarrage non remplies");
		}
		etat = EtatMission.EN_COURS;
		for (Engin e : engins) {
			e.affecterMisiion(this);
		}
		for (Operateur op : operateurs) {
			op.affecterMission(this);
		}
	}

	// ---------- Pendant la mission ----------

	public void ajouterEchantillon(Echantillon e) {
		echantillons.add(e);
	}

	// ---------- Fin ----------

	public void terminer() {
		if (etat != EtatMission.EN_COURS) {
			throw new IllegalStateException("Seule une mission en cours peut se terminer");
		}
		etat = EtatMission.TERMINEE;
		for (Engin e : engins) {
			e.libererMission(); // ne touche pas un engin tombe en panne
		}
		for (Operateur op : operateurs) {
			op.setMission(null);
			if (estReussie()) {
				for (Cadet c : op.getCadets()) {
					c.ajouterMission(this);
				}
			}
		}
	}

	// ---------- Infos pour le rapport ----------

	public List<Zone> getZonesExplorees() {
		return Collections.unmodifiableList(zonesExplorees);
	}

	public int getNbEchantillons() {
		return echantillons.size();
	}

	public List<Echantillon> getEchantillons() {
		return Collections.unmodifiableList(echantillons);
	}

	public double getInteretTotal() {
		double total = 0;
		for (Zone z : zonesExplorees) {
			total += z.getInteretScientifique();
		}
		return total;
	}

	public CorpsCeleste getCorps() {
		return corps;
	}

	public DirecteurDeMission getDirecteur() {
		return directeur;
	}

	public Set<Operateur> getOperateurs() {
		return Collections.unmodifiableSet(operateurs);
	}

	public List<Engin> getEngins() {
		return Collections.unmodifiableList(engins);
	}

	// La mission la plus proche dans le temps passe en premier (PriorityQueue, TreeSet...)
	@Override
	public int compareTo(Mission autre) {
		return this.dateDebut.compareTo(autre.dateDebut);
	}
}
