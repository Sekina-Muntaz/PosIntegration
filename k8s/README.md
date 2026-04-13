# Kubernetes Deployment for countrytest

This directory contains Kubernetes manifests for the `countrytest` Spring Boot application.

## Files

- `application-configmap.yaml`: application configuration stored in a ConfigMap.
- `deployment.yaml`: Deployment for the application.
- `service.yaml`: ClusterIP Service exposing port `8085`.
- `ingress.yaml`: Optional Ingress rule for `countrytest.local`.

## Usage

1. Build and push your image:

```bash
mvn clean package -DskipTests
docker build -t your-registry/countrytest:latest .
docker push your-registry/countrytest:latest
```

2. Apply Kubernetes manifests:

```bash
kubectl apply -f k8s/application-configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

3. If you have an ingress controller and want external access:

```bash
kubectl apply -f k8s/ingress.yaml
```

4. Verify pods and service:

```bash
kubectl get pods,svc -l app=countrytest
```

## Troubleshooting

### 1. Check pod status

```bash
kubectl get pods -l app=countrytest
kubectl describe pod <pod-name>
```

- Look for `CrashLoopBackOff`, `ImagePullBackOff`, or `ErrImagePull`.
- Check `Events` for startup failures, permission issues, or missing config.

### 2. Inspect container logs

```bash
kubectl logs deployment/countrytest
```

- If logs are empty, use `kubectl logs <pod-name>`.
- For repeated restarts, add `--previous` to view the last failed container logs.

### 3. Validate configuration mount

```bash
kubectl get configmap countrytest-config -o yaml
kubectl exec -it <pod-name> -- cat /app/application.yaml
```

- Confirm the ConfigMap exists and the file is mounted at `/app/application.yaml`.
- Verify `spring.datasource` and `server.port` values match expected settings.

### 4. Confirm service connectivity

```bash
kubectl get svc countrytest
kubectl describe svc countrytest
```

- Confirm the service selects pods with `app=countrytest`.
- Ensure `targetPort: 8085` matches the container port.

### 5. Test application endpoint

```bash
kubectl port-forward deployment/countrytest 8085:8085
curl http://localhost:8085/actuator/health
```

- Replace `/actuator/health` with an app endpoint if actuator is not enabled.
- If the service is not reachable, verify the pod and container ports.

### 6. Ingress checks (optional)

```bash
kubectl describe ingress countrytest-ingress
kubectl get ingress countrytest-ingress
```

- Ensure your ingress controller is installed and the ingress class is correct.
- Add a local host entry for `countrytest.local` if using the sample host name.

### 7. Fix common issues

- `ImagePullBackOff`: verify image tag and registry credentials.
- `CrashLoopBackOff`: inspect startup logs and JVM errors.
- `ConfigMap` not applied: re-run `kubectl apply -f k8s/application-configmap.yaml`.
- Port mismatch: ensure container and service ports both use `8085`.
