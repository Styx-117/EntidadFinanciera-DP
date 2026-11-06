package facilbank.patterns.state;

public class EstadoRechazado implements EstadoCredito {
    @Override
    public String nombre() { return "RECHAZADO"; }

    @Override
    public EstadoCredito evaluar() {
        throw new IllegalStateException("La solicitud ya fue rechazada");
    }

    @Override
    public EstadoCredito aprobar() {
        throw new IllegalStateException("Una solicitud rechazada no puede aprobarse");
    }

    @Override
    public EstadoCredito rechazar() {
        throw new IllegalStateException("La solicitud ya fue rechazada");
    }
}
