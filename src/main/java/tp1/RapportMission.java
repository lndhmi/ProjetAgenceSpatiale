package tp1;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rapports_mission")
public class RapportMission {

	  @Id
	   @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String nomMission;

	    private String titre;

	    @Column(length = 5000)
	    private String contenu;

	    private LocalDateTime dateCreation;

	    // Constructeur obligatoire pour JPA
	    public RapportMission() {
	    }

	    public RapportMission(String nomMission, String titre, String contenu) {
	        this.nomMission = nomMission;
	        this.titre = titre;
	        this.contenu = contenu;
	        this.dateCreation = LocalDateTime.now();
	    }

	    public Long getId() {
	        return id;
	    }

	    public String getNomMission() {
	        return nomMission;
	    }

	    public String getTitre() {
	        return titre;
	    }

	    public String getContenu() {
	        return contenu;
	    }

	    public LocalDateTime getDateCreation() {
	        return dateCreation;
	    }

	    public void setNomMission(String nomMission) {
	        this.nomMission = nomMission;
	    }

	    public void setTitre(String titre) {
	        this.titre = titre;
	    }

	    public void setContenu(String contenu) {
	        this.contenu = contenu;
	    }

	    @Override
	    public String toString() {
	        return "RapportMission{" +
	                "id=" + id +
	                ", nomMission='" + nomMission + '\'' +
	                ", titre='" + titre + '\'' +
	                ", contenu='" + contenu + '\'' +
	                ", dateCreation=" + dateCreation +
	                '}';
	    }
	}


