package tp1;

import java.util.HashSet;
import java.util.Set;


public class DirecteurDeMission extends Personne {
	private int budgetTot;
	private EtatMission etat;
	private Set<Mission> missions = new HashSet<>();
	
	public DirecteurDeMission(long id, String nom, String prenom, String genre, String tel) {
        super(id, nom, prenom, genre, tel);
    }

	public Set<Mission> getMissions() {
		return missions;
	}

	public void setMissions(Set<Mission> missions) {
		this.missions = missions;
	}

	public EtatMission getEtat() {
		return etat;
	}

	public void setEtat(EtatMission etat) {
		this.etat = etat;
	}


	public int getBudgetTot() {
		return budgetTot;
	}


	public void setBudgetTot(int budgetTot) {
		this.budgetTot = budgetTot;
	}
	
	void ajouterMission(Mission m) {
        missions.add(m);
    }
	public double budgetMissionsReussies() {
		double tot=0;
		for (Mission m : missions) {
			if(m.getEtat()==EtatMission.TERMINEE && m.estReussie()) {
				tot+=m.getBudget();
			}
		}
		return tot;
	}
	
	public int nbrDeMissionEnCours() {
		int cpt=0;
		for(Mission m : missions) {
			if(m.getEtat()==EtatMission.EN_COURS) {
				cpt++;
			}
		}
		return cpt;
	}
}
