package facilbank.dao.impl;

import facilbank.dao.CreditoDAO;
import facilbank.dao.DAOException;
import facilbank.model.Credito;
import facilbank.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreditoDAOImpl implements CreditoDAO {
    private static final String BASE =
            "SELECT c.id_credito, c.id_cliente, c.id_asesor, c.monto, c.plazo_meses, c.tasa_anual, c.estado, "
          + "c.observaciones, c.fecha_solicitud, c.fecha_evaluacion, "
          + "cl.nombres || ' ' || cl.apellidos AS cliente_nombre, cl.dni "
          + "FROM credito c JOIN cliente cl ON cl.id_cliente = c.id_cliente ";

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
    public Optional<Credito> buscarPorId(int idCredito) {
        List<Credito> lista = consultar(BASE + "WHERE c.id_credito = ?", idCredito);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Credito> listarPorCliente(int idCliente) {
        return consultar(BASE + "WHERE c.id_cliente = ? ORDER BY c.fecha_solicitud DESC, c.id_credito DESC", idCliente);
    }

    @Override
    public List<Credito> listarPendientes() {
        return consultar(BASE + "WHERE c.estado IN ('SOLICITADO','EN_EVALUACION') "
                + "ORDER BY c.fecha_solicitud ASC, c.id_credito ASC", null);
    }

    @Override
    public List<Credito> listarResueltas() {
        return consultar(BASE + "WHERE c.estado IN ('APROBADO','RECHAZADO') "
                + "ORDER BY c.fecha_evaluacion DESC NULLS LAST, c.id_credito DESC", null);
    }

    @Override
    public void actualizarEvaluacion(Credito c) {
        String sql = "UPDATE credito SET estado = ?, observaciones = ?, id_asesor = ?, fecha_evaluacion = ? "
                   + "WHERE id_credito = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, c.getEstado());
            ps.setString(2, c.getObservaciones());
            if (c.getIdAsesor() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, c.getIdAsesor());
            if (c.getFechaEvaluacion() == null) ps.setNull(4, Types.TIMESTAMP);
            else ps.setTimestamp(4, Timestamp.valueOf(c.getFechaEvaluacion()));
            ps.setInt(5, c.getIdCredito());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar la evaluación", e);
        }
    }

    private List<Credito> consultar(String sql, Integer parametro) {
        List<Credito> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            if (parametro != null) ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar créditos", e);
        }
        return lista;
    }

    private Credito mapear(ResultSet rs) throws SQLException {
        Credito c = new Credito();
        c.setIdCredito(rs.getInt("id_credito"));
        c.setIdCliente(rs.getInt("id_cliente"));
        int asesor = rs.getInt("id_asesor");
        c.setIdAsesor(rs.wasNull() ? null : asesor);
        c.setMonto(rs.getBigDecimal("monto"));
        c.setPlazoMeses(rs.getInt("plazo_meses"));
        c.setTasaAnual(rs.getBigDecimal("tasa_anual"));
        c.setEstado(rs.getString("estado"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setFechaSolicitud(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
        Timestamp evaluacion = rs.getTimestamp("fecha_evaluacion");
        c.setFechaEvaluacion(evaluacion == null ? null : evaluacion.toLocalDateTime());
        c.setClienteNombre(rs.getString("cliente_nombre"));
        c.setClienteDni(rs.getString("dni"));
        return c;
    }
}
