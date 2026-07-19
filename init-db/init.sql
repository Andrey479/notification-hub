-- Este script simula o schema que, em produção real, já existe no banco
-- porque o Library Manager (serviço proprietário do schema) o cria primeiro.
-- O Notification Hub usa ddl-auto=validate: ele NUNCA cria ou altera tabelas,
-- apenas valida que o schema esperado já existe.
--
-- Este schema espelha fielmente a entidade Loan do Library Manager,
-- incluindo colunas que o Notification Hub não usa (book_id, user_id, fine, etc.),
-- para que o ambiente local reproduza com precisão o banco compartilhado real.

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