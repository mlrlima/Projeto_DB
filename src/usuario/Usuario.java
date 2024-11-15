package usuario;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
//@SuppressWarnings("unused")

public class Usuario
{
    int id;
    String login;
    String email;
    String instituicao;

    private void setup(Connection connection)  // adicionar o resto das informacoes tipo email, instituicao, quantidade de espaco usado, etc depois
    {
        try 
        {
        Statement stmt;
        ResultSet result;

        stmt = connection.createStatement();
        stmt.execute("use webdriver;");
        result = stmt.executeQuery("SELECT USER();");

        result.next();
        String login = result.getString(1);
        login = login.substring(0, login.length()-10); 
        this.login = login;
        
        result = stmt.executeQuery("SELECT id from (select @echoVarChar:='"+this.login+"' p) parametro, getUserID;");
        result.next();
        this.id = result.getInt("id");

        }
        catch (SQLException e) { e.printStackTrace(); }

    }

    public void menu(Scanner scan, Connection connection) 
    {
        setup(connection);
        
        int menu = 10;
        do
        {
            System.out.print("\n\n------------------------\n O que voce quer fazer agora?\n\n [1] - ver perfil\n [0] - sair\n\n   >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {
                case 1: perfil(scan); break;
                case 0 : System.out.print("\n   :(\n"); break;
                default: menu = 10; break;
            }

        } while (menu != 0);
    }


    private void perfil(Scanner scan)
    {
        scan.nextLine();
        System.out.print("\n\n(WIP : completar essa pagina depois)\n------------------------ ");
        System.out.print("\nID : " + this.id);
        System.out.print("\nLogin : " + this.login);
        System.out.print("\n\nde Enter para voltar. ");
        scan.nextLine();
    }
}
