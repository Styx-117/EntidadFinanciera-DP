package facilbank.dao;

import facilbank.model.Movimiento;
import java.sql.Connection;
import java.util.List;

public interface MovimientoDAO {
    void insertar(Movimiento movimiento);
    void insertar(Connection conn, Movimiento movimiento);
    List<Movimiento> listarPorCuenta(int idCuenta);
    List<Movimiento> listarRecientesPorCliente(int idCliente, int limite);
}
