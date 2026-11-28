package tp1;

import java.time.LocalDate;

/**
 * Rapport produit automatiquement a la fin d'une mission.
 * Plus tard, cette classe deviendra l'@Entity JPA sauvegardee dans H2.
 */
public class RapportMission {
	private String nomMission;
	private LocalDate date;
	private String corpsCeleste;
	private int nbZonesExplorees;
	private int nbEchantillons;
	private double interetTotal;
	private boolean reussie;
	private int budget;

	public RapportMission(Mission m) {
		this.nomMission = m.getNom();
		this.date = m.getDateDebut();
		this.corpsCeleste = m.getCorps().getNom();
		this.nbZonesExplorees = m.getZonesExplorees().size();
		this.nbEchantillons = m.getNbEchantillons();
		this.interetTotal = m.getInteretTotal();
		this.reussie = m.estReussie();
		this.budget = m.getBudget();
	}

	public String getNomMission() { return nomMission; }
	public LocalDate getDate() { return date; }
	public String getCorpsCeleste() { return corpsCeleste; }
	public int getNbZonesExplorees() { return nbZonesExplorees; }
	public int getNbEchantillons() { return nbEchantillons; }
	public double getInteretTotal() { return interetTotal; }
	public boolean isReussie() { return reussie; }
	public int getBudget() { return budget; }

	@Override
	public String toString() {
		return "Rapport[" + nomMission + " sur " + corpsCeleste + " : " + nbZonesExplorees + " zones, "
				+ nbEchantillons + " echantillons, interet " + interetTotal
				+ (reussie ? ", REUSSIE" : ", ECHEC") + "]";
	}
}