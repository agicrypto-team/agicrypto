const API_BASE_URL = "http://localhost:8080/api/usuarios";
const API_TRANSACOES = "http://localhost:8080/transacoes";
const API_CRYPTOS = "http://localhost:8080/api/criptomoedas";
const API_ATIVOS = "http://localhost:8080/api/ativos-carteira";
const API_HISTORICO = "http://localhost:8080/carteira/historico";
const API_PORTFOLIO = "http://localhost:8080/carteira/portfolio";

let todasCriptos = []; // Criptos para COMPRA
let ativosUsuario = []; // Criptos que o usuário possui (para VENDA)

// Variável global para o ID do usuário (necessária para algumas chamadas)
const usuarioId = sessionStorage.getItem("usuarioId");


/**
 * 🔹 UTILS
 */

function escapeHtml(unsafe) {
    if (unsafe === null || unsafe === undefined) return "";
    return String(unsafe)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


/**
 * 🔹 FORMATADORES
 */

const formatarReais = (valor, semSimbolo = false) => {
    const numeroFormatado = Number(valor).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    return semSimbolo ? numeroFormatado : `R$ ${numeroFormatado}`;
};

const formatarRendimentoReais = (valor) => {
    // Usado para rendimento em R$ (melhor tratamento de prefixo da primeira versão)
    const numero = Number(valor);
    const prefixo = numero > 0 ? '+ ' : numero < 0 ? '- ' : '';
    const valorAbs = Math.abs(numero).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    return `${prefixo}R$ ${valorAbs}`;
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


/**
 * 🔹 CARREGAMENTO DE DADOS
 */

// 🔹 Carregar dados do usuário (Otimizado com Promise.all da primeira versão)
async function carregarUsuarioCliente() {
    const usernameEl = document.querySelector(".username");
    if (!usernameEl) return;

    try {
        const response = await fetch(`${API_BASE_URL}/eu`, { method: "GET", credentials: "same-origin" });

        if (response.ok) {
            const usuario = await response.json();
            usernameEl.textContent = usuario.nome;
            usernameEl.classList.remove("loading");

            // Armazenamento em sessão
            sessionStorage.setItem("usuarioId", usuario.id);
            sessionStorage.setItem("usuarioNome", usuario.nome);
            sessionStorage.setItem("usuarioTipo", usuario.tipo);

            // Carrega dados iniciais em paralelo (otimização)
            await Promise.all([
                carregarListasCriptomoedas(usuario.id),
                carregarPortfolio(),
                carregarHistoricoTransacoes()
            ]);

            inicializarDropdown(); // Depende das listas de criptos

            // Inicializa tooltips (recurso da primeira versão)
            const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
            [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

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


// 🔹 Carrega listas de criptomoedas (Lógica de ativos do usuário da versão que funcionava)
async function carregarListasCriptomoedas(usuarioId) {
    try {
        // --- 1️⃣ Busca todas criptomoedas ---
        const resCriptos = await fetch(API_CRYPTOS);
        if (resCriptos.ok) {
            const criptos = await resCriptos.json();
            todasCriptos = criptos.map(c => ({
                id: c.id,
                nome: c.nome,
                sigla: c.sigla,
                icone: c.icone || "💰"
            }));
        } else {
            console.warn("Falha ao buscar lista de criptos:", resCriptos.status);
        }

        // --- 2️⃣ Busca ativos do usuário (Versão que funcionava, usando ID na URL) ---
        // Se o seu backend exige o ID na URL para listar ativos, essa é a lógica correta:
        const resAtivos = await fetch(`${API_ATIVOS}/do-usuario/${usuarioId}`, { method: "GET", credentials: "same-origin" });

        if (resAtivos.ok) {
            const ativos = await resAtivos.json();

            // Lógica para garantir que os dados da criptomoeda estejam disponíveis (mantido do seu código funcional)
            ativosUsuario = await Promise.all(ativos.map(async (a) => {
                let cripto = a.criptomoeda;

                if (!cripto || !cripto.id) {
                    const resCripto = await fetch(`${API_CRYPTOS}/${a.idCriptomoeda || a.id}`);
                    if (resCripto.ok) {
                        cripto = await resCripto.json();
                    }
                }

                return {
                    id: cripto?.id || a.id,
                    nome: cripto?.nome || "Desconhecida",
                    sigla: cripto?.sigla || "---",
                    icone: cripto?.icone || "💰",
                    quantidade: a.quantidade || 0
                };
            }));

        } else {
            console.warn("Falha ao buscar ativos do usuário:", resAtivos.status);
        }

    } catch (err) {
        console.error("Erro ao carregar listas de criptomoedas:", err);
    }
}


// 🔹 Carregar dados do Portfólio (Incluindo tooltips e formatação correta)
async function carregarPortfolio() {
    try {
        const res = await fetch(API_PORTFOLIO, { method: "GET", credentials: "same-origin" });
        if (!res.ok) throw new Error("Falha ao buscar portfólio");

        const portfolio = await res.json();

        // 1. Card principal
        const patrimonioNumericoEl = document.getElementById('patrimonio-valor-numerico');
        patrimonioNumericoEl.dataset.valorReal = formatarReais(portfolio.patrimonioTotal, true);
        patrimonioNumericoEl.textContent = '••••••••';

        // 2. Detalhes gerais (com tooltips da primeira versão)
        const detalhesContainer = document.getElementById('detalhes-gerais-container');
        const classeRendimentoGeral = obterClasseRendimento(portfolio.rendimentoTotal);
        detalhesContainer.innerHTML = `
            <div class="detalhe-item">
                <span class="detalhe-label">Patrimônio Total<span class="tooltip-icon" data-bs-toggle="tooltip" data-bs-html="true" title="Valor total de todos os seus investimentos somados, com base na cotação de mercado atual de cada criptomoeda<br>---------------<br>Este é o valor que você teria em reais se vendesse todas as suas criptomoedas agora">?</span></span>
                <span class="detalhe-valor">${formatarReais(portfolio.patrimonioTotal)}</span>
            </div>
            <div class="detalhe-item">
                <span class="detalhe-label">Total Investido<span class="tooltip-icon" data-bs-toggle="tooltip" title="Soma de todos os valores que você investiu para adquirir suas criptomoedas">?</span></span>
                <span class="detalhe-valor">${formatarReais(portfolio.valorTotalComprado)}</span>
            </div>
            <div class="detalhe-item">
                <span class="detalhe-label">Rendimento Total<span class="tooltip-icon" data-bs-toggle="tooltip" title="Lucro ou prejuízo total de todos os seus investimentos">?</span></span>
                <span class="detalhe-valor ${classeRendimentoGeral}">${formatarRendimentoReais(portfolio.rendimentoTotal)}</span>
            </div>
            <div class="detalhe-item">
                <span class="detalhe-label">Rendimento Percentual<span class="tooltip-icon" data-bs-toggle="tooltip" title="Variação percentual do seu patrimônio em relação ao total investido">?</span></span>
                <span class="detalhe-valor ${classeRendimentoGeral}">${formatarRendimentoPercentual(portfolio.rendimentoPercentualTotal)}</span>
            </div>
        `;

        // 3. Lista de ativos
        const ativosContainer = document.getElementById('ativos-lista-container');
        if (portfolio.listaAtivos?.length > 0) {
            ativosContainer.innerHTML = portfolio.listaAtivos.map(ativo => {
                const classeRendimentoAtivo = obterClasseRendimento(ativo.rendimento);
                return `
                    <div class="ativo-item">
                        <div class="ativo-info">
                            ${ativo.icone
                                ? `<img src="${escapeHtml(ativo.icone)}" alt="${escapeHtml(ativo.nome)}" class="ativo-icon-img">`
                                : '<span class="ativo-icon-emoji">💰</span>'}
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


// 🔹 Histórico de transações
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
            const dataFormatada = data.toLocaleDateString("pt-BR", { day: "2-digit", month: "2-digit", year: "numeric" }) +
                " " + data.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" });
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


/**
 * 🔹 TRANSAÇÕES E EQUIVALÊNCIA (CÓDIGO FUNCIONAL RECUPERADO)
 */

// 🔹 Inicializa dropdown de criptomoedas e envio de transações
function inicializarDropdown() {
    const cryptoInput = document.querySelector("#crypto");
    const dropdown = document.querySelector(".crypto-dropdown");
    if (!cryptoInput || !dropdown) return;

    const tipoRadios = document.querySelectorAll("input[name='tipo']");
    const valorInput = document.querySelector("#valor");
    const equivalenciaEl = document.querySelector("#equivalencia");
    const form = document.querySelector(".transaction-form");

    // Reintroduzindo a variável para guardar a cripto selecionada (essencial para a equivalência)
    let criptomoedaSelecionada = null;

    // ================================
    // 🔸 1. Atualiza lista do dropdown
    // ================================
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
                <span class="crypto-sigla">(${escapeHtml(c.sigla)})</span>
            `;
            item.addEventListener("mousedown", e => e.preventDefault());
            item.addEventListener("click", () => {
                // 🔹 Guarda a cripto selecionada globalmente
                criptomoedaSelecionada = c;
                cryptoInput.value = `${c.nome} (${c.sigla})`;
                cryptoInput.dataset.id = c.id;
                dropdown.style.display = "none";

                // 🔹 Recalcula equivalência se já houver valor digitado
                calcularEquivalencia();
            });
            dropdown.appendChild(item);
        });
    }

    tipoRadios.forEach(r => r.addEventListener("change", () => {
        // Limpa a seleção e re-renderiza o dropdown
        criptomoedaSelecionada = null;
        cryptoInput.value = "";
        cryptoInput.dataset.id = "";
        equivalenciaEl.textContent = "0";
        atualizarDropdown();
    }));
    atualizarDropdown();

    // ================================
    // 🔸 2. Exibir/ocultar dropdown
    // ================================
    cryptoInput.addEventListener("focus", () => dropdown.style.display = "block");
    cryptoInput.addEventListener("blur", () => {
        setTimeout(() => dropdown.style.display = "none", 150);
    });

    // ================================
    // 🔸 3. Cálculo de equivalência (CÓDIGO FUNCIONAL RECUPERADO)
    // ================================
    async function calcularEquivalencia() {
        const valorReais = parseFloat((valorInput.value || "").replace(",", "."));
        if (!criptomoedaSelecionada || isNaN(valorReais) || valorReais <= 0) {
            equivalenciaEl.textContent = "0";
            return;
        }

        try {
            // 🔹 Busca cotação atual da cripto no backend
            const res = await fetch(`http://localhost:8080/api/historicos/${criptomoedaSelecionada.id}/cotacao-atual`);
            if (!res.ok) throw new Error("Erro ao buscar cotação.");

            const cotacao = await res.json();
            if (!cotacao || cotacao <= 0) {
                equivalenciaEl.textContent = "0";
                return;
            }

            // 🔹 Calcula a equivalência (quantidade de cripto que o valor em R$ compra/vende)
            const quantidadeCripto = valorReais / cotacao;
            equivalenciaEl.textContent = quantidadeCripto.toFixed(8);
        } catch (err) {
            console.error("Erro ao calcular equivalência:", err);
            equivalenciaEl.textContent = "0";
        }
    }

    // 🔹 Atualiza automaticamente quando digitar valor
    valorInput.addEventListener("input", calcularEquivalencia);

    // ================================
    // 🔸 4. Envio da transação (CÓDIGO FUNCIONAL RECUPERADO com quantidadeCripto)
    // ================================
    form.addEventListener("submit", async e => {
        e.preventDefault();

        const tipo = document.querySelector("input[name='tipo']:checked").value;
        const usuarioId = sessionStorage.getItem("usuarioId");
        const valor = parseFloat((valorInput.value || "").replace(",", "."));
        const criptoId = parseInt(cryptoInput.dataset.id);
        const quantidadeCripto = parseFloat(equivalenciaEl.textContent || "0"); // Usa o valor calculado

        if (!usuarioId || !criptoId || isNaN(valor) || isNaN(quantidadeCripto) || quantidadeCripto <= 0) {
            alert("Preencha todos os campos corretamente.");
            return;
        }

        const payload = {
            usuarioId: parseInt(usuarioId),
            criptomoedaId: criptoId,
            tipo: tipo.charAt(0).toUpperCase() + tipo.slice(1),
            valor: valor,
            quantidadeCripto: quantidadeCripto // Valor essencial para o backend
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
                criptomoedaSelecionada = null;

                // 🔹 Atualiza dados de tela
                await carregarListasCriptomoedas(usuarioId);
                await carregarPortfolio();
                await carregarHistoricoTransacoes();
                atualizarDropdown();
            } else {
                let errObj = {};
                try { errObj = await res.json(); } catch (_) { /* ignore */ }
                console.error("Erro ao enviar transação:", errObj);
                alert("Erro: " + (errObj.message || "Falha ao criar transação"));
            }
        } catch (err) {
            console.error("Erro ao processar transação:", err);
            alert("Erro ao processar transação.");
        }
    });
}


/**
 * 🔹 INICIALIZAÇÃO E EVENTOS DE TELA
 */

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


window.addEventListener("DOMContentLoaded", () => {
    // Carrega o cliente e, em cascata, todos os dados
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
        secaoPatrimonio.classList.remove('valor-visivel');
        patrimonioNumericoEl.textContent = '••••••••';
    });

    // Evento para alternar a visibilidade
    btnVisibilidade.addEventListener('click', () => {
        const estaVisivel = secaoPatrimonio.classList.toggle('valor-visivel');
        if (estaVisivel) {
            patrimonioNumericoEl.textContent = patrimonioNumericoEl.dataset.valorReal;
        } else {
            patrimonioNumericoEl.textContent = '••••••••';
        }
    });
});