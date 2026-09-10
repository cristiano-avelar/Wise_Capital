const inicialEl = document.getElementById("inicial");
const mensalEl = document.getElementById("mensal");
const mesesEl = document.getElementById("meses");
const rentabilidadeEl = document.getElementById("rentabilidade_valor");
const periodoEl = document.getElementById("rentabilidade_periodo");
const investidoEl = document.getElementById("resultado-investido");
const obtidoEl = document.getElementById("resultado-obtido");
const radios = document.querySelectorAll('input[name="tipo_investimento"]');

// Guarda as taxas que vêm da API
let taxasReais = { selic: 0, cdi: 0, poupanca: 0 };

document.addEventListener("DOMContentLoaded", () => {
  // 1. Vai buscar as taxas reais assim que a página abre
  buscarTaxasDeMercado();

  // 2. Se o utilizador escrever nos inputs de texto, recalcula
  const inputs = document.querySelectorAll('input[type="number"], select');
  inputs.forEach((input) => {
    input.addEventListener("input", calcularInvestimento);
  });

  // 3. Se o utilizador mudar o botão (Tesouro/CDI/Poupança), atualiza a taxa e recalcula
  radios.forEach((radio) => {
    radio.addEventListener("change", () => {
      atualizarInputRentabilidade();
      calcularInvestimento();
    });
  });

  calcularInvestimento();
});

// Calcula a regra da poupança com base na Selic
function calcularTaxaPoupanca(selicAtual) {
  if (selicAtual > 8.5) {
    return 6.17 + 1.5; // Aproximadamente 0.5% a.m + TR
  } else {
    return selicAtual * 0.7; // 70% da Selic
  }
}

// Vai à BrasilAPI buscar os dados atuais
async function buscarTaxasDeMercado() {
  try {
    const response = await fetch("https://brasilapi.com.br/api/taxas/v1");
    if (!response.ok) throw new Error("Erro ao buscar taxas");

    const dados = await response.json();

    const selicData = dados.find((item) => item.nome === "Selic");
    const cdiData = dados.find((item) => item.nome === "CDI");

    if (selicData) {
      taxasReais.selic = parseFloat(selicData.valor);
      taxasReais.poupanca = calcularTaxaPoupanca(taxasReais.selic);
    }
    if (cdiData) {
      taxasReais.cdi = parseFloat(cdiData.valor);
    }

    // Atualiza o input consoante o botão que está selecionado por defeito (CDI)
    atualizarInputRentabilidade();
    calcularInvestimento();
  } catch (error) {
    console.error("Erro ao buscar taxas reais:", error);
  }
}

// Muda o valor da caixa de texto da rentabilidade dependendo do botão escolhido
function atualizarInputRentabilidade() {
  const radioSelecionado = document.querySelector(
    'input[name="tipo_investimento"]:checked',
  );
  if (!radioSelecionado) return;

  const tipoSelecionado = radioSelecionado.value;
  periodoEl.value = "anual"; // Muda sempre para "ao ano" porque as taxas vêm anuais

  if (tipoSelecionado === "tesouro" && taxasReais.selic > 0) {
    rentabilidadeEl.value = taxasReais.selic.toFixed(2);
  } else if (tipoSelecionado === "cdi" && taxasReais.cdi > 0) {
    rentabilidadeEl.value = taxasReais.cdi.toFixed(2);
  } else if (tipoSelecionado === "poupanca" && taxasReais.poupanca > 0) {
    rentabilidadeEl.value = taxasReais.poupanca.toFixed(2);
  }
}

// Faz as contas e exibe o resultado final
function calcularInvestimento() {
  const P = parseFloat(inicialEl.value) || 0;
  const PMT = parseFloat(mensalEl.value) || 0;
  const n = parseFloat(mesesEl.value) || 0;
  let r = parseFloat(rentabilidadeEl.value) || 0;
  const periodo = periodoEl.value;

  r = r / 100;

  // Se a taxa estiver anual (como o CDI e Selic), converte para mensal para a fórmula
  if (periodo === "anual") {
    r = Math.pow(1 + r, 1 / 12) - 1;
  }

  const totalInvestido = P + PMT * n;
  let valorObtido = P * Math.pow(1 + r, n);

  if (r > 0) {
    valorObtido += PMT * ((Math.pow(1 + r, n) - 1) / r);
  } else {
    valorObtido += PMT * n;
  }

  const formatador = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

  investidoEl.innerText = formatador.format(totalInvestido);
  obtidoEl.innerText = formatador.format(valorObtido);
}
