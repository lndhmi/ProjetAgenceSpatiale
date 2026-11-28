package tp1;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Personnes : operateurs, cadets, promotion")
class PersonneTest {

	// Les id doivent etre uniques dans tout le programme (Personne les retient)
	private static long prochainId = 10_000;

	private Operateur tuteur;
	private Cadet cadet;

	private static long id() {
		return prochainId++;
	}

	@BeforeEach
	void setUp() {
		tuteur = new Operateur(id(), "Ripley", "Ellen", "F", "0601020304");
		tuteur.ajouterCompetences(Competence.pilotage_rover);
		tuteur.ajouterCompetences(Competence.geologie);
		cadet = new Cadet(id(), "Skywalker", "Luke", "H", "0605060708", tuteur);
	}

	@ParameterizedTest(name = "genre invalide : \"{0}\"")
	@ValueSource(strings = { "X", "", "h", "HF", "Homme" })
	void genreInvalideRefuse(String genre) {
		assertThrows(IllegalArgumentException.class,
				() -> new Operateur(id(), "Kirk", "James", genre, "0611111111"));
	}

	@ParameterizedTest(name = "telephone invalide : \"{0}\"")
	@ValueSource(strings = { "12345", "0012345678", "06123456789", "06-12-34-56", "abcdefghij" })
	void telephoneInvalideRefuse(String tel) {
		assertThrows(IllegalArgumentException.class,
				() -> new Operateur(id(), "Kirk", "James", "H", tel));
	}

	@Test
	void idDejaUtiliseRefuse() {
		long id = tuteur.getId();
		assertThrows(IllegalArgumentException.class,
				() -> new Operateur(id, "Kirk", "James", "H", "0611111111"));
	}

	@Test
	void pasPlusDeTroisCompetences() {
		tuteur.ajouterCompetences(Competence.energie); // 3e : acceptee
		assertEquals(3, tuteur.getCompetences().size());
		assertThrows(IllegalStateException.class,
				() -> tuteur.ajouterCompetences(Competence.telecommunications));
	}

	@Test
	void competencesNonModifiablesDeLExterieur() {
		assertThrows(UnsupportedOperationException.class, () -> tuteur.getCompetences().clear());
	}

	@Test
	void cadetSansTuteurRefuse() {
		assertThrows(IllegalArgumentException.class,
				() -> new Cadet(id(), "Solo", "Han", "H", "0622222222", null));
	}

	@Test
	void cadetInscritChezSonTuteur() {
		assertAll(
				() -> assertSame(tuteur, cadet.getOperateur()),
				() -> assertTrue(tuteur.getCadets().contains(cadet)));
	}

	@Test
	void changementDeTuteurMetAJourLesDeuxOperateurs() {
		Operateur nouveau = new Operateur(id(), "Uhura", "Nyota", "F", "0633333333");
		cadet.changerTuteur(nouveau);
		assertAll(
				() -> assertSame(nouveau, cadet.getOperateur()),
				() -> assertTrue(nouveau.getCadets().contains(cadet)),
				() -> assertFalse(tuteur.getCadets().contains(cadet)));
	}

	@Test
	void promotionApresTroisMissionsReussies() {
		DirecteurDeMission dir = new DirecteurDeMission(id(), "Picard", "Jean-Luc", "H", "0644444444");
		CorpsCeleste mars = new CorpsCeleste("Mars", true);

		for (int i = 0; i < 3; i++) {
			if (i == 2) {
				assertNull(cadet.devenirOperateur(), "pas de promotion avant 3 missions reussies");
			}
			Zone z = new Zone("Z" + i, new Position(i, 0), TypeTerrain.PLAINE, 5);
			Mission m = new Mission("M" + i, 1000, LocalDate.of(2027, 1, 1), LocalDate.of(2027, 2, 1),
					List.of(z), mars, dir);
			m.ajouterZoneExploree(z); // 100 % -> reussie
			cadet.ajouterMission(m);
		}

		Operateur promu = cadet.devenirOperateur();
		assertNotNull(promu);
		assertAll(
				() -> assertEquals(cadet.getId(), promu.getId()),
				() -> assertEquals("Skywalker", promu.getNom()),
				() -> assertEquals(tuteur.getCompetences(), promu.getCompetences()),
				() -> assertFalse(tuteur.getCadets().contains(cadet)));
	}
}
