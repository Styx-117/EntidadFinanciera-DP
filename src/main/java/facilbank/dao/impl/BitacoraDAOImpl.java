package facilbank.dao.impl;

import facilbank.dao.BitacoraDAO;
import facilbank.dao.DAOException;
import facilbank.util.ConexionBD;
import java.sql.*;

public class BitacoraDAOImpl implements BitacoraDAO {
    @Override
    public void insertar(Integer idUsuario, String accion, String detalle) {
        String sql = "INSERT INTO bitacora (id_usuario, accion, detalle) VALUES (?, ?, ?)";
        String texto = detalle != null && detalle.length() > 300 ? detalle.substring(0, 300) : detalle;
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            if (idUsuario == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, idUsuario);
            ps.setString(2, accion);
            ps.setString(3, texto);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al registrar en la bitácora", e);
        }
    }
}
