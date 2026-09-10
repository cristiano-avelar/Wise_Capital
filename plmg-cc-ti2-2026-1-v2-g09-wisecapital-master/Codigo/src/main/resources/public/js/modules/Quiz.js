document.addEventListener("DOMContentLoaded", async () => {
    
    const urlParams = new URLSearchParams(window.location.search);
    const quizId = urlParams.get('id');

    if (!quizId) {
        alert("Nenhum quiz selecionado.");
        window.location.href = "../trilha/index.html";
        return;
    }

    const areaPergunta = document.getElementById("area-pergunta");
    const progresso = document.getElementById("progresso");
    const feedbackArea = document.getElementById("feedback-area");
    const btnResponder = document.getElementById("btn-responder");
    const btnProxima = document.getElementById("btn-proxima");
    const btnVoltarTrilha = document.getElementById("btn-voltar-trilha");

    let perguntas = [];
    let indiceAtual = 0;
    let acertos = 0;

    
    try {
        const response = await fetch(`http://localhost:8080/quiz/${quizId}/perguntas`);
        perguntas = await response.json();
        
        if (perguntas.length > 0) {
            renderizarPergunta();
            btnResponder.style.display = "block";
        } else {
            progresso.innerHTML = "Este quiz ainda não tem perguntas cadastradas.";
        }
    } catch (error) {
        console.error("Erro ao carregar quiz:", error);
        progresso.innerHTML = "Erro ao carregar o quiz.";
    }

    
    function renderizarPergunta() {
        const p = perguntas[indiceAtual];
        progresso.innerHTML = `<strong>Pergunta ${indiceAtual + 1} de ${perguntas.length}</strong>`;
        feedbackArea.style.display = "none";
        feedbackArea.className = "";
        btnResponder.style.display = "block";
        btnProxima.style.display = "none";
        
        let html = `<h2 class="pergunta-titulo">${p.pergunta}</h2>`;

        if (p.tipo === "FECHADA") {
            html += `<div class="opcoes-container">`;
            p.opcoes.forEach(opcao => {
                html += `
                    <label class="opcao-label">
                        <input type="radio" name="opcao" value="${opcao.id}">
                        ${opcao.texto}
                    </label>`;
            });
            html += `</div>`;
        } else if (p.tipo === "ABERTA") {
            html += `<textarea id="resposta-aberta" placeholder="Escreva a sua resposta de forma detalhada..."></textarea>`;
        }

        areaPergunta.innerHTML = html;
    }

    
    btnResponder.addEventListener("click", async () => {
        const p = perguntas[indiceAtual];
        let estaCorreto = false;

        btnResponder.disabled = true;
        btnResponder.innerHTML = 'Processando... <i class="fas fa-spinner fa-spin"></i>';

        if (p.tipo === "FECHADA") {
            const opcaoSelecionada = document.querySelector('input[name="opcao"]:checked');
            if (!opcaoSelecionada) {
                alert("Selecione uma opção.");
                resetarBotao();
                return;
            }

            if (parseInt(opcaoSelecionada.value) === p.correta) {
                estaCorreto = true;
                acertos++;
                mostrarFeedback(true, "Correto! " + (p.explicacao || ""));
            } else {
                mostrarFeedback(false, "Incorreto. " + (p.explicacao || ""));
            }
            avancarEtapa();

        } else if (p.tipo === "ABERTA") {
            const textoResposta = document.getElementById("resposta-aberta").value;
            if (textoResposta.trim().length < 10) {
                alert("Por favor, desenvolva melhor a sua resposta.");
                resetarBotao();
                return;
            }

           
            try {
                const res = await fetch("http://localhost:8080/questionario/avaliar-aberta", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: new URLSearchParams({
                        pergunta: p.pergunta,
                        resposta_aluno: textoResposta
                    })
                });

                const avaliacao = await res.json();
                
                if (avaliacao.aprovado) {
                    acertos++;
                    mostrarFeedback(true, `Excelente! Pontuação: ${avaliacao.porcentagem}%<br><br><strong>Feedback da IA:</strong> ${avaliacao.feedback}`);
                } else {
                    mostrarFeedback(false, `Ainda pode melhorar. Pontuação: ${avaliacao.porcentagem}%<br><br><strong>Feedback da IA:</strong> ${avaliacao.feedback}`);
                }
                avancarEtapa();

            } catch (error) {
                alert("Erro ao conectar com a IA corretora.");
                resetarBotao();
            }
        }
    });

    function resetarBotao() {
        btnResponder.disabled = false;
        btnResponder.innerHTML = "Verificar Resposta";
    }

    function mostrarFeedback(sucesso, mensagem) {
        feedbackArea.style.display = "block";
        feedbackArea.innerHTML = mensagem;
        if (sucesso) {
            feedbackArea.className = "feedback-sucesso";
        } else {
            feedbackArea.className = "feedback-erro";
        }
    }

    function avancarEtapa() {
        btnResponder.style.display = "none";
        resetarBotao();
        
        if (indiceAtual + 1 < perguntas.length) {
            btnProxima.style.display = "block";
        } else {
            btnVoltarTrilha.style.display = "block";
            
            // Lógica de aprovação (70% de acertos)
            const aprovado = (acertos / perguntas.length) >= 0.7;
            const aprovacao = aprovado ? "Aprovado!" : "Tente novamente.";
            areaPergunta.innerHTML += `<hr><h3 style="margin-top:20px; text-align:center;">Fim do Quiz! Acertou ${acertos} de ${perguntas.length}. ${aprovacao}</h3>`;
            
            // NOVO: Notifica o backend se o utilizador foi aprovado
            if (aprovado) {
                fetch(`http://localhost:8080/quiz/${quizId}/concluir`, {
                    method: "POST"
                }).catch(err => console.error("Erro ao registar a conclusão do quiz:", err));
            }
        }
    }

    btnProxima.addEventListener("click", () => {
        indiceAtual++;
        renderizarPergunta();
    });

    btnVoltarTrilha.addEventListener("click", () => {
        window.location.href = "../index.html";
        
    });
	// --- NOVO: LÓGICA DO BOTÃO EXCLUIR PARA ADM ---
	    const isAdm = localStorage.getItem("usuario_adm") === "true";
	    
	    if (isAdm) {
	        // Cria o botão de excluir
	        const btnExcluirQuiz = document.createElement("button");
	        btnExcluirQuiz.innerHTML = '<i class="fas fa-trash"></i> Excluir Quiz';
	        btnExcluirQuiz.style.cssText = "background: #ef4444; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; font-weight: bold; margin-bottom: 20px; display: block; margin-left: auto; margin-right: auto;";
	        
	        // Insere o botão no topo, antes do elemento de progresso
	        progresso.parentNode.insertBefore(btnExcluirQuiz, progresso);

	        // Ação ao clicar em excluir
	        btnExcluirQuiz.addEventListener("click", async () => {
	            const confirmacao = confirm("TEM A CERTEZA que deseja excluir este quiz? Esta ação apagará todas as perguntas e o progresso dos alunos neste quiz.");
	            
	            if (confirmacao) {
	                try {
	                    const res = await fetch(`/quiz/delete/${quizId}`);
	                    const resultado = await res.json();
	                    
	                    if (resultado.success) {
	                        alert("Quiz excluído com sucesso!");
	                        window.location.href = "../index.html"; // Redireciona de volta para a trilha
	                    } else {
	                        alert("Erro ao excluir o quiz no servidor.");
	                    }
	                } catch (error) {
	                    console.error("Erro ao deletar:", error);
	                    alert("Falha na comunicação com o servidor.");
	                }
	            }
	        });
	    }
	    // ----------------------------------------------
});