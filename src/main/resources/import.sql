-- Inserir dados para a entidade Diretor
insert into diretor (id, nome) values (1, 'Christopher Nolan');
insert into diretor (id, nome) values (2, 'Quentin Tarantino');
insert into diretor (id, nome) values (3, 'Greta Gerwig');

-- Inserir dados para a entidade Ator
insert into ator (id, nome) values (1, 'Christian Bale');
insert into ator (id, nome) values (2, 'Margot Robbie');
insert into ator (id, nome) values (3, 'Ryan Gosling');
insert into ator (id, nome) values (4, 'Leonardo DiCaprio');
insert into ator (id, nome) values (5, 'Tom Hardy');

-- Inserir dados para a entidade Filme
insert into filme (id, titulo, anoLancamento, sinopse, genero, diretor_id) values (1, 'Oppenheimer', 2023, 'A história do pai da bomba atômica.', 'DRAMA', 1);
insert into filme (id, titulo, anoLancamento, sinopse, genero, diretor_id) values (2, 'Barbie', 2023, 'A boneca mais famosa do mundo.', 'COMEDIA', 3);
insert into filme (id, titulo, anoLancamento, sinopse, genero, diretor_id) values (3, 'Inception', 2010, 'A arte de entrar nos sonhos dos outros.', 'FICCAO_CIENTIFICA', 1);
insert into filme (id, titulo, anoLancamento, sinopse, genero, diretor_id) values (4, 'Pulp Fiction', 1994, 'O filme que marcou o cinema independente.', 'ACAO', 2);

-- Inserir dados na tabela de junção para o relacionamento N-para-N (filme_ator)
insert into filme_ator (atores_id, filme_id) values (1, 1);
insert into filme_ator (atores_id, filme_id) values (2, 2);
insert into filme_ator (atores_id, filme_id) values (3, 2);
insert into filme_ator (atores_id, filme_id) values (5, 3);
insert into filme_ator (atores_id, filme_id) values (4, 4);

-- Sequência para IDs
alter sequence diretor_seq restart with 4;
alter sequence ator_seq restart with 6;
alter sequence filme_seq restart with 5;