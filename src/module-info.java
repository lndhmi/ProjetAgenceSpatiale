/**
 * 
 */
/**
 * 
 */
module ProjetAgenceSpatiale {

    requires jakarta.persistence;// utilise les annotations 
    requires org.hibernate.orm.core;//utiliser hibernate 
    requires java.sql;//Accès JDBC

    opens tp1 to org.hibernate.orm.core;

    exports tp1;
}