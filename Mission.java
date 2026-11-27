package tp1;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Mission implements Comparable<Mission> {
	private CorpsCeleste corpsCel;
	private String nom;
	private LocalDate dateDebut;
	private LocalDate dateFin;
	private boolean estReussie;
	private int budget;
	private DirecteurDeMission directeur;
	private List<Operateur>operateurs;
	private EtatMission etat = EtatMission.PLANIFIEE;
	private final List<Zone> zonesExplorees = new ArrayList<>();
	
	public Mission(String nom,boolean reussie,int budget,LocalDate dateDebut,LocalDate dateFin) {
		if (dateDebut == null || dateFin == null) {
		        throw new IllegalArgumentException("Les dates sont obligatoires");
		}
		if (dateFin.isBefore(dateDebut)) {
		    throw new IllegalArgumentException("La date de fin doit être après la date de début");
		}
		if (directeur == null) {
			throw new IllegalArgumentException("Pas de mission sans directeur");
		}
		this.nom=nom;
		this.setEstReussie(reussie);
		this.dateDebut=dateDebut;
		this.dateFin=dateFin;
		this.budget=budget;
		
	}
	 public String getNom() {
		 return nom;
	 }
	 public void setNom(String nom) {
		 this.nom=nom;
	 }
	 public int getBudget() {
		 return budget;
	 }
	 public void SetBudget(int budget) {
		 this.budget=budget;
	 }
	 public LocalDate getDateDebut(){
		 return dateDebut;
	 }
	 public LocalDate getDateFin(){
		 return dateFin;
	 }
	 public EtatMission getEtat() {
		return etat;
	 }
	 public void setEtat(EtatMission etat) {
		this.etat = etat;
	 }
	 public boolean estReussie() {
		return estReussie;
	 }
	 public void setEstReussie(boolean estReussie) {
		this.estReussie = estReussie;
	 }
	 
	 @Override
	    public int compareTo(Mission autre) {
	        return this.dateDebut.compareTo(autre.dateDebut);
	    }
	 private double interetTotal() {
			double tot = 0;
			for (Zone z : zonesExplorees) {
				tot += z.getInteretScientifique();
			}
			return tot;
		}

	 
}
