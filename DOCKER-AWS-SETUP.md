# Configuración Docker con AWS

Este documento describe cómo configurar y ejecutar el servicio Crediya Reporting con conexión a los servicios de AWS (SQS y DynamoDB).

## Configuración

### 1. Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto basado en `.env.example`:

```bash
cp .env.example .env
```

Edita el archivo `.env` con tus credenciales y configuraciones de AWS:

```env
# AWS Configuration
AWS_REGION=us-east-2
AWS_ACCESS_KEY_ID=tu-access-key-id
AWS_SECRET_ACCESS_KEY=tu-secret-access-key

# SQS Configuration
ENTRYPOINT_SQS_REGION=us-east-2
ENTRYPOINT_SQS_QUEUE_URL=https://sqs.us-east-2.amazonaws.com/889522049804/generate-reports-queue
ENTRYPOINT_SQS_WAIT_TIME_SECONDS=20
ENTRYPOINT_SQS_MAX_NUMBER_OF_MESSAGES=10
ENTRYPOINT_SQS_VISIBILITY_TIMEOUT_SECONDS=10
ENTRYPOINT_SQS_NUMBER_OF_THREADS=1

# DynamoDB Configuration
APP_DYNAMO_TABLE=crediya-reports
APP_DYNAMO_GSI=metricId-updatedAt-index
APP_DYNAMO_PK=global-report
```

### 2. Servicios AWS Requeridos

Asegúrate de que los siguientes recursos estén configurados en AWS:

#### SQS Queue
- **Nombre**: `generate-reports-queue`
- **Región**: `us-east-2`
- **URL**: `https://sqs.us-east-2.amazonaws.com/889522049804/generate-reports-queue`

#### DynamoDB Table
- **Nombre**: `crediya-reports`
- **Partition Key**: `pk` (String)
- **Sort Key**: `sk` (String)
- **Global Secondary Index**: `metricId-updatedAt-index`
  - GSI Partition Key: `metricId` (String)
  - GSI Sort Key: `updatedAt` (String)

### 3. Permisos IAM

El usuario/rol IAM debe tener los siguientes permisos:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "sqs:ReceiveMessage",
                "sqs:DeleteMessage",
                "sqs:GetQueueAttributes",
                "sqs:SendMessage"
            ],
            "Resource": "arn:aws:sqs:us-east-2:889522049804:generate-reports-queue"
        },
        {
            "Effect": "Allow",
            "Action": [
                "dynamodb:GetItem",
                "dynamodb:PutItem",
                "dynamodb:UpdateItem",
                "dynamodb:DeleteItem",
                "dynamodb:Query",
                "dynamodb:Scan"
            ],
            "Resource": [
                "arn:aws:dynamodb:us-east-2:889522049804:table/crediya-reports",
                "arn:aws:dynamodb:us-east-2:889522049804:table/crediya-reports/index/*"
            ]
        }
    ]
}
```

## Ejecución

### Construir y ejecutar el servicio

```bash
docker-compose up --build
```

### Ejecutar en modo detached

```bash
docker-compose up -d --build
```

### Ver logs

```bash
docker-compose logs -f crediya-reporting-service
```

### Detener el servicio

```bash
docker-compose down
```

## Verificación

1. **Health Check**: El servicio debería estar disponible en `http://localhost:8080/actuator/health`
2. **Conexión SQS**: Verifica en los logs que el servicio se conecte correctamente a la cola SQS
3. **Conexión DynamoDB**: Verifica que el servicio pueda acceder a la tabla DynamoDB

## Troubleshooting

### Error de conexión AWS
- Verifica que las credenciales AWS sean correctas
- Confirma que la región esté configurada correctamente
- Verifica que los recursos (SQS, DynamoDB) existan en AWS

### Error de permisos
- Verifica que el usuario/rol IAM tenga los permisos necesarios
- Confirma que las políticas IAM estén correctamente asignadas

### Problemas de red
- Verifica que el contenedor tenga acceso a internet
- Confirma que no haya firewalls bloqueando la conexión a AWS