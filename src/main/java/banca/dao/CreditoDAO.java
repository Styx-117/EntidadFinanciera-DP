package banca.dao;

import banca.model.Credito;
import java.util.List;

public interface CreditoDAO {
    void insertar(Credito credito);
    List<Credito> listarPorCliente(int idCliente);
}
