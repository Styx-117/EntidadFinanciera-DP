package facilbank.service;

import facilbank.dao.NotificacionDAO;
import facilbank.dao.factory.DAOFactory;
import facilbank.model.Notificacion;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {
    private final NotificacionDAO notificacionDAO = DAOFactory.getNotificacionDAO();

    public List<Notificacion> listarNoLeidas(int idCliente) {
        return notificacionDAO.listarNoLeidas(idCliente);
    }

    public void marcarLeidas(int idCliente) {
        notificacionDAO.marcarLeidas(idCliente);
    }
}
