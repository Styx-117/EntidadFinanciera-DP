package banca.dao.impl;

import banca.dao.CuentaDAO;
import banca.dao.DAOException;
import banca.model.Cuenta;
import banca.util.ConexionBD;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CuentaDAOImpl implements CuentaDAO {

    private static final String COLS = "id_cuenta, id_cliente, numero_cuenta, tipo, saldo, estado";

    @Override
    public void insertar(Cuenta c) {
        String sql = "INSERT INTO cuenta (id_cliente, numero_cuenta, tipo, saldo, estado) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, new String[]{"id_cuenta"})) {
            ps.setInt(1, c.getIdCliente());
            ps.setString(2, c.getNumeroCuenta());
            ps.setString(3, c.getTipo());
            ps.setBigDecimal(4, c.getSaldo());
            ps.setString(5, c.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCuenta(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al insertar cuenta", e);
        }
    }

    @Override
    public List<Cuenta> listarPorCliente(int idCliente) {
        String sql = "SELECT " + COLS + " FROM cuenta WHERE id_cliente = ? ORDER BY id_cuenta";
        List<Cuenta> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar cuentas", e);
        }
        return lista;
    }

    @Override
    public Optional<Cuenta> buscarPorNumero(String numeroCuenta) {
        String sql = "SELECT " + COLS + " FROM cuenta WHERE numero_cuenta = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, numeroCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar cuenta", e);
        }
    }

    @Override
    public Optional<Cuenta> buscarPorNumeroBloqueando(Connection conn, String numeroCuenta) {
        String sql = "SELECT " + COLS + " FROM cuenta WHERE numero_cuenta = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numeroCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Error al bloquear cuenta", e);
        }
    }

    @Override
    public void actualizarSaldo(Connection conn, int idCuenta, BigDecimal nuevoSaldo) {
        String sql = "UPDATE cuenta SET saldo = ? WHERE id_cuenta = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, nuevoSaldo);
            ps.setInt(2, idCuenta);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar saldo", e);
        }
    }

    private Cuenta mapear(ResultSet rs) throws SQLException {
        Cuenta c = new Cuenta(rs.getInt("id_cliente"), rs.getString("numero_cuenta"), rs.getString("tipo"));
        c.setIdCuenta(rs.getInt("id_cuenta"));
        c.setSaldo(rs.getBigDecimal("saldo"));
        c.setEstado(rs.getString("estado"));
        return c;
    }
}
