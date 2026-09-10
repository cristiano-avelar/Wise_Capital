package model;
import java.sql.Timestamp;

public class ForumTopico {
    private int id;
    private int usuarioId;
    private String titulo;
    private String conteudo;
    private String imagemUrl;
    private int likes;
    private int dislikes;
    private int quantidadeComentarios;
    private Timestamp dataCriacao;

    public ForumTopico(int id, int usuarioId, String titulo, String conteudo, String imagemUrl, 
                       int likes, int dislikes, int quantidadeComentarios, Timestamp dataCriacao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.imagemUrl = imagemUrl;
        this.likes = likes;
        this.dislikes = dislikes;
        this.quantidadeComentarios = quantidadeComentarios;
        this.dataCriacao = dataCriacao;
    }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public String getTitulo() { return titulo; }
    public String getConteudo() { return conteudo; }
    public String getImagemUrl() { return imagemUrl; }
    public int getLikes() { return likes; }
    public int getDislikes() { return dislikes; }
    public int getQuantidadeComentarios() { return quantidadeComentarios; }
    public Timestamp getDataCriacao() { return dataCriacao; }
}