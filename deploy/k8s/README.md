# K8s env injection

This folder injects runtime variables through `ConfigMap` and `Secret`.

## Files

- `configmap.yaml`: non-sensitive app config
- `secret.example.yaml`: secret template (copy and replace values)
- `deployment.yaml`: app deployment that reads env from both
- `service.yaml`: internal app service

## Apply

```bash
kubectl apply -f deploy/k8s/configmap.yaml
kubectl apply -f deploy/k8s/secret.example.yaml
kubectl apply -f deploy/k8s/deployment.yaml
kubectl apply -f deploy/k8s/service.yaml
```

## Verify env in pod

```bash
kubectl get pods -l app=memorialbooklet-app
kubectl exec -it <pod-name> -- printenv | grep -E 'CONFLUX|SPRING_DATASOURCE|SPRING_PROFILES_ACTIVE'
```

