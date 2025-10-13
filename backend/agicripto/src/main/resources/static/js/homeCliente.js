const API_BASE_URL = "http://localhost:8080/api/usuarios";
const API_TRANSACOES = "http://localhost:8080/transacoes";
const API_CRYPTOS = "http://localhost:8080/api/criptomoedas";
const API_ATIVOS = "http://localhost:8080/api/ativos-carteira";
const API_HISTORICO = "http://localhost:8080/carteira/historico";

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

        // 🔽 Ordena do mais recente para o mais antigo
        historico.sort((a, b) => new Date(b.momentoTransacao) - new Date(a.momentoTransacao));

        historico.forEach((tx, index) => {
            const data = new Date(tx.momentoTransacao);
            const dataFormatada = data.toLocaleDateString("pt-BR", {
                day: "2-digit", month: "2-digit", year: "numeric"
            }) + " " + data.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" });

            const tipoClass = (tx.tipoTransacao || "").toLowerCase();
            const valorFormatado = `R$ ${Number(tx.valorComprado).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}`;

            const item = document.createElement("div");
            item.className = "transacao";
            item.style.animationDelay = `${index * 0.06}s`; // efeito gradual, mais sutil
            item.innerHTML = `
        <div class="tx-left">
          <div class="tx-name">${escapeHtml(tx.nomeCripto)} (${escapeHtml(tx.siglaCripto)})</div>
          <div class="tx-date">${dataFormatada}</div>
        </div>
        <div class="tx-right">
          <div class="tx-type ${tipoClass}">${escapeHtml(tx.tipoTransacao)}</div>
          <div class="tx-value">${valorFormatado}</div>
        </div>
      `;
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
        <span class="crypto-sigla">(${escapeHtml(c.sigla)})</span>
      `;
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
                // sucesso
                alert("Transação realizada com sucesso!");
                form.reset();
                equivalenciaEl.textContent = "0";
                cryptoInput.dataset.id = "";

                // Atualiza listas e histórico
                await carregarListasCriptomoedas(usuarioId);
                await carregarHistoricoTransacoes();
                atualizarDropdown();
            } else {
                // tenta extrair mensagem de erro do backend
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

// Pequena função utilitária para escapar HTML injetado nos nomes (segurança básica)
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
});
