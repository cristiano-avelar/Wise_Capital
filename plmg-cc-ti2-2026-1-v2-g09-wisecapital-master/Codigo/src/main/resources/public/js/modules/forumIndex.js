document.addEventListener("DOMContentLoaded", async () => {
  const feed = document.getElementById("feed-forum");
  if (!feed) return;

  // Busca quem está logado para permitir exibir botão de edição
  let usuarioLogadoId = null;
  let isAdm = false;
  try {
    const resUser = await fetch("http://localhost:8080/usuario/atual");
    if (resUser.ok) {
      const user = await resUser.json();
      usuarioLogadoId = user.id;
      isAdm = user.adm;
      
      // Se for administrador, exibe o botão de criar novo tópico
      if (isAdm) {
          const btnNovoTopico = document.getElementById("btn-novo-topico");
          if (btnNovoTopico) {
              btnNovoTopico.style.display = "inline-block";
              btnNovoTopico.innerHTML = '<i class="fas fa-plus"></i> Novo Tópico';
          }
      }
    }
  } catch (e) {}

  try {
    const response = await fetch("http://localhost:8080/forum");
    const topicos = await response.json();

    feed.innerHTML = "";
    topicos.forEach((t) => {
      let conteudoHTML =
        t.imagemUrl && t.imagemUrl.trim() !== ""
          ? `<img src="${t.imagemUrl}" style="max-width: 100%; border-radius: 8px; margin-top: 10px;" alt="Imagem do tópico">`
          : `<p style="margin-top: 10px; color: #555; line-height: 1.5;">${t.conteudo.substring(0, 150)}...</p>`;

      // Se for dono do post ou Adm, mostra o botão de edição
      let btnEdit = "";
      if (usuarioLogadoId === t.usuarioId || isAdm) {
        btnEdit = `<button onclick="editarTopico(event, ${t.id})" class="btn-icon btn-edit" style="position: absolute; top: 15px; right: 15px;" title="Editar Tópico"><i class="fas fa-edit"></i></button>`;
      }

      feed.innerHTML += `
                <div class="forum-card" onclick="window.location.href='topico.html?id=${t.id}'" style="position: relative;">
                    ${btnEdit}
                    <h2 style="margin: 0; padding-right: 40px;">${t.titulo}</h2>
                    ${conteudoHTML}
                    <div class="forum-actions">
                        <button class="btn-action" onclick="interagir('topico', ${t.id}, 'like', event)"><i class="fas fa-thumbs-up"></i> <span id="like-topico-${t.id}">${t.likes}</span></button>
                        <button class="btn-action" onclick="interagir('topico', ${t.id}, 'dislike', event)"><i class="fas fa-thumbs-down"></i> <span id="dislike-topico-${t.id}">${t.dislikes}</span></button>
                        <span class="comments-count"><i class="fas fa-comment"></i> ${t.quantidadeComentarios} Comentários</span>
                    </div>
                </div>`;
    });
  } catch (error) {
    console.error("Erro ao carregar o feed do fórum:", error);
  }
});

async function interagir(entidade, id, tipo, event) {
  if (event) event.stopPropagation();
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

window.editarTopico = (event, id) => {
  event.stopPropagation();
  window.location.href = `novo_topico.html?edit=${id}`;
};