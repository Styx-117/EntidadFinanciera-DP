package banca.dao;
public class DAOException extends RuntimeException {
    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
