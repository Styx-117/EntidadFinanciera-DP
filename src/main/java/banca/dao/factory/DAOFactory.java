package banca.dao.factory;

import banca.dao.*;
import banca.dao.impl.*;

/**
 * PATRÓN FACTORY (creacional).
 * Crea el DAO correcto según la entidad. Los servicios piden "un CuentaDAO"
 * sin conocer la clase concreta (SOLID: DIP y OCP; GRASP: Variaciones protegidas).
 * Si mañana se cambia JDBC por otra tecnología, solo cambia esta fábrica.
 */
public final class DAOFactory {

    public enum Entidad { USUARIO, CLIENTE, CUENTA, MOVIMIENTO, CREDITO }

    private DAOFactory() { }

    public static Object crear(Entidad entidad) {
        switch (entidad) {
            case USUARIO:    return new UsuarioDAOImpl();
            case CLIENTE:    return new ClienteDAOImpl();
            case CUENTA:     return new CuentaDAOImpl();
            case MOVIMIENTO: return new MovimientoDAOImpl();
            case CREDITO:    return new CreditoDAOImpl();
            default: throw new IllegalArgumentException("Entidad no soportada: " + entidad);
        }
    }

    public static UsuarioDAO getUsuarioDAO()       { return (UsuarioDAO) crear(Entidad.USUARIO); }
    public static ClienteDAO getClienteDAO()       { return (ClienteDAO) crear(Entidad.CLIENTE); }
    public static CuentaDAO getCuentaDAO()         { return (CuentaDAO) crear(Entidad.CUENTA); }
    public static MovimientoDAO getMovimientoDAO() { return (MovimientoDAO) crear(Entidad.MOVIMIENTO); }
    public static CreditoDAO getCreditoDAO()       { return (CreditoDAO) crear(Entidad.CREDITO); }
}
