package facilbank.model;

import java.time.LocalDateTime;

public class Notificacion {
    private int idNotificacion;
    private int idCliente;
    private String mensaje;
    private boolean leida;
    private LocalDateTime fecha;

    public Notificacion() { }

    public Notificacion(int idCliente, String mensaje) {
        this.idCliente = idCliente;
        this.mensaje = mensaje;
    }

    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
