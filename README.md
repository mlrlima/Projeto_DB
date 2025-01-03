# Projeto_DB

**Projeto para a disciplina de Projeto de Banco de Dados - UNICAP, 2024.2**

**Professor:** Jheymesson Apolinario

**Alunos:** 
- Maria Luiza Ribeiro de Lima (**RA:** 00000848982)
- Matheus Veríssimo Rodrigues Pinheiro (**RA:** 00000850062)
- Roberto Regis de Araújo Lima Neto (**RA:** 00000848959)
- Vinícius Martins Galindo Andrade (**RA:** 00000848753)
- Vitor Hugo José Sales da Silva (**RA:** 00000849201)

  <details>
    <summary>Modelo conceitual</summary>
    <img src="modelo_conceitual.png" width="100%">
  </details>
  <details>
    <summary>Modelo lógico</summary>
    <img src="modelo_logico.png" width="100%"> 
  </details>
  <details>
    <summary>Modelo físico</summary>
    ```
    
        stmt.execute("CREATE DATABASE webdriver;");
        stmt.execute("use webdriver;");

        stmt.execute
        (
        "CREATE TABLE Plano " +
        "(id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "nome VARCHAR(100) NOT NULL," +
        "duracao DATE," + 
        "limite_users INT);"
        );
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Plano to admin;");

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
        "FOREIGN KEY (plano_id) REFERENCES Plano(id) ON DELETE CASCADE );" 
        //"FOREIGN KEY (plano_id) REFERENCES Plano(id) ON DELETE RESTRICT ON UPDATE RESTRICT);" 
        );
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.instituicao to admin;");

                stmt.execute
        (
        "CREATE TABLE Administrador" +
        "(id INT PRIMARY KEY NOT NULL);" 
        );
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Administrador to admin;");

        stmt.execute(
        "CREATE TABLE Usuario (" +
        "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "login VARCHAR(100) NOT NULL," +
        "email VARCHAR(100) NOT NULL," + 
        "senha VARCHAR(100) NOT NULL," +
        "data_ingresso DATE NOT NULL," +
        "id_instituicao INT," +
        "id_admin INT," +

        "FOREIGN KEY (id_instituicao) REFERENCES instituicao(id) ON DELETE RESTRICT," +
        "FOREIGN KEY (id_admin) REFERENCES Administrador(id)" +
        ");");
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Usuario to admin;");

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
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Arquivo to admin;");

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
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Resposta to admin;");

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
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Suporte to admin;");

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
            stmt.execute("GRANT ALL PRIVILEGES on webdriver.Comentario to admin;");
        
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
        stmt.execute("GRANT ALL PRIVILEGES on webdriver.Compartilhamento to admin;");

        stmt.execute("create table Versionamento("+
        	"id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "conteudo TEXT," +
                "data DATE," +
                "hora TIME," +
                "operacao INT, "+
                "id_autor INT, "+
                "id_arquivo INT, "+
                "FOREIGN KEY (id_autor) REFERENCES Usuario(id) ON DELETE SET NULL, " +
                "FOREIGN KEY (id_arquivo) REFERENCES Arquivo(id) ON DELETE CASCADE); "
            );
            stmt.execute("GRANT ALL PRIVILEGES on webdriver.Versionamento to admin;");

            stmt.execute("create table Atividades_recentes("+
        	    "id_arquivo INT," +
                "data DATE," +
                "acesso TINYINT," +
                "FOREIGN KEY (id_arquivo) REFERENCES Arquivo(id) ON DELETE CASCADE); "
            );

           stmt.execute("create table Registro_operacoes("+
            "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
            "data DATE," +
            "hora TIME," +
            "id_arquivo INT," +
            "operacao INT, "+
            "id_autor INT, "+
            "id_alvo INT DEFAULT NULL ); "
            );
        ```

  </details>
