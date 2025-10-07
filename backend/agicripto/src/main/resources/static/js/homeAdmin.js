const API_BASE_URL = "http://localhost:8080/api/usuarios";

// 🔹 Função para carregar dados do administrador
async function carregarUsuarioAdmin() {
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
    } else {
        console.error("Erro ao carregar dados do usuário:", response.status);
        usernameEl.textContent = "Administrador";
    }

  } catch (error) {
    console.error("Erro ao buscar dados do administrador:", error);
    usernameEl.textContent = "Administrador";
  } finally {
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

// 🔹 Função de inicialização
window.addEventListener("DOMContentLoaded", () => {
    carregarUsuarioAdmin();

    const logoutBtn = document.querySelector(".btn-logout");
    if (logoutBtn) logoutBtn.addEventListener("click", realizarLogout);
});

