package tp1;

import java.util.List;
import java.util.ArrayList;

public class Cadet extends Personne {
	private Operateur operateur;
	private List<Mission>missions;
	
	public Cadet(long id, String nom, String prenon, String genre, String tel,Operateur operateur) {
		super(id, nom, prenon, genre, tel);
		this.operateur=operateur;
		missions=new ArrayList<>();
	}

	public List<Mission> getMissions() {
		return missions;
	}

	public void setMissions(List<Mission> missions) {
		this.missions = missions;
	}

	public Operateur getOperateur() {
		return operateur;
	}

	public void setOperateur(Operateur operateur) {
		this.operateur = operateur;
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
	        Operateur nvOperateur = new Operateur(
	        		this.getId(),
	        		this.getNom(),
	                this.getPrenom(),
	                this.getGenre(),
	                this.getTel()
	        		);
	        for(Competence c : operateur.getCompetences()) {
	        	nvOperateur.ajouterCompetences(c);
	        }
	        return nvOperateur;
	    }
	    return null;
	}
}
