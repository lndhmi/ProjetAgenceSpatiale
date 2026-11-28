package tp1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Missions : regles de demarrage, de fin et des 70 %")
class MissionTest {

	private static long prochainId = 20_000;

	private DirecteurDeMission directeur;
	private CorpsCeleste mars;
	private CorpsCeleste lune;
	private List<Zone> dixZones;
	private Mission mission;
	private Rover rover;
	private Drone drone;
	private Operateur piloteRover;

	private static long id() {
		return prochainId++;
	}

	@BeforeEach
	void setUp() {
		directeur = new DirecteurDeMission(id(), "Picard", "Jean-Luc", "H", "0644444444");
		mars = new CorpsCeleste("Mars", true);
		lune = new CorpsCeleste("Lune", false);
		dixZones = new ArrayList<>();
		for (int x = 0; x < 10; x++) {
			Zone z = new Zone("Z" + x, new Position(x, 0), TypeTerrain.PLAINE, 5);
			dixZones.add(z);
			mars.ajouterZone(z);
		}
		mission = new Mission("Ares", 100_000, LocalDate.of(2027, 1, 1), LocalDate.of(2027, 2, 1),
				dixZones, mars, directeur);
		rover = new Rover(1, "Curiosity", new Position(0, 0));
		drone = new Drone(2, "Ingenuity", new Position(0, 0));
		piloteRover = new Operateur(id(), "Ripley", "Ellen", "F", "0601020304");
		piloteRover.ajouterCompetences(Competence.pilotage_rover);
	}

	@ParameterizedTest(name = "{0} zones explorees sur 10 -> reussie = {1}")
	@CsvSource({ "0, false", "6, false", "7, true", "10, true" })
	void regleDesSoixanteDixPourcent(int nbExplorees, boolean attendu) {
		for (int i = 0; i < nbExplorees; i++) {
			mission.ajouterZoneExploree(dixZones.get(i));
		}
		assertEquals(attendu, mission.estReussie());
	}

	@Test
	void missionSansDirecteurRefusee() {
		assertThrows(IllegalArgumentException.class, () -> new Mission("Bug", 1000,
				LocalDate.of(2027, 1, 1), LocalDate.of(2027, 2, 1), dixZones, mars, null));
	}

	@Test
	void dateDeFinAvantDebutRefusee() {
		assertThrows(IllegalArgumentException.class, () -> new Mission("Bug", 1000,
				LocalDate.of(2027, 5, 1), LocalDate.of(2027, 1, 1), dixZones, mars, directeur));
	}

	@Test
	void pasDeDroneSurUnCorpsSansAtmosphere() {
		Mission surLaLune = new Mission("Artemis", 1000, LocalDate.of(2027, 1, 1),
				LocalDate.of(2027, 2, 1), List.of(), lune, directeur);
		assertThrows(IllegalArgumentException.class, () -> surLaLune.ajouterEngin(drone));
	}

	@Test
	void demarrageImpossibleSansPiloteDeDrone() {
		mission.ajouterEngin(rover);
		mission.ajouterEngin(drone);
		mission.ajouterOperateur(piloteRover);
		assertFalse(mission.peutDemarrer());
		assertThrows(IllegalStateException.class, mission::demarrer);
	}

	@Test
	void cycleDeVieComplet() {
		mission.ajouterEngin(rover);
		mission.ajouterOperateur(piloteRover);
		assertEquals(EtatMission.PLANIFIEE, mission.getEtat());

		mission.demarrer();
		assertEquals(EtatMission.EN_COURS, mission.getEtat());
		assertEquals(EtatEngin.EN_MISSION, rover.getetat());
		assertEquals(mission, piloteRover.getMission());
		assertThrows(IllegalStateException.class, () -> mission.ajouterEngin(drone),
				"on ne modifie plus une mission en cours");

		mission.terminer();
		assertEquals(EtatMission.TERMINEE, mission.getEtat());
		assertEquals(EtatEngin.DISPONIBLE, rover.getetat());
		assertNull(piloteRover.getMission());
	}

	@Test
	void enginEnPanneResteEnPanneApresLaMission() {
		mission.ajouterEngin(rover);
		mission.ajouterOperateur(piloteRover);
		mission.demarrer();
		rover.TomberPanne();
		mission.terminer();
		assertEquals(EtatEngin.EN_PANNE, rover.getetat());
		assertThrows(IllegalStateException.class, () -> rover.affecterMisiion(mission));
	}

	@Test
	void missionLaPlusProcheEnPremier() {
		Mission plusTard = new Mission("Plus tard", 1000, LocalDate.of(2030, 1, 1),
				LocalDate.of(2030, 2, 1), dixZones, mars, directeur);
		assertTrue(mission.compareTo(plusTard) < 0);
	}
}
