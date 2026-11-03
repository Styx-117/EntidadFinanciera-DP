package banca.service;
import banca.dao.CuentaDAO;
import banca.dao.DAOException;
import banca.dao.MovimientoDAO;
import banca.dao.factory.DAOFactory;
import banca.model.Cuenta;
import banca.model.Movimiento;
import banca.util.ConexionBD;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
@Service
public class OperacionService {
    public static final List<String> SERVICIOS = List.of("Luz", "Agua", "Telefonía", "Internet");
    private final CuentaDAO cuentaDAO = DAOFactory.getCuentaDAO();
    private final MovimientoDAO movimientoDAO = DAOFactory.getMovimientoDAO();
    @FunctionalInterface
    private interface Operacion {
        void ejecutar(Connection conn) throws SQLException;
    }
    public void transferir(int idCliente, String origen, String destino, BigDecimal monto) {
        validarMonto(monto);
        if (origen == null || origen.isBlank() || destino == null || destino.isBlank()) {
            throw new NegocioException("Indique la cuenta de origen y la de destino");
        }
        origen = origen.trim();
        destino = destino.trim();
        if (origen.equals(destino)) {
            throw new NegocioException("La cuenta destino debe ser distinta a la de origen");
        }
        final String nOrigen = origen;
        final String nDestino = destino;

        enTransaccion(conn -> {
            // Se bloquean las filas siempre en el mismo orden para evitar interbloqueos
            String primera = nOrigen.compareTo(nDestino) < 0 ? nOrigen : nDestino;
            String segunda = primera.equals(nOrigen) ? nDestino : nOrigen;
            Optional<Cuenta> c1 = cuentaDAO.buscarPorNumeroBloqueando(conn, primera);
            Optional<Cuenta> c2 = cuentaDAO.buscarPorNumeroBloqueando(conn, segunda);
            Optional<Cuenta> optOrigen = primera.equals(nOrigen) ? c1 : c2;
            Optional<Cuenta> optDestino = primera.equals(nOrigen) ? c2 : c1;

            Cuenta cOrigen = optOrigen.orElseThrow(() -> new NegocioException("La cuenta de origen no existe"));
            Cuenta cDestino = optDestino.orElseThrow(() -> new NegocioException("La cuenta destino no existe"));
            verificarPropia(cOrigen, idCliente);

            cOrigen.retirar(monto);
            cDestino.depositar(monto);

            cuentaDAO.actualizarSaldo(conn, cOrigen.getIdCuenta(), cOrigen.getSaldo());
            cuentaDAO.actualizarSaldo(conn, cDestino.getIdCuenta(), cDestino.getSaldo());
            movimientoDAO.insertar(conn, new Movimiento(cOrigen.getIdCuenta(), "TRANSFERENCIA", monto,
                    "Transferencia enviada a " + nDestino));
            movimientoDAO.insertar(conn, new Movimiento(cDestino.getIdCuenta(), "TRANSFERENCIA", monto,
                    "Transferencia recibida de " + nOrigen));
        });
    }
    public void pagarServicio(int idCliente, String origen, String servicio, String codigo, BigDecimal monto) {
        validarMonto(monto);
        if (servicio == null || !SERVICIOS.contains(servicio)) {
            throw new NegocioException("Seleccione un servicio válido");
        }
        if (codigo == null || !codigo.trim().matches("\\d{4,12}")) {
            throw new NegocioException("El código de suministro debe tener entre 4 y 12 dígitos");
        }
        if (origen == null || origen.isBlank()) {
            throw new NegocioException("Seleccione la cuenta de cargo");
        }
        final String nOrigen = origen.trim();
        final String nCodigo = codigo.trim();

        enTransaccion(conn -> {
            Cuenta cOrigen = cuentaDAO.buscarPorNumeroBloqueando(conn, nOrigen)
                    .orElseThrow(() -> new NegocioException("La cuenta de cargo no existe"));
            verificarPropia(cOrigen, idCliente);

            cOrigen.retirar(monto);
            cuentaDAO.actualizarSaldo(conn, cOrigen.getIdCuenta(), cOrigen.getSaldo());
            movimientoDAO.insertar(conn, new Movimiento(cOrigen.getIdCuenta(), "PAGO_SERVICIO", monto,
                    "Pago de " + servicio + " - suministro " + nCodigo));
        });
    }

    // ---------- utilidades internas ----------

    private void verificarPropia(Cuenta cuenta, int idCliente) {
        if (cuenta.getIdCliente() != idCliente) {
            throw new NegocioException("La cuenta de origen no le pertenece");
        }
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new NegocioException("El monto debe ser mayor a cero");
        }
        if (monto.stripTrailingZeros().scale() > 2) {
            throw new NegocioException("El monto admite como máximo 2 decimales");
        }
    }

    private void enTransaccion(Operacion op) {
        Connection conn = null;
        try {
            conn = ConexionBD.getInstancia().nuevaConexion();
            conn.setAutoCommit(false);
            op.ejecutar(conn);
            conn.commit();
        } catch (NegocioException e) {
            rollback(conn);
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            rollback(conn);
            throw new NegocioException(e.getMessage());
        } catch (SQLException | DAOException e) {
            rollback(conn);
            throw new NegocioException("No se pudo completar la operación. Intente nuevamente.", e);
        } finally {
            cerrar(conn);
        }
    }

    private void rollback(Connection conn) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ignorada) {

        }
    }

    private void cerrar(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (SQLException ignorada) {

        }
    }
}
