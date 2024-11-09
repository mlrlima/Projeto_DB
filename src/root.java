import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

@SuppressWarnings("unused")

public class root 
{

static void opcoes()
{
    System.out.print("\n\n---------------------------\n\nOpcoes :\n1- resetar database\n\n>>>");
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

        stmt.execute("CREATE TABLE Usuario (" +
                    "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                    "nome VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) NOT NULL," + 
                    "senha VARCHAR(100) NOT NULL," +
                    "id_plano INT,"  +
                    "id_instituicao INT NOT NULL," +

                    "CONSTRAINT fk_id_instituicao "+ 
                    "FOREIGN KEY (id_instituicao) REFERENCES instituicao(id)" +
                    ");");

        stmt.execute("CREATE TABLE Arquivo (" +
                    "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                    "nome VARCHAR(100) NOT NULL," +
                    "tipo VARCHAR(100) NOT NULL," +
                    "data_criacao DATE," +
                    "id_usuario INT," +

                    "CONSTRAINT fk_id_usuario " +
                    "FOREIGN KEY (id_usuario) REFERENCES Usuario(id)" +
                    ");"); 
    }

    catch ( SQLException e ) { e.printStackTrace(); resultado = false; }

    return resultado;
}


public static void main(String[] args) 
{
        
    String url = "jdbc:mariadb://localhost:3306";
    String user = "root";
    String pwd = "password";
    Connection connection = null;

    try { connection = DriverManager.getConnection(url, user, pwd); System.out.print ("conectado :)\n\n"); }
    catch (SQLException e ) { e.printStackTrace(); }

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
