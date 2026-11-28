package tp1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Agence : lancement et simulation des missions dans des threads")
class AgenceTest {

	private static long prochainId = 30_000;

	private Agence agence;
	private DirecteurDeMission directeur;
	private CorpsCeleste mars;

	private static long id() {
		return prochainId++;
	}

	/** Cree une mission independante (son propre rover et son propre pilote), prete a demarrer. */
	private Mission nouvelleMission(String nom, LocalDate date, int budget, int nbZones) {
		List<Zone> zones = new ArrayList<>();
		for (int i = 0; i < nbZones; i++) {
			Zone z = new Zone(nom + "-" + i, new Position(i, (int) (prochainId % 1000)), TypeTerrain.PLAINE, i % 11);
			zones.add(z);
			mars.ajouterZone(z);
		}
		Mission m = new Mission(nom, budget, date, date.plusMonths(1), zones, mars, directeur);
		Rover rover = new Rover((int) id(), "Rover " + nom, new Position(0, 0));
		Operateur pilote = new Operateur(id(), "Pilote", nom, "H", "0601020304");
		pilote.ajouterCompetences(Competence.pilotage_rover);
		m.ajouterEngin(rover);
		m.ajouterOperateur(pilote);
		agence.ajouterEngin(rover);
		agence.ajouterPersonne(pilote);
		return m;
	}

	@BeforeEach
	void setUp() {
		// graine fixe, 10 ms par zone pour des tests rapides, 100 % d'exploration, 0 % de panne, pas de base
		agence = new Agence(42, 10, 1.0, 0.0, null);
		directeur = new DirecteurDeMission(id(), "Picard", "Jean-Luc", "H", "0644444444");
		mars = new CorpsCeleste("Mars", true);
		agence.ajouterCorpsCeleste(mars);
	}

	@AfterEach
	void tearDown() {
		agence.arreter(); // aucun thread ne doit continuer a tourner apres le test
	}

	@Test
	void laMissionLaPlusProcheEstLanceeEnPremier() {
		Mission tard = nouvelleMission("Tard", LocalDate.of(2030, 1, 1), 1000, 2);
		Mission tot = nouvelleMission("Tot", LocalDate.of(2027, 1, 1), 1000, 2);
		agence.planifierMission(tard);
		agence.planifierMission(tot);

		agence.lancerProchaineMission();

		assertEquals(EtatMission.EN_COURS, tot.getEtat());
		assertEquals(EtatMission.PLANIFIEE, tard.getEtat());
	}

	@Test
	void simulationProduitUnRapport() {
		Mission m = nouvelleMission("Ares", LocalDate.of(2027, 1, 1), 50_000, 5);
		agence.planifierMission(m);

		Future<RapportMission> futur = agence.lancerProchaineMission();
		RapportMission rapport = agence.attendreRapport(futur, 2000);

		assertNotNull(rapport, "le rapport doit arriver en moins de 2 s");
		assertTrue(futur.isDone());
		assertEquals(EtatMission.TERMINEE, m.getEtat());
		assertEquals(5, rapport.getNbZonesExplorees()); // proba d'exploration = 100 %
		assertEquals(5, rapport.getNbEchantillons());   // un rover preleve dans chaque zone
		assertTrue(rapport.isReussie());
		assertEquals(50_000, rapport.getBudget());
		assertEquals(EtatEngin.DISPONIBLE, m.getEngins().get(0).getetat()); // proba de panne = 0 %
	}

	@Test
	void pasPlusDeTroisMissionsEnCours() {
		for (int i = 1; i <= 4; i++) {
			agence.planifierMission(nouvelleMission("M" + i, LocalDate.of(2027, i, 1), 1000, 20));
		}
		agence.lancerProchaineMission();
		agence.lancerProchaineMission();
		agence.lancerProchaineMission();

		assertEquals(3, agence.getMissionsEnCours().size());
		assertThrows(IllegalStateException.class, agence::lancerProchaineMission);
	}

	@Test
	void deuxLancementsSimultanesNeDepassentPasTrois() throws InterruptedException {
		for (int i = 1; i <= 6; i++) {
			agence.planifierMission(nouvelleMission("S" + i, LocalDate.of(2027, i, 1), 1000, 50));
		}
		// 6 threads essaient de lancer une mission en meme temps
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			Thread t = new Thread(() -> {
				try {
					agence.lancerProchaineMission();
				} catch (IllegalStateException e) {
					// refuse : deja 3 missions en cours
				}
			});
			threads.add(t);
			t.start();
		}
		for (Thread t : threads) {
			t.join();
		}
		assertEquals(3, agence.getMissionsEnCours().size());
		assertEquals(3, agence.getMissionsPlanifiees().size());
	}

	@Test
	void sansExplorationLaMissionEchoue() {
		agence.arreter();
		agence = new Agence(42, 10, 0.0, 0.0, null); // 0 % d'exploration
		Mission m = nouvelleMission("Echec", LocalDate.of(2027, 1, 1), 1000, 3);
		agence.planifierMission(m);

		RapportMission rapport = agence.attendreRapport(agence.lancerProchaineMission(), 2000);

		assertFalse(rapport.isReussie());
		assertEquals(0, rapport.getNbZonesExplorees());
	}

	@Test
	void missionsTermineesTrieesParBudgetDecroissant() {
		Mission petite = nouvelleMission("Petite", LocalDate.of(2027, 1, 1), 1_000, 1);
		Mission grosse = nouvelleMission("Grosse", LocalDate.of(2027, 2, 1), 9_000, 1);
		agence.planifierMission(petite);
		agence.planifierMission(grosse);
		Future<RapportMission> f1 = agence.lancerProchaineMission();
		Future<RapportMission> f2 = agence.lancerProchaineMission();
		agence.attendreRapport(f1, 2000);
		agence.attendreRapport(f2, 2000);

		assertEquals(List.of(grosse, petite), agence.getMissionsTermineesParBudget());
	}
}
