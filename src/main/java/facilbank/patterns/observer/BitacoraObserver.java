package facilbank.patterns.observer;

import facilbank.dao.BitacoraDAO;
import facilbank.dao.factory.DAOFactory;
import facilbank.model.Credito;

public class BitacoraObserver implements CreditoObserver {
    private final BitacoraDAO bitacoraDAO = DAOFactory.getBitacoraDAO();

    @Override
    public void actualizar(Credito credito, String estadoAnterior) {
        String detalle = "Crédito #" + credito.getIdCredito() + " de " + credito.getClienteNombre()
                + ": " + estadoAnterior + " -> " + credito.getEstado();
        bitacoraDAO.insertar(credito.getIdAsesor(), "CREDITO_" + credito.getEstado(), detalle);
    }
}
