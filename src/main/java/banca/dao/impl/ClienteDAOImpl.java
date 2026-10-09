package banca.dao.impl;

import banca.dao.ClienteDAO;
import banca.dao.DAOException;
import banca.model.Cliente;
import banca.util.ConexionBD;
import java.sql.*;
import java.util.Optional;

public class ClienteDAOImpl implements ClienteDAO {

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
        String sql = "SELECT id_cliente, id_usuario, dni, nombres, apellidos, email, telefono "
                   + "FROM cliente WHERE id_usuario = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
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
