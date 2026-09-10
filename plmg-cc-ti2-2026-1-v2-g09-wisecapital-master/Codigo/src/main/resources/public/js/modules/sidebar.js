import { usuarioService } from "../services/usuarioService.js";

document.addEventListener("DOMContentLoaded", async () => {
  const userNameSpan = document.querySelector("#user-name");
  const btnLogout = document.querySelector("#btn-logout");

  // Caminho absoluto para a tela de login a partir da raiz do servidor static
  const loginPageUrl = "/pages/auth/index.html";

  try {
    // 1. Verifica no backend se o usuário possui sessão ativa
    const response = await usuarioService.getUsuarioAtual();

    if (!response.ok) {
      // Caso retorne erro ou 401 Unauthorized, redireciona imediatamente
      window.location.href = loginPageUrl;
      return;
    }

    const data = await response.json();

    if (data.logged) {
      // 2. Altera o texto "Carregando..." para o nome real do usuário
      if (userNameSpan) {
        userNameSpan.textContent = data.nome;
      }
    } else {
      window.location.href = loginPageUrl;
    }
  } catch (error) {
    console.error("Erro ao validar sessão do usuário:", error);
    // Se o servidor estiver offline ou houver falha na requisição, bloqueia o acesso
    window.location.href = loginPageUrl;
  }

  // 3. Ativa a funcionalidade do botão de Sair (Logout)
  if (btnLogout) {
    btnLogout.addEventListener("click", async (e) => {
      e.preventDefault();
      try {
        const response = await usuarioService.logout();
        if (response.ok) {
          window.location.href = loginPageUrl;
        }
      } catch (error) {
        console.error("Erro ao efetuar logout:", error);
        alert("Ocorreu um erro ao tentar sair do sistema.");
      }
    });
  }
});
