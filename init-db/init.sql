-- Este script simula o schema que, em produção real, já existe no banco
-- porque o Library Manager (serviço proprietário do schema) o cria primeiro.
-- O Notification Hub usa ddl-auto=validate: ele NUNCA cria ou altera tabelas,
-- apenas valida que o schema esperado já existe.

CREATE TABLE IF NOT EXISTS loans (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    loan_date DATE NOT NULL,
    expected_return_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(255) NOT NULL,
    fine NUMERIC
);

-- books.cover_url / synopsis / page_count: colunas que o Library Manager
-- ainda vai adicionar em produção (pendente, sem data definida).
-- Incluídas aqui porque a entidade Book do Notification Hub já as declara
-- como parte do contrato de enriquecimento — ddl-auto=validate precisa
-- do schema completo do contrato para o ambiente local ser coerente,
-- mesmo que produção ainda não tenha alcançado esse estado.
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(255) NOT NULL UNIQUE,
    publication_year INTEGER NOT NULL,
    total_copies INTEGER NOT NULL,
    available_copies INTEGER NOT NULL,
    cover_url VARCHAR(255),
    synopsis TEXT,
    page_count INTEGER
);