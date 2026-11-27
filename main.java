package tp1;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class main {

	public static void main(String[] args) {

		System.out.println("1. PERSONNES");

		DirecteurDeMission picard = new DirecteurDeMission(1, "Picard", "Jean-Luc", "H", "0644444444");
		System.out.println("Directeur : " + picard.getPrenom() + " " + picard.getNom()
				+ " (id " + picard.getId() + ", tel " + picard.getTel() + ")");

		Operateur ripley = new Operateur(2, "Ripley", "Ellen", "F", "0601020304");
		ripley.ajouterCompetences(Competence.pilotage_rover);
		ripley.ajouterCompetences(Competence.geologie);
		System.out.println("Operatrice Ripley : " + ripley.getCompetences());

		// Test : pas plus de 3 compétences, pas de doublon
		ripley.ajouterCompetences(Competence.geologie);       // doublon
		ripley.ajouterCompetences(Competence.energie);         // 3e
		ripley.ajouterCompetences(Competence.telecommunications); // 4e -> doit être ignorée
		System.out.println("Apres ajouts (doublon + 4e) : " + ripley.getCompetences()
				+ " -> " + ripley.getCompetences().size() + " competences (attendu 3)");

		Cadet luke = new Cadet(3, "Skywalker", "Luke", "H", "0605060708", ripley);
		ripley.ajouterCadet(luke);
		System.out.println("Cadet Luke, tuteur : " + luke.getOperateur().getNom());

		System.out.println("\n----- Cas interdits -----");
		testerErreur("id deja utilise", () -> new Operateur(1, "Kirk", "James", "H", "0611111111"));
		testerErreur("genre invalide", () -> new Operateur(10, "Kirk", "James", "X", "0611111111"));
		testerErreur("telephone invalide", () -> new Operateur(11, "Kirk", "James", "H", "12345"));
		testerErreur("cadet sans tuteur", () -> new Cadet(12, "Solo", "Han", "H", "0622222222", null));

		System.out.println("\n2. ZONES et ECHANTILLONS");

		Zone z1 = new Zone("Mer de la Tranquillite", new Position(0, 0), TypeTerrain.PLAINE, 8);
		Zone z2 = new Zone("Cratere Tycho", new Position(1, 0), TypeTerrain.CRATERE, 9);
		Zone z3 = new Zone("Pole Sud", new Position(0, 1), TypeTerrain.GLACE, 10);
		Zone z4 = new Zone("Falaise Nord", new Position(2, 2), TypeTerrain.FALAISE, 3);
		System.out.println("compareTo z1 vs z2 (attendu < 0) : " + z1.compareTo(z2));
		System.out.println("compareTo z3 vs z2 (attendu > 0) : " + z3.compareTo(z2));

		System.out.println("Rover peut explorer z1 ? " + z1.peutEtreExploree(TypeVehicule.ROVER));
		System.out.println("1re exploration rover : " + z1.commencerExploration(TypeVehicule.ROVER));
		System.out.println("2e exploration rover (attendu false) : " + z1.commencerExploration(TypeVehicule.ROVER));
		System.out.println("Drone peut encore explorer z1 ? " + z1.peutEtreExploree(TypeVehicule.DRONE));

		// partie 2 (zones)
		z1.ajouterEchantillon(new Echantillon(2.5, TypeEchantillon.ROCHE));

		testerErreur("masse negative", () -> new Echantillon(-1, TypeEchantillon.GLACE));

		testerErreur("interet > 10", () -> new Zone("Bug", new Position(5, 5), TypeTerrain.PLAINE, 11));
		testerErreur("masse negative", () -> new Echantillon(-1, TypeEchantillon.GLACE));

		System.out.println("\n 3. CORPS CELESTE");

		CorpsCeleste lune = new CorpsCeleste("Lune", false);
		CorpsCeleste mars = new CorpsCeleste("Mars", true);
		lune.ajouterZone(z1);
		lune.ajouterZone(z2);
		lune.ajouterZone(z3);
		lune.ajouterZone(z4);
		System.out.println(lune.getNom() + " : " + lune.getZones().size() + " zones");

		System.out.println("\n4. ENGINS");

		Rover curiosity = new Rover(1, "Curiosity", new Position(0, 0));
		Drone ingenuity = new Drone(2, "Ingenuity", new Position(1, 1));
		System.out.println(curiosity.getnom() + " en " + curiosity.getposition() + " : " + curiosity.getetat());

		curiosity.seDeplacer(new Position(3, 4));
		System.out.println("Apres deplacement : " + curiosity.getposition());
		System.out.println("Distance depuis (0,0) (attendu 5.0) : "
				+ curiosity.getposition().distance(new Position(0, 0)));

		Echantillon e = curiosity.prelever(z2, new Random(42));
		System.out.println("Prelevement du rover : " + (e == null ? "null (a coder)" : e.getType() + " " + e.getMasse()));

		System.out.println("\n5. MISSIONS");

		Mission apollo = new Mission("Apollo", 100_000,
				LocalDate.of(2027, 1, 10), LocalDate.of(2027, 2, 10), List.of(z1, z2, z3, z4), mars, picard);
		Mission artemis = new Mission("Artemis", 250_000,
				LocalDate.of(2027, 3, 1), LocalDate.of(2027, 4, 1), List.of(z1, z2, z3, z4), mars, picard);
		System.out.println(apollo.getNom() + " : " + apollo.getEtat() + ", budget " + apollo.getBudget());

		testerErreur("date de fin avant debut", () -> new Mission("Bug", 1000,
				LocalDate.of(2027, 5, 1), LocalDate.of(2027, 1, 1), List.of(z1), mars, picard));

		testerErreur("mission sans directeur", () -> new Mission("Bug", 1000,
				LocalDate.of(2027, 1, 1), LocalDate.of(2027, 2, 1), List.of(z1), mars, null));
		testerErreur("drone sur la Lune", () -> new Mission("Bug lunaire", 1000,
				LocalDate.of(2027, 1, 1), LocalDate.of(2027, 2, 1), List.of(z1), lune, picard)
				.ajouterEngin(ingenuity));

		System.out.println("Apollo peut demarrer sans personne ? " + apollo.peutDemarrer() + " (attendu false)");
		apollo.ajouterEngin(curiosity);
		apollo.ajouterEngin(ingenuity);
		apollo.ajouterOperateur(ripley);
		System.out.println("Avec Ripley seule (pas pilote de drone) ? " + apollo.peutDemarrer() + " (attendu false)");

		Operateur uhura = new Operateur(4, "Uhura", "Nyota", "F", "0633333333");
		uhura.ajouterCompetences(Competence.pilotege_drone);
		apollo.ajouterOperateur(uhura);
		System.out.println("Avec Ripley + Uhura ? " + apollo.peutDemarrer() + " (attendu true)");

		apollo.demarrer();
		System.out.println("Apollo : " + apollo.getEtat() + ", Curiosity : " + curiosity.getetat()
				+ ", mission de Ripley : " + ripley.getMission().getNom());
		testerErreur("ajouter un engin pendant la mission", () -> apollo.ajouterEngin(new Rover(9, "Bug", new Position(0, 0))));

		artemis.ajouterEngin(curiosity);
		artemis.ajouterOperateur(ripley);
		System.out.println("Artemis peut demarrer (Curiosity et Ripley occupes) ? " + artemis.peutDemarrer() + " (attendu false)");

		// Pendant la mission : 3 zones sur 4 explorees
		apollo.ajouterZoneExploree(z1);
		apollo.ajouterZoneExploree(z2);
		apollo.ajouterZoneExploree(z3);
		// partie 5 (missions)
		apollo.ajouterEchantillon(new Echantillon(1.2, TypeEchantillon.ROCHE));
		apollo.ajouterEchantillon(new Echantillon(0.8, TypeEchantillon.POUSSIERE));
		ingenuity.TomberPanne();

		apollo.terminer();
		System.out.println("Apollo : " + apollo.getEtat() + ", reussie ? " + apollo.estReussie() + " (attendu true)");
		System.out.println("Rapport : " + apollo.getZonesExplorees().size() + " zones, "
				+ apollo.getNbEchantillons() + " echantillons, interet total " + apollo.getInteretTotal());
		System.out.println("Curiosity : " + curiosity.getetat() + ", Ingenuity : " + ingenuity.getetat()
				+ ", Ripley libre ? " + (ripley.getMission() == null));
		System.out.println("Luke a recu la mission ? " + luke.getMissions().contains(apollo));

		testerErreur("affecter un engin en panne", () -> ingenuity.affecterMisiion(artemis));
		ingenuity.RemettreService();
		System.out.println("Ingenuity apres remise en service : " + ingenuity.getetat());

		System.out.println("Artemis avant Apollo ? " + (artemis.compareTo(apollo) < 0) + " (attendu false)");

		System.out.println("\n===== 6. DIRECTEUR : budget des missions reussies =====");

		System.out.println("Budget missions reussies : " + picard.budgetMissionsReussies()
				+ " (attendu 100000.0 : seule Apollo est terminee et reussie)");

		System.out.println("\n===== 7. PROMOTION DU CADET =====");

		Mission m3 = new Mission("Luna 3", 5000, LocalDate.of(2027, 6, 1), LocalDate.of(2027, 6, 2), List.of(z1), mars, picard);
		m3.ajouterZoneExploree(z1);
		Mission m4 = new Mission("Luna 4", 5000, LocalDate.of(2027, 7, 1), LocalDate.of(2027, 7, 2), List.of(z2), mars, picard);
		m4.ajouterZoneExploree(z2);

		luke.ajouterMission(m3);
		System.out.println("Luke : " + luke.compterMissionsReussies() + " missions reussies -> promotion : "
				+ luke.devenirOperateur() + " (attendu null)");

		luke.ajouterMission(m4);
		System.out.println("Luke : " + luke.compterMissionsReussies() + " missions reussies");
		try {
			Operateur lukeOp = luke.devenirOperateur();
			System.out.println("Luke est operateur avec : " + lukeOp.getCompetences());
		} catch (IllegalArgumentException ex) {
			System.out.println("BUG : la promotion plante -> " + ex.getMessage());
		}
	}

	/** Lance une action qui DOIT lever une exception et affiche le résultat. */
	private static void testerErreur(String description, Runnable action) {
		try {
			action.run();
			System.out.println("PROBLEME : " + description + " accepte alors que c'est interdit !");
		} catch (RuntimeException ex) {
			System.out.println("OK, " + description + " refuse (" + ex.getClass().getSimpleName()
					+ (ex.getMessage() != null ? " : " + ex.getMessage() : "") + ")");
		}
	}
}