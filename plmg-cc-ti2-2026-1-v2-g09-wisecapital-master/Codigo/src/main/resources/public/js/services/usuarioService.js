const API_URL = "http://localhost:8080/usuario";

export const usuarioService = {
  async login(login, senha) {
    const response = await fetch(`${API_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `login=${login}&senha=${senha}`,
    });
    return response;
  },

  async cadastrar(dados) {
    const params = new URLSearchParams(dados).toString();
    const response = await fetch(`${API_URL}/insert`, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: params,
    });
    return response;
  },

  // --- NOVAS FUNÇÕES ---
  async getUsuarioAtual() {
    const response = await fetch(`${API_URL}/atual`);
    return response;
  },

  async logout() {
    const response = await fetch(`${API_URL}/logout`, { method: "POST" });
    return response;
  },
};
