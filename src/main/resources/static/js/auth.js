const mensagem = document.querySelector("#mensagem");

function mostrarMensagem(texto, tipo = "error") {
	mensagem.textContent = texto;
	mensagem.className = `message ${tipo}`;
}


/* ==========================================
CADASTRO
========================================== */

const cadastroForm = document.querySelector("#cadastroForm");

if (cadastroForm) {
	cadastroForm.addEventListener("submit", async event => {
		event.preventDefault();

		const nome = document.querySelector("#nome").value.trim();
		const email = document.querySelector("#email").value.trim();
		const senha = document.querySelector("#senha").value;
		const confirmarSenha = document.querySelector("#confirmarSenha").value;

		if (senha !== confirmarSenha) {
			mostrarMensagem("As senhas não coincidem.");
			return;
		}

		const submit = event.submitter || event.currentTarget.querySelector("button[type=submit]");
		submit.disabled = true;
		try {
			const response = await fetch("/api/auth/cadastro", {
				method: "POST",
				headers: {
					"Content-Type": "application/json"
				},
				body: JSON.stringify({
					nome,
					email,
					senha
				})
			});

			const data = await response.json().catch(() => null);

			if (!response.ok) {
				throw new Error(
					(data?.campos && Object.values(data.campos).join(" · ")) || data?.mensagem || "Não foi possível criar a conta."
				);
			}

			mostrarMensagem("Conta criada com sucesso!", "success");

			setTimeout(() => {
				window.MediCareUI.navigate("/login");
			}, 1200);
		} catch (error) {
			mostrarMensagem(error.message);
			submit.disabled = false;
		}
	});
}


/* ==========================================
LOGIN
========================================== */

const loginForm = document.querySelector("#loginForm");

if (loginForm) {
	loginForm.addEventListener("submit", async event => {
		event.preventDefault();

		const email = document.querySelector("#email").value.trim();
		const senha = document.querySelector("#senha").value;

		const submit = event.submitter || event.currentTarget.querySelector("button[type=submit]");
		submit.disabled = true;
		try {
			const response = await fetch("/api/auth/login", {
				method: "POST",
				headers: {
					"Content-Type": "application/json"
				},
				body: JSON.stringify({
					email,
					senha
				})
			});

			const data = await response.json().catch(() => null);

			if (!response.ok) {
				throw new Error(data?.mensagem || "Email ou senha inválidos.");
			}

			window.MediCareUI.navigate("/medicamentos");
		} catch (error) {
			mostrarMensagem(error.message);
			submit.disabled = false;
		}
	});
}