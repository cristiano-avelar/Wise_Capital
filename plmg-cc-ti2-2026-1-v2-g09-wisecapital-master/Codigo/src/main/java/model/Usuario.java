package model;

public class Usuario {
    private int id;
    private String login;
    private String senha;
    private String nome;
    private String email;
    private int streakDays;
    private int quizLiberadoMaximo;
    private boolean adm;

    // Construtor vazio
    public Usuario() {
        this.id = -1;
        this.login = "";
        this.senha = "";
        this.nome = "";
        this.email = "";
        this.streakDays = 0;
        this.quizLiberadoMaximo = 1; // Padrão
        this.adm = false;
    }

    // Construtor completo (utilizado pelo UsuarioDAO)
    public Usuario(int id, String login, String senha, String nome, String email, int streakDays, int quizLiberadoMaximo, boolean adm) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.email = email;
        this.streakDays = streakDays;
        this.quizLiberadoMaximo = quizLiberadoMaximo;
        this.adm = adm;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public int getQuizLiberadoMaximo() {
        return quizLiberadoMaximo;
    }

    public void setQuizLiberadoMaximo(int quizLiberadoMaximo) {
        this.quizLiberadoMaximo = quizLiberadoMaximo;
    }

    public boolean isAdm() {
        return adm;
    }

    public void setAdm(boolean adm) {
        this.adm = adm;
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", login=" + login + ", nome=" + nome + ", email=" + email 
                + ", streakDays=" + streakDays + ", quizLiberadoMaximo=" + quizLiberadoMaximo + ", adm=" + adm + "]";
    }
}