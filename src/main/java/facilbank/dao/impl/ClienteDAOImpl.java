package facilbank.dao.impl;

import facilbank.dao.ClienteDAO;
import facilbank.dao.DAOException;
import facilbank.model.Cliente;
import facilbank.util.ConexionBD;
import java.sql.*;
import java.util.Optional;

public class ClienteDAOImpl implements ClienteDAO {
    private static final String COLS = "id_cliente, id_usuario, dni, nombres, apellidos, email, telefono";

    @Override
    public void insertar(Cliente c) {
        String sql = "INSERT INTO cliente (id_usuario, dni, nombres, apellidos, email, telefono) "
                   + "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, new String[]{"id_cliente"})) {
            ps.setInt(1, c.getIdUsuario());
            ps.setString(2, c.getDni());
            ps.setString(3, c.getNombres());
            ps.setString(4, c.getApellidos());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getTelefono());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCliente(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al insertar cliente", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorUsuario(int idUsuario) {
        return buscarUno("SELECT " + COLS + " FROM cliente WHERE id_usuario = ?", idUsuario);
    }

    @Override
    public Optional<Cliente> buscarPorId(int idCliente) {
        return buscarUno("SELECT " + COLS + " FROM cliente WHERE id_cliente = ?", idCliente);
    }

    private Optional<Cliente> buscarUno(String sql, int parametro) {
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Cliente c = new Cliente(rs.getInt("id_usuario"), rs.getString("dni"),
                        rs.getString("nombres"), rs.getString("apellidos"),
                        rs.getString("email"), rs.getString("telefono"));
                c.setIdCliente(rs.getInt("id_cliente"));
                return Optional.of(c);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar cliente", e);
        }
    }
}
