document.addEventListener('DOMContentLoaded', async () => {
    verificarPermissaoAdmin();
    await carregarBiblioteca();
    configurarFiltros();
});

let todosMateriais = [];

function verificarPermissaoAdmin() {
    const isAdm = localStorage.getItem('usuario_adm') === 'true';
    if (isAdm) {
        const btn = document.getElementById('btn-cadastro-midia');
        if (btn) btn.style.display = 'inline-flex';
        document.body.classList.add('is-admin');
    }
}

window.deletarMaterial = async function(id) {
    if (confirm("Deseja remover este material de estudo?")) {
        await fetch(`/biblioteca/delete/${id}`);
        location.reload();
    }
};

async function carregarBiblioteca() {
    const container = document.getElementById("biblioteca-list");
    try {
        const response = await fetch("/biblioteca/listar");
        todosMateriais = await response.json();
        renderizarCards(todosMateriais);
    } catch (erro) {
        console.error("Erro ao obter biblioteca:", erro);
        container.innerHTML = "<p>Erro ao carregar materiais. Verifica se o backend está a correr e a base de dados tem a tabela.</p>";
    }
}

function renderizarCards(materiais) {
    const container = document.getElementById("biblioteca-list");
    const isAdm = document.body.classList.contains('is-admin');
    container.innerHTML = "";

    if (!materiais || materiais.length === 0) {
        container.innerHTML = "<p>Nenhum conteúdo encontrado para este filtro.</p>";
        return;
    }

    materiais.forEach(mat => {
        const card = document.createElement('div');
        card.className = 'material-card';

        let mediaRender = `<i class="fas fa-file-alt" style="font-size: 3rem; color: #94a3b8;"></i>`;
        
        if (mat.tipo === 'VIDEO') {
            if (mat.urlConteudo.includes('youtube.com/watch?v=')) {
                const videoId = mat.urlConteudo.split('v=')[1]?.split('&')[0];
                mediaRender = `<iframe src="https://www.youtube.com/embed/${videoId}" allowfullscreen style="width: 100%; height: 100%; border: none;"></iframe>`;
            } else if (mat.urlConteudo.includes('youtu.be/')) {
                const videoId = mat.urlConteudo.split('youtu.be/')[1]?.split('?')[0];
                mediaRender = `<iframe src="https://www.youtube.com/embed/${videoId}" allowfullscreen style="width: 100%; height: 100%; border: none;"></iframe>`;
            } else {
                mediaRender = `<i class="fas fa-play-circle" style="font-size: 3rem; color: #94a3b8;"></i>`;
            }
        }

        const btnExcluir = isAdm 
            ? `<button onclick="deletarMaterial(${mat.id})" style="background: #ef4444; color: white; border: none; padding: 8px; border-radius: 4px; cursor: pointer; margin-top: 10px; width: 100%; font-weight: bold;"><i class="fas fa-trash"></i> Remover</button>` 
            : '';

        card.innerHTML = `
            <div class="material-media" style="width: 100%; height: 180px; background: #e2e8f0; display: flex; align-items: center; justify-content: center; position: relative;">
                <span class="badge-categoria" style="position: absolute; top: 10px; left: 10px; background: #002d5b; color: #fff; padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: bold;">${mat.categoria}</span>
                ${mediaRender}
            </div>
            <div class="material-body" style="padding: 20px; display: flex; flex-direction: column; flex-grow: 1;">
                <h3 style="margin: 0 0 10px 0; font-size: 1.2rem; color: #1e293b;">${mat.titulo}</h3>
                <p style="color: #64748b; font-size: 0.95rem; line-height: 1.5; margin: 0 0 20px 0; flex-grow: 1;">${mat.descricao}</p>
                <a href="${mat.urlConteudo}" target="_blank" class="btn-acessar" style="background: #002d5b; color: white; text-align: center; padding: 10px; border-radius: 6px; text-decoration: none; font-weight: bold; display: block;">
                    ${mat.tipo === 'VIDEO' ? '<i class="fas fa-play"></i> Assistir' : '<i class="fas fa-book-open"></i> Ler Conteúdo'}
                </a>
                ${btnExcluir}
            </div>
        `;
        container.appendChild(card);
    });
}

function configurarFiltros() {
    const botoes = document.querySelectorAll('.btn-filtro');
    botoes.forEach(btn => {
        btn.addEventListener('click', (e) => {
            if (e.target.id === 'btn-cadastro-midia' || e.target.closest('#btn-cadastro-midia')) return;

            botoes.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            const filtro = btn.getAttribute('data-tipo');
            if (filtro === 'TODOS') {
                renderizarCards(todosMateriais);
            } else {
                const filtrados = todosMateriais.filter(m => m.tipo === filtro);
                renderizarCards(filtrados);
            }
        });
    });
}