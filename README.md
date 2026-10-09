# Banca Digital – Avance 1 (actor: Cliente)

Java 17 + Spring Boot (solo capa web) + JDBC puro + PostgreSQL. Vistas con Thymeleaf + Bootstrap.

## Funcionalidades del Cliente y su vista
| # | Funcionalidad | Ruta | Plantilla |
|---|---|---|---|
| 1 | Iniciar / cerrar sesión | `/login`, `/logout` | `login.html` |
| 2 | Consultar saldos | `/cliente/dashboard` | `dashboard.html` |
| 3 | Consultar movimientos | `/cliente/movimientos` | `movimientos.html` |
| 4 | Transferir entre cuentas | `/cliente/transferencia` | `transferencia.html` |
| 5 | Pagar servicios | `/cliente/pagos` | `pago_servicios.html` |
| 6 | Solicitar crédito | `/cliente/credito` | `credito.html` |

## Patrones aplicados
| Categoría | Patrón | Clase |
|---|---|---|
| Creacional | Singleton | `util.ConexionBD` |
| Creacional | Factory | `dao.factory.DAOFactory` |
| GRASP | Controlador | `controller.LoginController`, `ClienteController`, `OperacionController` |
| GRASP | Experto | `model.Cuenta` (saldo), `model.Credito` (cuota) |

## Puesta en marcha (IntelliJ IDEA)
1. `File > Open` y elegir la carpeta (IntelliJ detecta el `pom.xml` y descarga las dependencias).
2. Crear la BD y ejecutar el script:
   `createdb banca_digital` y luego `psql -d banca_digital -f sql/schema.sql`
   (o pegar el contenido de `sql/schema.sql` en pgAdmin/DBeaver).
3. Editar `src/main/resources/db.properties` con tu usuario y clave.
4. Ejecutar `banca.app.BancaDigitalApplication` (botón Run).
5. Abrir http://localhost:8080 -> usuario `cliente1`, clave `1234`
   (los datos de prueba se crean solos en el primer arranque).

Si un usuario se bloquea por 3 intentos fallidos:
`UPDATE usuario SET bloqueado = false, intentos_fallidos = 0 WHERE username = 'cliente1';`
