--
-- PostgreSQL database dump para Wise Capital
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- -----------------------------------------------------
-- Estrutura das Tabelas
-- -----------------------------------------------------

-- Tabela: niveis_trilha
CREATE TABLE public.niveis_trilha (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT
);

-- Tabela: usuarios (Substituindo pontos por streak_days e nível por quiz liberado)
CREATE TABLE public.usuarios (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    streak_days INTEGER DEFAULT 0, -- Sistema de ofensiva
    quiz_liberado_maximo INTEGER DEFAULT 1, -- Define até qual quiz o utilizador tem acesso livre (1, 15, 30, etc)
    data_ultimo_acesso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    adm BOOLEAN DEFAULT FALSE -- Flag de administrador
);

-- Tabela: questionario_diagnostico (Para a IA analisar)
CREATE TABLE public.questionario_diagnostico (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES public.usuarios(id),
    respostas_json TEXT,
    nivel_sugerido_ia INTEGER REFERENCES public.niveis_trilha(id),
    data_realizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela: quizzes (Removido nivel_id, agora os quizzes vão de 1 a 100)
CREATE TABLE public.quizzes (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL
);

-- Tabela: usuario_quizzes_feitos (Rastreia quais quizzes o utilizador já completou de facto)
CREATE TABLE public.usuario_quizzes_feitos (
    usuario_id INTEGER REFERENCES public.usuarios(id),
    quiz_id INTEGER REFERENCES public.quizzes(id),
    data_conclusao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, quiz_id)
);

-- Tabela: perguntas
CREATE TABLE public.perguntas (
    id SERIAL PRIMARY KEY,
    quiz_id INTEGER REFERENCES public.quizzes(id),
    pergunta TEXT NOT NULL,
    tipo VARCHAR(20) DEFAULT 'FECHADA',
    correta INTEGER, 
    explicacao TEXT
);

-- Tabela: opcoes_pergunta
CREATE TABLE public.opcoes_pergunta (
    id SERIAL PRIMARY KEY,
    pergunta_id INTEGER REFERENCES public.perguntas(id),
    texto TEXT NOT NULL
);

DROP TABLE IF EXISTS public.forum_comentarios;
DROP TABLE IF EXISTS public.forum_topicos;

CREATE TABLE public.forum_topicos (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES public.usuarios(id),
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL,
    imagem_url TEXT,
    likes INTEGER DEFAULT 0,
    dislikes INTEGER DEFAULT 0,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.forum_comentarios (
    id SERIAL PRIMARY KEY,
    topico_id INTEGER REFERENCES public.forum_topicos(id),
    usuario_id INTEGER REFERENCES public.usuarios(id),
    conteudo TEXT NOT NULL,
    likes INTEGER DEFAULT 0,
    dislikes INTEGER DEFAULT 0,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comentario_pai_id INTEGER
);

-- Tabela: investimentos (Dados vindos do db.json original)
CREATE TABLE public.investimentos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100),
    risco VARCHAR(20),
    retorno DECIMAL(5,2),
    categoria VARCHAR(50)
);

-- Tabela: faq
CREATE TABLE public.faq (
    id SERIAL PRIMARY KEY,
    pergunta TEXT,
    resposta TEXT,
    acessos INTEGER DEFAULT 0 -- Nova coluna de contagem de views
);
CREATE TABLE public.biblioteca (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    tipo VARCHAR(50) NOT NULL, -- 'TEXTO' ou 'VIDEO'
    categoria VARCHAR(100),    -- Ex: 'Renda Fixa', 'Ações', 'Introdução'
    url_conteudo TEXT NOT NULL
);

-- -----------------------------------------------------
-- Permissões
-- -----------------------------------------------------

-- Ajuste o 'wise' para o seu nome de utilizador do pgAdmin se for diferente
ALTER TABLE public.niveis_trilha OWNER TO wise;
ALTER TABLE public.usuarios OWNER TO wise;
ALTER TABLE public.questionario_diagnostico OWNER TO wise;
ALTER TABLE public.quizzes OWNER TO wise;
ALTER TABLE public.usuario_quizzes_feitos OWNER TO wise;
ALTER TABLE public.perguntas OWNER TO wise;
ALTER TABLE public.opcoes_pergunta OWNER TO wise;
ALTER TABLE public.forum_topicos OWNER TO wise;
ALTER TABLE public.forum_comentarios OWNER TO wise;
ALTER TABLE public.investimentos OWNER TO wise;
ALTER TABLE public.faq OWNER TO wise;
ALTER TABLE public.biblioteca OWNER TO wise;

--
-- PostgreSQL database dump complete
---- Criar tabela para os likes/dislikes dos Tópicos
CREATE TABLE public.forum_topico_likes (
    usuario_id INTEGER REFERENCES public.usuarios(id) ON DELETE CASCADE,
    topico_id INTEGER REFERENCES public.forum_topicos(id) ON DELETE CASCADE,
    tipo VARCHAR(10) NOT NULL, -- Vai guardar 'like' ou 'dislike'
    PRIMARY KEY (usuario_id, topico_id)
);

-- Criar tabela para os likes/dislikes dos Comentários
CREATE TABLE public.forum_comentario_likes (
    usuario_id INTEGER REFERENCES public.usuarios(id) ON DELETE CASCADE,
    comentario_id INTEGER REFERENCES public.forum_comentarios(id) ON DELETE CASCADE,
    tipo VARCHAR(10) NOT NULL, -- Vai guardar 'like' ou 'dislike'
    PRIMARY KEY (usuario_id, comentario_id)
);

-- Dar as permissões ao utilizador 'wise' (ajuste se o seu utilizador do banco for outro)
ALTER TABLE public.forum_topico_likes OWNER TO wise;
ALTER TABLE public.forum_comentario_likes OWNER TO wise;