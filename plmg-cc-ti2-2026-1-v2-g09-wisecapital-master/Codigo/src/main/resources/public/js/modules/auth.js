import { usuarioService } from "../services/usuarioService.js";

const loginForm = document.querySelector("#login-form");
const registerForm = document.querySelector("#register-form");
const goToRegister = document.querySelector("#go-to-register");
const goToLogin = document.querySelector("#go-to-login");

// Alternar entre Login e Registro
goToRegister.addEventListener("click", (e) => {
  e.preventDefault();
  loginForm.classList.add("hidden");
  registerForm.classList.remove("hidden");
});

goToLogin.addEventListener("click", (e) => {
  e.preventDefault();
  registerForm.classList.add("hidden");
  loginForm.classList.remove("hidden");
});


loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const login = loginForm.login.value;
  const senha = loginForm.senha.value;

  try {
    const response = await usuarioService.login(login, senha);
    
    
    const result = await response.json(); 

	if (response.ok && result.success) {
	      alert("Bem-vindo, " + result.nome + "!");

	      // ADICIONA ESTA LINHA PARA GUARDAR A PERMISSÃO DE ADMIN:
	      // (Se o teu backend retorna 'adm' no JSON, guardamos como true/false)
	      localStorage.setItem('usuario_adm', result.adm || result.usuario?.adm || false);

	      if (result.primeiroAcesso) {
	        window.location.href = "../trilha/formia/index.html"; 
	      } else {
	        window.location.href = "../trilha/index.html";
	      }
	    } else {
      alert(result.message || "Erro ao realizar o login.");
    }
  } catch (error) {
    console.error("Erro no login:", error);
    alert("Ocorreu um erro interno de comunicação com o servidor.");
  }
});


registerForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const dados = {
    nome: registerForm.nome.value,
    login: registerForm.login.value,
    email: registerForm.email.value,
    senha: registerForm.senha.value,
  };

  try {
    const response = await usuarioService.cadastrar(dados);
    const result = await response.text();

    if (response.ok) {
      alert(result);
      location.reload();
    } else {
      alert(result);
    }
  } catch (error) {
    console.error("Erro no cadastro:", error);
  }
});
