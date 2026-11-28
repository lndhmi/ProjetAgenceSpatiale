package tp1;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
//operation de sauvegarde et de recherche 
public class GestionRapport {
	private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("agencePU");

    // Sauvegarder un rapport
    public void sauvegarder(RapportMission rapport) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(rapport);

            em.getTransaction().commit();

            System.out.println("Rapport sauvegardé avec succès !");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Retrouver un rapport grâce à son identifiant
    public RapportMission rechercher(Long id) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.find(RapportMission.class, id);
        } finally {
            em.close();
        }
    }

    // Fermer la connexion Hibernate à la fin du programme
    public static void fermer() {
        emf.close();
    }

}
