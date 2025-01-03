package usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Usuario
{
    int id;
    String login;
    String email;
    String instituicao;
    String data_ingresso;

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
        
        result = stmt.executeQuery("SELECT id, data_ingresso, email, id_admin, nome from (select @echoVarChar:='"+this.login+"' p) parametro, getUserInfo;");
        result.next();
        this.id = result.getInt("id");
        this.email = result.getString("email");
        this.data_ingresso = result.getDate("data_ingresso").toString();
        this.instituicao = result.getString("nome");
        

        if (result.getInt("id_admin") != 0) { Admin admin = new Admin(result.getInt("id_admin")); admin.menu(connection); System.exit(0); }

        }
        catch (SQLException e) { e.printStackTrace(); }

    }

    public void menu(Scanner scan, Connection connection) 
    {
        setup(connection);
        
        int menu = 10;
        do
        {
            System.out.print("\n\nBem vindo, "+this.login+"\n------------------------\n O que voce quer fazer agora?\n\n [1] - ver perfil\n [2] - ver arquivos\n [3] - suporte\n [0] - sair\n\n   >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {   
                case 1 : perfil(scan); break;
                case 2 : MenuArquivo arquivo = new MenuArquivo(this); arquivo.menu(scan, connection); break;
                case 3 : menuSuporte(scan, connection); break;
                case 0 : System.out.print("\n   :(\n"); break;
                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;
            }

        } while (menu != 0);
    }

    private void menuSuporte(Scanner scan, Connection connection)
    {
        int menu = 10;
        do
        {
            System.out.print("\n\nSuporte\n------------------------\n\n [1] - criar novo ticket de suporte\n [2] - ver meus tickets\n [0] - voltar\n\n   >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {
                case 1 : criarSuporte(scan, connection); break;
                case 2 : verMeusSuportes(scan, connection);
                case 0 : break;
                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;
            }

        } while (menu != 0);
    }

    private void perfil(Scanner scan)
    {
        scan.nextLine();
        System.out.print("\n\n\nPerfil\n------------------------ ");
        System.out.print("\nID : " + this.id);
        System.out.print("\nLogin : " + this.login);
        System.out.print("\nEmail : " + this.email);
        System.out.print("\nData de ingresso : " + this.data_ingresso);
        if (this.instituicao != null) { System.out.print("\nInstituicao : " + this.instituicao); }
        System.out.print("\n------------------------");
        System.out.print("\n\nAperte Enter para voltar. ");
        scan.nextLine();
    }

    private void criarSuporte(Scanner scan, Connection connection)
    {
        try
        {

            scan.nextLine();
            System.out.print("\nDigite o conteudo do seu ticket a seguir, ou \"0\" para cancelar:\n>>>");
            String descricao = scan.nextLine();
            if (descricao.compareTo("0") == 0) { return; }

            PreparedStatement prep = connection.prepareStatement
            (
            "INSERT INTO Suporte (descricao, data, hora, status, id_usuario, id_resposta) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
            );
            Statement stmt = connection.createStatement();
            ResultSet result;

            result = stmt.executeQuery("SELECT CURDATE();");
            result.next();
            String data = result.getString(1);

            result = stmt.executeQuery("SELECT CURTIME();");
            result.next();
            String time = result.getString(1);

            prep.setString(1, descricao);
            prep.setDate(2, java.sql.Date.valueOf(data));
            prep.setTime(3, java.sql.Time.valueOf(time));
            prep.setInt(4, 0);
            prep.setInt(5, this.id);
            prep.setNull(6, Types.INTEGER);
            prep.addBatch();
            prep.executeBatch();

            System.out.print ("Suporte criado com sucesso :)\n"); 
            System.out.print("\nAperte Enter para voltar. ");
            scan.nextLine();


            System.out.print("teste :" + data + time + "\n\n");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void verMeusSuportes(Scanner scan, Connection connection)
    {
        ArrayList<Suporte> suportes = new ArrayList<>();
        Suporte suporte;

        try 
        {
        String descricao, data, hora, resposta;
        int status;

        Statement stmt = connection.createStatement();
        ResultSet result;
        result = stmt.executeQuery("SELECT descricao, data, hora, status, resposta FROM (select @echoInt:="+this.id+" p) parametro, verMeusSuportes;");

        while (result.next())
        {
            descricao =  result.getString("descricao");
            data = result.getDate("data").toString();
            hora = result.getTime("hora").toString();
            status = result.getInt("status");
            if (status == 0) { resposta = "0"; }
            else {resposta = result.getString("resposta"); } 

            suporte = new Suporte();
            suporte.conteudo = descricao; suporte.data = data; suporte.hora = hora; suporte.status = status; suporte.resposta = resposta;
            suportes.add(suporte);
        }

        if (suportes.size() == 0) 
        { 
            System.out.print("\n\n------------------------\n Voce nao tem nenhum pedido de suporte!");
            scan.nextLine();
            System.out.print("\n\nAperte Enter para voltar. ");
            scan.nextLine();
            return;
        }

        int menu = 10;
        do
        {
            for (int i = 0; i < suportes.size(); i++ )
            {
                suporte = suportes.get(i);
                System.out.print("------------------------\n");
                System.out.print("["+(i+1)+"]\n");
                System.out.print("Ticket suporte " + suporte.data + " " + suporte.hora +"\n");
                System.out.print("Status : ");
                if (suporte.status == 0) { System.out.print("\u001B[31m"+ "nao respondido" +"\u001B[0m\n"); }
                else { System.out.print("\u001B[33m" + "respondido" +"\u001B[0m\n"); }
            }
            System.out.print("------------------------\n");
            System.out.print("\n\n Selecione o pedido que voce quer ver, ou [0] para voltar.\n\n >>>");
 
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            if (menu == 0) { return; }
            else if (menu > 0 && menu <= suportes.size()) 
            {
                suporte = suportes.get(menu-1);
                System.out.print("\n-------------------------------\n\n");
                System.out.print("Pedido :\n\n");
                System.out.print(suporte.conteudo);
                System.out.print("\n-------------------------------\n");
                if (suporte.status == 1) 
                { System.out.print("\nResposta :\n\n"); 
                  System.out.print(suporte.resposta); 
                  System.out.print("\n-------------------------------\n\n");
                }
                else {System.out.print("<aguardando resposta>\n\n");}

                scan.nextLine();
                System.out.print(" Aperte enter para voltar.");
                scan.nextLine();

            }
            else { System.out.print("\n Entrada invalida!\n"); menu = 10; }


        } while (menu != 0);

        } catch (SQLException e) { e.printStackTrace(); }

        
    }


    private class Suporte
    {
        String conteudo;
        int status;
        String resposta;
        String data;
        String hora;

        int id; // nao acessivel pelas visoes do usuario normal
    }

    ///////////////////////////////////////// admin ////////////////////////////////////////
    //////////////////////////////////////// admin ////////////////////////////////////////
    /////////////////////////////////////// admin /////////////////////////////////////////

    private class Admin extends Usuario
    {
    
    private int id_admin;

    public Admin (int n)
    {
        this.id_admin = n;
    }

    public void menu(Connection connection) 
    {
        Scanner scan = new Scanner(System.in);
        
        int menu = 10;
        do
        {
            System.out.print("\n\n------------------------\n Menu admin\n\n [1] - ver pedidos de suporte nao-respondidos\n [0] - voltar\n\n >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {
                case 1 : verSuportes(scan, connection); break;
                case 0 : System.out.print("\n   :(\n"); break;
                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;
            }

        } while (menu != 0);
    }

    private void verSuportes(Scanner scan, Connection connection)
    {
        ArrayList<Suporte> suportes = new ArrayList<>();
        Suporte suporte;
        String descricao, hora, data;
        int id;

        try
        {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select id, descricao, data, hora from Suporte WHERE (status = 0);");

            while (result.next())
        {
            descricao =  result.getString("descricao");
            data = result.getDate("data").toString();
            hora = result.getTime("hora").toString();
            id = result.getInt("id");


            suporte = new Suporte();
            suporte.conteudo = descricao; suporte.data = data; suporte.hora = hora; suporte.id = id;
            suportes.add(suporte);
        }
        } catch (SQLException e) { e.printStackTrace(); }

        if (suportes.size() == 0) 
        { 
            System.out.print("\n Nenhum pedido de suporte para responder!");
            return;
        }

        int menu = 10;
        int target;
        do
        {
            System.out.print("\n------------------------\n");
            for (int i = 0; i < suportes.size(); i++ )
            {
                suporte = suportes.get(i);
                System.out.print("["+(i+1)+"]\n");
                System.out.print("Ticket suporte " + suporte.data + " " + suporte.hora);
                System.out.print(" ID : " + suporte.id + "\n\n");
            }
            System.out.print("\n\n Selecione o suporte que voce quer ver, ou [0] para voltar.\n\n >>>");

            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            if (menu == 0) { return; }
            else if (menu > 0 && menu <= suportes.size()) 
            {
                suporte = suportes.get(menu-1);
                System.out.print("\n-------------------------------\n\n");
                System.out.print(suporte.conteudo);
                System.out.print("\n-------------------------------\n\n");

                System.out.print("\n-------------------------------\n\n Digite [1] para responder, ou [0] para voltar.\n\n >>>");
                
                target = menu;
                try { menu = scan.nextInt(); } catch ( InputMismatchException e ) { menu = 10; }
                if (menu == 1) 
                {
                    System.out.print("Digite a sua resposta :\n\n");
                    scan.nextLine();
                    String resposta = scan.nextLine();
                    try
                    {
                        Statement stmt = connection.createStatement();
                        PreparedStatement prep = connection.prepareStatement
                        (
                        "INSERT INTO Resposta (descricao, id_admin) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS
                        );
                        prep.setString(1, resposta);
                        prep.setInt(2, this.id_admin);
                        prep.addBatch();
                        prep.executeBatch();
                        System.out.print ("Resposta criada com sucesso :)\n"); 

                        ResultSet result  = stmt.executeQuery("SELECT MAX(id_resposta) from Resposta;");
                        result.next();

                        stmt.execute("UPDATE Suporte SET id_resposta =" + result.getInt(1) + " WHERE (id = " + suportes.get(target-1).id + ")");
                        stmt.execute("UPDATE Suporte SET status = 1 WHERE (id = " + suportes.get(target-1).id + ")");

                        System.out.print("Suporte atualizado com sucesso :)\n");
                        System.out.print("Aperte enter para continuar.\n");
                        scan.nextLine();
                        return;

                    } catch ( SQLException e ) { e.printStackTrace(); }
                }
                else {menu = 10;};

            }
            else { System.out.print("\n Entrada invalida!\n"); menu = 10; }

        } while ( menu != 0 );
    }

    }
}
