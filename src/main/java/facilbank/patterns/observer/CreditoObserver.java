package facilbank.patterns.observer;

import facilbank.model.Credito;

public interface CreditoObserver {
    void actualizar(Credito credito, String estadoAnterior);
}
