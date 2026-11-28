package tp1;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Sauvegarde et recherche des rapports de mission dans la base H2 (via JPA).
 */
public class GestionRapport {

	private final EntityManagerFactory emf;

	public GestionRapport() {
		this("agencePU");
	}

	public GestionRapport(String nomUnite) {
		this.emf = Persistence.createEntityManagerFactory(nomUnite);
	}

	/** Sauvegarde un rapport. synchronized : plusieurs missions peuvent finir en meme temps. */
	public synchronized void sauvegarder(RapportMission rapport) {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(rapport);
			em.getTransaction().commit();
		} catch (RuntimeException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	/** Retrouve un rapport grace a son identifiant (null s'il n'existe pas). */
	public RapportMission rechercher(Long id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(RapportMission.class, id);
		} finally {
			em.close();
		}
	}

	/** Tous les rapports. */
	public List<RapportMission> tous() {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery("SELECT r FROM RapportMission r ORDER BY r.id", RapportMission.class)
					.getResultList();
		} finally {
			em.close();
		}
	}

	/** Les rapports d'un corps celeste donne. */
	public List<RapportMission> parCorpsCeleste(String nomCorps) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.createQuery(
					"SELECT r FROM RapportMission r WHERE r.corpsCeleste = :corps ORDER BY r.id",
					RapportMission.class)
					.setParameter("corps", nomCorps)
					.getResultList();
		} finally {
			em.close();
		}
	}

	/** Ferme la connexion a la fin du programme. */
	public void fermer() {
		if (emf.isOpen()) {
			emf.close();
		}
	}
}
