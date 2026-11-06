# FacilBank – Avance 2

Actores: Cliente (Avance 1) y Asesor de Crédito (Avance 2).

## Ejecución
1. Abrir la carpeta en IntelliJ IDEA (File > Open) y esperar a Maven.
2. Base de datos nueva: crear `banca_digital` y ejecutar `sql/schema.sql`.
   Base del Avance 1 ya creada: ejecutar `sql/migracion_desde_avance1.sql`.
3. Poner usuario y clave en `src/main/resources/db.properties`.
4. Ejecutar `facilbank.app.FacilBankApplication` y abrir http://localhost:8080

Usuarios de prueba (clave 1234): cliente1, cliente2 y asesor1.

## Patrones
| Categoría | Patrón | Clase |
|---|---|---|
| Creacional | Singleton | `util.ConexionBD` |
| Creacional | Factory | `dao.factory.DAOFactory` |
| Estructural | Facade | `patterns.facade.EvaluacionCreditoFacade` |
| Comportamiento | State | `patterns.state.EstadoCredito` y sus 4 estados |
| Comportamiento | Observer | `patterns.observer.CreditoSubject`, `NotificacionClienteObserver`, `BitacoraObserver` |
| GRASP | Controlador | `LoginController`, `ClienteController`, `OperacionController`, `AsesorController` |
| GRASP | Experto | `Cuenta`, `Credito`, `Movimiento` |
