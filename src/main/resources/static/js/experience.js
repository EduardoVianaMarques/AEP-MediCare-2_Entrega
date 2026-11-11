/* Experiência compartilhada: navegação progressiva, movimento e identidade.
   Nenhuma animação é necessária para acessar conteúdo ou concluir uma ação. */
(() => {
    const reduced = window.matchMedia('(prefers-reduced-motion: reduce)');
    let leaving = false;
    const internalPages = new Set(['/', '/index.html', '/login', '/login.html', '/cadastro', '/cadastro.html', '/medicamentos', '/medicamentos.html']);
    const nativeNavigation = 'onpageswap' in window && 'CSSViewTransitionRule' in window;

    function navigate(path) {
        if (leaving) return;
        const url = new URL(path, location.href);
        if (url.origin !== location.origin || reduced.matches || nativeNavigation) {
            location.assign(url.href); return;
        }
        leaving = true;
        document.body.classList.add('page-leaving');
        // Fallback curto: não depende de animationend, que pode não disparar.
        window.setTimeout(() => location.assign(url.href), 140);
        // Se o usuário interromper a navegação, a página atual volta a ficar utilizável.
        window.setTimeout(() => {
            leaving = false; document.body.classList.remove('page-leaving');
        }, 2000);
    }
    document.addEventListener('click', event => {
        const anchor = event.target.closest?.('a[href]');
        if (!anchor || event.defaultPrevented || event.button !== 0 || event.ctrlKey || event.metaKey || event.shiftKey || event.altKey ||
            anchor.hasAttribute('download') || (anchor.target && anchor.target !== '_self')) return;
        const url = new URL(anchor.href, location.href);
        if (url.origin !== location.origin || !internalPages.has(url.pathname) || url.hash ||
            (url.pathname === location.pathname && url.search === location.search)) return;
        if (!nativeNavigation && !reduced.matches) { event.preventDefault(); navigate(url.href); }
    });
    window.addEventListener('pageshow', () => {
        leaving = false;
        document.body.classList.remove('page-leaving');
    });

    function toggleTheme() {
        const change = () => {
            const theme = document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark';
            document.documentElement.dataset.theme = theme;
            try { localStorage.setItem('medicare-tema', theme); } catch {}
            document.querySelector('#tema')?.setAttribute('aria-pressed', String(theme === 'dark'));
        };
        if (document.startViewTransition && !reduced.matches) document.startViewTransition(change);
        else change();
    }
    async function closeDialog(dialog) {
        if (!dialog?.open) return;
        if (!reduced.matches && dialog.animate) {
            const animation = dialog.animate([{opacity:1,transform:'translateY(0)'},{opacity:0,transform:'translateY(8px) scale(.985)'}],
                {duration:130,easing:'ease-in',fill:'none'});
            try { await animation.finished; } catch {}
        }
        dialog.close();
    }
    window.MediCareUI = {navigate, toggleTheme, closeDialog};

    // O conteúdo permanece visível caso JS, observer ou animações não estejam disponíveis.
    const reveal = document.querySelectorAll('[data-reveal]');
    if (!reduced.matches && 'IntersectionObserver' in window) {
        const observer = new IntersectionObserver(entries => entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.remove('reveal-pending'); observer.unobserve(entry.target);
            }
        }), {threshold:.08});
        reveal.forEach(el => { el.classList.add('reveal-pending'); observer.observe(el); });
        reduced.addEventListener('change', () => {
            if (reduced.matches) { reveal.forEach(el => el.classList.remove('reveal-pending')); observer.disconnect(); }
        });
    }
    document.querySelectorAll('[data-password]').forEach(button => button.addEventListener('click', () => {
        const field = document.getElementById(button.dataset.password);
        const show = field.type === 'password';
        field.type = show ? 'text' : 'password';
        button.textContent = show ? 'Ocultar' : 'Mostrar';
        button.setAttribute('aria-pressed', String(show));
        button.setAttribute('aria-label', `${show ? 'Ocultar' : 'Mostrar'} ${field.id === 'confirmarSenha' ? 'confirmação de senha' : 'senha'}`);
    }));
    const date = document.getElementById('panel-date');
    if (date) date.textContent = new Intl.DateTimeFormat('pt-BR',{weekday:'long',day:'numeric',month:'long'}).format(new Date());
})();
