packpackage usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Callable;

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
            System.out.print("\n\n------------------------\n O que voce quer fazer agora?\n\n [1] - criar arquivo\n [2] - meus arquivos\n [3] - ver arquivos compartilhados comigo\n");
            if (this.user.instituicao != null) {System.out.print(" [4] - ver arquivos compartilhados com a instituicao\n"); } //[4] - ver arquivos compartilhados com a instituicao\n [0] - sair\n\n   >>>");
            System.out.print(" [0] - sair\n\n   >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {   
                case 1 :  criarArquivo(scan, connection); break;
                case 2 :  meusArquivos(scan, connection); break;
                case 3 :  arquivosCompartilhadosComigo(scan, connection); break;
                case 4 :  if (this.user.instituicao != null) { arquivosInstituicao(scan, connection);} break;
                case 0 :  break;
                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;
            }

        } while (menu != 0);
    }

    private void meusArquivos(Scanner scan, Connection connection) 
    {
        ArrayList<Arquivo> arquivos = new ArrayList<>();
        Arquivo arquivo;
        arquivos = arquivoQuery(connection, 1);

            int retorno;
            int menu = 10;

            do
            {
            if (arquivos.size() == 0) 
            { 
                System.out.print("\n\n------------------------\n Atualmente, voce nao e dono de nenhum arquivo!");
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
                    if (arquivo.permissoes == 0) {System.out.print("privado\n");}
                    else {System.out.print("compartilhado\n");}
                    System.out.print("tamanho : " + arquivo.tamanho + " bytes\n");
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
                    arquivo = arquivos.get(menu-1);
                    if (arquivo.permissoes == 0) { retorno = verArquivo(1, arquivo, connection, scan); }
                    else { retorno = verArquivo(2, arquivo, connection, scan); }

                    if (retorno == 0) { arquivos = arquivoQuery(connection, 1); }
                }
                else { System.out.print("\n Entrada invalida!\n"); menu = 10; }


            } while (menu != 0);

    }


    private void arquivosCompartilhadosComigo(Scanner scan, Connection connection) 
    {
        ArrayList<Arquivo> arquivos = new ArrayList<>();
        Arquivo arquivo;
        arquivos = arquivoQuery(connection, 2);
        int retorno; int menu = 10;

        do
        {
            if (arquivos.size() == 0) 
            { 
                System.out.print("\n\n------------------------\n Atualmente, nao tem nenhum arquivo compartilhado com voce!");
                scan.nextLine();
                System.out.print("\n\nAperte Enter para voltar. ");
                scan.nextLine();
                return;
            }

            for (int i = 0; i < arquivos.size(); i++)
                {
                    arquivo = arquivos.get(i);
                    System.out.print("------------------------\n");
                    System.out.print("["+(i+1)+"]\n");
                    System.out.print(arquivo.nome + "." + arquivo.tipo + "\n");
                    System.out.print("tamanho : " + arquivo.tamanho + " bytes\n");
                    System.out.print("proprietario : " + arquivo.dono_login + "\n");
                    System.out.print("------------------------\n");
                }
                    System.out.print("\n\n Selecione o arquivo que voce quer ver, ou [0] para voltar.\n\n >>>");
                    try {menu = scan.nextInt(); } 
		            catch (InputMismatchException e)
		            { scan.next(); menu = 10; }

                    if (menu == 0) { return; }
                    else if (menu > 0 && menu <= arquivos.size()) 
                    {
                    arquivo = arquivos.get(menu-1);
                    retorno = verArquivo(3, arquivo, connection, scan);
                    if (retorno == 0) { arquivos = arquivoQuery(connection, 2); }
                    }
                else { System.out.print("\n Entrada invalida!\n"); menu = 10; }

                

        } while (menu != 0);
    }

    private void arquivosInstituicao(Scanner scan, Connection connection) 
    {
        ArrayList<Arquivo> arquivos = new ArrayList<>();
        Arquivo arquivo;
        arquivos = arquivoQuery(connection, 3);
        int menu = 10;

        do
        {
            if (arquivos.size() == 0) 
            { 
                System.out.print("\n\n------------------------\n Atualmente, nao tem nenhum arquivo compartilhado com voce!");
                scan.nextLine();
                System.out.print("\n\nAperte Enter para voltar. ");
                scan.nextLine();
                return;
            }
            for (int i = 0; i < arquivos.size(); i++)
                {
                    arquivo = arquivos.get(i);
                    System.out.print("------------------------\n");
                    System.out.print("["+(i+1)+"]\n");
                    System.out.print(arquivo.nome + "." + arquivo.tipo + "\n");
                    System.out.print("tamanho : " + arquivo.tamanho + " bytes\n");
                    System.out.print("proprietario : " + arquivo.dono_login + "\n");
                    System.out.print("------------------------\n");
                }

                System.out.print("\n\n Selecione o arquivo que voce quer ver, ou [0] para voltar.\n\n >>>");
                try {menu = scan.nextInt(); } 
                catch (InputMismatchException e)
                { scan.next(); menu = 10; }

                if (menu == 0) { return; }
                else if (menu > 0 && menu <= arquivos.size()) 
                {
                arquivo = arquivos.get(menu-1);
                verArquivo(4, arquivo, connection, scan);
                }
                else { System.out.print("\n Entrada invalida!\n"); menu = 10; }
        } while (menu != 0);
    }
    // portal

    private ArrayList<Comentario> comentarioQuery(Connection connection, Arquivo arquivo)
    {
        ArrayList<Comentario> comentarios = new ArrayList<>();
        Comentario comentario;
        String conteudo, data, hora, autor_login;
        ResultSet result;
        try
        {

        CallableStatement cstmt = connection.prepareCall("{ call verComentarios (?, ?, ?)}");
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
            comentario = new Comentario();
            comentario.conteudo = conteudo; comentario.data = data; comentario.hora = hora; comentario.autor_login = autor_login;
            comentarios.add(comentario);
            
        }
        } catch (SQLException e) { e.printStackTrace(); }

        return comentarios;
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

    private ArrayList<Arquivo> arquivoQuery(Connection connection, int contexto)
    {
        ArrayList<Arquivo> arquivos = new ArrayList<>();
        Arquivo arquivo;

        switch (contexto)
        {
            case 1 : // dono do arquivo
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
                arquivo.dono_login = this.user.login;
                arquivos.add(arquivo);
            }
            }
            catch (SQLException e) { e.printStackTrace(); }
            break;


            case 2 : // vendo privados compartilhados
            try
            {
            String tipo, conteudo, data_alteracao, url, nome, login;
            Integer tamanho;

            Statement stmt = connection.createStatement();
            ResultSet result;
            result = stmt.executeQuery("SELECT conteudo, nome, tipo, data_alteracao, tamanho, url, login FROM (select @echoInt:="+this.user.id+" p) parametro, arquivosCompartilhadosComigo");
            while (result.next())
            {
                conteudo = result.getString("conteudo");
                nome = result.getString("nome");
                tipo = result.getString("tipo");
                data_alteracao = result.getDate("data_alteracao").toString();
                tamanho = result.getInt("tamanho");
                url = result.getString("url");
                login = result.getString("login");

                arquivo = new Arquivo();
                arquivo.conteudo = conteudo; arquivo.nome = nome; arquivo.tipo = tipo; arquivo.data_alteracao = data_alteracao; arquivo.tamanho = tamanho; arquivo.url = url; arquivo.dono_login = login;
                arquivos.add(arquivo);
            }
            }
            catch (SQLException e) { e.printStackTrace(); }
            break;

            
            case 3 : // vendo da instituicao
            try 
            {

            String tipo, conteudo, data_alteracao, url, nome, login;
            Integer tamanho;
            Statement stmt = connection.createStatement();
            ResultSet result;
            result = stmt.executeQuery("SELECT conteudo, nome, tipo, data_alteracao, tamanho, url, login FROM (select @echoVarchar:='"+this.user.instituicao+"' p) parametro, arquivosCompartilhadosComInstituicao");
            while (result.next())
            {
                conteudo = result.getString("conteudo");
                nome = result.getString("nome");
                tipo = result.getString("tipo");
                data_alteracao = result.getDate("data_alteracao").toString();
                tamanho = result.getInt("tamanho");
                url = result.getString("url");
                login = result.getString("login");

                arquivo = new Arquivo();
                arquivo.conteudo = conteudo; arquivo.nome = nome; arquivo.tipo = tipo; arquivo.data_alteracao = data_alteracao; arquivo.tamanho = tamanho; arquivo.url = url; arquivo.dono_login = login;
                arquivos.add(arquivo);
            }
            } 
            catch (SQLException e) { e.printStackTrace(); }
            break;
        }

        return arquivos;
    }

    private int verArquivo(int contexto, Arquivo arquivo, Connection connection, Scanner scan)
    {
        int menu = 10;
        String input;
        int retorno = 1; // 1 -> nao precisa atualizar lista de arquivos, 0 -> mudou algo

    switch (contexto){ 

        case 1 : // quando dono do arquivo privado
        do
        {
            System.out.print("-------------------------------\n" + arquivo.nome + "." + arquivo.tipo + "\nTamanho : " + arquivo.tamanho );
            if (arquivo.url != null) {System.out.print("\nurl : " + arquivo.url);}

            System.out.print("\nPermissoes : Apenas compartilhado\n");
            try { CallableStatement cstmt = connection.prepareCall("{ call getQtdCompartilhamentos(?, ?, ?, ?)}"); 
                 cstmt.setInt(1, this.user.id);
                 cstmt.setString(2, arquivo.nome);
                 cstmt.setString(3, arquivo.tipo);
                 cstmt.registerOutParameter(4, Types.INTEGER);
                            //                                         call getQtdCompartilhamentos(1, 'teste', 'bin', @result);
                cstmt.execute();
                System.out.print("Quantidade de compartilhamentos : " + cstmt.getInt(4) + "\n\n");

                } 
                 catch (SQLException e) { e.printStackTrace(); }

            System.out.print("Conteudo : \n-------------------------------------------------------------\n" + arquivo.conteudo + "\n-------------------------------------------------------------\n");
            System.out.print("Opcoes :\n [1] - Compartilhar arquivo\n [2] - Atualizar arquivo\n [3] - Ver historico de versao\n [4] - Ver comentarios\n [123] - remover todos os compartilhamentos\n [321] - remover arquivo\n [0] voltar \n\n >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {
                case 1: compartilharArquivo(scan, connection, arquivo); 
                break;

                case 2: retorno = atualizarArquivo(scan, connection, arquivo); 
                break; 

                case 3: verVersionamento(scan, connection, arquivo);
                break;

                case 4: verComentarios(scan, connection, arquivo);
                break;

                case 123: try
                    {   Statement stmt = connection.createStatement();
                        stmt.execute("call Remover_Acessos(" + this.user.id +", '" + arquivo.nome + "', '"+ arquivo.tipo + "');");
                        System.out.print("\n Compartilhamentos removidos com sucesso!\n");
                    } catch (SQLException e) { e.printStackTrace(); }
                break;

                case 321: try
                    {   Statement stmt = connection.createStatement();
                        stmt.execute("call Remover_Arquivo(" + this.user.id +", '" + arquivo.nome + "', '"+ arquivo.tipo + "');");
                        System.out.print("\n Arquivo removido com sucesso!\n"); return 0;
                    } catch (SQLException e) { e.printStackTrace(); }
                break;


                case 0 : break;

                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;
            }

        } while (menu != 0); 
        break;

        case 2 : // quando eh o dono do arquivo compartilhado com a instituicao
        do
        {
            System.out.print("-------------------------------\n" + arquivo.nome + "." + arquivo.tipo + "\nTamanho : " + arquivo.tamanho );
            if (arquivo.url != null) {System.out.print("\nurl : " + arquivo.url);}
            
           
            System.out.print("\nPermissoes : Toda instituicao\n");

            System.out.print("Conteudo : \n-------------------------------------------------------------\n" + arquivo.conteudo + "\n-------------------------------------------------------------\n");
            System.out.print("Opcoes :\n [1] - Atualizar arquivo\n [2] - Ver historico de versao\n [3] - Ver Comentarios\n [321] - Remover Arquivo\n [0] voltar \n\n >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {
                case 1: retorno = atualizarArquivo(scan, connection, arquivo); 
                break;

                case 2: verVersionamento(scan, connection, arquivo);
                break;

                case 3: verComentarios(scan, connection, arquivo);
                break;

                case 321: try
                    {   Statement stmt = connection.createStatement();
                        stmt.execute("call Remover_Arquivo(" + this.user.id +", '" + arquivo.nome + "', '"+ arquivo.tipo + "');");
                        System.out.print("\n Arquivo removido com sucesso!\n"); return 0;
                    } catch (SQLException e) { e.printStackTrace(); }
                break;

                case 0:
                break;

                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;

            }

        } while (menu != 0); 
        break;

        case 3 :  // vendo arquivos compartilhado com o usuario
        do
        {
            System.out.print("-------------------------------\n" + arquivo.nome + "." + arquivo.tipo + "\nTamanho : " + arquivo.tamanho );
            if (arquivo.url != "0") {System.out.print("\nurl : " + arquivo.url);}
            System.out.print("\nNome do dono : " + arquivo.dono_login + "\n");
            System.out.print("Conteudo : \n-------------------------------------------------------------\n" + arquivo.conteudo + "\n-------------------------------------------------------------\n");
            System.out.print("Opcoes :\n [1] - Atualizar arquivo\n [2] - Ver historico de versao\n [3] - Ver Comentarios\n [0] voltar \n\n >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {
                case 1: retorno = atualizarArquivo(scan, connection, arquivo); 
                break;

                case 2: verVersionamento(scan, connection, arquivo);
                break;

                case 3: verComentarios(scan, connection, arquivo);
                break;

                case 0:
                break;

                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;
            }

        
        } while (menu != 0);

        case 4 : // vendo arquivos compartilhados com instituicao
        do
        {
            System.out.print("-------------------------------\n" + arquivo.nome + "." + arquivo.tipo + "\nTamanho : " + arquivo.tamanho );
            if (arquivo.url != "0") {System.out.print("\nurl : " + arquivo.url);}
            System.out.print("\nNome do dono : " + arquivo.dono_login + "\n");
            System.out.print("Conteudo : \n-------------------------------------------------------------\n" + arquivo.conteudo + "\n-------------------------------------------------------------\n");
            System.out.print("Opcoes :\n [1] - Ver historico de versao\n [2] - Ver Comentarios\n [0] voltar \n\n >>>");
            try {menu = scan.nextInt(); } 
		    catch (InputMismatchException e)
		    { scan.next(); menu = 10; }

            switch (menu)
            {

                case 1: verVersionamento(scan, connection, arquivo);
                break;

                case 2: verComentarios(scan, connection, arquivo);
                break;

                case 0:
                break;

                default: System.out.print("\n Entrada invalida!\n"); menu = 10; break;
            }

        } while (menu != 0);

        break;

        }
        return retorno;
    }

    private void verComentarios(Scanner scan, Connection connection, Arquivo arquivo)
    {
        ArrayList<Comentario> comentarios = new ArrayList<>();
        Comentario comentario;
        comentarios = comentarioQuery(connection, arquivo);

        int menu = 10;
        do
        {
            if (comentarios.size() == 0)
            {
                System.out.print("\n\n------------------------\n Esse arquivo nao tem nenhum comentario!\n");
            }
            else 
            {

            for (int i = 0; i < comentarios.size(); i++ )
                {
                    comentario = comentarios.get(i);
                    System.out.print("\n-------------------------------------------------------------\n");
                    System.out.print("Escrito por " + comentario.autor_login + " " + comentario.data + " " + comentario.hora);
                    System.out.print("\n\n" + comentario.conteudo);
                }
            }
            System.out.print("\n-------------------------------------------------------------\n");
                System.out.print("\n\n [1] Criar comentario\n [0] Voltar\n\n >>>");
                try {menu = scan.nextInt(); } 
		        catch (InputMismatchException e)
		        { scan.next(); menu = 10; }

                if (menu == 0) { return; }
                else if (menu == 1) { criarComentario(scan, connection, arquivo); comentarios = comentarioQuery(connection, arquivo); }
                else { System.out.print("\n Entrada invalida!\n"); menu = 10; }
            
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
            System.out.print("\nDigite o conteudo do arquivo:\n>>> ");
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

    private int atualizarArquivo(Scanner scan, Connection connection, Arquivo arquivo)
    {
        int retorno = 1; String input;
        System.out.print("Insira o novo conteudo do arquivo :\n >>>");
        scan.nextLine();
        try { input = scan.nextLine(); } catch (InputMismatchException e) { System.out.print("Erro : Entrada invalida!"); return 1; }
                try
                    {
                        Statement stmt = connection.createStatement();
                        stmt.execute("call Atualizar_Arquivo(" + this.user.id +", '" + arquivo.nome + "', '"+ arquivo.tipo + "', '"+ input + "');");
                        System.out.print("\n Arquivo atualizado com sucesso!\n");
                        arquivo.conteudo = input;
                        retorno = 0;
                    } catch (SQLException e) { e.printStackTrace(); }
        return retorno;
    }

    private void compartilharArquivo(Scanner scan, Connection connection, Arquivo arquivo) 
    {
        System.out.print("Digite o login do usuario com que voce quer compartilhar o arquivo :\n >>>");
        String input;
        scan.nextLine();
        input = scan.nextLine();
        try{ Statement stmt = connection.createStatement();
             stmt.execute("call compartilharArquivo(" + this.user.id +", '" + arquivo.tipo + "', '"+ arquivo.nome + "', '"+ input + "');");
             System.out.print("\n Arquivo compartilhado com sucesso!\n");
           } catch (SQLException e) { e.printStackTrace(); }
    }


    private void criarComentario(Scanner scan, Connection connection, Arquivo arquivo)
    {
        System.out.print("Digite a sua mensagem :\n >>>");
        String input;
        scan.nextLine();
        try { input = scan.nextLine(); } catch (InputMismatchException e) { System.out.print("Erro : Entrada invalida!"); return; }

        try{ Statement stmt = connection.createStatement();
            stmt.execute("call criarComentario("+this.user.id+", '" + arquivo.dono_login +"', '" + arquivo.nome + "', '"+ arquivo.tipo + "', '"+ input + "');");
            System.out.print("\n Comentario criado com sucesso!\n");
          } catch (SQLException e) { e.printStackTrace(); }

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

        // "select a.conteudo, a.tipo, a.permissoes, a.data_alteracao, a.tamanho, a.url FROM Arquivo a LEFT JOIN Usuario u on (a.id_dono = u.id) where id_dono = echoInt(); "

    }

    private class Comentario
    {
        String conteudo;
        String data;
        String hora;
        String autor_login;

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
