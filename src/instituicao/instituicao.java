package instituicao;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import usuario.MenuArquivo;

@SuppressWarnings("unused")

public class Instituicao 
{
    int id;
    String nome;
    String endereco; 
    String causa_social;
    String data_aquisicao;
    int plano_id;

    private void setup (Connection connection)
    {
        try
        {

        ResultSet result;
        Statement stmt = connection.createStatement();
        stmt.execute("use webdriver;");
        result = stmt.executeQuery("SELECT USER();");

        result.next();
        String nome = result.getString(1);
        nome = nome.substring(0, nome.length()-10); 
        this.nome = nome;

        result = stmt.executeQuery("SELECT id, causa_social, endereco, data_aquisicao, plano_id from (select @echoVarChar:='"+this.nome+"' p) parametro, getInstituicaoInfo;");
        result.next();
        this.id = result.getInt("id");
        this.causa_social = result.getString("causa_social");
        this.endereco = result.getString("endereco");
        this.data_aquisicao = result.getDate("data_aquisicao").toString();
        this.plano_id = result.getInt("plano_id");
        

        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void menu(Scanner scan, Connection connection)
    {
        setup(connection);

        int menu = 10;
        do
        {
            System.out.print("\n\n------------------------\n O que voce quer fazer agora?\n\n [1] - ver perfil e dados do plano\n [2] - ver arquivos de usuarios da instituicao\n [3] - ver usuarios da instituicao\n [0] - sair\n\n   >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {   
                case 1 : perfil(connection, scan); break;
                case 2 : verArquivos(connection, scan);break;
                case 3 : verUsuarios(scan, connection); break;
                case 0 : System.out.print("\n   :(\n"); break;
                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;
            }

        } while (menu != 0);
    }

    private void perfil(Connection connection, Scanner scan)
    {
        scan.nextLine();
        System.out.print("\n\n\n------------------------ ");
        System.out.print("\nID : " + this.id);
        System.out.print("\nNome : " + this.nome);
        System.out.print("\nCausa social : " + this.causa_social);
        System.out.print("\nEndereco : " + this.endereco);
        System.out.print("\nData de aquisicao do plano : " + this.data_aquisicao);
        System.out.print("\n------------------------");

        try 
        {
            Statement stmt = connection.createStatement();
            ResultSet result;
            result = stmt.executeQuery("SELECT nome, duracao, limite_users FROM (select @echoInt:="+this.plano_id+" p) parametro, getPlanoInfo ;");
            result.next();
            System.out.print("\nInformacoes do plano :\n\n");
            System.out.println("Nome : " + result.getString("nome"));
            System.out.println("Dura ate : " + result.getDate("duracao").toString());
            System.out.println("Limite de usuarios : " + result.getInt("limite_users"));
         // result = stmt.executeQuery("SELECT descricao, data, hora, status, resposta FROM (select @echoInt:="+this.id+" p) parametro, verMeusSuportes;");

        } catch (SQLException e ) { e.printStackTrace(); }

        System.out.print("\n\nAperte Enter para voltar. ");
        scan.nextLine();
    }

    private void verArquivos(Connection connection, Scanner scan)
    {
        ArrayList<Arquivo> arquivos = new ArrayList<>();
        Arquivo arquivo;
        arquivos = arquivoQuery(connection);

        int retorno;
        int menu = 10;

        do
        {
            if (arquivos.size() == 0) 
            { 
                System.out.print("\n\n------------------------\n Atualmente, nao tem nenhum arquivo criado por um membro dessa instituicao!");
                scan.nextLine();
                System.out.print("\n\nAperte Enter para voltar. ");
                scan.nextLine();
                return;
            }

            for (int i = 0; i < arquivos.size(); i++ )
                {
                    arquivo = arquivos.get(i);
                    System.out.print("------------------------\n");
                    System.out.print("["+(i+1)+"]\n");
                    System.out.print(arquivo.nome + "." + arquivo.tipo + "\n");
                    System.out.print("Dono : " + arquivo.dono_login);
                    if (arquivo.permissoes == 0) {System.out.print("\nprivado\n");}
                    else {System.out.print("\ncompartilhado\n");}
                    System.out.print("tamanho : " + arquivo.tamanho + " bytes\n");
                }
                System.out.print("------------------------\n");
                System.out.print("\n\n Selecione o arquivo que voce quer ver, ou [0] para voltar.\n\n >>>");

                try {menu = scan.nextInt(); } 
		        catch (InputMismatchException e)
		        { scan.next(); menu = 10; }

                if (menu == 0) { return; }
                else if (menu > 0 && menu <= arquivos.size()) 
                {
                    arquivo = arquivos.get(menu-1);
                    verArquivo(connection, scan, arquivo);
                    
                }
                else { System.out.print("\n Entrada invalida!\n"); menu = 10; }

        } while (menu != 0);

        return;
    }

    private ArrayList<Arquivo> arquivoQuery (Connection connection)
    {
        ArrayList<Arquivo> arquivos = new ArrayList<>();
        Arquivo arquivo;
        
        String dono_login, nome, conteudo, tipo;
        int permissoes;
        String data_alteracao;
        Integer tamanho;
        String url;

        try
        {
            Statement stmt = connection.createStatement();
            ResultSet result;
    
            result = stmt.executeQuery("SELECT login, nome, conteudo, tipo, permissoes, data_alteracao, tamanho, url, login FROM (select @echoInt:="+this.id+" p) parametro, verArquivosInstituicao;");
            while (result.next())
            {
                conteudo = result.getString("conteudo");
                nome = result.getString("nome");
                tipo = result.getString("tipo");
                permissoes = result.getInt("permissoes");
                data_alteracao = result.getDate("data_alteracao").toString();
                tamanho = result.getInt("tamanho");
                url = result.getString("url");
                dono_login = result.getString("login"); //

                arquivo = new Arquivo();
                arquivo.conteudo = conteudo; arquivo.nome = nome; arquivo.tipo = tipo; arquivo.permissoes = permissoes; arquivo.data_alteracao = data_alteracao; arquivo.tamanho = tamanho; arquivo.url = url;
                arquivo.dono_login = dono_login;
                arquivos.add(arquivo);
            }
            

        } catch (SQLException e) {e.printStackTrace();}

        
        return arquivos;    
        
    }

    private ArrayList<Versionamento> versaoQuery(Connection connection, Arquivo arquivo)
    {
        ArrayList<Versionamento> versoes = new ArrayList<>();
        Versionamento versao;
        String conteudo, data, hora, autor_login;
        int operacao;
        ResultSet result;

        try
        {   

            CallableStatement cstmt = connection.prepareCall("{ call verVersionamento (?, ?, ?)}");
            cstmt.setString(1, arquivo.dono_login);
            cstmt.setString(2, arquivo.nome);
            cstmt.setString(3, arquivo.tipo);
            cstmt.execute();

            result = cstmt.getResultSet();


            while (result.next())
            {   
            conteudo = result.getString("conteudo");
            data = result.getDate("data").toString();
            hora = result.getTime("hora").toString();
            autor_login = result.getString("login");
            operacao = result.getInt("operacao");
            versao = new Versionamento();
            versao.conteudo = conteudo; versao.data = data; versao.hora = hora; versao.operacao = operacao; versao.autor_login = autor_login;
            versoes.add(versao);
            }

        } catch (SQLException e) { e.printStackTrace(); }

        return versoes;
    }

    private void verArquivo (Connection connection, Scanner scan, Arquivo arquivo)
    {
        int menu = 10;

        do
        {
        System.out.print("-------------------------------\n" + arquivo.nome + "." + arquivo.tipo + "\nTamanho : " + arquivo.tamanho );
        if (arquivo.url != null) {System.out.print("\nurl : " + arquivo.url);}
        if (arquivo.permissoes == 0)  { System.out.print("\nPermissoes : Privado\n"); }
        else {  System.out.print("\nPermissoes : Compartilhado com a empresa\n"); }

        System.out.print("Conteudo : \n-------------------------------------------------------------\n" + arquivo.conteudo + "\n-------------------------------------------------------------\n");
        System.out.print("Opcoes :\n [1] - Ver versionamento\n [0] voltar \n\n >>>");
        try {menu = scan.nextInt(); } 
		catch (InputMismatchException e)
		{ scan.next(); menu = 10; }

        switch (menu)
        {
            case 0 : break;
            case 1 : verVersionamento(scan, connection, arquivo);
        }

        } while (menu != 0);

    }

    private void verVersionamento(Scanner scan, Connection connection, Arquivo arquivo)
    {
        ArrayList<Versionamento> versoes = new ArrayList<>();
        Versionamento versao;
        versoes = versaoQuery(connection, arquivo);
        int menu = 10;

        do
        {
            if (versoes.size() == 0)
            {
                System.out.print("\n\n------------------------\n Esse arquivo nao tem nenhuma versao!... Como isso pode acontecer??? Isso nao era pra acontecer!\n");
            }
            else
            {
                for (int i = versoes.size()-1; i >= 0; i-- )
                {
                    versao = versoes.get(i);
                    System.out.print("\n-------------------------------------------------------------\n");
                    System.out.print("Versao " + (i+1) + "\n");
                    if (versao.operacao == 1) { System.out.print("\u001B[32mCriado\u001B[0m por ");} else { System.out.print("\u001B[32mAtualizado\u001B[0m por ");  }
                    System.out.print("\u001B[33m" + versao.autor_login + "\u001B[0m " + versao.data + " " + versao.hora);
                    System.out.print("\n\n" + versao.conteudo +"\n");
                }
            }

            System.out.print("\n-------------------------------------------------------------\n");
            scan.nextLine();
            System.out.print("\n\nAperte Enter para voltar. ");
            scan.nextLine();
            return;
        } while (menu != 0);
    }

    private void verUsuarios (Scanner scan, Connection connection)
    {
        try
        {
        int check = 0;
        
        Statement stmt = connection.createStatement();
        ResultSet result;
        // result = stmt.executeQuery("SELECT id, causa_social, endereco, data_aquisicao, plano_id from (select @echoVarChar:='"+this.nome+"' p) parametro, getInstituicaoInfo;");
        result = stmt.executeQuery("select login, email, senha, data_ingresso from (select @echoInt:= '"+this.id+"' p) parametro, verMembrosInstituicao; ");

        while (result.next())
        {
            check++;

            System.out.print("\n-------------------------------\n");
            System.out.print("Usuario : " + result.getString("login"));
            System.out.print("\nEmail : " + result.getString("email"));
            System.out.print("\nSenha : " + result.getString("email"));
            System.out.print("\nData de ingresso : " + result.getDate("data_ingresso").toString());
        }
        System.out.print("\n-------------------------------\n");
        if (check == 0) { System.out.print("Parece que nao tem nenhum usuario nessa instituicao!\n Tente umas tecnicas de recrutamento melhor\n\n "); }

        scan.nextLine();
        System.out.print("\n\nAperte Enter para voltar. ");
        scan.nextLine();
        
        return;
    

        } catch (SQLException e) { e.printStackTrace();}
    }

    private class Arquivo 
    {
        String nome;
        String conteudo;
        String tipo;
        int permissoes;
        String data_alteracao;
        Integer tamanho; 
        String url;

        String dono_login;
    }

    private class Versionamento
    {
        String conteudo;
        String data;
        String hora; 
        int operacao;
        String autor_login;
    }


}
