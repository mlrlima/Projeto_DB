package usuario;

import java.util.Scanner;
import java.sql.*;
import java.util.InputMismatchException;

public class menuArquivo {
    Usuario user;

    public void MenuArquivo(Usuario user){
        this.user = user;
    }

    public void menu(Scanner scan, Connection connection){
        int menu = 10;

        while (menu != 0){
            System.out.print("\n\nArquivos\n------------------------\n\n[1] - Ver Arquivos\n[2] - Carregar arquivo\n[3] - Compartilhar arquivo\n[4] - alterar autorização do arquivo\n[5] - alterar o conteudo do arquivo\n[6] - arquivos compartilhados comigo\n[7] - remover arquivo\n[8] - fazer comentario\n[0] - sair\n");
            try{
                menu = scan.nextInt();
            }
            catch(InputMismatchException e){
                scan.next(); menu = 10;
            }

            switch (menu) {
                case 1: meusArquivos(scan, connection); break;
                case 2: criarArquivo(scan, connection); break;
                case 3: CompartilharArquivo(scan, connection); break;
                case 4: break;
                case 5: alterarArquivo(scan, connection); break;
                case 6: arquivosCompartilhadosComigo(scan, connection);
                case 7: removerArquivo(scan, connection); break;
                case 8: break;
                case 0: System.out.print("\n Good Bye");
                default: System.out.println("\nOpção Invalida!"); break;
            }
        }

    }

    private void meusArquivos(Scanner scan, Connection connection){

    }

    private void arquivosIntituicao(Scanner scan, Connection connection){

    }

    private void fazerComentarios(Scanner scan, Connection connection){

    }
    private void arquivosCompartilhadosComigo(Scanner scan, Connection connection){

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

    private void CompartilharArquivo(Scanner scan, Connection connection){

    }

    private void alterarArquivo(Scanner scan, Connection connection){

    }

    private void removerArquivo(Scanner scan, Connection connection){

    }
    private class Arquivo {
        String tipo;
        int permissoes;
        String data_alteracao;
        String tamanho;
        String url;
    }
}
