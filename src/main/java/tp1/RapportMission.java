package tp1;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Rapport cree automatiquement a la fin de chaque mission et sauvegarde dans H2 (JPA).
 * Champs demandes par le sujet : id, nom de la mission, date, corps celeste,
 * nombre de zones explorees, nombre d'echantillons, interet scientifique total, reussite, budget.
 */
@Entity
@Table(name = "rapports_mission")
public class RapportMission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nomMission;
	private LocalDate date;
	private String corpsCeleste;
	private int nbZonesExplorees;
	private int nbEchantillons;
	private double interetTotal;
	private boolean reussie;
	private int budget;

	// Constructeur vide obligatoire pour JPA
	protected RapportMission() {
	}

	/** Construit le rapport a partir d'une mission terminee. */
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

	public Long getId() { return id; }
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
		return "Rapport#" + id + "[" + nomMission + " sur " + corpsCeleste + " le " + date + " : "
				+ nbZonesExplorees + " zones, " + nbEchantillons + " echantillons, interet " + interetTotal
				+ ", budget " + budget + (reussie ? ", REUSSIE" : ", ECHEC") + "]";
	}
}