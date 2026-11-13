# Práctica S11 AP8: Persistencia y Comunicación Multi-componente en Kubernetes

Despliegue en Kubernetes (Minikube) de un backend reactivo **Spring WebFlux** conectado a **MongoDB** con almacenamiento persistente, dentro del namespace `practica-s11`.

## Arquitectura

    Cliente (curl / navegador)
            |
            v  NodePort 30080
    +-------------------+      ClusterIP 27017      +-------------------+
    | backend (WebFlux) | ------------------------> | mongo (MongoDB 7) |
    | ConfigMap + Secret|  DNS interno:             | PVC en /data/db   |
    +-------------------+  mongo-service            +-------------------+

| Componente | Recurso | Detalle |
|---|---|---|
| Aislamiento | Namespace `practica-s11` | Agrupa todos los recursos |
| Persistencia | PVC `mongo-pvc` | 1Gi, ReadWriteOnce, montado en `/data/db` |
| Configuración | ConfigMap `app-config` | `DB_HOST`, `DB_PORT`, `DB_NAME`, `APP_PORT` |
| Credenciales | Secret `db-secret` (Opaque) | Usuario y contraseña en Base64 |
| Base de datos | Deployment + Service ClusterIP | Solo accesible dentro del clúster |
| Backend | Deployment + Service NodePort 30080 | `envFrom` (ConfigMap y Secret), probes en `/actuator/health` |

## Estructura del repositorio

    k8s/   Manifiestos YAML (00 al 05)
    src/   Código fuente Spring WebFlux
    Dockerfile, pom.xml

## Despliegue

    minikube start --driver=docker
    docker build -t tareas-webflux:1.0 .
    minikube image load tareas-webflux:1.0

    kubectl apply -f k8s\00-namespace.yaml
    kubectl apply -f k8s\01-mongo-pvc.yaml
    kubectl apply -f k8s\02-app-configmap.yaml
    kubectl apply -f k8s\03-db-secret.yaml
    kubectl apply -f k8s\04-mongo-stack.yaml
    kubectl rollout status deployment/mongo -n practica-s11
    kubectl apply -f k8s\05-backend-stack.yaml
    kubectl rollout status deployment/backend -n practica-s11

## Pruebas

    minikube service backend-service -n practica-s11 --url
    $URL = "http://127.0.0.1:PUERTO"
    Invoke-RestMethod "$URL/actuator/health"
    Invoke-RestMethod -Method Post -Uri "$URL/api/tareas" -ContentType "application/json" -Body '{"titulo":"Aprender Kubernetes","completada":false}'
    Invoke-RestMethod "$URL/api/tareas"

**Prueba de persistencia:** se elimina el pod de MongoDB con `kubectl delete pod -l app=mongo -n practica-s11`. Kubernetes crea uno nuevo, lo vuelve a unir al mismo PVC y los datos siguen disponibles.

## Nota de seguridad

Las credenciales de este repositorio son de práctica académica. En un entorno real el Secret no se sube a un repositorio público.