import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import usuario.*;


public class login 
{
    public static void main(String[] args) 
    {
        Connection connection = null;
        String user; String pwd;

        Scanner scan = new Scanner(System.in);
        int menu = 10;

        do
        {
        
        System.out.print("\n\n\n                                 ~WEBDRIVER~                               \n");
        System.out.print("       ____________________________________________________________       \n");
        
        System.out.print("             jheymesson industries® Ltd.       \n\n\n");

        System.out.print("       	     [1] Realizar login\n");
        System.out.print("       	     [2] Sobre\n");
        System.out.print("       	     [0] Sair\n");
        System.out.print("       ____________________________________________________________       \n\n");
        if (menu == -1) { System.out.print ("          Erro : Entrada invalida!\n"); scan.next(); }
        if (menu == -2) { System.out.print ("          Erro : Login mal sucedido! Verifique o nome de usuario & senha\n");}
        System.out.print("          >>>");

    
        try {menu = scan.nextInt(); } catch (InputMismatchException e) { menu = -1; }

        switch (menu)
        {
            case 1 : 
            scan.nextLine();
            System.out.print("          insira o nome de usuario : ");
            user = scan.nextLine();
            System.out.print("          insira a senha : ");
            pwd = scan.nextLine();

            try 
            { 
                connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306", user, pwd); System.out.print ("          conectado :)\n\n"); 
                Usuario usuario = new Usuario();
                usuario.menu(scan, connection);

                return; 
            }
            catch (SQLException e ) { menu = -2; }
            break;

            case 2 :
            System.out.print("\n\n                                   ~SOBRE~");
            System.out.print("\n       ____________________________________________________________       \n\n");
            System.out.print("          Trabalho feito por :\n\n");
            System.out.print("          Matheus Veríssimo\n");
            System.out.print("          Maria Luiza\n");
            System.out.print("          Vinicius martins\n");
            System.out.print("          Victor Hugo\n");
            System.out.print("          Roberto Regis\n");
            System.out.print("\n\n          18/11/2024 ");
            System.out.print("\n       ____________________________________________________________       \n\n");
            scan.nextLine();
            System.out.print("          Aperte Enter para voltar.\n\n");
            System.out.print("          >>>");
            scan.nextLine();
            break;

            case 0 : return; 

            default : menu = -1; break;
        }


        } while (menu != 0);


        scan.close();
    }
}
