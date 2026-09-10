package model;
import java.sql.Timestamp;

public class ForumComentario {
    private int id;
    private int topicoId;
    private int usuarioId;
    private String conteudo;
    private int likes;
    private int dislikes;
    private Timestamp dataCriacao;

    public ForumComentario(int id, int topicoId, int usuarioId, String conteudo, int likes, int dislikes, Timestamp dataCriacao) {
        this.id = id;
        this.topicoId = topicoId;
        this.usuarioId = usuarioId;
        this.conteudo = conteudo;
        this.likes = likes;
        this.dislikes = dislikes;
        this.dataCriacao = dataCriacao;
    }

    public int getId() { return id; }
    public int getTopicoId() { return topicoId; }
    public int getUsuarioId() { return usuarioId; }
    public String getConteudo() { return conteudo; }
    public int getLikes() { return likes; }
    public int getDislikes() { return dislikes; }
    public Timestamp getDataCriacao() { return dataCriacao; }
}