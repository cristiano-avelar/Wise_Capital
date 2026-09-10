# Wise Capital

O Wise Capital é uma plataforma web interativa voltada para a educação financeira e o desenvolvimento de investidores. O projeto visa democratizar o acesso ao conhecimento sobre finanças através de uma abordagem gamificada, oferecendo trilhas de aprendizagem, quizzes com correção inteligente, simuladores de investimento, uma biblioteca de recursos, e um fórum social para a troca de ideias entre a comunidade.

Para além das funcionalidades tradicionais, o sistema destaca-se pela integração de Sistemas Inteligentes (Azure OpenAI), que atuam ativamente no diagnóstico automático do perfil de investidor do utilizador e na avaliação pedagógica (com feedback detalhado) de questões dissertativas.

## Alunos integrantes da equipe

- Victorio Pinto da Silva Neto
- Cristiano de Avelar Marques Pires
- Matheus Medeiros de Carvalho
- [Nome completo do aluno 4]

## Professores responsáveis

- Wladmir Cardoso Brandao
- Sandro Jerônimo de Almeida

## Instruções de utilização

Para compilar e executar o projeto localmente para avaliação, siga os passos abaixo:

### 1. Pré-requisitos

- **Java Development Kit (JDK):** Versão 11 ou superior.
- **IDE (Ambiente de Desenvolvimento):** Eclipse (recomendado), IntelliJ IDEA ou VS Code.
- **Maven:** Para gestão de dependências.
- **Banco de Dados:** PostgreSQL (Pode ser local ou hospedado na nuvem).

### 2. Configuração do Banco de Dados

1. Abra a sua ferramenta de gestão do PostgreSQL (ex: pgAdmin ou DBeaver).
2. Execute o script SQL localizado em `Codigo/src/main/resources/dump.sql` para criar todas as tabelas (utilizadores, fórum, quizzes, etc.) e as respetivas relações.
3. No código-fonte do projeto, navegue até `Codigo/src/main/java/dao/DAO.java` e certifique-se de que as credenciais (URL, utilizador e senha) correspondem ao banco de dados onde o script foi executado.

### 3. Configuração da Inteligência Artificial (Azure OpenAI)

O projeto utiliza a SDK oficial da OpenAI e autenticação do Azure para os recursos inteligentes.

1. Se as variáveis de ambiente não estiverem configuradas na máquina, certifique-se de que a chave da API (Bearer Token) está corretamente inserida ou configurada nas classes `UsuarioService.java` e `QuizService.java` antes da execução.

### 4. Executando a Aplicação

1. Importe a pasta `Codigo` como um **Projeto Maven** na sua IDE.
2. Aguarde o Maven baixar todas as dependências especificadas no `pom.xml` (Spark Java, PostgreSQL JDBC, jBCrypt, Gson, Azure Identity e OpenAI Java SDK).
3. Execute a classe principal `Aplicacao.java` localizada no pacote `app`.
4. O terminal exibirá a mensagem `"Servidor Wise Capital rodando em http://localhost:8080"`.
5. Abra o seu navegador web e aceda a `http://localhost:8080` (ou `http://localhost:8080/pages/auth/index.html` para ir direto ao login).
