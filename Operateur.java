package tp1;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Operateur extends Personne{
	
	private List<Competence>competences;
	private List<Cadet>cadets;
	private Mission mission;
	
	public Operateur(long id, String nom, String prenon, String genre, String tel) {
		super(id, nom, prenon,genre, tel);
		competences =new ArrayList<>();
		cadets=new ArrayList<>();
	}
	public void ajouterCompetences(Competence c) {
		if(competences.size()<3 && !competences.contains(c)) {
			competences.add(c);
		}
	}
	public List<Competence> getCompetences() {
	    return competences;
	}
	public void ajouterCadet(Cadet c) {
		cadets.add(c);
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
	public List<Cadet> getCadets() {
	    return Collections.unmodifiableList(cadets);
	}
	public void retirerCadet(Cadet c) {
	    cadets.remove(c);
	}
	
}
