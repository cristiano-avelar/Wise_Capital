document.addEventListener("DOMContentLoaded", () => {
    const formDiagnostico = document.getElementById("form-diagnostico");
  
    formDiagnostico.addEventListener("submit", async (e) => {
      e.preventDefault();
  
     
      const btn = formDiagnostico.querySelector(".btn-enviar");
      const textoOriginal = btn.innerHTML;
      btn.innerHTML = 'Analisando respostas... <i class="fas fa-spinner fa-spin"></i>';
      btn.disabled = true;
  
      
      const respostas = {
        reserva_emergencia: formDiagnostico.reserva_emergencia.value,
        diferenca_renda: formDiagnostico.diferenca_renda.value,
        perfil_risco: formDiagnostico.perfil_risco.value,
        juros_compostos_txt: formDiagnostico.juros_compostos_txt.value,
        objetivos_txt: formDiagnostico.objetivos_txt.value
      };
  
      
      const respostasJsonStr = JSON.stringify(respostas);
  
      try {
        
        const response = await fetch("http://localhost:8080/questionario/salvar", {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          
          body: new URLSearchParams({ respostas_json: respostasJsonStr })
        });
  
        const result = await response.json();
  
        if (response.ok && result.success) {
          alert("Análise concluída! Você foi classificado no Nível " + result.nivelSugerido);
         
          window.location.href = "../index.html";
        } else {
          alert("Erro ao salvar respostas: " + (result.message || "Tente novamente."));
          btn.innerHTML = textoOriginal;
          btn.disabled = false;
        }
      } catch (error) {
        console.error("Erro na comunicação com o servidor:", error);
        alert("Ocorreu um erro de rede. Verifique se o backend está rodando.");
        btn.innerHTML = textoOriginal;
        btn.disabled = false;
      }
    });
  });