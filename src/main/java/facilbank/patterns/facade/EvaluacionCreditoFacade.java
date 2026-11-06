package facilbank.patterns.facade;

import facilbank.dao.CreditoDAO;
import facilbank.dao.factory.DAOFactory;
import facilbank.model.Credito;
import facilbank.patterns.observer.BitacoraObserver;
import facilbank.patterns.observer.CreditoSubject;
import facilbank.patterns.observer.NotificacionClienteObserver;
import facilbank.service.NegocioException;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class EvaluacionCreditoFacade {
    private final CreditoDAO creditoDAO = DAOFactory.getCreditoDAO();
    private final CreditoSubject subject = new CreditoSubject();

    public EvaluacionCreditoFacade() {
        subject.agregar(new NotificacionClienteObserver());
        subject.agregar(new BitacoraObserver());
    }

    public Credito iniciarEvaluacion(int idCredito, int idAsesor, String observaciones) {
        return transicionar(idCredito, idAsesor, observaciones, Credito::iniciarEvaluacion);
    }

    public Credito aprobar(int idCredito, int idAsesor, String observaciones) {
        return transicionar(idCredito, idAsesor, observaciones, Credito::aprobar);
    }

    public Credito rechazar(int idCredito, int idAsesor, String observaciones) {
        if (observaciones == null || observaciones.isBlank()) {
            throw new NegocioException("Indique el motivo del rechazo");
        }
        return transicionar(idCredito, idAsesor, observaciones, Credito::rechazar);
    }

    public Credito registrarObservaciones(int idCredito, int idAsesor, String observaciones) {
        if (observaciones == null || observaciones.isBlank()) {
            throw new NegocioException("Escriba la observación antes de guardar");
        }
        Credito credito = cargar(idCredito);
        if (!"EN_EVALUACION".equals(credito.getEstado())) {
            throw new NegocioException("Solo se registran observaciones en solicitudes en evaluación");
        }
        credito.setObservaciones(observaciones.trim());
        credito.setIdAsesor(idAsesor);
        creditoDAO.actualizarEvaluacion(credito);
        return credito;
    }

    private Credito transicionar(int idCredito, int idAsesor, String observaciones, Consumer<Credito> transicion) {
        Credito credito = cargar(idCredito);
        String anterior = credito.getEstado();
        try {
            transicion.accept(credito);
        } catch (IllegalStateException e) {
            throw new NegocioException(e.getMessage());
        }
        credito.setIdAsesor(idAsesor);
        if (observaciones != null && !observaciones.isBlank()) {
            credito.setObservaciones(observaciones.trim());
        }
        credito.setFechaEvaluacion(LocalDateTime.now());
        creditoDAO.actualizarEvaluacion(credito);
        subject.notificar(credito, anterior);
        return credito;
    }

    private Credito cargar(int idCredito) {
        return creditoDAO.buscarPorId(idCredito)
                .orElseThrow(() -> new NegocioException("La solicitud no existe"));
    }
}
