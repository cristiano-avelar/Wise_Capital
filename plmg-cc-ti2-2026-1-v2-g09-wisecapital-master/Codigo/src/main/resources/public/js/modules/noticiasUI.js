// A mesma chave que forneceste do teu projeto antigo
const API_KEY = "b9177d3e708eab9d7a16d82276d60032"; 
const API_NOTICIA = `https://gnews.io/api/v4/search?q=investimentos+OR+economia+OR+bolsa&lang=pt&country=br&max=12&apikey=${API_KEY}`;

document.addEventListener("DOMContentLoaded", () => {
    carregarNoticias();
});

async function carregarNoticias() {
    const container = document.getElementById("news-container");
    const loading = document.getElementById("loading-news");

    try {
        const res = await fetch(API_NOTICIA);
        const data = await res.json();
        
        loading.style.display = 'none'; // Esconde o texto "A carregar..."

        if (!data.articles || data.articles.length === 0) {
            container.innerHTML = "<p>Nenhuma notícia encontrada no momento.</p>";
            return;
        }

        data.articles.forEach((noticia) => {
            const card = document.createElement("div");
            card.className = "card-noticia";

            // Se a notícia não tiver imagem, colocamos uma imagem genérica sobre finanças
            const imagemUrl = noticia.image || "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=400&q=80";

            card.innerHTML = `
                <img src="${imagemUrl}" alt="Imagem da Notícia">
                <div class="card-content">
                    <h3>${noticia.title}</h3>
                    <p>${noticia.description || "Sem descrição disponível. Clique para ler a matéria completa."}</p>
                </div>
            `;

            // Abre a notícia no site original
            card.addEventListener("click", () => {
                window.open(noticia.url, "_blank");
            });

            container.appendChild(card);
        });
        
    } catch (error) {
        console.error("Erro ao procurar notícias:", error);
        loading.style.display = 'none';
        container.innerHTML = "<p style='color: red;'>Erro ao carregar o feed de notícias. Tente novamente mais tarde.</p>";
    }
}