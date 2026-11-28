package tp1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * L'agence AstroUPHF : elle garde tout le monde et pilote les missions.
 */
public class Agence {

	public static final int MAX_MISSIONS_EN_COURS = 3;

	// Donnees de l'agence
	private final List<Personne> personnes = new ArrayList<>();
	private final List<Engin> engins = new ArrayList<>();
	private final List<CorpsCeleste> corpsCelestes = new ArrayList<>();

	// Missions selon leur etat
	private final PriorityQueue<Mission> planifiees = new PriorityQueue<>(); // la plus proche d'abord (compareTo)
	private final List<Mission> enCours = new ArrayList<>();
	private final List<Mission> terminees = new ArrayList<>();
	private final List<RapportMission> rapports = new ArrayList<>();

	// Simulation
	private final ExecutorService pool = Executors.newFixedThreadPool(MAX_MISSIONS_EN_COURS);
	private final Random random;
	private final long tempsParZoneMs;
	private final double probaExploration;
	private final double probaPanne;
	private final GestionRapport gestionRapport; // peut etre null : pas de sauvegarde en base

	public Agence(long graine, long tempsParZoneMs, double probaExploration, double probaPanne,
			GestionRapport gestionRapport) {
		this.random = new Random(graine);
		this.gestionRapport = gestionRapport;
		this.tempsParZoneMs = tempsParZoneMs;
		this.probaExploration = probaExploration;
		this.probaPanne = probaPanne;
	}

	/** Valeurs du sujet : 200 ms par zone, 80 % d'exploration, 10 % de panne, sauvegarde en base. */
	public Agence(long graine, GestionRapport gestionRapport) {
		this(graine, 200, 0.8, 0.1, gestionRapport);
	}

	/** Sans base de donnees (pratique pour les tests). */
	public Agence(long graine) {
		this(graine, 200, 0.8, 0.1, null);
	}

	// ---------- Ajouts ----------

	public synchronized void ajouterPersonne(Personne p) {
		personnes.add(p);
	}

	public synchronized void ajouterEngin(Engin e) {
		engins.add(e);
	}

	public synchronized void ajouterCorpsCeleste(CorpsCeleste c) {
		corpsCelestes.add(c);
	}

	public synchronized void planifierMission(Mission m) {
		if (m.getEtat() != EtatMission.PLANIFIEE) {
			throw new IllegalArgumentException("Seule une mission planifiee peut etre ajoutee");
		}
		planifiees.add(m);
	}

	// ---------- Lancement ----------

	/**
	 * Lance la mission planifiee la plus proche dans le temps.
	 * synchronized : si deux threads appellent cette methode en meme temps,
	 * ils passent l'un apres l'autre, donc on ne depasse jamais 3 missions en cours.
	 */
	public synchronized Future<RapportMission> lancerProchaineMission() {
		if (enCours.size() >= MAX_MISSIONS_EN_COURS) {
			throw new IllegalStateException("Deja " + MAX_MISSIONS_EN_COURS + " missions en cours");
		}
		Mission m = planifiees.peek();
		if (m == null) {
			throw new IllegalStateException("Aucune mission planifiee");
		}
		if (!m.peutDemarrer()) {
			throw new IllegalStateException("La mission " + m.getNom() + " ne peut pas demarrer");
		}
		planifiees.poll();
		m.demarrer();
		enCours.add(m);
		return pool.submit(() -> simuler(m)); // la simulation tourne dans un autre thread
	}

	// ---------- Simulation (executee dans un thread du pool) ----------

	private RapportMission simuler(Mission m) throws InterruptedException {
		List<Engin> enginsMission = m.getEngins();

		// 1. Duree : (nb zones x temps par zone) / nb engins
		long duree = m.getZonesObj().size() * tempsParZoneMs / enginsMission.size();
		Thread.sleep(duree);

		// 2. Zones : chaque zone est exploree avec une probabilite, par un engin tire au sort
		for (Zone zone : m.getZonesObj()) {
			if (tirage() < probaExploration) {
				Engin engin = enginsMission.get(tirageEntier(enginsMission.size()));
				boolean libre = zone.peutEtreExploree(engin.getType());
				Echantillon e = engin.explorer(zone, random);
				if (libre) {
					m.ajouterZoneExploree(zone);
				}
				if (e != null) {
					m.ajouterEchantillon(e);
					zone.ajouterEchantillon(e);
				}
			}
		}

		// 3. Pannes
		for (Engin engin : enginsMission) {
			if (tirage() < probaPanne) {
				engin.TomberPanne();
			}
		}

		// 4. Fin automatique
		RapportMission rapport;
		synchronized (this) {
			m.terminer();
			enCours.remove(m);
			terminees.add(m);
			promouvoirCadets(m);
			rapport = new RapportMission(m);
			rapports.add(rapport);
		}
		if (gestionRapport != null) {
			gestionRapport.sauvegarder(rapport); // persistance automatique en fin de mission
		}
		return rapport;
	}

	// Random est partage entre les threads : on le protege pour garder des tirages coherents
	private synchronized double tirage() {
		return random.nextDouble();
	}

	private synchronized int tirageEntier(int borne) {
		return random.nextInt(borne);
	}

	/** Un cadet qui a 3 missions reussies devient operateur (appelee sous verrou). */
	private void promouvoirCadets(Mission m) {
		for (Operateur op : m.getOperateurs()) {
			for (Cadet c : new ArrayList<>(op.getCadets())) { // copie : la liste change pendant la boucle
				Operateur promu = c.devenirOperateur();
				if (promu != null) {
					personnes.remove(c);
					personnes.add(promu);
				}
			}
		}
	}

	// ---------- Consultation ----------

	/** Attend le rapport, mais pas plus de delaiMs millisecondes. Renvoie null si trop long. */
	public RapportMission attendreRapport(Future<RapportMission> f, long delaiMs) {
		try {
			return f.get(delaiMs, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			return null; // pas encore fini
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		} catch (ExecutionException e) {
			throw new IllegalStateException("La simulation a echoue", e.getCause());
		}
	}

	public synchronized List<Mission> getMissionsPlanifiees() {
		List<Mission> l = new ArrayList<>(planifiees);
		Collections.sort(l); // une PriorityQueue n'est pas triee quand on la parcourt
		return l;
	}

	public synchronized List<Mission> getMissionsEnCours() {
		return new ArrayList<>(enCours);
	}

	public synchronized List<Mission> getMissionsTerminees() {
		return new ArrayList<>(terminees);
	}

	public synchronized List<RapportMission> getRapports() {
		return new ArrayList<>(rapports);
	}

	public synchronized List<Personne> getPersonnes() {
		return new ArrayList<>(personnes);
	}

	public synchronized List<Engin> getEngins() {
		return new ArrayList<>(engins);
	}

	public synchronized List<CorpsCeleste> getCorpsCelestes() {
		return new ArrayList<>(corpsCelestes);
	}

	// ---------- Listes demandees par le sujet ----------

	/** Missions : de la plus chere a la moins chere. */
	private static final Comparator<Mission> PAR_BUDGET_DECROISSANT =
			Comparator.comparingInt(Mission::getBudget).reversed();

	/** Zones : interet decroissant, puis coordonnees (ordre naturel de Zone, son compareTo). */
	private static final Comparator<Zone> PAR_INTERET_PUIS_COORDONNEES =
			Comparator.comparingDouble(Zone::getInteretScientifique).reversed()
					.thenComparing(Comparator.naturalOrder());

	/** Missions terminees, de la plus chere a la moins chere. */
	public synchronized List<Mission> getMissionsTermineesParBudget() {
		List<Mission> l = new ArrayList<>(terminees);
		l.sort(PAR_BUDGET_DECROISSANT);
		return l;
	}

	/** Engins regroupes par etat (EnumMap : une case par valeur de l'enum). */
	public synchronized Map<EtatEngin, List<Engin>> enginsParEtat() {
		return Engin.enginsParEtat(engins);
	}

	public synchronized List<Operateur> operateursDisponibles() {
		return Operateur.operateursDisponibles(operateurs());
	}

	public synchronized List<Operateur> operateursOccupes() {
		return Operateur.operateursOccupes(operateurs());
	}

	private List<Operateur> operateurs() {
		List<Operateur> l = new ArrayList<>();
		for (Personne p : personnes) {
			if (p instanceof Operateur) {
				l.add((Operateur) p);
			}
		}
		return l;
	}

	/** Zones explorees d'un corps celeste, par interet decroissant puis par coordonnees. */
	public List<Zone> zonesExploreesTriees(CorpsCeleste corps) {
		List<Zone> l = new ArrayList<>();
		for (Zone z : corps.getZones()) {
			if (z.estExploree()) {
				l.add(z);
			}
		}
		l.sort(PAR_INTERET_PUIS_COORDONNEES);
		return l;
	}

	/** Echantillons d'une mission regroupes par type (ROCHE, GLACE, POUSSIERE). */
	public Map<TypeEchantillon, List<Echantillon>> echantillonsParType(Mission m) {
		Map<TypeEchantillon, List<Echantillon>> resultat = new EnumMap<>(TypeEchantillon.class);
		for (Echantillon e : m.getEchantillons()) {
			resultat.computeIfAbsent(e.getType(), t -> new ArrayList<>()).add(e);
		}
		return resultat;
	}

	/** Taux d'exploration d'un corps celeste, en % (zones explorees / zones totales). */
	public double tauxExploration(CorpsCeleste corps) {
		return corps.tauxExploration();
	}

	// ---------- Arret propre ----------

	/** Ferme le pool : plus de nouvelles missions, on attend celles en cours, puis plus aucun thread. */
	public void arreter() {
		pool.shutdown();
		try {
			if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
				pool.shutdownNow();
			}
		} catch (InterruptedException e) {
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}