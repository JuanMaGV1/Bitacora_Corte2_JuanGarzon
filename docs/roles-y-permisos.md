# Roles y Permisos — Sakura Sushi

## Roles del sistema

| Código | Rol | Descripción |
|--------|-----|-------------|
| `ROLE_GERENTE` | Gerente | Administra la carta completa y ve reportes |
| `ROLE_MESERO` | Mesero | Toma pedidos y gestiona mesas |
| `ROLE_COCINERO` | Cocinero | Gestiona el flujo de cocina y tandas |
| `ROLE_CLIENTE` | Cliente | Consulta la carta y hace reservas |

## Matriz de permisos

| Funcionalidad | GERENTE | MESERO | COCINERO | CLIENTE |
|---------------|:-------:|:------:|:--------:|:-------:|
| Ver menú (GET /menu) | ✅ | ✅ | ✅ | ✅ |
| Crear plato | ✅ | ❌ | ❌ | ❌ |
| Editar plato | ✅ | ❌ | ❌ | ❌ |
| Eliminar plato | ✅ | ❌ | ❌ | ❌ |
| Marcar disponibilidad | ✅ | ✅ | ❌ | ❌ |
| Crear pedido | ✅ | ✅ | ❌ | ❌ |
| Cambiar estado pedido | ✅ | ✅ | ✅ | ❌ |
| Crear tanda | ✅ | ❌ | ✅ | ❌ |
| Abrir cuenta | ✅ | ✅ | ❌ | ❌ |
| Cerrar cuenta | ✅ | ✅ | ❌ | ❌ |
| Crear reserva | ✅ | ✅ | ❌ | ✅ |
| Registrar entrada vehículo | ✅ | ✅ | ❌ | ❌ |
| Ver reportes | ✅ | ❌ | ❌ | ❌ |