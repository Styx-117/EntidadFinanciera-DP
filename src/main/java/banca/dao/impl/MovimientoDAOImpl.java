package banca.dao.impl;

import banca.dao.DAOException;
import banca.dao.MovimientoDAO;
import banca.model.Movimiento;
import banca.util.ConexionBD;
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
        String sql = "SELECT id_movimiento, id_cuenta, tipo, monto, descripcion, fecha "
                   + "FROM movimiento WHERE id_cuenta = ? ORDER BY fecha DESC, id_movimiento DESC LIMIT 50";
        List<Movimiento> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimiento m = new Movimiento(rs.getInt("id_cuenta"), rs.getString("tipo"),
                            rs.getBigDecimal("monto"), rs.getString("descripcion"));
                    m.setIdMovimiento(rs.getInt("id_movimiento"));
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
