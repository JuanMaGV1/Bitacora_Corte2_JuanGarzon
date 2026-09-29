# Bitacora_Corte2_JuanGarzon

## Autor
Juan Garzón — DOSW Grupo 1

## Restaurante: Sakura Sushi
**Barra de sushi con preparación por lotes y rolls armados a pedido.**

**Concepto del restaurante:**
- Los rolls se preparan en **tandas de máximo 6 unidades** para mantener el ritmo de la barra
- Los ingredientes agotados bloquean la creación de rolls que los usen
- Los pedidos se asocian automáticamente a la cuenta abierta de la mesa
- Flujo de estados: `RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO`

## Descripción
API REST que digitaliza la operación de Sakura Sushi: gestión de carta, pedidos,
mesas, cuentas, reservas, ingredientes, parqueadero, tandas de rolls y reportes.

**Arquitectura híbrida de persistencia:**
- **PostgreSQL + JPA** → datos relacionales (platos, mesas, pedidos, cuentas, etc.)
- **MongoDB** → datos de estructura flexible (tandas de preparación)

Construida con arquitectura MVC en capas, MapStruct, Lombok, JUnit 5, Mockito,
JaCoCo, SonarQube y Springdoc OpenAPI.

## Arquitectura

Cliente (Swagger/Postman)
   ↓
Controller    → Recibe HTTP, valida @Valid, delega, responde
   ↓
Service       → Orquesta, aplica lógica, usa Streams
   ↓
Validator     → Reglas de negocio puras
   ↓
Dominio       → Objetos con comportamiento (sin saber de BD)
   ↓
Mapper        → Traduce DTO ↔ Dominio ↔ Entity/Document
   ↓
Repository    → JPA (PostgreSQL) o Mongo (MongoDB)
   ↓
Cliente (JSON Response)

### Responsabilidades por capa

| Capa | Qué hace | Qué NO hace |
|------|----------|-------------|
| **Controller** | Recibe HTTP, delega, responde | No tiene lógica de negocio |
| **Service** | Orquesta, aplica reglas | No conoce HTTP, no convierte DTOs |
| **Validator** | Aplica reglas de negocio puras | No conoce HTTP ni DTOs |
| **Mapper** | Traduce entre 3 capas | No tiene lógica de negocio |
| **Dominio** | Objetos con comportamiento propio | No conoce Spring ni JPA |
| **Entity/Document** | Estructura de persistencia | No tiene lógica de negocio |
| **Repository** | Acceso a datos | No conoce HTTP |
| **ExceptionHandler** | Respuestas uniformes | No conoce reglas |

### El Mapper tiene 3 capas

RequestDTO ←→ Dominio ←→ Entity (PostgreSQL)
                  ↕
              Document (MongoDB)

- `PlatoMapper` → RequestDTO ↔ Dominio (usado por el Controller)
- `PlatoEntityMapper` → Dominio ↔ PlatoEntity (usado por el Service)
- `TandaDocumentMapper` → Dominio/DTO ↔ TandaDocument (usado por el Service de Tanda)

### Estructura de paquetes
com.restaurante
├── controller/           → Endpoints REST + GlobalExceptionHandler
├── service/              → Interfaces + Implementaciones
├── validator/            → Reglas de negocio
├── mapper/               → MapStruct (presentación + persistencia)
├── util/                 → Utilidades compartidas
├── model/
│   ├── domain/           → Entidades puras + enums
│   └── dto/
│       ├── request/      → DTOs de entrada (@Valid)
│       └── response/     → DTOs de salida
├── persistence/          → Clases de infraestructura de datos
│   ├── entity/           → Entidades JPA (@Entity) — PostgreSQL
│   └── document/         → Documentos Mongo (@Document) — MongoDB
├── repository/           → JpaRepository + MongoRepository
├── exception/            → Excepciones propias
└── config/               → Swagger + CORS

## Funcionalidades

| # | Funcionalidad | Descripción | Persistencia |
|---|--------------|-------------|:------------:|
| 1 | **Carta y Platos** | Rolls, sashimis, nigiris con disponibilidad | PostgreSQL |
| 2 | **Menú del Cliente** | Vista filtrada solo de platos disponibles | — |
| 3 | **Ingredientes** | Catálogo para personalización de rolls | PostgreSQL |
| 4 | **Pedidos** | Flujo completo con transiciones de estado | PostgreSQL |
| 5 | **Tandas** | Preparación por lotes de máximo 6 rolls | **MongoDB** |
| 6 | **Mesas** | Apertura y cierre de cuentas | PostgreSQL |
| 7 | **Cuentas** | Consolidación automática de pedidos y cierre | PostgreSQL |
| 8 | **Reservas** | Validación de conflictos de horario y capacidad | PostgreSQL |
| 9 | **Parqueadero** | Entrada/salida con cálculo de cobro | PostgreSQL |
| 10 | **Reportes** | Consolidados con Streams | — |

## Tabla de Endpoints

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

**Automatización:** al crear un pedido, se asocia automáticamente a la cuenta abierta de la mesa.

### TandaController — `/api/v1/tandas`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/tandas` | 200 | — |
| POST | `/api/v1/tandas` | 201 | 404, 422 |

**Persistencia:** MongoDB (documentos con IDs de rolls embebidos).

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
| POST | `/api/v1/parqueadero/entrada` | 201 | 400, 409 |
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

## Reglas de Negocio

### Reglas específicas de Sakura Sushi

1. **Tandas de máximo 6 rolls** — Los rolls se preparan en lotes de máximo 6 unidades
2. **Solo ROLLs en una tanda** — Un té verde o un postre no pueden ir en una tanda
3. **Rolls disponibles** — Si un roll está agotado, no se puede incluir
4. **Rolls existentes** — Cada ID en la tanda debe existir en la carta
5. **Auto-asociación de pedido a cuenta** — Al crear un pedido, se agrega automáticamente a la cuenta abierta de la mesa

### Reglas generales

6. **Pedido requiere mesa con cuenta abierta**
7. **Platos disponibles** — No se pueden pedir platos agotados
8. **Modificar solo en RECIBIDO**
9. **Transiciones de estado válidas** — `RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO`
10. **Cuenta no cierra con pedidos activos**
11. **Una cuenta por mesa**
12. **Mesa no se elimina con cuenta abierta**
13. **Plato no se elimina con pedidos activos**
14. **Conflicto de reservas** — No pueden solaparse reservas vigentes en la misma mesa
15. **Comensales ≤ capacidad de mesa**
16. **Precio congelado** — El `ItemPedido` congela el precio al momento del pedido

## Cómo ejecutar en OTRO PC (configuración)

### A. Cambios en `application.yml`

**Ajusta según tu entorno:**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sakura_sushi   # ← tu host/puerto/BD
    username: postgres                                    # ← tu usuario
    password: TU_PASSWORD_AQUI                            # ← tu password
    driver-class-name: org.postgresql.Driver

  data:
    mongodb:
      uri: mongodb://localhost:27017/sakura_sushi_nosql   # ← tu host/puerto/BD
```

**Si PostgreSQL está en otro host (ej: servidor remoto):**

```yaml
url: jdbc:postgresql://192.168.1.100:5432/sakura_sushi
username: mi_usuario
password: mi_password
```

**Si MongoDB tiene autenticación:**

```yaml
uri: mongodb://usuario:password@localhost:27017/sakura_sushi_nosql?authSource=admin
```

### B. Pasos en el nuevo PC

```bash
# 1. Clonar
git clone https://github.com/TU_USUARIO/Bitacora_Corte2_JuanGarzon.git
cd Bitacora_Corte2_JuanGarzon/bitacora

# 2. Crear BD en PostgreSQL (una sola vez)
psql -U postgres -c "CREATE DATABASE sakura_sushi;"

# 3. Verificar servicios
Get-Service MongoDB           # Debe estar Running
Get-Service postgresql-x64-16 # Debe estar Running

# 4. Editar application.yml con tu password

# 5. Compilar y ejecutar
mvn clean package -DskipTests
java -jar target/bitacora-1.0.0-SNAPSHOT.jar

# 6. Abrir Swagger
# http://localhost:8080/swagger-ui.html
```

## Guía paso a paso — Cómo crear objetos en Swagger

**Sigue este orden estricto** porque hay dependencias entre dominios.

### Paso 1 — Ingredientes (independiente)

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

### Paso 2 — Platos / Rolls (independiente)

**POST** `/api/v1/platos`

**Rolls** (categoría `ROLL` — importantes para tandas):

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

**Otros platos** (NO rolls — para probar rechazos):

```json
{
  "nombre": "Té verde",
  "precio": 5000,
  "categoria": "BEBIDA",
  "descripcion": "Té verde caliente"
}
```

### Paso 3 — Mesas (independiente)

**POST** `/api/v1/mesas`

```json
{ "numero": 1, "capacidad": 4 }
```

```json
{ "numero": 2, "capacidad": 2 }
```

```json
{ "numero": 3, "capacidad": 6 }
```

### Paso 4 — Abrir cuenta en una mesa

**Obligatorio antes de crear pedidos.**

**POST** `/api/v1/cuentas/mesa/1`

Sin body.

**Respuesta:** `201 Created` con `id: 1`, `total: 0.0`.

### Paso 5 — Pedidos (auto-asocia a cuenta)

**POST** `/api/v1/pedidos`

```json
{
  "idMesa": 1,
  "idPlatos": [1, 2],
  "notas": "Sin wasabi"
}
```

**El pedido se agrega automáticamente a la cuenta abierta de la mesa.**

**En la consola verás:**
```
Pedido #1 creado para mesa 1 — 2 items
Pedido #1 agregado automáticamente a cuenta #1
```

### Paso 6 — Tandas (MongoDB)

**POST** `/api/v1/tandas`

```json
[1, 2]
```

(Array de IDs de rolls.)

**Respuesta:** `id` tipo String de Mongo (`6abb...`), `cantidad: 2`.

### Paso 7 — Reservas

**POST** `/api/v1/reservas`

```json
{
  "idMesa": 2,
  "cliente": "Juan Pérez",
  "fechaHora": "2026-12-31T20:00:00",
  "comensales": 2
}
```

### Paso 8 — Parqueadero

**POST** `/api/v1/parqueadero/entrada`

```json
{ "placa": "ABC-123" }
```

**Simular salida:**

**POST** `/api/v1/parqueadero/salida/ABC-123`

### Paso 9 — Reportes (solo lectura)

**GET** `/api/v1/reportes/resumen`

**GET** `/api/v1/reportes/ingresos-por-categoria`

**GET** `/api/v1/reportes/platos-populares?top=3`

### Flujo completo resumido

| # | Método | Endpoint | Depende de |
|---|--------|----------|-----------|
| 1 | POST | `/api/v1/ingredientes` | — |
| 2 | POST | `/api/v1/platos` | — |
| 3 | POST | `/api/v1/mesas` | — |
| 4 | POST | `/api/v1/cuentas/mesa/{idMesa}` | Mesa existente |
| 5 | POST | `/api/v1/pedidos` | Mesa con cuenta + platos |
| 6 | POST | `/api/v1/tandas` | Platos ROLL |
| 7 | POST | `/api/v1/reservas` | Mesa existente |
| 8 | POST | `/api/v1/parqueadero/entrada` | — |
| 9 | GET | `/api/v1/reportes/*` | Datos previos |

## Resetear la base de datos

### PostgreSQL — Borrar todos los datos

Ejecuta en DBeaver (conectado a `sakura_sushi`, alt+x):

```sql
SET session_replication_role = 'replica';

TRUNCATE TABLE items_pedido RESTART IDENTITY CASCADE;
TRUNCATE TABLE pedidos RESTART IDENTITY CASCADE;
TRUNCATE TABLE cuenta_pedidos RESTART IDENTITY CASCADE;
TRUNCATE TABLE cuentas RESTART IDENTITY CASCADE;
TRUNCATE TABLE reservas RESTART IDENTITY CASCADE;
TRUNCATE TABLE registros_vehiculo RESTART IDENTITY CASCADE;
TRUNCATE TABLE mesas RESTART IDENTITY CASCADE;
TRUNCATE TABLE platos RESTART IDENTITY CASCADE;
TRUNCATE TABLE ingredientes RESTART IDENTITY CASCADE;

SET session_replication_role = 'origin';
```

**Los IDs se resetean** — el próximo POST empieza en 1.

### MongoDB — Borrar todas las tandas

Abre **MongoDB Compass**:

1. Conexión a `localhost:27017`
2. Expande `sakura_sushi_nosql` → colección `tandas`
3. Clic derecho → **Drop Collection**

O desde la consola de Compass (MongoDB shell):

```javascript
use sakura_sushi_nosql
db.tandas.deleteMany({})
```

## 🧪 Ejecutar tests

```bash
mvn clean test
```

**Resultado esperado:** ~155 tests en verde.

### Reporte de cobertura (JaCoCo)

```bash
mvn clean test
start target/site/jacoco/index.html
```

## 📸 Evidencias

### Swagger UI — Todos los grupos
![Swagger UI](docs/images/Swagger.png)

### Cobertura JaCoCo
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
| Spring Data JPA | 3.4.1 |
| Spring Data MongoDB | 3.4.1 |
| PostgreSQL | 15+ |
| MongoDB | 7.0+ |
| Lombok | 1.18.48 |
| MapStruct | 1.6.2 |
| springdoc-openapi | 2.8.4 |
| JUnit | 5 |
| Mockito | 5.x |
| JaCoCo | 0.8.13 |
| SonarQube | 9.9 LTS |

## 🎓 Decisiones de diseño

### ¿Por qué PostgreSQL para la mayoría de dominios?

- **Datos relacionados** — un pedido tiene una mesa, una cuenta tiene pedidos, etc.
- **Integridad referencial** — `@ManyToOne` y `@OneToMany` garantizan consistencia
- **Transacciones ACID** — crítico para cuentas y pagos
- **Joins eficientes** — `@Query` con `JOIN FETCH` evita N+1

### ¿Por qué MongoDB para Tanda?

- **Estructura flexible** — la lista de rolls es solo un array de IDs
- **No requiere tabla intermedia** — se embebe directamente en el documento
- **Escritura rápida** — sin overhead de joins
- **Un documento por preparación** — patrón natural para eventos

### ¿Por qué el Mapper tiene 3 capas?

Cada capa tiene una razón de cambio distinta:
- **Presentación** (Request/Response) → puede cambiar por decisiones de API
- **Dominio** → puede cambiar por lógica de negocio
- **Persistencia** (Entity/Document) → puede cambiar por decisiones de BD

Mantenerlas separadas permite cambiar cualquiera sin tocar las otras dos.