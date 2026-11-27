package tp1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cadet extends Personne {
	private Operateur operateur; // le tuteur
	private List<Mission> missions;

	public Cadet(long id, String nom, String prenon, String genre, String tel, Operateur operateur) {
		super(id, nom, prenon, genre, tel);
		if (operateur == null) {
			throw new IllegalArgumentException("Un cadet doit avoir un tuteur");
		}
		this.operateur = operateur;
		this.missions = new ArrayList<>();
		operateur.ajouterCadet(this); // le cadet s'inscrit tout seul chez son tuteur
	}

	public List<Mission> getMissions() {
		return Collections.unmodifiableList(missions);
	}

	public Operateur getOperateur() {
		return operateur;
	}

	public void changerTuteur(Operateur nouveau) {
		if (nouveau == null) {
			throw new IllegalArgumentException("Un cadet doit avoir un tuteur");
		}
		if (nouveau == operateur) {
			return; // c'est deja son tuteur
		}
		operateur.retirerCadet(this);
		operateur = nouveau;
		nouveau.ajouterCadet(this);
	}

	public void ajouterMission(Mission m) {
		missions.add(m);
	}

	public int compterMissionsReussies() {
		int cpt = 0;
		for (Mission m : missions) {
			if (m.estReussie()) {
				cpt++;
			}
		}
		return cpt;
	}

	public Operateur devenirOperateur() {
		if (compterMissionsReussies() >= 3) {
			libererId(this.getId()); // la meme personne garde son id
			Operateur nvOperateur = new Operateur(
					this.getId(),
					this.getNom(),
					this.getPrenom(),
					this.getGenre(),
					this.getTel()
			);
			for (Competence c : operateur.getCompetences()) {
				nvOperateur.ajouterCompetences(c);
			}
			operateur.retirerCadet(this); // il n'est plus cadet
			return nvOperateur;
		}
		return null;
	}
}
