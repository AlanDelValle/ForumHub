# FórumHub API

## Descrição

O **FórumHub** é uma API RESTful desenvolvida para gerenciar tópicos de discussão em um fórum online. A aplicação permite que usuários autenticados criem, listem, detalhem, atualizem e excluam tópicos, com suporte a validações, paginação e autenticação baseada em JWT (JSON Web Token). O projeto utiliza **Spring Boot** com **Java 21**, banco de dados **MySQL**, e **Flyway** para gerenciamento de migrations.

### Funcionalidades

- **Autenticação**: Usuários devem fazer login para obter um token JWT, que é necessário para acessar os endpoints protegidos.
- **Cadastro de Tópicos**: Criação de tópicos com título, mensagem, autor e curso, com validação de duplicatas.
- **Listagem de Tópicos**: Listagem paginada de tópicos, com opção de ordenação por data de criação e filtragem por curso e ano.
- **Detalhamento de Tópicos**: Consulta de detalhes de um tópico específico por ID.
- **Atualização de Tópicos**: Atualização de dados de um tópico existente, com validação de duplicatas.
- **Exclusão de Tópicos**: Remoção de um tópico por ID.

## Tecnologias Utilizadas

- **Java 21**: Linguagem de programação.
- **Spring Boot 3.3.x**: Framework para desenvolvimento da API.
- **Spring Security**: Autenticação e autorização com JWT.
- **Spring Data JPA**: Persistência de dados.
- **MySQL 8**: Banco de dados relacional.
- **Flyway**: Gerenciamento de migrations do banco de dados.
- **Lombok**: Redução de código boilerplate.
- **JJWT**: Geração e validação de tokens JWT.
- **Maven**: Gerenciamento de dependências e build.
- **Insomnia**: Ferramenta para testes de API.

## Pré-requisitos

- **Java 21** (JDK instalado).
- **Maven** (para gerenciamento de dependências).
- **MySQL 8** (instalado e configurado).
- **MySQL Workbench** (opcional, para gerenciar o banco).
- **Insomnia** (para testar os endpoints).
- **Visual Studio Code** (ou outra IDE para desenvolvimento).

## Configuração do Ambiente

### 1. Configuração do Banco de Dados

1. Instale o **MySQL 8** e o **MySQL Workbench**.
2. Crie um banco de dados chamado `forumhub`:
   ```sql
   CREATE DATABASE forumhub;
   ```
3. Configure as credenciais do banco no arquivo `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/forumhub
   spring.datasource.username=root
   spring.datasource.password=sua_senha
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.jpa.hibernate.ddl-auto=none
   spring.flyway.enabled=true
   spring.flyway.locations=classpath:db/migration
   jwt.secret=chave-secreta-super-segura-1234567890abcdef
   jwt.expiration=3600000
   ```
   - Substitua `sua_senha` pela senha do seu usuário MySQL.
   - A propriedade `jwt.secret` é usada para assinar tokens JWT (use uma chave segura em produção).
   - A propriedade `jwt.expiration` define o tempo de expiração do token (3600000 ms = 1 hora).

4. O Flyway aplicará automaticamente as migrations localizadas em `src/main/resources/db/migration`:
   - `V1__create_table_topicos.sql`: Cria a tabela `topicos`.
   - `V2__create_table_usuarios.sql`: Cria a tabela `usuarios`.

5. Insira um usuário de teste no banco para autenticação:
   ```sql
   INSERT INTO usuarios (login, senha) VALUES ('joao', '$2a$10$XURPShQ5uMjZ6Q.9pH2T3u/3qC2P3K1s3M3N3O3P3Q3R3S3T3U3V3');
   ```
   - A senha acima é o hash BCrypt de `123456`. Use [bcrypt-generator.com](https://bcrypt-generator.com/) para gerar outros hashes.

### 2. Clonando e Configurando o Projeto

1. Clone o repositório (se disponível) ou crie o projeto com a estrutura fornecida:
   ```bash
   git clone <URL_DO_REPOSITORIO>
   ```
2. Abra o projeto no **Visual Studio Code** (ou outra IDE).
3. Certifique-se de que as extensões recomendadas estão instaladas:
   - **Spring Boot Extension Pack**
   - **Java Extension Pack**
   - **Lombok Annotations Support**

### 3. Estrutura do Projeto

```
forumhub/
├── src/
│   ├── main/
│   │   ├── java/com/seu_dominio/forumhub/
│   │   │   ├── api/controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── TopicoController.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfigurations.java
│   │   │   │   ├── SecurityFilter.java
│   │   │   ├── domain/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── LoginRequestDTO.java
│   │   │   │   │   ├── TopicoRequestDTO.java
│   │   │   │   │   ├── TopicoResponseDTO.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Topico.java
│   │   │   │   │   ├── Usuario.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── TopicoRepository.java
│   │   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── TokenService.java
│   │   │   │   │   ├── UsuarioService.java
│   │   ├── resources/
│   │   │   ├── db/migration/
│   │   │   │   ├── V1__create_table_topicos.sql
│   │   │   │   ├── V2__create_table_usuarios.sql
│   │   │   ├── application.properties
├── pom.xml
├── README.md
```

### 4. Executando o Projeto

1. No terminal, navegue até o diretório do projeto:
   ```bash
   cd forumhub
   ```
2. Execute o comando para compilar e rodar a aplicação:
   ```bash
   mvn spring-boot:run
   ```
3. A API estará disponível em `http://localhost:8080`.

## Uso da API

### Endpoints

A API possui os seguintes endpoints:

#### Autenticação

- **POST /login**
  - **Descrição**: Autentica um usuário e retorna um token JWT.
  - **Corpo da Requisição**:
    ```json
    {
      "login": "joao",
      "senha": "123456"
    }
    ```
  - **Resposta de Sucesso** (200 OK):
    ```json
    "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvIiwiaWF0IjoxNzI0MTQ2NzQ1LCJleHAiOjE3MjQxNTAzNDV9.XYZ..."
    ```
  - **Erros**:
    - 401 Unauthorized: Credenciais inválidas.
    - 400 Bad Request: Campos obrigatórios ausentes.

#### Tópicos (Requer Autenticação)

- **POST /topicos**
  - **Descrição**: Cria um novo tópico.
  - **Cabeçalho**: `Authorization: Bearer <token>`
  - **Corpo da Requisição**:
    ```json
    {
      "titulo": "Problema com Java",
      "mensagem": "Como resolver NullPointerException?",
      "autor": "João Silva",
      "curso": "Java Intermediário"
    }
    ```
  - **Resposta de Sucesso** (201 Created):
    ```json
    {
      "id": 1,
      "titulo": "Problema com Java",
      "mensagem": "Como resolver NullPointerException?",
      "dataCriacao": "2025-07-18T20:32:00.000Z",
      "status": "ATIVO",
      "autor": "João Silva",
      "curso": "Java Intermediário"
    }
    ```
  - **Erros**:
    - 401 Unauthorized: Token inválido ou ausente.
    - 400 Bad Request: Campos obrigatórios ausentes ou tópico duplicado.

- **GET /topicos?page=0&size=10&sort=dataCriacao,asc**
  - **Descrição**: Lista tópicos com paginação e ordenação.
  - **Cabeçalho**: `Authorization: Bearer <token>`
  - **Resposta de Sucesso** (200 OK):
    ```json
    {
      "content": [
        {
          "id": 1,
          "titulo": "Problema com Java",
          "mensagem": "Como resolver NullPointerException?",
          "dataCriacao": "2025-07-18T20:32:00.000Z",
          "status": "ATIVO",
          "autor": "João Silva",
          "curso": "Java Intermediário"
        }
      ],
      "pageable": { /* ... */ },
      "totalPages": 1,
      "totalElements": 1,
      "last": true,
      "size": 10,
      "number": 0,
      "sort": { /* ... */ },
      "first": true,
      "numberOfElements": 1,
      "empty": false
    }
    ```
  - **Erros**:
    - 401 Unauthorized: Token inválido ou ausente.

- **GET /topicos/{id}**
  - **Descrição**: Detalha um tópico por ID.
  - **Cabeçalho**: `Authorization: Bearer <token>`
  - **Resposta de Sucesso** (200 OK):
    ```json
    {
      "id": 1,
      "titulo": "Problema com Java",
      "mensagem": "Como resolver NullPointerException?",
      "dataCriacao": "2025-07-18T20:32:00.000Z",
      "status": "ATIVO",
      "autor": "João Silva",
      "curso": "Java Intermediário"
    }
    ```
  - **Erros**:
    - 401 Unauthorized: Token inválido ou ausente.
    - 404 Not Found: ID inexistente.

- **PUT /topicos/{id}**
  - **Descrição**: Atualiza um tópico por ID.
  - **Cabeçalho**: `Authorization: Bearer <token>`
  - **Corpo da Requisição**:
    ```json
    {
      "titulo": "Problema com Java Atualizado",
      "mensagem": "Como evitar NullPointerException?",
      "autor": "João Silva",
      "curso": "Java Avançado"
    }
    ```
  - **Resposta de Sucesso** (200 OK):
    ```json
    {
      "id": 1,
      "titulo": "Problema com Java Atualizado",
      "mensagem": "Como evitar NullPointerException?",
      "dataCriacao": "2025-07-18T20:32:00.000Z",
      "status": "ATIVO",
      "autor": "João Silva",
      "curso": "Java Avançado"
    }
    ```
  - **Erros**:
    - 401 Unauthorized: Token inválido ou ausente.
    - 404 Not Found: ID inexistente.
    - 400 Bad Request: Campos obrigatórios ausentes ou tópico duplicado.

- **DELETE /topicos/{id}**
  - **Descrição**: Exclui um tópico por ID.
  - **Cabeçalho**: `Authorization: Bearer <token>`
  - **Resposta de Sucesso** (204 No Content):
    - Sem corpo.
  - **Erros**:
    - 401 Unauthorized: Token inválido ou ausente.
    - 404 Not Found: ID inexistente.

- **GET /topicos/busca?curso=<curso>&ano=<ano>&page=0&size=10&sort=dataCriacao,asc**
  - **Descrição**: Lista tópicos filtrados por curso e ano.
  - **Cabeçalho**: `Authorization: Bearer <token>`
  - **Resposta de Sucesso** (200 OK):
    ```json
    {
      "content": [
        {
          "id": 1,
          "titulo": "Problema com Java",
          "mensagem": "Como resolver NullPointerException?",
          "dataCriacao": "2025-07-18T20:32:00.000Z",
          "status": "ATIVO",
          "autor": "João Silva",
          "curso": "Java Intermediário"
        }
      ],
      "pageable": { /* ... */ },
      "totalPages": 1,
      "totalElements": 1,
      "last": true,
      "size": 10,
      "number": 0,
      "sort": { /* ... */ },
      "first": true,
      "numberOfElements": 1,
      "empty": false
    }
    ```
  - **Erros**:
    - 401 Unauthorized: Token inválido ou ausente.

## Testando a API com Insomnia

### 1. Configuração do Insomnia

1. Baixe e instale o Insomnia ([insomnia.rest](https://insomnia.rest/)).
2. Crie uma nova **Collection** chamada `FórumHub`.
3. Configure um ambiente chamado `Local`:
   ```json
   {
     "base_url": "http://localhost:8080",
     "token": ""
   }
   ```

### 2. Testando o Endpoint de Login

1. Crie uma requisição na pasta `Autenticação`:
   - **Nome**: `POST - Login`
   - **Método**: POST
   - **URL**: `{{base_url}}/login`
   - **Headers**:
     ```
     Content-Type: application/json
     ```
   - **Body**:
     ```json
     {
       "login": "joao",
       "senha": "123456"
     }
     ```
2. Envie a requisição e copie o token JWT retornado.
3. Atualize o ambiente `Local` com o token:
   ```json
   {
     "base_url": "http://localhost:8080",
     "token": "<seu_token_jwt>"
   }
   ```

### 3. Testando Endpoints Protegidos

Para cada endpoint, adicione o cabeçalho:
```
Authorization: Bearer {{token}}
```

#### Exemplo: Cadastrar Tópico

- **Requisição**:
  - **Método**: POST
  - **URL**: `{{base_url}}/topicos`
  - **Headers**:
    ```
    Content-Type: application/json
    Authorization: Bearer {{token}}
    ```
  - **Body**:
    ```json
    {
      "titulo": "Problema com Java",
      "mensagem": "Como resolver NullPointerException?",
      "autor": "João Silva",
      "curso": "Java Intermediário"
    }
    ```

#### Fluxo Completo

1. **Login**: Envie `POST /login` e armazene o token.
2. **Criar Tópico**: Envie `POST /topicos` com o token.
3. **Listar Tópicos**: Envie `GET /topicos?page=0&size=10&sort=dataCriacao,asc`.
4. **Detalhar Tópico**: Envie `GET /topicos/1`.
5. **Atualizar Tópico**: Envie `PUT /topicos/1`.
6. **Excluir Tópico**: Envie `DELETE /topicos/1`.
7. **Buscar por Curso e Ano**: Envie `GET /topicos/busca?curso=Java Intermediário&ano=2025`.

### 4. Testes de Erro

- **Login**:
  - Credenciais inválidas → 401 Unauthorized.
  - Campos vazios → 400 Bad Request.
- **Endpoints Protegidos**:
  - Sem token → 401 Unauthorized.
  - Token inválido → 401 Unauthorized.
  - Token expirado → 401 Unauthorized.
  - ID inexistente → 404 Not Found.
  - Tópico duplicado → 400 Bad Request.

## Regras de Negócio

- **Campos Obrigatórios**: Todos os campos (título, mensagem, autor, curso) são obrigatórios.
- **Tópicos Duplicados**: Não é permitido cadastrar ou atualizar tópicos com o mesmo título e mensagem.
- **Autenticação**: Todos os endpoints `/topicos/*` requerem um token JWT válido.
- **Paginação**: A listagem suporta paginação (10 resultados por página, ordenados por data de criação).

## Considerações para Produção

- Use uma chave secreta mais segura para `jwt.secret` (armazene em variáveis de ambiente).
- Considere implementar roles/permissoes (ex.: `ROLE_ADMIN`, `ROLE_USER`) no modelo `Usuario`.
- Adicione suporte a refresh tokens para sessões mais longas.
- Configure logs detalhados para monitoramento.
- Use um banco de dados seguro com backups regulares.

## Contribuição

1. Fork o repositório.
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`).
3. Faça commit das alterações (`git commit -m 'Adiciona nova funcionalidade'`).
4. Envie para o repositório remoto (`git push origin feature/nova-funcionalidade`).
5. Crie um Pull Request.

## Licença

Este projeto é licenciado sob a [MIT License](LICENSE).