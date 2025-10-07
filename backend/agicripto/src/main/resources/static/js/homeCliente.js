const API_BASE_URL = "http://localhost:8080/api/usuarios";

// 🔹 Função para carregar dados do usuário cliente
async function carregarUsuarioCliente() {
  const usernameEl = document.querySelector(".username");
  if (!usernameEl) return;


  try {
    const response = await fetch(`${API_BASE_URL}/eu`, {
      method: "GET",
      credentials: "same-origin",
    });

    console.log("Status ao buscar /eu:", response.status);

    if (response.ok) {
      const usuario = await response.json();
      console.log("Usuário logado:", usuario);

      usernameEl.textContent = usuario.nome;
      usernameEl.classList.remove("loading");

      // 🔹 Atualiza sessionStorage
      sessionStorage.setItem("usuarioNome", usuario.nome);
      sessionStorage.setItem("usuarioTipo", usuario.tipo);
    }
    else if (response.status === 401) {
      alert("Sessão expirada. Faça login novamente.");
      window.location.replace("/pages/login/login.html");
    }
    else {
      console.error("Erro ao carregar dados do usuário:", response.status);
      usernameEl.textContent = "Cliente";
      usernameEl.classList.remove("loading");
    }

  } catch (error) {
    console.error("Erro ao buscar dados do usuário:", error);
    usernameEl.textContent = "Cliente";
    usernameEl.classList.remove("loading");
  }
}

// 🔹 Função para logout
async function realizarLogout() {
  try {
    await fetch(`${API_BASE_URL}/logout`, {
      method: "POST",
      credentials: "same-origin",
    });
  } catch (error) {
    console.error("Erro ao realizar logout:", error);
  } finally {
    sessionStorage.clear();
    window.location.replace("/pages/auth/login.html");
  }
}

// 🔹 Inicialização ao carregar página
window.addEventListener("DOMContentLoaded", () => {
  carregarUsuarioCliente();

  const logoutBtn = document.querySelector(".btn-logout");
  if (logoutBtn) logoutBtn.addEventListener("click", realizarLogout);
});
