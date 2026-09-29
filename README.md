# Bitacora_Corte2_JuanGarzon

## Autor
Juan Garzón — DOSW Grupo 1

## Restaurante
**Sakura Sushi** — Barra de sushi con preparación por lotes y rolls armados a pedido.

**Concepto del restaurante:**
- Los rolls se preparan en **tandas de máximo 6 unidades** para mantener el ritmo de la barra
- Los ingredientes pueden agotarse y bloquean la creación de platos que los usen
- Los rolls personalizados se arman a pedido del cliente
- Flujo de estados del pedido: `RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO`

## Descripción
API REST que digitaliza la operación de Sakura Sushi: gestión de carta, pedidos, mesas, cuentas, reservas, ingredientes, parqueadero, tandas de rolls y reportes. Sin persistencia aún — todo en memoria con Streams. Construida con arquitectura MVC en capas, MapStruct, Lombok, JUnit 5, Mockito, JaCoCo, SonarQube y Springdoc OpenAPI.

## Funcionalidades

| # | Funcionalidad | Descripción |
|---|--------------|-------------|
| 1 | **Carta y Platos** | Rolls, sashimis, nigiris con disponibilidad |
| 2 | **Menú del Cliente** | Vista filtrada solo de platos disponibles |
| 3 | **Ingredientes** | Catálogo para personalización de rolls |
| 4 | **Pedidos** | Flujo completo con transiciones de estado |
| 5 | **Tandas** | **Preparación por lotes de máximo 6 rolls** |
| 6 | **Mesas** | Apertura y cierre de cuentas |
| 7 | **Cuentas** | Consolidación de pedidos y cierre |
| 8 | **Reservas** | Gestión con validación de conflictos y capacidad |
| 9 | **Parqueadero** | Entrada/salida con cálculo de cobro |
| 10 | **Reportes** | Consolidados con Streams |

## Arquitectura

La API sigue un patrón de **arquitectura en capas**, donde cada capa tiene una única responsabilidad:

Cliente (Swagger/Postman)
      ↓
Controller    → Recibe HTTP, valida @Valid, delega, responde
      ↓
Service       → Orquesta, aplica lógica, usa Streams
      ↓
Validator     → Reglas de negocio puras
      ↓
Dominio       → Objetos con comportamiento (en memoria)
      ↓
Mapper        → Traduce DTO ↔ Dominio (MapStruct)
      ↓
Cliente (JSON Response)

### Responsabilidades por capa

| Capa | Qué hace | Qué NO hace |
|------|----------|-------------|
| **Controller** | Recibe HTTP, delega, responde | No tiene lógica de negocio |
| **Service** | Orquesta, aplica reglas | No conoce HTTP, no convierte DTOs |
| **Validator** | Aplica reglas de negocio puras | No conoce HTTP ni DTOs |
| **Mapper** | Traduce DTO ↔ Dominio | No tiene lógica de negocio |
| **Domain** | Objetos con comportamiento propio | No conoce Spring ni JPA |
| **ExceptionHandler** | Respuestas uniformes de error | No conoce reglas de negocio |

### Estructura de paquetes

```
com.restaurante
├── controller/           → Endpoints REST + GlobalExceptionHandler
├── service/              → Interfaces + Implementaciones
├── validator/            → Reglas de negocio
├── mapper/               → MapStruct
├── util/                 → Utilidades compartidas
├── model/
│   ├── domain/           → Entidades puras + enums
│   └── dto/
│       ├── request/      → DTOs de entrada (@Valid)
│       └── response/     → DTOs de salida
├── exception/            → Excepciones propias
└── config/               → Swagger + CORS
```

## 🎯 Funcionalidades

| # | Funcionalidad | Descripción |
|---|--------------|-------------|
| 1 | **Carta y Platos** | Rolls, sashimis, nigiris con disponibilidad |
| 2 | **Menú del Cliente** | Vista filtrada solo de platos disponibles |
| 3 | **Ingredientes** | Catálogo para personalización de rolls |
| 4 | **Pedidos** | Flujo completo con transiciones de estado |
| 5 | **Tandas** | **Preparación por lotes de máximo 6 rolls** |
| 6 | **Mesas** | Apertura y cierre de cuentas |
| 7 | **Cuentas** | Consolidación de pedidos y cierre |
| 8 | **Reservas** | Validación de conflictos de horario y capacidad |
| 9 | **Parqueadero** | Entrada/salida con cálculo de cobro |
| 10 | **Reportes** | Consolidados con Streams |

## 📊 Tabla de Endpoints

### PlatoController — `/api/v1/platos`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/platos` | 200 | — |
| GET | `/api/v1/platos/{id}` | 200 | 404 |
| POST | `/api/v1/platos` | 201 | 400, 409 |
| PUT | `/api/v1/platos/{id}` | 200 | 400, 404, 409 |
| PATCH | `/api/v1/platos/{id}/disponible` | 200 | 404 |
| DELETE | `/api/v1/platos/{id}` | 204 | 404, 422 |

### MenuController — `/api/v1/menu`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/menu` | 200 | — |
| GET | `/api/v1/menu/{id}` | 200 | 404 |
| GET | `/api/v1/menu/categoria/{cat}` | 200 | — |

### IngredienteController — `/api/v1/ingredientes`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/ingredientes` | 200 | — |
| GET | `/api/v1/ingredientes/disponibles` | 200 | — |
| GET | `/api/v1/ingredientes/tipo/{tipo}` | 200 | — |
| POST | `/api/v1/ingredientes` | 201 | 400, 409 |
| PATCH | `/api/v1/ingredientes/{id}/disponible` | 200 | 404 |
| DELETE | `/api/v1/ingredientes/{id}` | 204 | 404 |

### PedidoController — `/api/v1/pedidos`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/pedidos` | 200 | — |
| GET | `/api/v1/pedidos/activos` | 200 | — |
| GET | `/api/v1/pedidos/mesa/{idMesa}` | 200 | — |
| GET | `/api/v1/pedidos/{id}` | 200 | 404 |
| POST | `/api/v1/pedidos` | 201 | 400, 404, 422 |
| POST | `/api/v1/pedidos/{id}/items` | 200 | 404, 422 |
| PUT | `/api/v1/pedidos/{id}/items/{idPlato}` | 200 | 404, 422 |
| DELETE | `/api/v1/pedidos/{id}/items/{idPlato}` | 200 | 404, 422 |
| PATCH | `/api/v1/pedidos/{id}/estado` | 200 | 404, 422 |
| DELETE | `/api/v1/pedidos/{id}` | 204 | 404, 422 |

### TandaController — `/api/v1/tandas`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/tandas` | 200 | — |
| POST | `/api/v1/tandas` | 201 | 404, 422 |

**Regla característica:** máximo 6 rolls por tanda. Solo acepta platos de categoría `ROLL`.

### MesaController — `/api/v1/mesas`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/mesas` | 200 | — |
| GET | `/api/v1/mesas/disponibles` | 200 | — |
| GET | `/api/v1/mesas/{id}` | 200 | 404 |
| POST | `/api/v1/mesas` | 201 | 400, 409 |
| PATCH | `/api/v1/mesas/{id}/abrir` | 200 | 404, 422 |
| PATCH | `/api/v1/mesas/{id}/cerrar` | 200 | 404, 422 |
| DELETE | `/api/v1/mesas/{id}` | 204 | 404, 422 |

### CuentaController — `/api/v1/cuentas`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/cuentas` | 200 | — |
| GET | `/api/v1/cuentas/{id}` | 200 | 404 |
| GET | `/api/v1/cuentas/mesa/{idMesa}` | 200 | 404 |
| POST | `/api/v1/cuentas/mesa/{idMesa}` | 201 | 404, 422 |
| POST | `/api/v1/cuentas/{id}/pedidos/{idPedido}` | 200 | 404, 422 |
| PATCH | `/api/v1/cuentas/{id}/cerrar` | 200 | 404, 422 |

### ReservaController — `/api/v1/reservas`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/reservas` | 200 | — |
| GET | `/api/v1/reservas/vigentes` | 200 | — |
| GET | `/api/v1/reservas/cliente/{cliente}` | 200 | — |
| GET | `/api/v1/reservas/{id}` | 200 | 404 |
| POST | `/api/v1/reservas` | 201 | 400, 404, 409, 422 |
| PATCH | `/api/v1/reservas/{id}/estado` | 200 | 404, 422 |
| DELETE | `/api/v1/reservas/{id}` | 204 | 404, 422 |

### ParqueaderoController — `/api/v1/parqueadero`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| POST | `/api/v1/parqueadero/entrada` | 201 | 400, 409, 422 |
| POST | `/api/v1/parqueadero/salida/{placa}` | 200 | 404 |
| GET | `/api/v1/parqueadero/activos` | 200 | — |
| GET | `/api/v1/parqueadero/estado` | 200 | — |
| GET | `/api/v1/parqueadero/placa/{placa}` | 200 | — |

### ReporteController — `/api/v1/reportes`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/reportes/resumen` | 200 | — |
| GET | `/api/v1/reportes/ingresos-por-categoria` | 200 | — |
| GET | `/api/v1/reportes/platos-populares?top=N` | 200 | — |

## Reglas de Negocio de Sakura Sushi

### Reglas específicas del concepto japonés

1. **Tandas de máximo 6 rolls** — Los rolls de la barra se preparan en lotes de máximo 6 unidades
2. **Solo ROLLs en una tanda** — Un té verde o un postre no pueden ir en una tanda de rolls
3. **Rolls disponibles** — Si un roll está agotado, no se puede incluir en la tanda
4. **Rolls existentes** — Cada ID en la tanda debe existir en la carta

### Reglas generales del restaurante

5. **Pedido requiere mesa con cuenta abierta** — No se pueden crear pedidos en mesas sin cuenta
6. **Platos disponibles** — No se pueden pedir platos agotados
7. **Modificar solo en RECIBIDO** — Una vez el pedido pasa a cocina, no se modifica
8. **Transiciones de estado válidas** — `RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO`
9. **Cuenta no cierra con pedidos activos** — Todos los pedidos deben estar ENTREGADOS o CANCELADOS
10. **Una cuenta por mesa** — No se puede abrir una segunda cuenta en una mesa ya ocupada
11. **Mesa no se elimina con cuenta abierta**
12. **Plato no se elimina con pedidos activos**
13. **Conflicto de reservas** — No pueden solaparse reservas vigentes en la misma mesa
14. **Comensales ≤ capacidad de mesa** — La reserva no puede exceder la capacidad
15. **Precio congelado** — El `ItemPedido` congela el precio al momento del pedido

## Cómo ejecutar

### Compilar y empaquetar
```bash
mvn clean package -DskipTests
```

### Ejecutar la aplicación
```bash
java -jar target/bitacora-1.0.0-SNAPSHOT.jar
```

Abrir en el navegador: **http://localhost:8080/swagger-ui.html**

### Ejecutar tests
```bash
mvn clean test
```

### Generar reporte de cobertura (JaCoCo)
```bash
mvn clean test
start target/site/jacoco/index.html
```

### Análisis estático (SonarQube)
```bash
mvn clean verify sonar:sonar "-Dsonar.login=TU_TOKEN"
```

## Orden para crear objetos en Swagger

**El orden importa** porque hay dependencias entre objetos. Sigue este orden:

### Ingredientes

**POST** `/api/v1/ingredientes`

```json
{ "nombre": "Salmón fresco", "precio": 3000, "tipo": "PESCADO" }
```

```json
{ "nombre": "Aguacate", "precio": 1500, "tipo": "VERDURA" }
```

```json
{ "nombre": "Queso crema", "precio": 2000, "tipo": "QUESO" }
```

### Platos

**POST** `/api/v1/platos`

**Rolls** (categoría `ROLL`):

```json
{
  "nombre": "California Roll",
  "precio": 18000,
  "categoria": "ROLL",
  "descripcion": "Cangrejo, aguacate y pepino"
}
```

```json
{
  "nombre": "Spicy Tuna Roll",
  "precio": 22000,
  "categoria": "ROLL",
  "descripcion": "Atún picante"
}
```

**Otros platos** (para probar el rechazo de tandas):

```json
{
  "nombre": "Té verde",
  "precio": 5000,
  "categoria": "BEBIDA",
  "descripcion": "Té verde caliente"
}
```

### Mesas

**POST** `/api/v1/mesas`

```json
{ "numero": 1, "capacidad": 4 }
```

```json
{ "numero": 2, "capacidad": 2 }
```

###  Abrir Cuenta

**Obligatorio antes de crear pedidos.**

**POST** `/api/v1/cuentas/mesa/1`

Sin body.

### Crear Pedido

**POST** `/api/v1/pedidos`

```json
{
  "idMesa": 1,
  "idPlatos": [1, 2],
  "notas": "Sin wasabi"
}
```

###  Crear Tanda

**POST** `/api/v1/tandas`

Body: array de IDs de rolls

```json
[1, 2]
```

###  Flujo del pedido (cocina)

```
PATCH /api/v1/pedidos/1/estado?nuevoEstado=EN_PREPARACION
PATCH /api/v1/pedidos/1/estado?nuevoEstado=LISTO
PATCH /api/v1/pedidos/1/estado?nuevoEstado=ENTREGADO
```

###  Cerrar Cuenta

```
POST /api/v1/cuentas/1/pedidos/1
PATCH /api/v1/cuentas/1/cerrar
```

###  Reservas

**POST** `/api/v1/reservas`

```json
{
  "idMesa": 2,
  "cliente": "Juan Pérez",
  "fechaHora": "2026-12-31T20:00:00",
  "comensales": 2
}
```

### Parqueadero

**POST** `/api/v1/parqueadero/entrada`

```json
{ "placa": "ABC-123" }
```

### Reportes

**GET** `/api/v1/reportes/resumen`

## 🧪 Pruebas de reglas de negocio

### Prueba 1 — Tanda con más de 6 rolls

**POST** `/api/v1/tandas`

```json
[1, 2, 1, 2, 1, 2, 1]
```

**Respuesta esperada — `422 Unprocessable Entity`:**
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Una tanda de Sakura Sushi no puede tener más de 6 rolls. Recibidos: 7"
}
```

### Prueba 2 — Tanda con plato que no es ROLL

**POST** `/api/v1/tandas`

```json
[1, 3]
```

(siendo 3 la bebida)

**Respuesta esperada — `422`:**
```json
{
  "status": 422,
  "message": "Solo se pueden agrupar rolls en una tanda. Los siguientes no son rolls: [Té verde (BEBIDA)]"
}
```

### Prueba 3 — Transición de estado inválida

**PATCH** `/api/v1/pedidos/1/estado?nuevoEstado=RECIBIDO`

**Respuesta esperada — `422`:**
```json
{
  "status": 422,
  "message": "No se puede pasar de ENTREGADO a RECIBIDO"
}
```

### Prueba 4 — Conflicto de reserva

**POST** `/api/v1/reservas` con misma mesa y horario cercano

**Respuesta esperada — `409 Conflict`:**
```json
{
  "status": 409,
  "message": "Ya existe una reserva vigente para la mesa 2 cerca de ese horario"
}
```

### Prueba 5 — Comensales exceden capacidad

**POST** `/api/v1/reservas` en mesa de capacidad 2 con 10 comensales

**Respuesta esperada — `422`:**
```json
{
  "status": 422,
  "message": "La reserva tiene 10 comensales, pero la mesa 2 solo tiene capacidad para 2"
}
```

## 📸 Evidencias

### Swagger UI — Todos los grupos
![Swagger UI](docs/images/Swagger.png)

### Cobertura de pruebas — JaCoCo
![Cobertura](docs/images/Cobertura.png)

### Tests JUnit + Mockito
![Tests](docs/images/test.png)

### Análisis estático — SonarQube
![SonarQube Overview](docs/images/SonarQube.png)

## 🛠️ Stack Técnico

| Componente | Versión |
|-----------|---------|
| Java | 24 |
| Spring Boot | 3.4.1 |
| Maven | 3.9+ |
| Lombok | 1.18.48 |
| MapStruct | 1.6.2 |
| springdoc-openapi | 2.8.4 |
| JUnit | 5 |
| Mockito | 5.x |
| JaCoCo | 0.8.13 |
| SonarQube | 9.9 LTS |