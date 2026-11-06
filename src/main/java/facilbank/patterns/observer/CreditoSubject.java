package facilbank.patterns.observer;

import facilbank.model.Credito;
import java.util.ArrayList;
import java.util.List;

public class CreditoSubject {
    private final List<CreditoObserver> observadores = new ArrayList<>();

    public void agregar(CreditoObserver observador) {
        observadores.add(observador);
    }

    public void quitar(CreditoObserver observador) {
        observadores.remove(observador);
    }

    public void notificar(Credito credito, String estadoAnterior) {
        for (CreditoObserver observador : observadores) {
            try {
                observador.actualizar(credito, estadoAnterior);
            } catch (RuntimeException e) {
                System.err.println("Observador con error: " + e.getMessage());
            }
        }
    }
}
