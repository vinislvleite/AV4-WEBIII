# Autobots Automanager — AV4

> Sistema de gerenciamento automotivo com autenticação e autorização via **JWT** (JSON Web Token).  
> Desenvolvido com **Spring Boot 2.7.0**, **Spring Security** e **H2 Database**.

---

## Requisitos

- Java 17+
- Maven (ou usar o `mvnw` incluído no projeto)
- Insomnia / Postman para testes

---

## Como Rodar

### Windows (PowerShell)
```powershell
cd automanager
.\mvnw.cmd spring-boot:run
```

### Linux / Mac
```bash
cd automanager
./mvnw spring-boot:run
```

A aplicação sobe em: **http://localhost:8080**  
Console H2 (banco em memória): **http://localhost:8080/h2-console**  
- JDBC URL: `jdbc:h2:mem:automanagerdb`
- User: `sa` | Senha: *(vazia)*

> Ao iniciar, o sistema cria automaticamente o usuário **admin** com senha **123456**.

---

## Perfis de Usuário

| Perfil | Role | Permissões |
|---|---|---|
| Administrador | `ROLE_ADMIN` | CRUD completo em tudo |
| Gerente | `ROLE_GERENTE` | CRUD em clientes, usuários (exceto admin) |
| Vendedor | `ROLE_VENDEDOR` | Criar e listar clientes, sem deletar |
| Cliente | `ROLE_CLIENTE` | Apenas leitura do próprio cadastro |

---

## Autenticação

### Fazer login

**POST** `http://localhost:8080/login`

```json
{
    "nomeUsuario": "admin",
    "senha": "123456"
}
```

O token JWT vem no **header** `Authorization` da resposta:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

> Copie o token completo e use em todas as requisições protegidas no header `Authorization`.

---

## Endpoints

### Rotas Públicas (sem token)

| Método | Rota | Descrição |
|---|---|---|
| POST | `/login` | Autenticar e obter token JWT |
| POST | `/cadastrar-usuario` | Cadastrar novo usuário |
| GET | `/obter-usuarios` | Listar todos os usuários |

### Rotas Protegidas (requer token)

| Método | Rota | Perfis permitidos |
|---|---|---|
| GET | `/obter-usuario/{id}` | Todos |
| PUT | `/atualizar-usuario/{id}` | ADMIN, GERENTE, VENDEDOR |
| DELETE | `/deletar-usuario/{id}` | ADMIN, GERENTE |
| GET | `/obter-clientes` | ADMIN, GERENTE, VENDEDOR |
| GET | `/obter-cliente/{id}` | Todos |
| POST | `/cadastrar-cliente` | ADMIN, GERENTE, VENDEDOR |
| PUT | `/atualizar-cliente/{id}` | ADMIN, GERENTE, VENDEDOR |
| DELETE | `/deletar-cliente/{id}` | ADMIN, GERENTE |

---

## Roteiro de Testes

### ETAPA 1 — Criar usuários (sem token)

**POST** `/cadastrar-usuario`

```json
{
    "nome": "gerente",
    "credencial": { "nomeUsuario": "gerente", "senha": "123456" },
    "perfis": ["ROLE_GERENTE"]
}
```

```json
{
    "nome": "vendedor",
    "credencial": { "nomeUsuario": "vendedor", "senha": "123456" },
    "perfis": ["ROLE_VENDEDOR"]
}
```

```json
{
    "nome": "cliente",
    "credencial": { "nomeUsuario": "cliente", "senha": "123456" },
    "perfis": ["ROLE_CLIENTE"]
}
```

---

### ETAPA 2 — Login de cada perfil

**POST** `/login`

```json
{ "nomeUsuario": "admin", "senha": "123456" }
```
```json
{ "nomeUsuario": "gerente", "senha": "123456" }
```
```json
{ "nomeUsuario": "vendedor", "senha": "123456" }
```
```json
{ "nomeUsuario": "cliente", "senha": "123456" }
```

---

### ETAPA 3 — Testes com token do ADMIN

| Requisição | Esperado |
|---|---|
| POST `/cadastrar-cliente` | 201 |
| GET `/obter-clientes` | 200 |
| GET `/obter-usuario/1` | 200 |
| PUT `/atualizar-cliente/1` | 200 |
| DELETE `/deletar-cliente/1` | 204 |

**POST** `/cadastrar-cliente`:
```json
{
    "nome": "Toyota Cliente Teste",
    "email": "cliente@toyota.com",
    "telefone": "11999999999"
}
```

**PUT** `/atualizar-cliente/1`:
```json
{
    "nome": "Toyota Cliente Atualizado",
    "email": "atualizado@toyota.com",
    "telefone": "11988888888"
}
```

---

### ETAPA 4 — Testes com token do GERENTE

| Requisição | Esperado |
|---|---|
| POST `/cadastrar-cliente` | 201 |
| GET `/obter-clientes` | 200 |
| PUT `/atualizar-cliente/{id}` | 200 |
| DELETE `/deletar-cliente/{id}` | 204 |

**POST** `/cadastrar-cliente`:
```json
{
    "nome": "Cliente do Gerente",
    "email": "gerente@toyota.com",
    "telefone": "11977777777"
}
```

---

### ETAPA 5 — Testes com token do VENDEDOR

| Requisição | Esperado |
|---|---|
| POST `/cadastrar-cliente` | 201 |
| GET `/obter-clientes` | 200 |
| DELETE `/deletar-cliente/{id}` | 403 |
| DELETE `/deletar-usuario/{id}` | 403 |

**POST** `/cadastrar-cliente`:
```json
{
    "nome": "Cliente do Vendedor",
    "email": "vendedor@toyota.com",
    "telefone": "11955555555"
}
```

> Os 403 provam que o vendedor não tem permissão para deletar.

---

### ETAPA 6 — Testes com token do CLIENTE

| Requisição | Esperado |
|---|---|
| GET `/obter-cliente/1` | 200 |
| GET `/obter-clientes` | 403 |
| POST `/cadastrar-cliente` | 403 |
| DELETE `/deletar-cliente/{id}` | 403 |

**POST** `/cadastrar-cliente` (deve ser bloqueado):
```json
{
    "nome": "Tentativa Cliente",
    "email": "tentativa@toyota.com",
    "telefone": "11944444444"
}
```

---

### ETAPA 7 — Sem token

| Requisição | Esperado |
|---|---|
| GET `/obter-cliente/1` sem token | 403 |
| GET `/obter-clientes` sem token | 403 |

---

## Estrutura do Projeto

```
automanager/
├── src/main/java/com/autobots/automanager/
│   ├── adaptadores/         # UserDetailsImpl, UserDetailsServiceImpl
│   ├── configuracao/        # Seguranca (Spring Security config)
│   ├── controles/           # ControleCliente, ControleUsuario
│   ├── entidades/           # Usuario, Credencial, Cliente
│   ├── filtros/             # Autenticador, Autorizador, filtros JWT
│   ├── jwt/                 # Gerador, Analisador, Validador, Provedor
│   ├── modelos/             # Enum Perfil
│   └── repositorios/        # RepositorioUsuario, RepositorioCliente
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

---

## Tecnologias

| Tecnologia | Versao |
|---|---|
| Java | 17 |
| Spring Boot | 2.7.0 |
| Spring Security | 5.7.1 |
| JJWT | 0.9.1 |
| H2 Database | em memoria |
| Lombok | latest |
| Maven | 3.8.6 |

---
