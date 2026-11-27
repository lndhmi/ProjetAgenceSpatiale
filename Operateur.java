package tp1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Operateur extends Personne {

	private List<Competence> competences;
	private List<Cadet> cadets;
	private Mission mission;

	public Operateur(long id, String nom, String prenon, String genre, String tel) {
		super(id, nom, prenon, genre, tel);
		competences = new ArrayList<>();
		cadets = new ArrayList<>();
	}

	public void ajouterCompetences(Competence c) {
		if (c == null) {
			throw new IllegalArgumentException("Competence invalide");
		}
		if (competences.contains(c)) {
			return; // deja presente, on ne l'ajoute pas deux fois
		}
		if (competences.size() >= 3) {
			throw new IllegalStateException("Un operateur a au maximum 3 competences");
		}
		competences.add(c);
	}

	public List<Competence> getCompetences() {
		return Collections.unmodifiableList(competences);
	}

	// Appelees par Cadet (constructeur, changerTuteur, devenirOperateur)
	void ajouterCadet(Cadet c) {
		if (!cadets.contains(c)) {
			cadets.add(c);
		}
	}

	void retirerCadet(Cadet c) {
		cadets.remove(c);
	}

	public List<Cadet> getCadets() {
		return Collections.unmodifiableList(cadets);
	}

	public void affecterMission(Mission m) {
		setMission(m);
	}

	public Mission getMission() {
		return mission;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}
	public static List<Operateur> operateursDisponibles(List<Operateur> operateurs) {
	    List<Operateur> resultat = new ArrayList<>();

	    for (Operateur op : operateurs) {
	        if (op.getMission() == null) {
	            resultat.add(op);
	        }
	    }

	    return resultat;
	}
	// le nombres d'operateur disponibles 
	 public static List<Operateur> operateursOccupes(List<Operateur> operateurs ){
		 List<Operateur> resultat = new ArrayList<>();
		 for (Operateur op : operateurs) {
			 if (op.getMission() != null) {
		            resultat.add(op);
		        }
		    }
		 return resultat;
		 }
	 // le nombre d'oprateur non diponibles 
}


