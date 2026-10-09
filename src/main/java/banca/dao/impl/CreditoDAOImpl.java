package banca.dao.impl;

import banca.dao.CreditoDAO;
import banca.dao.DAOException;
import banca.model.Credito;
import banca.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreditoDAOImpl implements CreditoDAO {

    @Override
    public void insertar(Credito c) {
        String sql = "INSERT INTO credito (id_cliente, monto, plazo_meses, tasa_anual, estado) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, new String[]{"id_credito"})) {
            ps.setInt(1, c.getIdCliente());
            ps.setBigDecimal(2, c.getMonto());
            ps.setInt(3, c.getPlazoMeses());
            ps.setBigDecimal(4, c.getTasaAnual());
            ps.setString(5, c.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCredito(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar el crédito", e);
        }
    }

    @Override
    public List<Credito> listarPorCliente(int idCliente) {
        String sql = "SELECT id_credito, id_cliente, monto, plazo_meses, tasa_anual, estado, "
                   + "observaciones, fecha_solicitud FROM credito WHERE id_cliente = ? "
                   + "ORDER BY fecha_solicitud DESC, id_credito DESC";
        List<Credito> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Credito c = new Credito();
                    c.setIdCredito(rs.getInt("id_credito"));
                    c.setIdCliente(rs.getInt("id_cliente"));
                    c.setMonto(rs.getBigDecimal("monto"));
                    c.setPlazoMeses(rs.getInt("plazo_meses"));
                    c.setTasaAnual(rs.getBigDecimal("tasa_anual"));
                    c.setEstado(rs.getString("estado"));
                    c.setObservaciones(rs.getString("observaciones"));
                    c.setFechaSolicitud(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar créditos", e);
        }
        return lista;
    }
}
