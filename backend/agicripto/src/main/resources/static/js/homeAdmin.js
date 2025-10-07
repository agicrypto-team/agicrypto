const API_BASE_URL = "http://localhost:8080/api/usuarios";

// 🔹 Função para carregar dados do administrador
async function carregarUsuarioAdmin() {
  const usernameEl = document.querySelector(".username");
  if (!usernameEl) return;

  // 🔸 Exibe nome armazenado, se existir
  const nomeSalvo = sessionStorage.getItem("usuarioNome");
  if (nomeSalvo) {
    usernameEl.textContent = nomeSalvo;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/eu`, {
      method: "GET",
      credentials: "same-origin",
    });

    if (response.ok) {
      const usuario = await response.json();
      usernameEl.textContent = usuario.nome;
      usernameEl.classList.remove("loading");
    } else {
      usernameEl.textContent = "Administrador";
    }

  } catch (error) {
    console.error("Erro ao buscar dados do administrador:", error);
    usernameEl.textContent = "Administrador";
  } finally {
    usernameEl.classList.remove("loading");
  }
}

// 🔹 Função de inicialização
window.addEventListener("DOMContentLoaded", carregarUsuarioAdmin);
