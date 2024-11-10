import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

@SuppressWarnings("unused")

public class root 
{

static void opcoes()
{
    System.out.print("\n\n---------------------------\nOpcoes :\n1- ver todos os usuarios\n2- inserir usuario\n4- remover usuario\n5- alterar dados de um usuario\n50- resetar database\n\n>>>");
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
        "(id INT PRIMARY KEY NOT NULL AUTO_INCREMENT);" 
        );

        stmt.execute(
        "CREATE TABLE Usuario (" +
        "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "nome VARCHAR(100) NOT NULL," +
        "email VARCHAR(100) NOT NULL," + 
        "senha VARCHAR(100) NOT NULL," +
        "id_instituicao INT," +
        "id_admin INT," +

        "FOREIGN KEY (id_instituicao) REFERENCES instituicao(id)," +
        "FOREIGN KEY (id_admin) REFERENCES Administrador(id)" +
        ");");


        stmt.execute
        (  
        "CREATE TABLE Arquivo (" +
        "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "tipo VARCHAR(100) NOT NULL," +
        "permissoes INT," +
        "data_alteracao DATE," +
        "tamanho VARCHAR(100) NOT NULL," +
        "url VARCHAR(100) NOT NULL," +
        "localizacao VARCHAR(100) NOT NULL," +
        "id_dono INT," +
            
        "CONSTRAINT fk_id_dono " +
        "FOREIGN KEY (id_dono) REFERENCES Usuario(id)" +");"    
        ); 

        stmt.execute
        (  
        "CREATE TABLE Resposta (" +
        "id_resposta INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "descricao VARCHAR(100) NOT NULL," +
        "id_admin INT," +
        
        "CONSTRAINT fk_id_admin " +
        "FOREIGN KEY (id_admin) REFERENCES Administrador(id)" + ");"
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

        "FOREIGN KEY (id_usuario) REFERENCES Usuario(id)," +
        "FOREIGN KEY (id_resposta) REFERENCES Resposta(id_resposta)" +
        ");");

    stmt.execute("DROP ROLE IF EXISTS usuario;"); // usuarios e roles ficam fora da db
    stmt.execute("flush privileges;");
    stmt.execute("CREATE ROLE usuario;");

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
    System.out.print("\nDigite o nome do novo usuario :\n>>>");
    String nome = scan.nextLine();
    System.out.print("\nDigite a senha do novo usuario :\n>>>");
    String senha = scan.nextLine();
    System.out.print("\nDigite o email do novo usuario :\n>>>");
    String email = scan.nextLine();
    System.out.print("\nDigite a instituicao do novo usuario, ou 0 para nenhuma :\n>>>");
    String instituicao = scan.nextLine();

    PreparedStatement prep = connection.prepareStatement("INSERT INTO Usuario (nome, email, senha, id_instituicao, id_admin) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

    prep.setString(1, nome);
    prep.setString(2, email);
    prep.setString(3, senha);
    if (instituicao.equals("0")) { System.out.print("testeteste"); prep.setNull(4, Types.INTEGER); } else { prep.setString(4, instituicao); }
    prep.setNull(5, Types.INTEGER);
    prep.addBatch();
    prep.executeBatch();

    System.out.print ("tabela pessoa adicionada :)\n"); // a partir daqui ta certo
    
    stmt.execute("CREATE USER '"+nome+"'@'localhost' IDENTIFIED BY '"+ senha +"';");
    System.out.print ("usuario adicionado :)\n\n");


    stmt.execute("GRANT usuario TO '"+nome+"'@localhost");
    stmt.execute("SET DEFAULT ROLE usuario FOR 'jcd'@localhost;");



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
            System.out.print("Nome : " + result.getString("nome") + "\n");
            System.out.print("Email : " + result.getString("email") + "\n");
            System.out.print("Senha : " + result.getString("senha") + "\n");
            System.out.print("Instituicao : " + result.getInt("id_instituicao") + "\n"); // retorna 0 caso seja nulo?
            System.out.print("Admin : ");
            
            admincheck = result.getInt("id_admin");
            if (admincheck == null)
            {  System.out.print("Nao\n");  }
            else
            {  System.out.print("Sim\n");  }

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

        ResultSet result = stmt.executeQuery("SELECT nome FROM Usuario WHERE (id = " + id + ");");
        while (result.next())
        {
            stmt.execute("DELETE FROM Usuario WHERE (id = " + id + ");");
            stmt.execute("DROP USER '" + result.getString("nome") + "'@localhost;");
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
       ResultSet result = stmt.executeQuery("SELECT nome FROM Usuario WHERE (id = " + id + ");");
       
       if (result.next()) {
	       System.out.print("\nDigite o novo nome do usuario\n>>> ");
    	   String name = scan.nextLine();
    	   System.out.print("\nDigite o novo email do usuario\n>>> ");
    	   String email = scan.nextLine();
    	   System.out.print("\nDigite a nova senha do usuario\n>>> ");
    	   String senha = scan.nextLine();
    	   //System.out.print("\nDigite a nova instituição do usuario\n>>> ");
    	   //String instituicao = scan.nextLine();
    	   
	       if (name != "") {
	    	   stmt.execute("UPDATE Usuario SET nome = '" + name + "' WHERE (id = " + id + ");");
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
        

        case 50: 
            if (resetarDatabase(connection)) { System.out.print("\n\n DB resetada com sucesso :)))\n\n");}
            else { System.out.print("\n\n deu ruim :(((\n\n"); }
            break;

        default:
            System.out.println("opcao invalida!");
            break;

        }

    } while (menu != 0);


scan.close();

}
}
