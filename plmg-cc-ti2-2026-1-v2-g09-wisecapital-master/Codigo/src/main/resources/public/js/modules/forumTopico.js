import { usuarioService } from "../services/usuarioService.js";

const urlParams = new URLSearchParams(window.location.search);
const topicoId = urlParams.get("id");

// Variáveis globais para guardar quem está logado
let usuarioLogadoId = null;
let isAdm = false;

document.addEventListener("DOMContentLoaded", async () => {
  if (!topicoId) return;

  // Busca sessão
  try {
    const response = await usuarioService.getUsuarioAtual();
    if (response.ok) {
      const usuario = await response.json();
      usuarioLogadoId = usuario.id;
      isAdm = usuario.adm;
    }
  } catch (error) {}

  // 1. Carregar Tópico Principal
  try {
    const resTopico = await fetch(`http://localhost:8080/forum/${topicoId}`);
    const topico = await resTopico.json();

    // Se for dono do tópico, permite editar o Post principal
    let btnEditTopico = "";
    if (usuarioLogadoId === topico.usuarioId || isAdm) {
      btnEditTopico = `<button type="button" onclick="window.location.href='novo_topico.html?edit=${topico.id}'" class="btn-icon btn-edit" style="float: right;" title="Editar Tópico"><i class="fas fa-edit"></i></button>`;
    }

    let htmlTopico = `${btnEditTopico}<h1 style="margin-bottom: 10px; padding-right: 40px; color: var(--primary-color);">${topico.titulo}</h1>`;
    if (topico.imagemUrl) {
      htmlTopico += `<img src="${topico.imagemUrl}" style="max-width: 100%; border-radius: 12px; margin-bottom: 20px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);" alt="Imagem do Post">`;
    }
    htmlTopico += `<p style="font-size: 16px; line-height: 1.8; color: #444;">${topico.conteudo}</p>`;

    // Inserindo likes também na postagem principal aberta
    htmlTopico += `
    <div class="forum-actions" style="border: none; padding-top: 10px;">
         <button type="button" class="btn-action" onclick="interagir('topico', ${topico.id}, 'like', event)"><i class="fas fa-thumbs-up"></i> <span id="like-topico-${topico.id}">${topico.likes}</span></button>
         <button type="button" class="btn-action" onclick="interagir('topico', ${topico.id}, 'dislike', event)"><i class="fas fa-thumbs-down"></i> <span id="dislike-topico-${topico.id}">${topico.dislikes}</span></button>
    </div>`;

    document.getElementById("post-principal").innerHTML = htmlTopico;

    const btnSubmit = document.querySelector(".btn-comentar");
    if (btnSubmit)
      btnSubmit.innerHTML = '<i class="fas fa-paper-plane"></i> Enviar';
  } catch (error) {
    console.error("Erro ao carregar o tópico:", error);
  }

  // 2. Carregar Comentários
  carregarComentarios();

  // 3. Monitorizar o envio de novos comentários
  const formComentario = document.getElementById("form-comentario");
  if (formComentario) {
    formComentario.addEventListener("submit", async (e) => {
      e.preventDefault();

      if (!usuarioLogadoId) {
        alert("Você precisa fazer login para poder comentar!");
        return;
      }

      const params = new URLSearchParams();
      params.append("usuarioId", usuarioLogadoId);
      params.append(
        "conteudo",
        document.getElementById("texto-comentario").value,
      );

      try {
        const res = await fetch(
          `http://localhost:8080/forum/${topicoId}/comentarios/insert`,
          { method: "POST", body: params },
        );
        if (res.ok) {
          document.getElementById("texto-comentario").value = "";
          carregarComentarios();
        } else {
          alert("Erro ao salvar o comentário.");
        }
      } catch (err) {
        console.error("Erro ao enviar comentário:", err);
      }
    });
  }
});

async function carregarComentarios() {
  const lista = document.getElementById("lista-comentarios");
  if (!lista) return;
  lista.innerHTML = "";

  try {
    const resComentarios = await fetch(
      `http://localhost:8080/forum/${topicoId}/comentarios`,
    );
    const comentarios = await resComentarios.json();

    comentarios.forEach((c) => {
      // Cria o botão de editar e excluir o comentário se for do dono ou Admin
      let actionsHtml = "";

      if (usuarioLogadoId === c.usuarioId || isAdm) {
        // Ícones FontAwesome substituem totalmente os emojis amadores
        actionsHtml = `
            <button type="button" onclick="editarComentario(${c.id}, this)" data-texto="${c.conteudo.replace(/"/g, "&quot;")}" class="btn-icon btn-edit-comment" title="Editar Comentário"><i class="fas fa-edit"></i></button>
            <button type="button" onclick="deletarComentario(${c.id})" class="btn-icon btn-delete-comment" title="Excluir Comentário"><i class="fas fa-trash"></i></button>
        `;
      }

      lista.innerHTML += `
                <div class="comentario">
                    <p style="margin: 0 0 15px 0; color: #444; line-height: 1.6;">${c.conteudo}</p>
                    <div class="comentario-actions" style="margin-top: 0; padding-top: 10px; border-top: 1px solid var(--border-color);">
                        <button type="button" class="btn-action" onclick="interagir('comentario', ${c.id}, 'like', event)"><i class="fas fa-thumbs-up"></i> <span id="like-comentario-${c.id}">${c.likes}</span></button>
                        <button type="button" class="btn-action" onclick="interagir('comentario', ${c.id}, 'dislike', event)"><i class="fas fa-thumbs-down"></i> <span id="dislike-comentario-${c.id}">${c.dislikes}</span></button>
                        <div style="margin-left: auto; display: flex; gap: 5px;">${actionsHtml}</div>
                    </div>
                </div>`;
    });
  } catch (error) {
    console.error("Erro ao carregar comentários:", error);
  }
}

async function interagir(entidade, id, tipo, event) {
  if (event) {
    event.preventDefault();
    event.stopPropagation();
  }
  const url =
    entidade === "topico"
      ? `http://localhost:8080/forum/topico/${id}/${tipo}`
      : `http://localhost:8080/forum/comentario/${id}/${tipo}`;

  try {
    const res = await fetch(url, { method: "POST" });
    const result = await res.json();
    if (res.ok && result.success) {
      location.reload();
    } else {
      if (res.status === 401) alert("Você precisa fazer login para interagir!");
    }
  } catch (error) {
    console.error("Erro ao registrar interação:", error);
  }
}

window.interagir = interagir;

// Função para Editar Comentário de forma simples (com Pop-up)
window.editarComentario = async (id, btnElement) => {
  const textoAtual = btnElement.getAttribute("data-texto");
  const novoTexto = prompt("Edite o seu comentário:", textoAtual);

  if (novoTexto && novoTexto.trim() !== "" && novoTexto !== textoAtual) {
    const params = new URLSearchParams();
    params.append("conteudo", novoTexto);
    try {
      const res = await fetch(
        `http://localhost:8080/forum/comentario/${id}/atualizar`,
        {
          method: "POST",
          body: params,
        },
      );
      if (res.ok) {
        location.reload();
      } else {
        alert("Erro ao atualizar o comentário.");
      }
    } catch (e) {
      console.error(e);
    }
  }
};

// Função para Deletar Comentário
window.deletarComentario = async (id) => {
  if (confirm("Tem certeza que deseja excluir este comentário?")) {
    try {
      const res = await fetch(
        `http://localhost:8080/forum/comentario/delete/${id}`,
      );
      if (res.ok) {
        carregarComentarios(); // Recarrega os comentários instantaneamente
      } else {
        alert("Erro ao deletar o comentário.");
      }
    } catch (e) {
      console.error(e);
    }
  }
};
