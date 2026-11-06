package facilbank.dao.impl;

import facilbank.dao.DAOException;
import facilbank.dao.MovimientoDAO;
import facilbank.model.Movimiento;
import facilbank.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDAOImpl implements MovimientoDAO {
    private static final String INSERT =
            "INSERT INTO movimiento (id_cuenta, tipo, monto, descripcion) VALUES (?,?,?,?)";

    @Override
    public void insertar(Movimiento m) {
        try {
            insertarCon(ConexionBD.getInstancia().getConexion(), m);
        } catch (SQLException e) {
            throw new DAOException("Error al insertar movimiento", e);
        }
    }

    @Override
    public void insertar(Connection conn, Movimiento m) {
        try {
            insertarCon(conn, m);
        } catch (SQLException e) {
            throw new DAOException("Error al insertar movimiento", e);
        }
    }

    private void insertarCon(Connection conn, Movimiento m) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
            ps.setInt(1, m.getIdCuenta());
            ps.setString(2, m.getTipo());
            ps.setBigDecimal(3, m.getMonto());
            ps.setString(4, m.getDescripcion());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Movimiento> listarPorCuenta(int idCuenta) {
        String sql = "SELECT m.id_movimiento, m.id_cuenta, c.numero_cuenta, m.tipo, m.monto, m.descripcion, m.fecha "
                   + "FROM movimiento m JOIN cuenta c ON c.id_cuenta = m.id_cuenta "
                   + "WHERE m.id_cuenta = ? ORDER BY m.fecha DESC, m.id_movimiento DESC LIMIT 50";
        return consultar(sql, idCuenta, 0);
    }

    @Override
    public List<Movimiento> listarRecientesPorCliente(int idCliente, int limite) {
        String sql = "SELECT m.id_movimiento, m.id_cuenta, c.numero_cuenta, m.tipo, m.monto, m.descripcion, m.fecha "
                   + "FROM movimiento m JOIN cuenta c ON c.id_cuenta = m.id_cuenta "
                   + "WHERE c.id_cliente = ? ORDER BY m.fecha DESC, m.id_movimiento DESC LIMIT ?";
        return consultar(sql, idCliente, limite);
    }

    private List<Movimiento> consultar(String sql, int parametro, int limite) {
        List<Movimiento> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, parametro);
            if (limite > 0) ps.setInt(2, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimiento m = new Movimiento(rs.getInt("id_cuenta"), rs.getString("tipo"),
                            rs.getBigDecimal("monto"), rs.getString("descripcion"));
                    m.setIdMovimiento(rs.getInt("id_movimiento"));
                    m.setNumeroCuenta(rs.getString("numero_cuenta"));
                    m.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar movimientos", e);
        }
        return lista;
    }
}
