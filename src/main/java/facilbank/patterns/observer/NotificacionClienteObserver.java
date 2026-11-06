package facilbank.patterns.observer;

import facilbank.dao.NotificacionDAO;
import facilbank.dao.factory.DAOFactory;
import facilbank.model.Credito;
import facilbank.model.Notificacion;
import java.util.Locale;

public class NotificacionClienteObserver implements CreditoObserver {
    private final NotificacionDAO notificacionDAO = DAOFactory.getNotificacionDAO();

    @Override
    public void actualizar(Credito credito, String estadoAnterior) {
        String monto = String.format(Locale.US, "%,.2f", credito.getMonto());
        String mensaje;
        switch (credito.getEstado()) {
            case "EN_EVALUACION":
                mensaje = "Tu solicitud de crédito de S/ " + monto + " ya está en evaluación.";
                break;
            case "APROBADO":
                mensaje = "¡Tu crédito de S/ " + monto + " fue aprobado! Tu cuota aproximada es de S/ "
                        + String.format(Locale.US, "%,.2f", credito.cuotaMensual()) + ".";
                break;
            case "RECHAZADO":
                mensaje = "Tu solicitud de crédito de S/ " + monto + " no fue aprobada. Motivo: "
                        + credito.getObservaciones();
                break;
            default:
                return;
        }
        notificacionDAO.insertar(new Notificacion(credito.getIdCliente(), mensaje));
    }
}
