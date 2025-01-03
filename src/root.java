import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class root 
{


static boolean resetarDatabase(Connection connection)
{
    Statement stmt;
    boolean resultado = true;
    try 
    {
        stmt = connection.createStatement();

        stmt.execute("DROP ROLE IF EXISTS admin;");
        stmt.execute("flush privileges;");
        stmt.execute("CREATE ROLE admin;");


        stmt.execute("DROP DATABASE IF EXISTS webdriver;");
        stmt.execute("CREATE DATABASE webdriver;");
        stmt.execute("use webdriver;");

        stmt.execute
        (
        "CREATE TABLE Plano " +
        "(id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "nome VARCHAR(100) NOT NULL," +
        "duracao DATE," + 
        "limite_users INT);"
        );
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Plano to admin;");

        stmt.execute
        (
        "CREATE TABLE instituicao" +
        "(id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "nome varchar(100) NOT NULL," +
        "causa_social TEXT," +
        "endereco TEXT NOT NULL," +
        "data_aquisicao DATE NOT NULL," +
        "plano_id INT NOT NULL," +
        
        "CONSTRAINT fk_plano_id " +
        "FOREIGN KEY (plano_id) REFERENCES Plano(id) ON DELETE CASCADE );" 
        //"FOREIGN KEY (plano_id) REFERENCES Plano(id) ON DELETE RESTRICT ON UPDATE RESTRICT);" 
        );
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.instituicao to admin;");

        stmt.execute
        (
        "CREATE TABLE Administrador" +
        "(id INT PRIMARY KEY NOT NULL);" 
        );
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Administrador to admin;");

        stmt.execute(
        "CREATE TABLE Usuario (" +
        "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "login VARCHAR(100) NOT NULL," +
        "email VARCHAR(100) NOT NULL," + 
        "senha VARCHAR(100) NOT NULL," +
        "data_ingresso DATE NOT NULL," +
        "id_instituicao INT," +
        "id_admin INT," +

        "FOREIGN KEY (id_instituicao) REFERENCES instituicao(id) ON DELETE RESTRICT," +
        "FOREIGN KEY (id_admin) REFERENCES Administrador(id)" +
        ");");
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Usuario to admin;");


        stmt.execute
        (  
        "CREATE TABLE Arquivo (" +
        "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "conteudo TEXT," +
        "nome VARCHAR(100) NOT NULL," +
        "tipo VARCHAR(100) NOT NULL," +
        "permissoes INT," +
        "data_alteracao DATE," +
        "tamanho BIGINT UNSIGNED," +
        "url VARCHAR(100)," +
        "localizacao VARCHAR(100) NOT NULL," +
        "id_dono INT," +
    
        "FOREIGN KEY (id_dono) REFERENCES Usuario(id) ON DELETE CASCADE" +");"    
        ); 
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Arquivo to admin;");

        stmt.execute
        (  
        "CREATE TABLE Resposta (" +
        "id_resposta INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "descricao VARCHAR(100) NOT NULL," +
        "id_admin INT," +
        
        "CONSTRAINT fk_id_admin " +
        "FOREIGN KEY (id_admin) REFERENCES Administrador(id) ON DELETE CASCADE" + 
        ");"
        ); 
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Resposta to admin;");

        stmt.execute
        (
        "CREATE TABLE Suporte (" +
        "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "descricao TEXT," +
        "data DATE," +
        "hora TIME," +
        "status INT," +
        "id_usuario INT," +
        "id_resposta INT," +

        "FOREIGN KEY (id_usuario) REFERENCES Usuario(id) ON DELETE CASCADE," +
        "FOREIGN KEY (id_resposta) REFERENCES Resposta(id_resposta) ON DELETE CASCADE" +
        ");");
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Suporte to admin;");

        stmt.execute("create table Comentario("+
                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "conteudo TEXT," +
                "hora TIME," +
                "data DATE," +
                "id_autor INT,"
                +"id_arquivo INT,"
                + "FOREIGN KEY (id_arquivo) REFERENCES Arquivo(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (id_autor) REFERENCES Usuario(id) ON DELETE CASCADE);"
            );
            stmt.execute("GRANT ALL PRIVILEGES on webdriver.Comentario to admin;");
        
        stmt.execute("create table Compartilhamento("+
        	"id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "id_dono INT, "+
                "id_arquivo INT, "+
                "id_usuario_compartilhado INT,"+
                "data DATE,"
                + "FOREIGN KEY (id_dono) REFERENCES Usuario(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (id_arquivo) REFERENCES Arquivo(id) ON DELETE CASCADE,"
                + "FOREIGN KEY(id_usuario_compartilhado) REFERENCES Usuario(id) ON DELETE CASCADE);"
            );
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Compartilhamento to admin;");

        stmt.execute("create table Versionamento("+
        	"id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "conteudo TEXT," +
                "data DATE," +
                "hora TIME," +
                "operacao INT, "+
                "id_autor INT, "+
                "id_arquivo INT, "+
                "FOREIGN KEY (id_autor) REFERENCES Usuario(id) ON DELETE SET NULL, " +
                "FOREIGN KEY (id_arquivo) REFERENCES Arquivo(id) ON DELETE CASCADE); "
            );
            stmt.execute("GRANT ALL PRIVILEGES on webdriver.Versionamento to admin;");

            stmt.execute("create table Atividades_recentes("+
        	    "id_arquivo INT," +
                "data DATE," +
                "acesso TINYINT," +
                "FOREIGN KEY (id_arquivo) REFERENCES Arquivo(id) ON DELETE CASCADE); "
            );

            stmt.execute("create table Registro_operacoes("+
            "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
            "data DATE," +
            "hora TIME," +
            "id_arquivo INT," +
            "operacao INT, "+
            "id_autor INT, "+
            "id_alvo INT DEFAULT NULL ); "
            );

           



    //// coisas de pessoas e roles 
    /// 
        stmt.execute("DROP ROLE IF EXISTS usuario;"); // usuarios e roles ficam fora da db
        stmt.execute("DROP ROLE IF EXISTS instituicao;");
        stmt.execute("flush privileges;");
        stmt.execute("CREATE ROLE usuario;");
        stmt.execute("GRANT INSERT on webdriver.Arquivo to usuario;");

        stmt.execute("GRANT INSERT on webdriver.Suporte to usuario;");
        stmt.execute("GRANT INSERT on webdriver.Comentario to usuario;");

        stmt.execute("CREATE ROLE instituicao;");


        


    /// functions
    
    stmt.execute("DROP FUNCTION IF EXISTS echoVarchar");
    stmt.execute
    (
    "create function echoVarchar() returns VARCHAR(100) return @echoVarchar;" 
    );

    stmt.execute("DROP FUNCTION IF EXISTS echoInt");
    stmt.execute
    (
    "create function echoInt() returns INT return @echoInt;"
    );


    stmt.execute("DROP FUNCTION IF EXISTS maisQueCemDias");
    stmt.execute
    (
    "create function maisQueCeMDias(input INT) returns TINYINT " +
    "BEGIN " +
    "DECLARE data_arquivo DATE; " +
    "DECLARE diferenca INT; " +
    "SET data_arquivo = (SELECT data from Atividades_recentes WHERE (id_arquivo = input) ); " +
    "SET diferenca = (DATEDIFF(CURDATE(), data_arquivo)); " +
    "if diferenca > 100 then  " +
    "RETURN 1; " +
    "end if; " +
    "if diferenca <= 100 then  " +
    "RETURN 0; " +
    "end if; " +
    "END"
    );

    stmt.execute("DROP VIEW IF EXISTS getInstituicaoInfo");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW getInstituicaoInfo AS  "+
    "select id, causa_social, endereco, data_aquisicao, plano_id FROM Instituicao where nome = echoVarchar(); "
    );
    stmt.execute("GRANT SELECT on webdriver.getInstituicaoInfo to instituicao;");
    stmt.execute("GRANT SELECT on webdriver.getInstituicaoInfo to admin;");

    stmt.execute("DROP VIEW IF EXISTS getPlanoInfo");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW getPlanoInfo AS  "+
    "select nome, duracao, limite_users FROM Plano where id = echoInt(); "
    );
    stmt.execute("GRANT SELECT on webdriver.getPlanoInfo to instituicao;");
    stmt.execute("GRANT SELECT on webdriver.getPlanoInfo to admin;");

    
    stmt.execute("DROP VIEW IF EXISTS getUserInfo");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW getUserInfo AS  "+
    "select u.id, u.id_admin, u.email, u.data_ingresso, i.nome FROM Usuario u LEFT JOIN Instituicao i on (u.id_instituicao = i.id) where u.login = echoVarchar(); "
    );
    stmt.execute("GRANT SELECT on webdriver.getUserInfo to usuario;");
    stmt.execute("GRANT SELECT on webdriver.getUserInfo to admin;");


    stmt.execute("DROP VIEW IF EXISTS verMeusSuportes");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW verMeusSuportes AS  "+
    "select s.descricao, s.data, s.hora, s.status, r.descricao as resposta FROM Suporte s LEFT JOIN Resposta r on(s.id_resposta = r.id_resposta) where id_usuario = echoInt()  ; "
    );
    stmt.execute("GRANT SELECT on webdriver.verMeusSuportes to usuario;");
    stmt.execute("GRANT SELECT on webdriver.verMeusSuportes to admin;");

    stmt.execute("DROP VIEW IF EXISTS verMeusArquivos");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW verMeusArquivos AS  "+
    "select a.conteudo, a.nome, a.tipo, a.permissoes, a.data_alteracao, a.tamanho, a.url FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) where id_dono = echoInt(); "
    );
    stmt.execute("GRANT SELECT on webdriver.verMeusArquivos to usuario;");
    stmt.execute("GRANT SELECT on webdriver.verMeusArquivos to admin;");

    stmt.execute("DROP VIEW IF EXISTS verArquivosInstituicao");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW verArquivosInstituicao AS  "+
    "select u.login, a.nome, a.conteudo, a.tipo, a.permissoes, a.data_alteracao, a.tamanho, a.url FROM Usuario u INNER JOIN Arquivo a on (a.id_dono = u.id) where permissoes = 1 AND id_instituicao = echoInt(); "
    );
    stmt.execute("GRANT SELECT on webdriver.verArquivosInstituicao to instituicao;");
    stmt.execute("GRANT SELECT on webdriver.verArquivosInstituicao to admin;");


    stmt.execute("DROP VIEW IF EXISTS arquivosCompartilhadosComInstituicao"); // usuario vendo arquivos
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW arquivosCompartilhadosComInstituicao AS  " +
    "select a.conteudo, a.nome, a.tipo, a.data_alteracao, a.tamanho, a.url, u.login FROM Arquivo a LEFT JOIN Usuario u on (u.id = a.id_dono) LEFT JOIN Instituicao i on (u.id_instituicao = i.id) where permissoes = 1 AND i.nome = echoVarchar(); "
    );
    stmt.execute("GRANT SELECT on webdriver.arquivosCompartilhadosComInstituicao to usuario;");
    stmt.execute("GRANT SELECT on webdriver.arquivosCompartilhadosComInstituicao to admin;");


    stmt.execute("DROP VIEW IF EXISTS verMembrosInstituicao"); // instituicao vendo arquivos
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW verMembrosInstituicao AS  "+
    "select login, email, senha, data_ingresso FROM Usuario where id_instituicao = echoInt(); "
    );
    stmt.execute("GRANT SELECT on webdriver.verMembrosInstituicao to instituicao;");
    stmt.execute("GRANT SELECT on webdriver.verMembrosInstituicao to admin;");



    stmt.execute("DROP VIEW IF EXISTS arquivosCompartilhadosComigo");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW arquivosCompartilhadosComigo AS  "+
    "select a.conteudo, a.nome, a.tipo, a.data_alteracao, a.tamanho, a.url, u.login FROM Arquivo a LEFT JOIN Usuario u on (u.id = a.id_dono) LEFT JOIN Compartilhamento c on (a.id = c.id_arquivo) where id_usuario_compartilhado = echoInt(); "
    );
    stmt.execute("GRANT SELECT on webdriver.arquivosCompartilhadosComigo to usuario;");
    stmt.execute("GRANT SELECT on webdriver.arquivosCompartilhadosComigo to admin;");





 // triggers

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS safe_security "+
        "BEFORE INSERT ON Arquivo FOR EACH ROW " +
        "BEGIN " +
        "if new.tipo = 'exe' then " +
        "signal sqlstate '45000' set message_text = 'Erro : Arquivos executaveis nao podem ser inseridos!'; " +
        "end if; "+
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS arquivo_duplicado "+
        "BEFORE INSERT ON Arquivo FOR EACH ROW " +
        "BEGIN " +
        "DECLARE checkDuplicado INT; " +
        "SET checkDuplicado = ( SELECT EXISTS ( select * from Arquivo WHERE (nome = new.nome) AND (tipo = new.tipo) AND (id_dono = new.id_dono) ) ); " +
        "if checkDuplicado = 1 then " +
        "signal sqlstate '45000' set message_text = 'Erro : Voce ja tem um arquivo com o mesmo nome e tipo!'; " +
        "end if; "+
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS Usuario_duplicado "+
        "BEFORE INSERT ON Usuario FOR EACH ROW " +
        "BEGIN " +
        "DECLARE checkDuplicado INT; " +
        "DECLARE checkDuplicado2 INT; " +
        "SET checkDuplicado = ( SELECT EXISTS ( select * from Usuario WHERE (login = new.login) ) ); " +
        "SET checkDuplicado2 = ( SELECT EXISTS ( select * from Instituicao WHERE (nome = new.login) ) ); " +
        "if checkDuplicado = 1 then " +
        "signal sqlstate '45000' set message_text = 'Erro : Ja existe um usuario com esse login!'; " +
        "end if; "+
        "if checkDuplicado2 = 1 then " +
        "signal sqlstate '45000' set message_text = 'Erro : Ja existe uma instituicao com esse login!'; " +
        "end if; "+
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS Instituicao_duplicada "+
        "BEFORE INSERT ON Instituicao FOR EACH ROW " +
        "BEGIN " +
        "DECLARE checkDuplicado INT; " +
        "DECLARE checkDuplicado2 INT; " +
        "SET checkDuplicado = ( SELECT EXISTS ( select * from Instituicao WHERE (nome = new.nome) ) ); " +
        "SET checkDuplicado2 = ( SELECT EXISTS ( select * from Usuario WHERE (login = new.nome) ) ); " +
        "if checkDuplicado = 1 then " +
        "signal sqlstate '45000' set message_text = 'Erro : Ja existe uma instituicao com esse login!'; " +
        "end if; "+
        "if checkDuplicado2 = 1 then " +
        "signal sqlstate '45000' set message_text = 'Erro : Ja existe um usuario com esse login!'; " +
        "end if; "+
        "END"
    );


    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS auto_compartilhamento "+
        "BEFORE INSERT ON Compartilhamento FOR EACH ROW " +
        "BEGIN " +
        "if new.id_dono = new.id_usuario_compartilhado then " +
        "signal sqlstate '45000' set message_text = 'Erro : Nao pode compartilhar arquivo com si mesmo!'; " +
        "end if; "+
        "END"
    );


    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS versionamento_criacao "+
        "AFTER INSERT ON Arquivo FOR EACH ROW " +
        "BEGIN " +
        "INSERT INTO Versionamento (conteudo, data, hora, operacao, id_autor, id_arquivo) VALUES (new.conteudo, CURDATE(), CURTIME(), 1, new.id_dono, new.id); " +
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS versionamento_update "+
        "AFTER UPDATE ON Arquivo FOR EACH ROW " +
        "BEGIN " +
        "DECLARE nome VARCHAR(100); " +
        "DECLARE targetID INT; " +
        "SET nome = ( USER() ); " +
        "SET nome = ( LEFT(nome, CHAR_LENGTH(nome) - 10) ); " +
        "SET targetID = ( SELECT id from Usuario WHERE (login = nome) ); " +
        "INSERT INTO Versionamento (conteudo, data, hora, operacao, id_autor, id_arquivo) VALUES (new.conteudo, CURDATE(), CURTIME(), 2, targetID, new.id); " +
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS registrar_Criacao "+
        "AFTER INSERT ON Arquivo FOR EACH ROW " +
        "BEGIN " +
        "INSERT INTO Atividades_recentes (id_arquivo, data, acesso) VALUES (new.id, new.data_alteracao, 1); " +
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS registrar_Alteracao "+
        "AFTER UPDATE ON Arquivo FOR EACH ROW " +
        "BEGIN " +
        "UPDATE Atividades_recentes SET data = new.data_alteracao WHERE id_arquivo = new.id; " +
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS log_operacoes_versionamento "+
        "AFTER INSERT ON Versionamento FOR EACH ROW " +
        "BEGIN " +
        "INSERT INTO Registro_operacoes (data, hora, id_arquivo, operacao, id_autor) VALUES (new.data, new.hora, new.id_arquivo, new.operacao, new.id_autor) ; " +
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS log_remover_arquivo "+
        "AFTER DELETE ON Arquivo FOR EACH ROW " +
        "BEGIN " +
        "INSERT INTO Registro_operacoes (data, hora, id_arquivo, operacao, id_autor) VALUES (CURDATE(), CURTIME(), old.id, 3, old.id_dono) ; " +
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS log_operacoes_compartilhamento "+
        "AFTER INSERT ON Compartilhamento FOR EACH ROW " +
        "BEGIN " +
        "INSERT INTO Registro_operacoes (data, hora, id_arquivo, operacao, id_autor, id_alvo) VALUES (new.data, CURTIME(), new.id_arquivo, 4, new.id_dono, new.id_usuario_compartilhado) ; " +
        "END"
    );

    stmt.execute
    (
        "CREATE DEFINER=`root`@`localhost` TRIGGER IF NOT EXISTS log_remocao_compartilhamento "+
        "AFTER DELETE ON Compartilhamento FOR EACH ROW " +
        "BEGIN " +
        "INSERT INTO Registro_operacoes (data, hora, id_arquivo, operacao, id_autor, id_alvo) VALUES (CURDATE(), CURTIME(), old.id_arquivo, 5, old.id_dono, old.id_usuario_compartilhado) ; " +
        "END"
    );


    // procedures

    stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS Chavear" + 
    "(IN input INT) " +
    "BEGIN " +
    "DECLARE resultado TINYINT; " +
    "SET resultado = ( SELECT acesso FROM Atividades_recentes WHERE (id_arquivo = input) ); " +
    "if resultado = 1 then " +
    "UPDATE Atividades_recentes SET acesso = 0 WHERE id_arquivo = input; " +
    "end if; " +
    "if resultado = 0 then " +
    "UPDATE Atividades_recentes SET acesso = 1 WHERE id_arquivo = input; " +
    "end if; " +
    "END"
    );

    stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS Atualizar_acessos()" + 
    "BEGIN " +

    "UPDATE Atividades_recentes " +
    " SET acesso = 0 WHERE (acesso = 1) AND (maisQueCemDias(id_arquivo) = 1); " +

    "UPDATE Atividades_recentes " +
    " SET acesso = 1 WHERE (acesso = 0) AND (maisQueCemDias(id_arquivo) = 0); " +
    "END"
    );



    /// procedures

    stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS getArqID" +
    "(IN thisid INT, IN nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100), OUT result INT ) " +
    "BEGIN " +
    "DECLARE checkPerm INT; " +
    "SET checkPerm = ( SELECT EXISTS ( SELECT * FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) WHERE (id_dono = thisid) AND (tipo = tipoConfirm) AND (nome = nomeConfirm) ) AS result); " +
    "if checkPerm = 1 then " +
    "SET result = ( SELECT a.id FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) WHERE  (id_dono = thisid) AND (tipo = tipoConfirm) AND (nome = nomeConfirm)); " +
    "SET checkPerm = 2; " +
    "end if; " +

    "if checkPerm = 0 then " + 
    "SET checkPerm = ( SELECT EXISTS ( SELECT a.id from Arquivo a INNER JOIN Compartilhamento c on (a.id = c.id_arquivo) AND (a.id_dono = c.id_dono) WHERE (a.nome = nomeConfirm) AND (a.tipo = tipoConfirm) ) AS result); " +
    "end if; " +
    "if checkPerm = 0 then " +
    "signal sqlstate '45000' set message_text = 'Erro : Nao tem permissao pra ver o arquivo!'; SET result = checkPerm; " +
    "end if; " +
    "if checkPerm = 1 then " +
    "SET result = (SELECT a.id from Arquivo a INNER JOIN Compartilhamento c on (a.id = c.id_arquivo) AND (a.id_dono = c.id_dono) WHERE (a.nome = nomeConfirm) AND (a.tipo = tipoConfirm) );  " +

    "end if; " +
    "END"
    );

    stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS hostOnlyArqID" +
    "(IN thisid INT, IN nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100), OUT result INT ) " +
    "BEGIN " +
    "DECLARE checkPerm INT; " +
    "SET checkPerm = ( SELECT EXISTS ( SELECT * FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) WHERE (id_dono = thisid) AND (tipo = tipoConfirm) AND (nome = nomeConfirm) ) AS result); " +
    "if checkPerm = 1 then " +
    "SET result = ( SELECT a.id FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) WHERE  (id_dono = thisid) AND (tipo = tipoConfirm) AND (nome = nomeConfirm)); " +
    "end if; " +

    "if checkPerm = 0 then " +
    "signal sqlstate '45000' set message_text = 'Erro : Nao e o dono do arquivo arquivo!'; SET result = checkPerm; " +
    "end if; " +
    
    "END"
    );

    stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS anyArqID" +
    "(IN hostlogin VARCHAR(100), IN nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100), OUT result INT) " +
    "BEGIN " +
    "DECLARE hostID INT; " +
    "SET hostID = ( SELECT u.id FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) WHERE (login = hostlogin) AND (tipo = tipoConfirm) and (nome = nomeConfirm) ); " +
    "SET result = ( SELECT a.id FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) WHERE (id_dono = hostID) AND (tipo = tipoConfirm) AND (nome = nomeConfirm) ); " +
    "END"
    );

    stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS getQtdCompartilhamentos" +
   "(IN ownerid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100), OUT result INT) " +
   "BEGIN " +
   "CALL hostOnlyArqID(ownerid, nomeConfirm, tipoConfirm, @arqID); " +
   "SET result = ( SELECT COUNT(*) FROM Compartilhamento WHERE (id_arquivo = @arqID) ); " +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.getQtdCompartilhamentos to usuario;");
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.getQtdCompartilhamentos to admin;");


   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS Remover_Acessos" +
   "(IN ownerid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100)) " +
   "BEGIN " +
   "CALL hostOnlyArqID(ownerid, nomeConfirm, tipoConfirm, @hostOnlyArqID); " +
   "DELETE FROM Compartilhamento WHERE (id_arquivo = @arqID); " +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.Remover_Acessos to usuario;");
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.Remover_Acessos to admin;");


   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS Remover_Arquivo" +
   "(IN ownerid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100)) " +
   "BEGIN " +
   "CALL hostOnlyArqID(ownerid, nomeConfirm, tipoConfirm, @arqID); " +
   "DELETE FROM Arquivo WHERE (id = @arqID); " +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.Remover_Arquivo to usuario;");
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.Remover_Acessos to admin;");


   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS Atualizar_Arquivo" +
   "(IN userid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100), IN novoConteudo TEXT) " +
   "BEGIN " +
   "CALL getArqID(userid, nomeConfirm, tipoConfirm, @arqID); " +
   "UPDATE Arquivo SET conteudo = novoConteudo, data_alteracao = CURDATE() WHERE (id = @arqID); " +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.Atualizar_Arquivo to usuario;");
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.Atualizar_Arquivo to admin;");


   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS CriarComentario" +
   "(IN userid INT, IN hostlogin VARCHAR(100), in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100), IN novoComentario TEXT )" +
   "BEGIN " +
   "CALL anyArqID(hostlogin, nomeConfirm, tipoConfirm, @arqID); " +
   "INSERT INTO Comentario (conteudo, hora, data, id_autor, id_arquivo) VALUES (novoComentario, CURTIME(), CURDATE(), userid, @arqID); " +
   "END" 
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.CriarComentario to usuario;");
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.CriarComentario to admin;");
   

   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS verComentarios" +
   "(IN hostlogin VARCHAR(100), in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100) ) " +
   "BEGIN " +   
   "CALL anyArqID(hostlogin, nomeConfirm, tipoConfirm, @arqID); " +
   "SELECT c.conteudo, c.hora, c.data, u.login FROM Comentario c LEFT JOIN Usuario u on (u.id = c.id_autor) where id_arquivo = @arqID;" +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.verComentarios to usuario;");
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.verComentarios to instituicao;");
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.verComentarios to admin;");


   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS verVersionamento" +
   "(IN hostlogin VARCHAR(100), in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100) ) " +
   "BEGIN " +   
   "CALL anyArqID(hostlogin, nomeConfirm, tipoConfirm, @arqID); " +
   "SELECT v.conteudo, v.data, v.hora, v.operacao, u.login FROM Versionamento v LEFT JOIN Usuario u on (u.id = v.id_autor) where id_arquivo = @arqID;" +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.verVersionamento to usuario;");
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.verVersionamento to instituicao;");
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.verVersionamento to admin;");




    stmt.execute( "CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS compartilharArquivo" +
                " (IN ownerID INT, IN tipoConfirm VARCHAR(100), IN nomeConfirm VARCHAR(100), IN targetlogin VARCHAR(100))" +
                "BEGIN " +
                "DECLARE checkOwner INT; " +
                "DECLARE checkTarget INT; " +
                "DECLARE checkRepeated INT; " +
                "DECLARE id_arq INT; " +
                "DECLARE id_target INT; " +
                "SET checkOwner = (SELECT EXISTS (SELECT * FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) WHERE (id_dono = ownerID) ) AS result); " +
                "if checkOwner = 0 then " +
                "signal sqlstate '45000' set message_text = 'Erro : Nao e dono do arquivo!'; " +
                "end if; " +
                "if checkOwner = 1 then " +
                "SET id_arq = (SELECT id from Arquivo WHERE (id_dono = ownerID) AND (nome = nomeConfirm) AND (tipo = tipoConfirm)); " +

                "SET checkTarget = (SELECT EXISTS (SELECT * from Usuario where (login = targetlogin)) as result); " +
                "if checkTarget = 0 then " +
                "signal sqlstate '45000' set message_text = 'Erro : Usuario com qual compartilhar nao existe!'; " +
                "end if; " +
                "if checkTarget = 1 then " +
                "SET id_target = (SELECT id from Usuario WHERE (login = targetlogin)); " + // clear

                "SET checkRepeated = (SELECT EXISTS (SELECT * FROM Compartilhamento WHERE (id_usuario_compartilhado = id_target) AND (id_arquivo = id_arq)) AS result); " +
                "if checkRepeated = 1 then " +
                "signal sqlstate '45000' set message_text = 'Erro : Compartilho com o usuario ja foi feito!'; " +
                "end if; " +
                "if checkRepeated = 0 then " +
                "INSERT INTO Compartilhamento (id_dono, id_arquivo, id_usuario_compartilhado, data) VALUES (ownerID, id_arq, id_target, CURDATE() ); " +
                "end if; " +
                "end if; " +
                "end if; " +
                "END"
        );
        stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.compartilharArquivo to usuario;");
        stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.compartilharArquivo to admin;");

        stmt.execute("INSERT INTO Plano (nome, duracao, limite_users) VALUES ('Plano_Default', '2030-10-10', 100000000)");


    }

    catch ( SQLException e ) { e.printStackTrace(); resultado = false; }

    return resultado;
}


static void criarUsuario(Connection connection, Scanner scan) 
{
    try 
    {
    Statement stmt = connection.createStatement();

    scan.nextLine();
    System.out.print("\nDigite o login do novo usuario :\n>>>");
    String login = scan.nextLine();
    System.out.print("\nDigite a senha do novo usuario :\n>>>");
    String senha = scan.nextLine();
    System.out.print("\nDigite o email do novo usuario :\n>>>");
    String email = scan.nextLine();
    System.out.print("\nDigite a instituicao do novo usuario, ou 0 para nenhuma :\n>>>");
    int instituicao = scan.nextInt();

    ResultSet result = stmt.executeQuery("SELECT CURDATE();");
    result.next();

    PreparedStatement prep = connection.prepareStatement("INSERT INTO Usuario (login, email, senha, data_ingresso, id_instituicao, id_admin) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

    prep.setString(1, login);
    prep.setString(2, email);
    prep.setString(3, senha);
    prep.setDate(4, java.sql.Date.valueOf(result.getString(1)));
    if (instituicao == 0) { prep.setNull(5, Types.INTEGER); } else { prep.setInt(5, instituicao); }
    prep.setNull(6, Types.INTEGER);
    prep.addBatch();
    prep.executeBatch();

    System.out.print ("tabela pessoa adicionada :)\n"); 
    
    stmt.execute("CREATE USER '"+login+"'@'localhost' IDENTIFIED BY '"+ senha +"';");
    System.out.print ("usuario adicionado :)\n\n");


    stmt.execute("GRANT usuario TO '"+login+"'@localhost");
    stmt.execute("SET DEFAULT ROLE usuario FOR '"+login+"'@localhost;");



    }
    catch (SQLException e) { e.printStackTrace(); }
}

static void verTabelaUsuarios(Connection connection, Scanner scan)
{
    try 
    {
        Statement stmt = connection.createStatement();
        Integer admincheck;
        Integer instituicaocheck;
        int check = 0;

        ResultSet result = stmt.executeQuery("SELECT * FROM Usuario;");
        while (result.next())
        {
            check++;
            System.out.print("\n----------------------------\n");
            System.out.print("ID : " + result.getInt("id") + "\n\n");
            System.out.print("Login : " + result.getString("login") + "\n");
            System.out.print("Email : " + result.getString("email") + "\n");
            System.out.print("Senha : " + result.getString("senha") + "\n");
            System.out.print("Data de ingresso : " + result.getDate("data_ingresso").toString() + "\n");
            System.out.print("Instituicao : ");
            instituicaocheck = result.getInt("id_instituicao");
            if (instituicaocheck == 0)
            {  System.out.print("Nenhuma\n");  }
            else
            {  System.out.print(instituicaocheck + "\n");  }

            System.out.print("Admin : ");
            
            admincheck = result.getInt("id_admin");
            if (admincheck == 0)
            {  System.out.print("Nao\n");  }
            else
            {  System.out.print(admincheck + "\n");  }

        }
        if (check == 0 ) { System.out.print("\nNao tem Usuarios registrados!\n");}
        scan.nextLine();
        System.out.print("\n\nAperte Enter para voltar. ");
        scan.nextLine();
    }
    catch (SQLException e) { e.printStackTrace(); }
}

static void verTabelaInstituicao(Connection connection, Scanner scan)
{
    try 
    {
        Statement stmt = connection.createStatement();
        int check = 0;
        ResultSet result = stmt.executeQuery("SELECT * FROM Instituicao;");
        while (result.next())
        {
            check++;
            System.out.print("\n----------------------------\n");
            System.out.print("ID : " + result.getInt("id") + "\n\n");
            System.out.print("Nome : " + result.getString("nome") + "\n");
            System.out.print("Data aquisicao : " + result.getDate("data_aquisicao").toString() + "\n");
            System.out.print("Endereco : " + result.getString("endereco") + "\n");
            System.out.print("\nCausa social :\n" + result.getString("causa_social") + "\n");
        }
        if (check == 0 ) { System.out.print("\nNao tem instituicoes registradas!\n");}
        scan.nextLine();
        System.out.print("\n\nAperte Enter para voltar. ");
        scan.nextLine();
        
    }
    catch (SQLException e) { e.printStackTrace(); }
    
}


static void removerUsuario(Connection connection, Scanner scan)
{
    scan.nextLine();
    System.out.print("\nDigite o ID do usuario que voce quer remover :\n\n>>>");
    String id = scan.nextLine();

    try
    {
        Statement stmt = connection.createStatement();

        ResultSet result = stmt.executeQuery("SELECT login, id_admin FROM Usuario WHERE (id = " + id + ");");
        while (result.next())
        {
            stmt.execute("DELETE FROM Usuario WHERE (id = " + id + ");");
            stmt.execute("DELETE FROM Administrador WHERE (id = " + result.getInt("id_admin") + ");");
            stmt.execute("DROP USER '" + result.getString("login") + "'@localhost;");
            stmt.execute("flush privileges;");
            
            System.out.print("\nUsuario removido com sucesso.\n"); return;
        }
        System.out.print("\nUsuario nao encontrado!\n"); 
    }
    catch ( SQLException e ) { e.printStackTrace(); }

}

/* static void RootAlterUser(Connection connection, Scanner scan){ // inutilizada, alguns erros, falta atualizar o usuario do database
    scan.nextLine();
    System.out.println("Digite o ID do usuario que você quer alterar :\n\n>>> ");
    String id = scan.nextLine();
    int action;
    try{
       Statement stmt = connection.createStatement();
       ResultSet result = stmt.executeQuery("SELECT login FROM Usuario WHERE (id = " + id + ");");
       
       if (result.next()) {
	       System.out.print("\nDigite o novo nome do usuario\n>>> ");
    	   String login = scan.nextLine();
    	   System.out.print("\nDigite o novo email do usuario\n>>> ");
    	   String email = scan.nextLine();
    	   System.out.print("\nDigite a nova senha do usuario\n>>> ");
    	   String senha = scan.nextLine();
    	   //System.out.print("\nDigite a nova instituição do usuario\n>>> ");
    	   //String instituicao = scan.nextLine();
    	   
	       if (login != "") {
	    	   stmt.execute("UPDATE Usuario SET login = '" + login + "' WHERE (id = " + id + ");");
	       }
           System.out.print("teste 1\n");
	       if (email != "") {
	    	   stmt.execute("UPDATE Usuario SET email = '" + email + "' WHERE (id = " + id + ");");
	       }
           System.out.print("teste 2\n");
	       if (senha != "") {
	    	   stmt.execute("UPDATE Usuario SET senha = '" + senha + "' WHERE (id = " + id + ");");
	       }
           //System.out.print("teste 3\n");
	       //if (instituicao != "") {
	    	//   stmt.execute("UPDATE Usuario SET id_instituicao = " + instituicao + " WHERE (id = " + id + ");");
	       //}
           System.out.print("\nteste 4\n");

       }
    }

    catch(SQLException e){
        e.printStackTrace();
    }
} */

static void grantAdmin(Connection connection, Scanner scan)
{
    scan.nextLine();
    System.out.print("\nDigite o ID do usuario que voce quer dar privilegios de admin:\n\n>>>");
    String id = scan.nextLine();

    try
    {
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT login, id_admin from Usuario WHERE (id = " + id + ");");
        if (!(result.next())) { System.out.print ("ID de usuario nao encontrado!\n"); return; }
        String login = result.getString("login");

        System.out.print("\nDigite o novo ID de admin desse usuario (por favor nao seja 0) : ");
        int new_admin_id = scan.nextInt();

        PreparedStatement prep = connection.prepareStatement("INSERT INTO Administrador (id) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
        prep.setInt(1, new_admin_id);
        prep.addBatch();
        prep.executeBatch();

        System.out.print ("adicionado na tabela admin :)\n"); 
        stmt.execute("UPDATE Usuario SET id_admin = " + new_admin_id + " WHERE (id = " + id + ");");
        System.out.print ("fk adicionada :)\n"); 
        stmt.execute("GRANT admin TO '"+login+"'@localhost");
        stmt.execute("SET DEFAULT ROLE admin FOR '"+login+"'@localhost;");
        System.out.print ("role atualizada com sucesso :)\n"); 
        
    } 
    catch (SQLException e ) { e.printStackTrace(); }
}

/*
static void alterarDadosPlano(Connection connection, Scanner scan) { // inutilizado, nao testado
        try {
            scan.nextLine();
            System.out.print("Digite o ID do plano que deseja alterar:\n>>> ");
            int idPlano = scan.nextInt();
            scan.nextLine();
            System.out.print("Digite o novo nome do plano:\n>>> ");
            String nome = scan.nextLine();
            System.out.print("Digite a nova duracao do plano (hh:mm:ss):\n>>> ");
            String duracao = scan.nextLine();
            System.out.print("Digite o novo limite de usuarios:\n>>> ");
            int limiteUsers = scan.nextInt();

            PreparedStatement prep = connection.prepareStatement(
                    "UPDATE Plano SET nome = ?, duracao = ?, limite_users = ? WHERE id = ?"
            );
            prep.setString(1, nome);
            prep.setTime(2, Time.valueOf(duracao));
            prep.setInt(3, limiteUsers);
            prep.setInt(4, idPlano);
            prep.executeUpdate();

            System.out.println("Dados do plano alterados com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
}
*/

/* static void alterarDadosInstituicao(Connection connection, Scanner scan) { // inutilizado, nao testado
        try {
            scan.nextLine();
            System.out.print("Digite o ID da instituicao que deseja alterar:\n>>> ");
            int idInstituicao = scan.nextInt();
            scan.nextLine();
            System.out.print("Digite o novo nome da instituicao:\n>>> ");
            String nome = scan.nextLine();
            System.out.print("Digite a nova causa social:\n>>> ");
            String causaSocial = scan.nextLine();
            System.out.print("Digite o novo endereco:\n>>> ");
            String endereco = scan.nextLine();

            PreparedStatement prep = connection.prepareStatement(
                    "UPDATE Instituicao SET nome = ?, causa_social = ?, endereco = ? WHERE id = ?"
            );
            prep.setString(1, nome);
            prep.setString(2, causaSocial);
            prep.setString(3, endereco);
            prep.setInt(4, idInstituicao);
            prep.executeUpdate();

            System.out.println("Dados da instituicao alterados com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } */

static void atividades_recentes(Connection connection, Scanner scan)
{
    int menu = 10;
    int check = 0;
    try
    {
    Statement stmt = connection.createStatement();
    CallableStatement cstmt;
    ResultSet result;
    do 
    {
    check = 0;

    result = stmt.executeQuery("SELECT * from Atividades_recentes");
    while (result.next())
    {
        check++;
        System.out.print("\n----------------------------\n");
        System.out.print("ID do arquivo : " + result.getInt("id_arquivo") + "\n");
        System.out.print("Acesso : ");
        if (result.getShort("acesso") == 1 ) { System.out.print("Prioritario\n"); }
        if (result.getShort("acesso") == 0 ) { System.out.print("Nao prioritario\n"); }
        System.out.print("Data : " + result.getDate("data").toString() );
    }
    if (check == 0) 
    { System.out.print("Nao existe nenhum arquivo na database!");
      scan.nextLine(); System.out.print("\nAperte Enter para voltar. "); scan.nextLine(); return; }

    System.out.print("\n----------------------------\n\n");
    System.out.print("[1] atualizar acessos automaticamente (+- 100 dias)\n[2] chavear arquivo especifico \n[0] voltar \n\n >>>");

    try {menu = scan.nextInt(); } catch (InputMismatchException e) { scan.next(); menu = 10; } 
    
    switch (menu)
    {
        case 1 : cstmt = connection.prepareCall("{ call Atualizar_acessos()}"); cstmt.execute(); break;

        case 2 : System.out.print("Digite o id a inverter o acesso :\n\n >>>");
                    try {menu = scan.nextInt();  
                        cstmt = connection.prepareCall("{ call Chavear(?)}");
                        cstmt.setInt(1, menu);
                        cstmt.execute();
                    }
                    catch (InputMismatchException e) { scan.next(); menu = 10; } 
                    break;
    }

    } while (menu != 0);
    } catch (SQLException e) { e.printStackTrace(); }
}

static void registroOperacoes(Connection connection, Scanner scan)
{
    try
    {
        int operacao;

        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM Registro_operacoes;");

        if (!result.isBeforeFirst() ) 
        {    
            System.out.print("\n-----------------------------------------\n");
            System.out.println("Nenhum registro de operacao!\n"); 
        } 

        
        while (result.next())
        {
            System.out.print("\n-----------------------------------------\n\n");

            operacao = result.getInt("operacao");
            switch (operacao)
            {
                case 1 :
                System.out.print("Usuario de ID\u001B[33m " + result.getInt("id_autor") + "\u001B[32m criou\u001B[0m arquivo de ID \u001B[35m" + result.getInt("id_arquivo") + "\u001B[0m\n");
                break;

                case 2 :
                System.out.print("Usuario de ID\u001B[33m " + result.getInt("id_autor") + "\u001B[32m atualizou\u001B[0m arquivo de ID \u001B[35m" + result.getInt("id_arquivo") + "\u001B[0m\n");
                break;

                case 3 :
                System.out.print("Usuario de ID\u001B[33m " + result.getInt("id_autor") + "\u001B[31m deletou\u001B[0m arquivo de ID \u001B[35m" + result.getInt("id_arquivo") + "\u001B[0m\n");
                break;

                case 4 :
                System.out.print("Usuario de ID\u001B[33m " + result.getInt("id_autor") + "\u001B[34m compartilhou\u001B[0m arquivo de ID \u001B[35m" + result.getInt("id_arquivo") + "\u001B[0m com usuario de id \u001B[33m" + result.getInt("id_alvo") + "\u001B[0m\n" );
                break;

                case 5 :
                System.out.print("Usuario de ID\u001B[33m " + result.getInt("id_autor") + "\u001B[31m removeu compartilhamento\u001B[0m do arquivo de ID \u001B[35m" + result.getInt("id_arquivo") + "\u001B[0m com usuario de id \u001B[33m" + result.getInt("id_alvo") + "\u001B[0m\n" );
                break;
            }

        }

        System.out.print("\n-----------------------------------------\n\n");
        scan.nextLine();
        System.out.print("Aperte Enter para voltar. ");
        scan.nextLine();


    } catch (SQLException e ) { e.printStackTrace(); }

}

static void criarPlano(Connection connection, Scanner scan){
    try 
    {
    try{
        scan.nextLine();
        System.out.print("\nDigite o nome do novo plano:\n>>>");
        String login = scan.nextLine();
        System.out.print("\nDigite a duração do plano: \n>>>");
        String duracao = scan.nextLine();
        System.out.print("\nDigite o limite de usuarios do plano: \n>>>");
        int limit_user = scan.nextInt();

        PreparedStatement prep = connection.prepareStatement("INSERT INTO Plano (nome, duracao, limite_users) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
        prep.setString(1, login);
        prep.setDate(2, java.sql.Date.valueOf(duracao));
        prep.setInt(3, limit_user);
        prep.addBatch();
        prep.executeBatch();

        System.out.println("Novo plano adicionado");
    }
    catch(SQLException e){
        e.printStackTrace();
    }
    } catch (IllegalArgumentException e) { System.out.println("\n\nIllegalArgumentException!! aaa\n");  }
}

static void criarInstituicao(Connection connection, Scanner scan){
    try{

        Statement stmt = connection.createStatement();
        scan.nextLine();
        System.out.print("\nDigite o nome da Instituição(login): \n>>>");
        String login = scan.nextLine();
        System.out.print("\nDigite a senha da instituição: \n>>>");
        String senha = scan.nextLine();
        System.out.print("\nDigite a causa social da instituição: \n>>>");
        String causa_social = scan.nextLine();
        //System.out.print("\nDigite a data de aquisição da instituição: \n>>>");
        //String date = scan.nextLine();
        System.out.print("\n Digite o endereço da instituição: \n>>>");
        String end = scan.nextLine();
        System.out.print("\nDigite o id do plano associado a instituição: \n>>>");
        int id = scan.nextInt();

        ResultSet result = stmt.executeQuery("SELECT CURDATE();");
        result.next();

        PreparedStatement prep = connection.prepareStatement("INSERT INTO Instituicao (nome, causa_social, endereco, data_aquisicao, plano_id) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        prep.setString(1, login);
        prep.setString(2, causa_social);
        prep.setString(3, end);
        prep.setDate(4, java.sql.Date.valueOf(result.getString(1)));
        prep.setInt(5, id);
        prep.addBatch();
        prep.executeBatch();

        stmt.execute("CREATE USER '"+login+"'@'localhost' IDENTIFIED BY '"+ senha + "';");
        System.out.print("Nova intituição adicionada\n");
        stmt.execute("GRANT instituicao TO '" + login +"'@localhost");
        stmt.execute("SET DEFAULT ROLE instituicao FOR '" + login + "'@localhost;");
        System.out.print("Usuario instituicao criado\n");
        }
    catch (SQLException e){
        e.printStackTrace();
    }
}

static void removerInstituicao(Connection connection, Scanner scan)
{
    scan.nextLine();
    System.out.print("Digite o id a instituição que você quer remover : \n\n>>>");
    String id = scan.nextLine();

    try{
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT nome FROM instituicao WHERE (id =" + id +");");
        
        while (result.next()){
            stmt.execute("DELETE FROM instituicao WHERE (id=" + id +");");
            stmt.execute("DROP USER '" + result.getString("nome")+"'@localhost;");
            System.out.print("Instituição removida com sucesso\n\n");
            stmt.execute("flush privileges;");
            return;
        }
        System.out.println("Instituição não encontrada");
    }
    catch(SQLException e){
        e.printStackTrace();
    }
}



/* static void teste(Connection connection)
{

    try
    {
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery("SELECT @echoVarchar:='teste';");
        result.next();
            System.out.print(result.getString(1) + "\n\n");


            ResultSet resultado = stmt.executeQuery("SELECT * from (select @echoVarChar:='jcd' p) parametro, getUserInfo;"  );
            resultado.next();
            System.out.print(resultado.getInt("id") + "\n\n");
            System.out.print(resultado.getInt(2) + "\n\n");
            //System.out.print(resultado.getString(1) + "\n\n");
            //System.out.print(resultado.getString("p") + "\n\n");
        

    } catch ( SQLException e ) { e.printStackTrace(); }

}  */

public static void main(String[] args) 
{
        
    String url = "jdbc:mariadb://localhost:3306";
    String user = "root";
    String pwd = "password";
    Connection connection = null;

    try 
    { 
        connection = DriverManager.getConnection(url, user, pwd); System.out.print ("conectado :)\n\n"); 
    }
    catch (SQLException e ) { e.printStackTrace(); return; }

    try 
    {
        Statement stmt = connection.createStatement();
        stmt.execute("use webdriver;");    
    }   catch (SQLException e) { resetarDatabase(connection); }


    Scanner scan = new Scanner(System.in);
    int menu;
    do
    {
        opcoes();
        try {menu = scan.nextInt(); } catch (InputMismatchException e) { scan.next(); menu = 10; } 

        switch (menu)
        {

        case 1:
            verTabelaUsuarios(connection, scan);
            break;

        case 2:
            criarUsuario(connection, scan);
            break;

        case 3:
            removerUsuario(connection, scan);
            break;

        case 4:
            verTabelaInstituicao(connection, scan);
            break;

        case 5:
            criarInstituicao(connection, scan);
            break;

        case 6:
            removerInstituicao(connection, scan);
            break;
          

        case 7:
            grantAdmin(connection, scan);
            break;

        case 8:
            atividades_recentes(connection, scan);
            break;

        case 9:
            registroOperacoes(connection, scan);
            break;
            

        case 10: criarPlano(connection, scan);
            break;

        //case 8: 
            //teste(connection);
            //break;
        

        case 451: 
            if (resetarDatabase(connection)) { System.out.print("\n\n DB resetada com sucesso :)))");}
            else { System.out.print("\n\n deu ruim :((("); }
            break;

        default:
            System.out.println("opcao invalida!");
            break;

        }

    } while (menu != 0);


scan.close();

}

static void opcoes()
{
    System.out.print("\n\n---------------------------\nOpcoes :\n1- ver todos os usuarios\n2- criar usuario\n3- remover usuario\n\n4- ver todas instituicoes\n5- criar instituicao\n6- remover instituicao \n\n7- dar privilegios de admin para um usuario\n8- tabela de atividades recentes\n9- registro de operacoes com arquivos \n\n10 - criar plano \n\n451- resetar database\n\n>>>");
}

}
