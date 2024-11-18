import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

@SuppressWarnings("unused")

public class root 
{

static void opcoes()
{
    System.out.print("\n\n---------------------------\nOpcoes :\n1- ver todos os usuarios\n2- inserir usuario\n4- remover usuario\n5- alterar dados de um usuario\n6- dar privilegios de admin para um usuario\n50- resetar database\n\n>>>");
}

static boolean resetarDatabase(Connection connection)
{
    Statement stmt;
    boolean resultado = true;
    try 
    {
        stmt = connection.createStatement();
        stmt.execute("DROP DATABASE IF EXISTS webdriver;");
        stmt.execute("CREATE DATABASE webdriver;");
        stmt.execute("use webdriver;");
        
        stmt.execute
        (
        "CREATE TABLE Plano " +
        "(id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "nome TEXT NOT NULL," +
        "duracao TIME," + 
        "limite_users INT);"
        );

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
        "FOREIGN KEY (plano_id) REFERENCES Plano(id) ON DELETE RESTRICT ON UPDATE RESTRICT);" 
        );

        stmt.execute
        (
        "CREATE TABLE Administrador" +
        "(id INT PRIMARY KEY NOT NULL);" 
        );

        stmt.execute(
        "CREATE TABLE Usuario (" +
        "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "login VARCHAR(100) NOT NULL," +
        "email VARCHAR(100) NOT NULL," + 
        "senha VARCHAR(100) NOT NULL," +
        "data_ingresso DATE NOT NULL," +
        "id_instituicao INT," +
        "id_admin INT," +

        "FOREIGN KEY (id_instituicao) REFERENCES instituicao(id)," +
        "FOREIGN KEY (id_admin) REFERENCES Administrador(id)" +
        ");");


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



    //// coisas de pessoas e roles 
    /// 
        stmt.execute("DROP ROLE IF EXISTS usuario;"); // usuarios e roles ficam fora da db
        stmt.execute("flush privileges;");
        stmt.execute("CREATE ROLE usuario;");
        stmt.execute("GRANT INSERT on webdriver.Arquivo to usuario;");
        //stmt.execute("GRANT INSERT, UPDATE, DELETE on webdriver.Compartilhamento to usuario;");
        stmt.execute("GRANT INSERT, UPDATE, DELETE on webdriver.Suporte to usuario;");
        stmt.execute("GRANT INSERT on webdriver.Comentario to usuario;");

        stmt.execute("DROP ROLE IF EXISTS admin;");
        stmt.execute("flush privileges;");
        stmt.execute("CREATE ROLE admin;");
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.* to admin;");


    /// functions e views e procedures e 
    
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

    
    stmt.execute("DROP VIEW IF EXISTS getUserInfo");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW getUserInfo AS  "+
    "select id, id_admin, email, data_ingresso, id_instituicao FROM Usuario where login = echoVarchar(); "
    );
    stmt.execute("GRANT SELECT on webdriver.getUserInfo to usuario;");


    stmt.execute("DROP VIEW IF EXISTS verMeusSuportes");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW verMeusSuportes AS  "+
    "select s.descricao, s.data, s.hora, s.status, r.descricao as resposta FROM Suporte s LEFT JOIN Resposta r on(s.id_resposta = r.id_resposta) where id_usuario = echoInt()  ; "
    );
    stmt.execute("GRANT SELECT on webdriver.verMeusSuportes to usuario;");

    stmt.execute("DROP VIEW IF EXISTS verMeusArquivos");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW verMeusArquivos AS  "+
    "select a.conteudo, a.nome, a.tipo, a.permissoes, a.data_alteracao, a.tamanho, a.url FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) where id_dono = echoInt(); "
    );
    stmt.execute("GRANT SELECT on webdriver.verMeusArquivos to usuario;");

    stmt.execute("DROP VIEW IF EXISTS arquivosCompartilhadosComigo");
    stmt.execute("CREATE SQL SECURITY DEFINER VIEW arquivosCompartilhadosComigo AS  "+
    "select a.conteudo, a.nome, a.tipo, a.data_alteracao, a.tamanho, a.url, u.login FROM Arquivo a LEFT JOIN Usuario u on (u.id = a.id_dono) LEFT JOIN Compartilhamento c on (a.id = c.id_arquivo) where id_usuario_compartilhado = echoInt(); "
    );
    stmt.execute("GRANT SELECT on webdriver.arquivosCompartilhadosComigo to usuario;");

///
/// 
    /// procedures
    

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

    stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS getQtdCompartilhamentos" +
   "(IN ownerid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100), OUT result INT) " +
   "BEGIN " +
   "CALL hostOnlyArqID(ownerid, nomeConfirm, tipoConfirm, @arqID); " +
   "SET result = ( SELECT COUNT(*) FROM Compartilhamento WHERE (id_arquivo = @arqID) ); " +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.getQtdCompartilhamentos to usuario;");


   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS Remover_Acessos" +
   "(IN ownerid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100)) " +
   "BEGIN " +
   "CALL hostOnlyArqID(ownerid, nomeConfirm, tipoConfirm, @hostOnlyArqID); " +
   "DELETE FROM Compartilhamento WHERE (id_arquivo = @arqID); " +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.Remover_Acessos to usuario;");


   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS Remover_Arquivo" +
   "(IN ownerid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100)) " +
   "BEGIN " +
   "CALL hostOnlyArqID(ownerid, nomeConfirm, tipoConfirm, @arqID); " +
   "DELETE FROM Arquivo WHERE (id = @arqID); " +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.Remover_Arquivo to usuario;");


   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS Atualizar_Arquivo" +
   "(IN userid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100), IN novoConteudo TEXT) " +
   "BEGIN " +
   "CALL getArqID(userid, nomeConfirm, tipoConfirm, @arqID); " +
   "UPDATE Arquivo SET conteudo = novoConteudo, data_alteracao = CURDATE() WHERE (id = @arqID); " +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.Atualizar_Arquivo to usuario;");

   // "INSERT INTO Compartilhamento (id_dono, id_arquivo, id_usuario_compartilhado, data) VALUES (ownerID, id_arq, id_target, CURDATE() ); " +

   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS CriarComentario" +
   "(IN userid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100), IN novoComentario TEXT )" +
   "BEGIN " +
   "CALL getArqID(userid, nomeConfirm, tipoConfirm, @arqID); " +
   "INSERT INTO Comentario (conteudo, hora, data, id_autor, id_arquivo) VALUES (novoComentario, CURTIME(), CURDATE(), userid, @arqID); " +
   "END" 
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.CriarComentario to usuario;");

   stmt.execute("CREATE DEFINER=`root`@`localhost` PROCEDURE IF NOT EXISTS verComentarios" +
   "(IN userid INT, in nomeConfirm VARCHAR(100), IN tipoConfirm VARCHAR(100) ) " +
   "BEGIN " +   
   "CALL getArqID(userid, nomeConfirm, tipoConfirm, @arqID); " +
   "SELECT c.conteudo, c.hora, c.data, u.login FROM Comentario c LEFT JOIN Usuario u on (u.id = c.id_autor) where id_arquivo = @arqID;" +
   "END"
   );
   stmt.execute("GRANT EXECUTE ON PROCEDURE webdriver.verComentarios to usuario;");





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

    }

    catch ( SQLException e ) { e.printStackTrace(); resultado = false; }

    return resultado;
}


static void criarUsuario(Connection connection, Scanner scan) // conteudo aqui e um placeholder temporario
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
    String instituicao = scan.nextLine();

    ResultSet result = stmt.executeQuery("SELECT CURDATE();");
    result.next();

    PreparedStatement prep = connection.prepareStatement("INSERT INTO Usuario (login, email, senha, data_ingresso, id_instituicao, id_admin) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

    prep.setString(1, login);
    prep.setString(2, email);
    prep.setString(3, senha);
    prep.setDate(4, java.sql.Date.valueOf(result.getString(1)));
    if (instituicao.equals("0")) { System.out.print("testeteste"); prep.setNull(5, Types.INTEGER); } // else { prep.setString(5, instituicao); }// nao eh string!! e pra ser id!
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

static void verTabelaUsuarios(Connection connection)
{
    try 
    {
        Statement stmt = connection.createStatement();
        Integer admincheck;

        ResultSet result = stmt.executeQuery("SELECT * FROM Usuario;");
        while (result.next())
        {
            System.out.print("\n----------------------------\n");
            System.out.print("ID : " + result.getInt("id") + "\n\n");
            System.out.print("Login : " + result.getString("login") + "\n");
            System.out.print("Email : " + result.getString("email") + "\n");
            System.out.print("Senha : " + result.getString("senha") + "\n");
            System.out.print("Data de ingresso : " + result.getDate("data_ingresso").toString() + "\n");
            System.out.print("Instituicao : " + result.getInt("id_instituicao") + "\n"); // retorna 0 caso seja nulo? 
            System.out.print("Admin : ");
            
            admincheck = result.getInt("id_admin");
            if (admincheck == 0)
            {  System.out.print("Nao\n");  }
            else
            {  System.out.print(admincheck + "\n");  }

        }
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

static void RootAlterUser(Connection connection, Scanner scan){
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
}

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
static void alterarDadosPlano(Connection connection, Scanner scan) {
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

static void alterarDadosInstituicao(Connection connection, Scanner scan) {
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
    }


static void teste(Connection connection)
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
}

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
Integer num;
    do
    {
        opcoes();
        try {menu = scan.nextInt(); } catch (InputMismatchException e) { scan.next(); menu = 10; } 

        switch (menu)
        {

        case 1:
            verTabelaUsuarios(connection);
            break;

        case 2:
            criarUsuario(connection, scan);
            break;

        case 4:
            removerUsuario(connection, scan);
            break;

        case 5:
            RootAlterUser(connection, scan);
            break;

        case 6:
            grantAdmin(connection, scan);
            break;

        case 8: 
            teste(connection);
            break;
        

        case 50: 
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
}
