package usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

@SuppressWarnings("unused")

public class MenuArquivo 
{
    Usuario user;

    public MenuArquivo (Usuario user) // construtor
    {
        this.user = user;
    }
    
    public void menu(Scanner scan, Connection connection) 
    {
        int menu = 10;
        do
        {
            System.out.print("\n\n------------------------\n O que voce quer fazer agora?\n\n [1] - criar arquivo\n [2] - meus arquivos\n [3] - ver arquivos compartilhados comigo\n [4] - ver arquivos compartilhados com a instituicao\n [0] - sair\n\n   >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {   
                case 1 :  criarArquivo(scan, connection); break;
                case 2 :  meusArquivos(scan, connection); break;
                case 3 :  break;
                case 0 :  break;
                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;
            }

        } while (menu != 0);
    }

    private void meusArquivos(Scanner scan, Connection connection) 
    {
        ArrayList<Arquivo> arquivos = new ArrayList<>();
        Arquivo arquivo;

        try
        {
            String tipo, conteudo, data_alteracao, url, nome;
            int permissoes;
            Integer tamanho;

            Statement stmt = connection.createStatement();
            ResultSet result;
            result = stmt.executeQuery("SELECT conteudo, nome, tipo, permissoes, data_alteracao, tamanho, url FROM (select @echoInt:="+this.user.id+" p) parametro, verMeusArquivos");
            while (result.next())
            {
                conteudo = result.getString("conteudo");
                nome = result.getString("nome");
                tipo = result.getString("tipo");
                permissoes = result.getInt("permissoes");
                data_alteracao = result.getDate("data_alteracao").toString();
                tamanho = result.getInt("tamanho");
                url = result.getString("url");

                arquivo = new Arquivo();
                arquivo.conteudo = conteudo; arquivo.nome = nome; arquivo.tipo = tipo; arquivo.permissoes = permissoes; arquivo.data_alteracao = data_alteracao; arquivo.tamanho = tamanho; arquivo.url = url;
                arquivos.add(arquivo);
            }

            if (arquivos.size() == 0) 
            { 
                System.out.print("\n\n------------------------\n Atualmente, voce nao e dono de nenhum arquivo!");
                scan.nextLine();
                System.out.print("\n\nAperte Enter para voltar. ");
                scan.nextLine();
                return;
            }

            int menu = 10;

            do
            {
                for (int i = 0; i < arquivos.size(); i++ )
                {
                    arquivo = arquivos.get(i);
                    System.out.print("------------------------\n");
                    System.out.print("["+(i+1)+"]\n");
                    System.out.print(arquivo.nome + "." + arquivo.tipo + "\n");
                    if (arquivo.permissoes == 0) {System.out.print("privado\n");}
                    else {System.out.print("compartilhado\n");}
                    System.out.print("tamanho : " + arquivo.tamanho + "bytes\n");
                    //System.out.print("Status : ");
                }
                System.out.print("------------------------\n");
                System.out.print("\n\n Selecione o arquivo que voce quer ver, ou [0] para voltar.\n\n >>>");

                try {menu = scan.nextInt(); } 
		        catch (InputMismatchException e)
		        { scan.next(); menu = 10; }

                if (menu == 0) { return; }
                else if (menu > 0 && menu <= arquivos.size()) 
                {
                    // menu do arquivo
                }
                else { System.out.print("\n Entrada invalida!\n"); menu = 10; }


            } while (menu != 0);

        }   catch (SQLException e) { e.printStackTrace(); }
    }

    private void arquivosCompartilhadosComigo(Scanner scan, Connection connection) 
    {
        // aqui o cara seleciona as coisas
    }

    private void arquivosInstituicao(Scanner scan, Connection connection) 
    {
        // aqui o cara seleciona as coisas
    }



    private void criarArquivo(Scanner scan, Connection connection){
        Statement stmt;
        try{
            scan.nextLine(); // pra comer o line break nextint passado
            stmt = connection.createStatement();
            PreparedStatement prep = connection.prepareStatement("INSERT INTO Arquivo (nome, permissoes, conteudo, tipo, tamanho, data_alteracao, url, id_dono, localizacao) VALUE (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            System.out.print("\nDigite o nome do arquivo:\n>>> ");
            String name = scan.nextLine();
            System.out.print("\nDeseja deixar privado ou compartilhar com a instituição (Digite \"0\" ou \"1\")\n>>> ");
            int perm = scan.nextInt();
            scan.nextLine();
            System.out.print("\nDigite o nome do conteudo do arquivo:\n>>> ");
            String conteudo = scan.nextLine();
            System.out.print("\nDigite o tipo do arquivo:\n >>> ");
            String tipo = scan.nextLine();
            System.out.print("\nDigite o tamanho do arquivo: \n>>> ");
            int size = scan.nextInt();
            System.out.print("\nDeseja enviar a URL do arquivo ou a Localização (Digite \"0\" ou \"1\"):\n>>>");
            int url = scan.nextInt();

            ResultSet result = stmt.executeQuery("SELECT CURDATE();");
            result.next();

            if (url == 0){
                scan.nextLine();
                System.out.print("\nDigite a url do arquivo:\n>>>");
                String loc = scan.nextLine();
                prep.setString(7, loc);
            }
            else{
                scan.nextLine();
                System.out.print("\nDigite a localização do arquivo:\n>>>");
                String loc = scan.nextLine();
                prep.setNull(7, Types.VARCHAR);
            }
            prep.setString(1, name);
            prep.setInt(2, perm);
            prep.setString(3, conteudo);
            prep.setString(4, tipo);
            prep.setInt(5, size);
            prep.setDate(6, java.sql.Date.valueOf(result.getString(1)));
            prep.setInt(8, this.user.id);
            prep.setString(9, "lugar/em/que/a/gente/guarda/os/arquivos\n");
            prep.addBatch();
            prep.executeBatch();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
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

        // "select a.conteudo, a.tipo, a.permissoes, a.data_alteracao, a.tamanho, a.url FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) where id_dono = echoInt(); "

    }

}
