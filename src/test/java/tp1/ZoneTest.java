package tp1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Zones, engins, echantillons et listes triees")
class ZoneTest {

	private static long prochainId = 40_000;

	private Zone zone;
	private Rover rover;
	private Drone drone;
	private Random random;

	@BeforeEach
	void setUp() {
		zone = new Zone("Tycho", new Position(1, 1), TypeTerrain.CRATERE, 9);
		rover = new Rover(1, "Curiosity", new Position(0, 0));
		drone = new Drone(2, "Ingenuity", new Position(0, 0));
		random = new Random(42); // graine fixe : resultats reproductibles
	}

	static Stream<Arguments> interetsInvalides() {
		return Stream.of(Arguments.of(-1.0), Arguments.of(10.5), Arguments.of(42.0));
	}

	@ParameterizedTest(name = "interet {0} refuse")
	@MethodSource("interetsInvalides")
	void interetHorsDeZeroADixRefuse(double interet) {
		assertThrows(IllegalArgumentException.class,
				() -> new Zone("Bug", new Position(0, 0), TypeTerrain.PLAINE, interet));
	}

	@Test
	void uneSeuleExplorationParTypeDeVehicule() {
		Echantillon premier = rover.explorer(zone, random);
		Echantillon second = rover.explorer(zone, random);

		assertEquals(TypeVehicule.ROVER, rover.getType());
		assertTrue(premier != null && premier.getMasse() > 0);
		assertNull(second, "un 2e rover ne peut pas explorer la meme zone");
		assertTrue(zone.peutEtreExploree(TypeVehicule.DRONE), "un drone peut encore passer");

		drone.explorer(zone, random);
		assertTrue(zone.estCartographiee());
		assertFalse(zone.peutEtreExploree(TypeVehicule.DRONE));
	}

	@Test
	void ordreDesZonesParCoordonnees() {
		Zone a = new Zone("A", new Position(0, 0), TypeTerrain.PLAINE, 1);
		Zone b = new Zone("B", new Position(1, 0), TypeTerrain.PLAINE, 1);
		Zone c = new Zone("C", new Position(0, 1), TypeTerrain.PLAINE, 1);
		assertTrue(a.compareTo(b) < 0);
		assertTrue(b.compareTo(c) < 0); // ligne y=0 avant la ligne y=1
	}

	@Test
	void zonesExploreesTrieesParInteretPuisCoordonnees() {
		CorpsCeleste mars = new CorpsCeleste("Mars", true);
		Zone faible = new Zone("Faible", new Position(0, 0), TypeTerrain.PLAINE, 2);
		Zone forteLoin = new Zone("ForteLoin", new Position(2, 0), TypeTerrain.PLAINE, 8);
		Zone forteProche = new Zone("ForteProche", new Position(1, 0), TypeTerrain.PLAINE, 8);
		Zone nonExploree = new Zone("Jamais", new Position(3, 0), TypeTerrain.PLAINE, 10);
		for (Zone z : List.of(faible, forteLoin, forteProche, nonExploree)) {
			mars.ajouterZone(z);
		}
		for (Zone z : List.of(faible, forteLoin, forteProche)) {
			z.commencerExploration(TypeVehicule.ROVER);
		}

		Agence agence = new Agence(1);
		assertEquals(List.of(forteProche, forteLoin, faible), agence.zonesExploreesTriees(mars));
		assertEquals(75.0, agence.tauxExploration(mars), 0.001);
		agence.arreter();
	}

	@Test
	void echantillonsRegroupesParType() {
		DirecteurDeMission dir = new DirecteurDeMission(prochainId++, "Picard", "Jean-Luc", "H", "0644444444");
		Mission m = new Mission("Ares", 1000, LocalDate.of(2027, 1, 1), LocalDate.of(2027, 2, 1),
				List.of(zone), new CorpsCeleste("Mars", true), dir);
		m.ajouterEchantillon(new Echantillon(1.0, TypeEchantillon.ROCHE));
		m.ajouterEchantillon(new Echantillon(2.0, TypeEchantillon.GLACE));
		m.ajouterEchantillon(new Echantillon(3.0, TypeEchantillon.ROCHE));

		Agence agence = new Agence(1);
		Map<TypeEchantillon, List<Echantillon>> parType = agence.echantillonsParType(m);
		agence.arreter();

		assertEquals(2, parType.get(TypeEchantillon.ROCHE).size());
		assertEquals(1, parType.get(TypeEchantillon.GLACE).size());
		assertFalse(parType.containsKey(TypeEchantillon.POUSSIERE));
	}

	@Test
	void masseNegativeRefusee() {
		assertThrows(IllegalArgumentException.class, () -> new Echantillon(-1.0, TypeEchantillon.GLACE));
	}
}
