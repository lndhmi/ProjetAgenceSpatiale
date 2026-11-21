package tp1;

import java.util.HashSet;
import java.util.Set;

public abstract class Personne {
	 private static Set<Long>idUtilise=new HashSet<>();
	 private final long id;
	 private String nom;
	 private String prenom;
	 private String genre;
	 private String tel;
	 
	 public Personne(long id, String nom, String prenom,String genre,String tel) {
		 if(!idUtilise.add(id)) {
			 throw new IllegalArgumentException("id deja utilise");
		 }
		 this.id=id;
		 this.nom=nom;
		 this.prenom=prenom;
		 setGenre(genre);
		 setTel(tel);
	 }
	 
	 public void setTel(String tel) {
		 if(tel==null|| !tel.matches("0[1-9][0-9]{8}")) {
			 throw new IllegalArgumentException("Numero non valide");
		 }
		 this.tel=tel;
	 }
	 public long getId() {
		 return id;
	 }
	 public String getTel() {
		 return tel;
	 }
	 public String getNom() {
		 return nom;
	 }
	 public String getPrenom() {
		 return prenom;
	 }
	 public String getGenre() {
		 return genre;
	 }
	 public void setNom(String nom) {
		 this.nom=nom;
	 }
	 public void setPrenom(String prenom) {
		 this.prenom=prenom;
	 }
	 public void setGenre(String genre) {
		 if(genre==null || !genre.matches("[FH]")) {
			 throw new IllegalArgumentException("genre non valide");
		 }
		 this.genre=genre;
	 }
}
