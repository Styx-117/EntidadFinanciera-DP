package facilbank.dao;

import facilbank.model.Cuenta;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface CuentaDAO {
    void insertar(Cuenta cuenta);
    List<Cuenta> listarPorCliente(int idCliente);
    Optional<Cuenta> buscarPorNumero(String numeroCuenta);

    Optional<Cuenta> buscarPorNumeroBloqueando(Connection conn, String numeroCuenta);
    void actualizarSaldo(Connection conn, int idCuenta, BigDecimal nuevoSaldo);
}
