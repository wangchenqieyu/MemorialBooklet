# MemorialBooklet
As long as you haven't forgotten, they have never left. Your beautiful memories, we will protect together and forever

## Deployment (Windows ECS + Docker Compose)

### Local/Server first-time run

```bash
cp .env.example .env
docker compose up -d --build
docker compose ps
```

Default app URL after startup:

- `http://<ECS_PUBLIC_IP>:8081`

### GitHub Actions auto deploy

Workflow: `.github/workflows/deploy-ecs-prod.yml`

- Trigger: push to `release/*`
- Runner labels: `self-hosted`, `ecs_prod`
- Deploy command: `docker compose up -d --build`

Set these GitHub repository **Secrets**:

- `MYSQL_ROOT_PASSWORD`
- `MYSQL_APP_PASSWORD`
- `CONFLUX_PRIVATE_KEY`
- `CONFLUX_CONTRACT_ADDRESS`

Optional GitHub repository **Variables**:

- `APP_PORT`, `MYSQL_HOST_PORT`, `MYSQL_DATABASE`, `MYSQL_APP_USER`
- `CONFLUX_RPC_URL`
- `IPFS_MULTI_ADDRESS`, `IPFS_API_PORT`, `IPFS_GATEWAY_PORT`, `IPFS_SWARM_PORT`
- `MYSQL_IMAGE`, `IPFS_IMAGE`
- `MYSQL_IMAGE_MIRROR`, `IPFS_IMAGE_MIRROR`, `IPFS_IMAGE_CANDIDATES`
- `DOCKER_REGISTRY_MIRROR`
- `ENFORCE_MIRROR_GUARD` (`true`/`false`)

Default image strategy used by workflow/compose:

- MySQL: `docker.m.daocloud.io/library/mysql:8.0`
- IPFS: `ghcr.io/ipfs/kubo:v0.30.0`

### Pipeline quick checks

If deploy fails, first confirm these logs in Actions:

- `Resolve image source`: check final `MYSQL_IMAGE_EFFECTIVE` and `IPFS_IMAGE_EFFECTIVE`
- `Preflight resolved image reachability`: both manifest checks should pass
- `Deploy with docker compose`: `compose.images` should match the resolved images

If one image is still unreachable, set `MYSQL_IMAGE_MIRROR` / `IPFS_IMAGE_MIRROR` to another accessible registry in your ECS network.
