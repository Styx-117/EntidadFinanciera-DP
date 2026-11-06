package facilbank.dao;

import facilbank.model.Notificacion;
import java.util.List;

public interface NotificacionDAO {
    void insertar(Notificacion notificacion);
    List<Notificacion> listarNoLeidas(int idCliente);
    void marcarLeidas(int idCliente);
}
