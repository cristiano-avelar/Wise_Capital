import { listarFaqs } from "../services/faqService.js";

document.addEventListener("DOMContentLoaded", async () => {
  verificarPermissaoAdmin();
  await carregarFaqs();
});

window.deletarFaq = async function (id) {
  if (confirm("Tem certeza que deseja excluir esta pergunta?")) {
    await fetch(`/faq/delete/${id}`);
    location.reload();
  }
};

function verificarPermissaoAdmin() {
  const isAdm = localStorage.getItem("usuario_adm") === "true";
  if (isAdm) {
    const btnCadastro = document.getElementById("btn-cadastro-faq");
    if (btnCadastro) btnCadastro.style.display = "inline-flex";
    document.body.classList.add("is-admin");
  }
}

async function carregarFaqs() {
  const container = document.getElementById("faq-list");
  const isAdm = document.body.classList.contains("is-admin");

  try {
    const faqs = await listarFaqs();
    container.innerHTML = "";

    if (faqs.length === 0) {
      container.innerHTML = "<p>Nenhuma pergunta cadastrada.</p>";
      return;
    }

    faqs.forEach((faq) => {
      const faqItem = document.createElement("div");
      faqItem.className = "faq-item";

      const btnExcluir = isAdm
        ? `<button onclick="deletarFaq(${faq.id})" style="background: #ef4444; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; margin-left: 10px;"><i class="fas fa-trash"></i></button>`
        : "";

      faqItem.innerHTML = `
                <div class="faq-question">
                    <span style="flex-grow: 1;">${faq.pergunta}</span>
                    
            

                    ${btnExcluir}
                    <i class="fas fa-chevron-down" style="margin-left: 15px;"></i>
                </div>
                <div class="faq-answer">
                    <p style="margin: 15px 0;">${faq.resposta}</p>
                </div>
            `;

      // Lógica para abrir/fechar o cartão e contar a view
      const questionDiv = faqItem.querySelector(".faq-question");
      questionDiv.addEventListener("click", async (e) => {
        if (e.target.closest("button")) return;

        const isActive = faqItem.classList.contains("active");

        // Fecha as outras
        document
          .querySelectorAll(".faq-item")
          .forEach((item) => item.classList.remove("active"));

        if (!isActive) {
          // Abre a aba atual
          faqItem.classList.add("active");

          // Envia o acesso para o backend
          try {
            await fetch(`/faq/view/${faq.id}`, { method: "POST" });

            // Atualiza o contador na tela sem precisar recarregar a página!
            const viewSpan = document.querySelector(`#view-${faq.id} span`);
            const contagemAtual = parseInt(viewSpan.innerText);
            viewSpan.innerText = contagemAtual + 1;

            // Opcional: Para não contar várias vezes seguidas se o utilizador fechar e abrir sem parar
            faq.acessos = contagemAtual + 1;
          } catch (err) {
            console.error("Erro ao contabilizar acesso", err);
          }
        }
      });

      container.appendChild(faqItem);
    });
  } catch (erro) {
    console.error("Erro ao carregar FAQs:", erro);
    container.innerHTML = "<p>Erro ao carregar as perguntas.</p>";
  }
}
