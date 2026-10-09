package banca.dao;

/** Envuelve los SQLException para que las capas superiores no dependan de JDBC. */
public class DAOException extends RuntimeException {
    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
