const API_BASE_URL = "http://localhost:8080/api/usuarios";
const API_TRANSACOES = "http://localhost:8080/transacoes";
const API_CRYPTOS = "http://localhost:8080/api/criptomoedas";
const API_ATIVOS = "http://localhost:8080/api/ativos-carteira";
const API_HISTORICO = "http://localhost:8080/carteira/historico";
const API_PORTFOLIO = "http://localhost:8080/carteira/portfolio";

let todasCriptos = [];
let ativosUsuario = [];

// 🔹 Carregar dados do usuário
async function carregarUsuarioCliente() {
    const usernameEl = document.querySelector(".username");
    if (!usernameEl) return;

    try {
        const response = await fetch(`${API_BASE_URL}/eu`, { method: "GET", credentials: "same-origin" });

        if (response.ok) {
            const usuario = await response.json();
            usernameEl.textContent = usuario.nome;
            usernameEl.classList.remove("loading");

            sessionStorage.setItem("usuarioId", usuario.id);
            sessionStorage.setItem("usuarioNome", usuario.nome);
            sessionStorage.setItem("usuarioTipo", usuario.tipo);

            await carregarListasCriptomoedas(usuario.id);
            await carregarPortfolio();
            inicializarDropdown();
            await carregarHistoricoTransacoes();
        } else if (response.status === 401) {
            alert("Sessão expirada. Faça login novamente.");
            window.location.replace("/pages/login/login.html");
        } else {
            console.error("Erro ao carregar usuário:", response.status);
            usernameEl.textContent = "Cliente";
            usernameEl.classList.remove("loading");
        }
    } catch (error) {
        console.error("Erro ao buscar dados do usuário:", error);
        usernameEl.textContent = "Cliente";
        usernameEl.classList.remove("loading");
    }
}

// 🔹 Logout
async function realizarLogout() {
    try {
        await fetch(`${API_BASE_URL}/logout`, { method: "POST", credentials: "same-origin" });
    } catch (err) {
        console.error("Erro ao realizar logout:", err);
    } finally {
        sessionStorage.clear();
        window.location.replace("/pages/auth/login.html");
    }
}

// 🔹 Carrega listas de criptomoedas
async function carregarListasCriptomoedas(usuarioId) {
    try {
        const resCriptos = await fetch(API_CRYPTOS);
        if (resCriptos.ok) {
            const criptos = await resCriptos.json();
            todasCriptos = criptos.map(c => ({ id: c.id, nome: c.nome, sigla: c.sigla, icone: c.icone || "💰" }));
        } else {
            console.warn("Falha ao buscar lista de criptos:", resCriptos.status);
        }

        const resAtivos = await fetch(`${API_ATIVOS}/do-usuario`, { method: "GET", credentials: "same-origin" });
        if (resAtivos.ok) {
            const ativos = await resAtivos.json();
            ativosUsuario = ativos.map(a => ({
                id: a.criptomoeda?.id,
                nome: a.criptomoeda?.nome,
                sigla: a.criptomoeda?.sigla,
                icone: a.criptomoeda?.icone || "💰"
            }));
        } else {
            console.warn("Falha ao buscar ativos do usuário:", resAtivos.status);
        }
    } catch (err) {
        console.error("Erro ao carregar listas de criptomoedas:", err);
    }
}

// 🔹 Carregar dados do Portfólio
async function carregarPortfolio() {
    try {
        const res = await fetch(API_PORTFOLIO, { method: "GET", credentials: "same-origin" });
        if (!res.ok) throw new Error("Falha ao buscar portfólio");

        const portfolio = await res.json();

        // Funções de formatação
        const formatarReais = (valor, semSimbolo = false) => {
            const numeroFormatado = Number(valor).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
            return semSimbolo ? numeroFormatado : `R$ ${numeroFormatado}`;
        };
        const formatarRendimentoReais = (valor) => {
            const numero = Number(valor);
            const prefixo = numero > 0 ? '+' : '';
            return `${prefixo} R$ ${numero.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
        };
        const formatarRendimentoPercentual = (valor) => {
            const numero = Number(valor);
            const prefixo = numero > 0 ? '+' : '';
            return `${prefixo}${numero.toFixed(2).replace('.', ',')}%`;
        };
        const obterClasseRendimento = (valor) => {
            const numero = Number(valor);
            if (numero > 0) return 'positivo';
            if (numero < 0) return 'negativo';
            return 'neutro';
        };

        // 1. Popula o card de patrimônio resumido
        const patrimonioNumericoEl = document.getElementById('patrimonio-valor-numerico');
        patrimonioNumericoEl.dataset.valorReal = formatarReais(portfolio.patrimonioTotal, true); // Guarda o valor real sem "R$"
        patrimonioNumericoEl.textContent = '••••••••'; // Exibe as bolinhas por padrão

        // 2. Popula os detalhes gerais (sempre visíveis)
        const detalhesContainer = document.getElementById('detalhes-gerais-container');
        const classeRendimentoGeral = obterClasseRendimento(portfolio.rendimentoTotal);
        detalhesContainer.innerHTML = `
            <div class="detalhe-item">
                <span class="detalhe-label">Patrimônio Total</span>
                <span class="detalhe-valor">${formatarReais(portfolio.patrimonioTotal)}</span>
            </div>
            <div class="detalhe-item">
                <span class="detalhe-label">Total Investido</span>
                <span class="detalhe-valor">${formatarReais(portfolio.valorTotalComprado)}</span>
            </div>
            <div class="detalhe-item">
                <span class="detalhe-label">Rendimento Total</span>
                <span class="detalhe-valor ${classeRendimentoGeral}">${formatarRendimentoReais(portfolio.rendimentoTotal)}</span>
            </div>
            <div class="detalhe-item">
                <span class="detalhe-label">Rendimento Percentual</span>
                <span class="detalhe-valor ${classeRendimentoGeral}">${formatarRendimentoPercentual(portfolio.rendimentoPercentualTotal)}</span>
            </div>
        `;

        // 3. Popula a lista de ativos (sempre visíveis)
        const ativosContainer = document.getElementById('ativos-lista-container');
        if (portfolio.listaAtivos && portfolio.listaAtivos.length > 0) {
            ativosContainer.innerHTML = portfolio.listaAtivos.map(ativo => {
                const classeRendimentoAtivo = obterClasseRendimento(ativo.rendimento);
                return `
                    <div class="ativo-item">
                        <div class="ativo-info">
                            ${ativo.icone
                    ? `<img src="${escapeHtml(ativo.icone)}" alt="${escapeHtml(ativo.nome)}" class="ativo-icon-img">`
                    : '<span class="ativo-icon-emoji">💰</span>'
                }
                            <div class="ativo-nome-sigla">
                                <div class="nome">${escapeHtml(ativo.nome)}</div>
                                <div class="sigla">${escapeHtml(ativo.sigla)}</div>
                            </div>
                        </div>
                        <div class="ativo-comprado">
                            <div class="valor-comprado">${formatarReais(ativo.valorComprado)}</div>
                            <div class="quantidade">${Number(ativo.quantidade).toLocaleString('pt-BR', { maximumFractionDigits: 8 })} ${escapeHtml(ativo.sigla)}</div>
                        </div>
                        <div class="ativo-cotacao">${formatarReais(ativo.cotacaoAtual)}</div>
                        <div class="ativo-mercado">${formatarReais(ativo.valorAtualMercado)}</div>
                        <div class="ativo-rendimento col-direita">
                            <div class="valor-reais ${classeRendimentoAtivo}">${formatarRendimentoReais(ativo.rendimento)}</div>
                            <div class="valor-percent ${classeRendimentoAtivo}">${formatarRendimentoPercentual(ativo.rendimentoPercentual)}</div>
                        </div>
                    </div>
                `;
            }).join('');
        } else {
            ativosContainer.innerHTML = '<div class="sem-ativos">Você ainda não possui ativos na carteira.</div>';
        }

    } catch (err) {
        console.error("Erro ao carregar portfólio:", err);
        document.getElementById('patrimonio-valor-numerico').textContent = 'Erro';
    }
}


// 🔹 Carregar histórico de transações
async function carregarHistoricoTransacoes() {
    const container = document.querySelector("#transacoes-lista");
    if (!container) return;

    container.innerHTML = "<div class='loading'>Carregando histórico...</div>";

    try {
        const res = await fetch(API_HISTORICO, { method: "GET", credentials: "same-origin" });
        if (!res.ok) throw new Error("Falha ao buscar histórico");
        const historico = await res.json();

        container.innerHTML = "";

        if (!Array.isArray(historico) || historico.length === 0) {
            container.innerHTML = "<div class='sem-transacoes'>Nenhuma transação encontrada.</div>";
            return;
        }

        historico.sort((a, b) => new Date(b.momentoTransacao) - new Date(a.momentoTransacao));

        historico.forEach((tx, index) => {
            const data = new Date(tx.momentoTransacao);
            const dataFormatada = data.toLocaleDateString("pt-BR", { day: "2-digit", month: "2-digit", year: "numeric" }) + " " + data.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" });
            const tipoClass = (tx.tipoTransacao || "").toLowerCase();
            const valorFormatado = `R$ ${Number(tx.valorComprado).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}`;

            const item = document.createElement("div");
            item.className = "transacao";
            item.style.animationDelay = `${index * 0.06}s`;
            item.innerHTML = `
                <div class="tx-left">
                  <div class="tx-name">${escapeHtml(tx.nomeCripto)} (${escapeHtml(tx.siglaCripto)})</div>
                  <div class="tx-date">${dataFormatada}</div>
                </div>
                <div class="tx-right">
                  <div class="tx-type ${tipoClass}">${escapeHtml(tx.tipoTransacao)}</div>
                  <div class="tx-value">${valorFormatado}</div>
                </div>`;
            container.appendChild(item);
        });
    } catch (err) {
        console.error("Erro ao carregar histórico:", err);
        container.innerHTML = "<div class='erro-transacoes'>Erro ao carregar histórico.</div>";
    }
}

// 🔹 Inicializa dropdown de criptos e envio de transações
function inicializarDropdown() {
    const cryptoInput = document.querySelector("#crypto");
    const dropdown = document.querySelector(".crypto-dropdown");
    if (!cryptoInput || !dropdown) return;

    const tipoRadios = document.querySelectorAll("input[name='tipo']");

    function atualizarDropdown() {
        const tipoSelecionado = document.querySelector("input[name='tipo']:checked").value;
        const lista = tipoSelecionado === "compra" ? todasCriptos : ativosUsuario;
        dropdown.innerHTML = "";

        lista.forEach(c => {
            const item = document.createElement("div");
            item.className = "crypto-item";
            item.dataset.id = c.id;
            item.innerHTML = `
                <span class="crypto-name">${escapeHtml(c.nome)}</span>
                <span class="crypto-sigla">(${escapeHtml(c.sigla)})</span>`;
            item.addEventListener("mousedown", e => e.preventDefault());
            item.addEventListener("click", () => {
                cryptoInput.value = `${c.nome} (${c.sigla})`;
                cryptoInput.dataset.id = c.id;
                dropdown.style.display = "none";
            });
            dropdown.appendChild(item);
        });
    }

    tipoRadios.forEach(r => r.addEventListener("change", atualizarDropdown));
    atualizarDropdown();

    cryptoInput.addEventListener("focus", () => dropdown.style.display = "block");
    cryptoInput.addEventListener("blur", () => {
        setTimeout(() => dropdown.style.display = "none", 150);
    });

    const form = document.querySelector(".transaction-form");
    const valorInput = document.querySelector("#valor");
    const equivalenciaEl = document.querySelector("#equivalencia");

    if (!form) return;

    form.addEventListener("submit", async e => {
        e.preventDefault();
        const tipo = document.querySelector("input[name='tipo']:checked").value;
        const usuarioId = sessionStorage.getItem("usuarioId");
        const valor = parseFloat((valorInput.value || "").replace(",", "."));
        const criptoId = parseInt(cryptoInput.dataset.id);

        if (!criptoId || isNaN(valor)) {
            alert("Selecione uma criptomoeda e informe o valor corretamente.");
            return;
        }

        const payload = {
            usuarioId: parseInt(usuarioId),
            criptomoedaId: criptoId,
            tipo: tipo.charAt(0).toUpperCase() + tipo.slice(1),
            valor: valor,
            quantidadeCripto: 0
        };

        try {
            const res = await fetch(API_TRANSACOES, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "same-origin",
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                alert("Transação realizada com sucesso!");
                form.reset();
                equivalenciaEl.textContent = "0";
                cryptoInput.dataset.id = "";
                await carregarListasCriptomoedas(usuarioId);
                await carregarPortfolio();
                await carregarHistoricoTransacoes();
                atualizarDropdown();
            } else {
                let errObj = {};
                try { errObj = await res.json(); } catch (_) { /* ignore */ }
                alert("Erro: " + (errObj.message || "Falha ao criar transação"));
            }
        } catch (err) {
            console.error("Erro ao processar transação:", err);
            alert("Erro ao processar transação.");
        }
    });
}

function escapeHtml(unsafe) {
    if (unsafe === null || unsafe === undefined) return "";
    return String(unsafe)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

// 🔹 Inicialização
window.addEventListener("DOMContentLoaded", () => {
    carregarUsuarioCliente();

    const logoutBtn = document.querySelector(".btn-logout");
    if (logoutBtn) logoutBtn.addEventListener("click", realizarLogout);

    const secaoPatrimonio = document.getElementById('secao-patrimonio');
    const btnVerDetalhes = document.querySelector('.ver-detalhes');
    const btnFecharDetalhes = document.querySelector('.fechar-detalhes');
    const btnVisibilidade = document.getElementById('btn-visibilidade');
    const patrimonioNumericoEl = document.getElementById('patrimonio-valor-numerico');

    // Evento para expandir para a visão detalhada
    btnVerDetalhes.addEventListener('click', (e) => {
        e.preventDefault();
        secaoPatrimonio.classList.add('expandido');
    });

    // Evento para voltar para a visão resumida
    btnFecharDetalhes.addEventListener('click', (e) => {
        e.preventDefault();
        secaoPatrimonio.classList.remove('expandido');
        // Garante que o valor volte a ficar oculto
        secaoPatrimonio.classList.remove('valor-visivel');
        patrimonioNumericoEl.textContent = '••••••••';
    });

    // Evento para alternar a visibilidade apenas na visão resumida
    btnVisibilidade.addEventListener('click', () => {
        const estaVisivel = secaoPatrimonio.classList.toggle('valor-visivel');
        if (estaVisivel) {
            patrimonioNumericoEl.textContent = patrimonioNumericoEl.dataset.valorReal;
        } else {
            patrimonioNumericoEl.textContent = '••••••••';
        }
    });
});
