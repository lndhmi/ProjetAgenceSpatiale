package tp1;

import java.time.LocalDate;

public class Mission {
	private String nom;
	private LocalDate dateDebut;
	private LocalDate dateFin;
	private boolean estReussie;
	private int budget;
	private EtatMission etat = EtatMission.PLANIFIEE;
	
	public Mission(String nom,boolean reussie,int budget,LocalDate dateDebut,LocalDate dateFin) {
		if (dateDebut == null || dateFin == null) {
		        throw new IllegalArgumentException("Les dates sont obligatoires");
		}
		if (dateFin.isBefore(dateDebut)) {
		    throw new IllegalArgumentException("La date de fin doit être après la date de début");
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
	 
	 
	 
}
