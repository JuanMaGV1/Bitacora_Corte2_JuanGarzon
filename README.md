# Bitacora_Corte2_JuanGarzon

## Autor
Juan Garzón — DOSW Grupo 1

## Restaurante
**SakuraSushi** — Barra de sushi con preparación por lotes y rolls armados a pedido. 

## Descripción
API REST que digitaliza la operación del restaurante: gestión de carta, pedidos, 
mesas, cuentas, reservas, ingredientes, parqueadero y reportes. Sin persistencia 
aún — todo en memoria con Streams.

## Funcionalidades por Dominio

| # | Funcionalidad | Descripción |
|---|--------------|-------------|
| 1 | **Carta y Platos** | Gestión de disponibilidad |
| 2 | **Menú del Cliente** | Vista filtrada de solo platos disponibles |
| 3 | **Ingredientes** | Catálogo para personalización de platos |
| 4 | **Pedidos** | Flujo completo con transiciones de estado |
| 5 | **Mesas** | Apertura y cierre de cuentas |
| 6 | **Cuentas** | Consolidación de pedidos y cierre |
| 7 | **Reservas** | Gestión con validación de conflictos de horario |
| 8 | **Parqueadero** | Entrada/salida con cálculo de cobro |
| 9 | **Reportes** | Consolidados con Streams |

## Tabla de Endpoints

### PlatoController — `/api/v1/platos`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/platos` | 200 | — |
| GET | `/api/v1/platos/{id}` | 200 | 404 |
| POST | `/api/v1/platos` | 201 | 400, 409 |
| PUT | `/api/v1/platos/{id}` | 200 | 400, 404, 409 |
| PATCH | `/api/v1/platos/{id}/disponible` | 200 | 404 |
| DELETE | `/api/v1/platos/{id}` | 204 | 404 |

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
| POST | `/api/v1/pedidos` | 201 | 400, 422 |
| POST | `/api/v1/pedidos/{id}/items` | 200 | 404, 422 |
| PUT | `/api/v1/pedidos/{id}/items/{idPlato}` | 200 | 404, 422 |
| DELETE | `/api/v1/pedidos/{id}/items/{idPlato}` | 200 | 404, 422 |
| PATCH | `/api/v1/pedidos/{id}/estado` | 200 | 404, 422 |
| DELETE | `/api/v1/pedidos/{id}` | 204 | 404, 422 |

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
| POST | `/api/v1/reservas` | 201 | 400, 409 |
| PATCH | `/api/v1/reservas/{id}/estado` | 200 | 404, 422 |
| DELETE | `/api/v1/reservas/{id}` | 204 | 404, 422 |

### ReporteController — `/api/v1/reportes`
| Verbo | Endpoint | Éxito | Errores |
|-------|----------|-------|---------|
| GET | `/api/v1/reportes/resumen` | 200 | — |
| GET | `/api/v1/reportes/ingresos-por-categoria` | 200 | — |
| GET | `/api/v1/reportes/platos-populares?top=N` | 200 | — |

## Diagramas

### Diagrama de Clases (Dominio)
![Diagrama de Clases](/docs/diagrams/DiagramaClases.drawio.png)


## Evidencias

### Swagger UI — Todos los grupos
![Swagger UI](/docs/images/Swagger.png)

### Cobertura JaCoCo
![Cobertura](/docs/images/Cobertura.png)

### Tests JUnit + Mockito
![Tests](/docs/images/test.png)

## Overview
![SonarQube Overview](/docs/images/SonarQube.png)

## Arquitectura

- **Controller:** Recibe HTTP, delega al Service, responde
- **Service:** Logica de negocio con Streams
- **Validator:** Reglas de negocio especificas
- **Mapper:** MapStruct traduce DTO <-> Dominio
- **Exception Handler:** Manejo centralizado de errores
