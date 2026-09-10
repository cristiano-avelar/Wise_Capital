// Lógica específica para a página inicial (Home/Dashboard)
document.addEventListener("DOMContentLoaded", () => {
  personalizarMensagemBoasVindas();
});

function personalizarMensagemBoasVindas() {
  const welcomeElement = document.getElementById("welcome-message");

  // Tenta ir buscar os dados do utilizador guardados pelo login
  const usuarioLogado =
    JSON.parse(localStorage.getItem("usuario")) ||
    JSON.parse(sessionStorage.getItem("usuario"));

  if (usuarioLogado && usuarioLogado.nome) {
    // Se houver um nome, personaliza o título!
    // Pega apenas o primeiro nome (separando por espaços)
    const primeiroNome = usuarioLogado.nome.split(" ")[0];
    welcomeElement.innerText = `Bem-vindo de volta, ${primeiroNome}!`;
  } else {
    welcomeElement.innerText = "Bem-vindo ao Wise Capital!";
  }
}
