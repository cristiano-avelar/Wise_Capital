import { usuarioService } from "../services/usuarioService.js";

document.addEventListener("DOMContentLoaded", async () => {
  const container = document.getElementById("quiz-container");

  try {
    let isAdm = false;
    try {
      const userResponse = await usuarioService.getUsuarioAtual();
      if (userResponse.ok) {
        const userData = await userResponse.json();
        isAdm = userData.adm;
      }
    } catch (e) {
      console.error("Erro ao verificar status de ADM", e);
    }

    const response = await fetch("/trilha/quizzes");

    if (!response.ok) {
      console.error("Erro ao carregar trilha. Talvez usuário não logado.");
      return;
    }

    const quizzes = await response.json();
    container.innerHTML = "";

    quizzes.forEach((quiz) => {
      let iconeSVG = "";
      if (quiz.status === "feito") {
        iconeSVG =
          '<path d="M5 13l4 4L19 7" stroke="#333" fill="none" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>';
      } else if (quiz.status === "liberado") {
        iconeSVG =
          '<path d="M7 11h10a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-6a2 2 0 0 1 2-2zM9 11V7.5a3.5 3.5 0 0 1 7 0" stroke="#333" fill="none" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>';
      } else if (quiz.status === "bloqueado") {
        iconeSVG =
          '<path d="M5 11h14a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-6a2 2 0 0 1 2-2zM7 11V7a5 5 0 0 1 10 0v4" stroke="#333" fill="none" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>';
      }

      // NOVO: Botão de editar e deletar dinâmico para Administradores
      let botoesAdmin = "";
      if (isAdm) {
        botoesAdmin = `
			                    <div style="position: absolute; top: 15px; right: 15px; display: flex; flex-direction: column; gap: 10px;">
			                        <button class="btn-edit-quiz" data-id="${quiz.id}" data-titulo="${quiz.titulo}" title="Editar Quiz" style="background: white; border: 1px solid #ccc; border-radius: 4px; padding: 5px; cursor: pointer; display: flex; justify-content: center; align-items: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
			                            <svg viewBox="0 0 24 24" width="20" height="20">
			                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="#002d5b" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
			                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="#002d5b" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
			                            </svg>
			                        </button>
			                        
			                        <button class="btn-delete-quiz" data-id="${quiz.id}" title="Excluir Quiz" style="background: #ef4444; border: none; border-radius: 4px; padding: 5px; cursor: pointer; display: flex; justify-content: center; align-items: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
			                            <svg viewBox="0 0 24 24" width="20" height="20" stroke="white" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
			                                <polyline points="3 6 5 6 21 6"></polyline>
			                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
			                            </svg>
			                        </button>
			                    </div>
			                `;
      }

      const cardHTML = `
			                <div class="quiz-card ${quiz.status}" data-id="${quiz.id}" style="position: relative;">
			                    <div class="quiz-card-icon-container">
			                        <svg class="quiz-card-icon" viewBox="0 0 24 24">${iconeSVG}</svg>
			                    </div>
			                    <div class="quiz-card-info">
			                        <h3>Quiz ${quiz.id}</h3>
			                        <p>${quiz.titulo}</p>
			                    </div>
			                    ${botoesAdmin}
			                </div>
			            `;

      container.innerHTML += cardHTML;
    });

    if (isAdm) {
      container.innerHTML += `
                <button class="btn-add-quiz" id="btn-add-quiz" title="Cadastrar Novo Quiz">
                    <svg class="btn-add-quiz-plus" viewBox="0 0 24 24">
                        <path d="M12 5v14M5 12h14" stroke="#333" fill="none" stroke-width="1.5" stroke-linecap="round"/>
                    </svg>
                </button>
            `;

      document.getElementById("btn-add-quiz").addEventListener("click", () => {
        window.location.href = "../admin/cadastro_quiz.html";
      });

      // NOVO: Adiciona o evento de clique aos botões de editar
      document.querySelectorAll(".btn-edit-quiz").forEach((btn) => {
        btn.addEventListener("click", (e) => {
          e.stopPropagation(); // Impede que clicar no lápis entre no quiz como jogador
          const qId = btn.getAttribute("data-id");
          const qTitulo = btn.getAttribute("data-titulo");
          // Passa os dados via URL
          window.location.href = `../admin/cadastro_quiz.html?edit=${qId}&titulo=${encodeURIComponent(qTitulo)}`;
        });
      });
      // NOVO: Adiciona o evento de clique aos botões de deletar
      document.querySelectorAll(".btn-delete-quiz").forEach((btn) => {
        btn.addEventListener("click", async (e) => {
          e.stopPropagation(); // Impede que clicar no botão entre no quiz como jogador
          const qId = btn.getAttribute("data-id");

          const confirmacao = confirm(
            "TEM A CERTEZA que deseja excluir este quiz? Esta ação é irreversível.",
          );
          if (confirmacao) {
            try {
              const res = await fetch(`/quiz/delete/${qId}`);
              const resultado = await res.json();

              if (resultado.success) {
                alert("Quiz excluído com sucesso!");
                location.reload(); // Recarrega a página da trilha para remover o cartão
              } else {
                alert("Erro ao excluir o quiz no servidor.");
              }
            } catch (error) {
              console.error("Erro ao deletar:", error);
              alert("Falha na comunicação com o servidor.");
            }
          }
        });
      });
    }

    // Eventos de clique para JOGAR os quizzes (Apenas liberados e feitos)
    document
      .querySelectorAll(".quiz-card.liberado, .quiz-card.feito")
      .forEach((card) => {
        card.addEventListener("click", () => {
          const quizId = card.getAttribute("data-id");
          window.location.href = `./quizzes/index.html?id=${quizId}`;
        });
      });

    document.querySelectorAll(".quiz-card.bloqueado").forEach((card) => {
      card.addEventListener("click", () => {
        alert("Este quiz está bloqueado. Complete o anterior primeiro!");
      });
    });
  } catch (error) {
    console.error("Erro na requisição:", error);
  }
});
