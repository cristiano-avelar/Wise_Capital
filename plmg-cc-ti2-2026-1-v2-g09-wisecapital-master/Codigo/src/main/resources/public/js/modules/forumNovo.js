import { usuarioService } from "../services/usuarioService.js";

document.addEventListener("DOMContentLoaded", async () => {
  const formTopico = document.getElementById("form-topico");
  if (!formTopico) return;

  // NOVO: Verifica se está em modo de Edição
  const urlParams = new URLSearchParams(window.location.search);
  const editId = urlParams.get("edit");

  if (editId) {
    document.querySelector("h2").innerText = "Editar Discussão";
    document.querySelector(".btn-submit").innerText = "Atualizar";
    try {
      const res = await fetch(`http://localhost:8080/forum/${editId}`);
      if (res.ok) {
        const topico = await res.json();
        document.getElementById("titulo").value = topico.titulo;
        document.getElementById("imagemUrl").value = topico.imagemUrl || "";
        document.getElementById("conteudo").value = topico.conteudo;
      }
    } catch (e) {
      console.error("Erro ao carregar tópico para edição:", e);
    }
  }

  formTopico.addEventListener("submit", async (e) => {
    e.preventDefault();

    let usuarioId = null;

    try {
      const response = await usuarioService.getUsuarioAtual();
      if (response.ok) {
        const usuario = await response.json();
        usuarioId = usuario.id;
      }
    } catch (error) {
      console.error("Erro ao buscar o usuário atual:", error);
    }

    if (!usuarioId) {
      alert("Você precisa fazer login para poder publicar/editar um tópico!");
      return;
    }

    const params = new URLSearchParams();
    params.append("usuarioId", usuarioId);
    params.append("titulo", document.getElementById("titulo").value);
    params.append("imagemUrl", document.getElementById("imagemUrl").value);
    params.append("conteudo", document.getElementById("conteudo").value);

    // Se estiver editando usa a rota de atualizar, senão usa a de insert
    const url = editId
      ? `http://localhost:8080/forum/topico/${editId}/atualizar`
      : `http://localhost:8080/forum/insert`;

    try {
      const res = await fetch(url, {
        method: "POST",
        body: params,
      });
      if (res.ok) {
        window.location.href = "index.html";
      } else {
        alert("Erro ao salvar o tópico no servidor.");
      }
    } catch (error) {
      console.error("Erro ao enviar o tópico:", error);
    }
  });
});
