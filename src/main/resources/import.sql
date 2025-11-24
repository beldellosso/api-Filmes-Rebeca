-- Inserir dados para a entidade Diretor
insert into Diretor (id, nome) values (1, 'Christopher Nolan');
insert into Diretor (id, nome) values (2, 'Quentin Tarantino');
insert into Diretor (id, nome) values (3, 'Greta Gerwig');

-- Inserir dados para a entidade Ator
insert into Ator (id, nome) values (1, 'Christian Bale');
insert into Ator (id, nome) values (2, 'Margot Robbie');
insert into Ator (id, nome) values (3, 'Ryan Gosling');
insert into Ator (id, nome) values (4, 'Leonardo DiCaprio');
insert into Ator (id, nome) values (5, 'Tom Hardy');

-- Inserir dados para a entidade Filme
insert into Filme (id, titulo, anoLancamento, genero, diretor_id) values (1, 'Oppenheimer', 2023, 'DRAMA', 1);
insert into Filme (id, titulo, anoLancamento, genero, diretor_id) values (2, 'Barbie', 2023, 'COMEDIA', 3);
insert into Filme (id, titulo, anoLancamento, genero, diretor_id) values (3, 'Inception', 2010, 'FICCAO_CIENTIFICA', 1);
insert into Filme (id, titulo, anoLancamento, genero, diretor_id) values (4, 'Pulp Fiction', 1994, 'ACAO', 2);

-- Inserir dados na tabela de junção para o relacionamento N-para-N (filme_ator)
insert into Filme_Ator (elenco_id, FilmeV2_id) values (1, 1);
insert into Filme_Ator (elenco_id, FilmeV2_id) values (2, 2);
insert into Filme_Ator (elenco_id, FilmeV2_id) values (3, 2);
insert into Filme_Ator (elenco_id, FilmeV2_id) values (5, 3);
insert into Filme_Ator (elenco_id, FilmeV2_id) values (4, 4);

-- Sequência para IDs
-- **CORREÇÃO APLICADA:** Usa FilmeV2_SEQ
alter sequence Diretor_SEQ restart with 4;
alter sequence Ator_SEQ restart with 6;
alter sequence Filme_SEQ restart with 5;