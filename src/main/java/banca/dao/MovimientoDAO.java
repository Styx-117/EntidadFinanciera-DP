package banca.dao;
import banca.model.Movimiento;
import java.sql.Connection;
import java.util.List;
public interface MovimientoDAO {
    void insertar(Movimiento movimiento);
    void insertar(Connection conn, Movimiento movimiento);
    List<Movimiento> listarPorCuenta(int idCuenta);
}
