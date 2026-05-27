/* ====================================================
   O TAREFEIRO - Main JavaScript
   ==================================================== */

// Sidebar toggle
document.addEventListener('DOMContentLoaded', function () {
  const toggle = document.getElementById('sidebarToggle');
  const sidebar = document.getElementById('sidebar');

  if (toggle && sidebar) {
    toggle.addEventListener('click', function () {
      sidebar.classList.toggle('open');
    });

    // Close sidebar on outside click (mobile)
    document.addEventListener('click', function (e) {
      if (window.innerWidth <= 768 && sidebar.classList.contains('open')) {
        if (!sidebar.contains(e.target) && !toggle.contains(e.target)) {
          sidebar.classList.remove('open');
        }
      }
    });
  }

  // Auto-dismiss alerts after 5 seconds
  const alerts = document.querySelectorAll('.alert-dismissible');
  alerts.forEach(function (alert) {
    setTimeout(function () {
      alert.style.opacity = '0';
      alert.style.transform = 'translateY(-8px)';
      alert.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
      setTimeout(function () { alert.remove(); }, 400);
    }, 5000);
  });

  // Animate stat values (count up)
  const statValues = document.querySelectorAll('.stat-value');
  statValues.forEach(function (el) {
    const target = parseInt(el.textContent, 10);
    if (isNaN(target) || target === 0) return;
    let current = 0;
    const duration = 800;
    const step = Math.max(1, Math.floor(target / (duration / 16)));
    const timer = setInterval(function () {
      current = Math.min(current + step, target);
      el.textContent = current;
      if (current >= target) clearInterval(timer);
    }, 16);
  });

  // Highlight active nav link
  const currentPath = window.location.pathname;
  document.querySelectorAll('.nav-link').forEach(function (link) {
    const href = link.getAttribute('href');
    if (href && href !== '/' && currentPath.startsWith(href)) {
      link.classList.add('active');
    }
  });
});

// Confirm action utility
function confirmar(mensagem) {
  return confirm(mensagem || 'Tens a certeza?');
}

// Copy to clipboard
function copiar(texto) {
  if (navigator.clipboard) {
    navigator.clipboard.writeText(texto).then(function () {
      mostrarToast('Copiado para a área de transferência!');
    });
  }
}

// Simple toast notification
function mostrarToast(mensagem, tipo) {
  const toast = document.createElement('div');
  toast.className = 'alert alert-' + (tipo || 'success') + ' fade-in';
  toast.style.cssText = 'position:fixed;bottom:1.5rem;right:1.5rem;z-index:9999;min-width:280px;max-width:400px;';
  toast.innerHTML = '<i class="bi bi-check-circle-fill me-2"></i>' + mensagem;
  document.body.appendChild(toast);
  setTimeout(function () {
    toast.style.opacity = '0';
    setTimeout(function () { toast.remove(); }, 300);
  }, 3000);
}

// CSRF token helper
function getCsrfToken() {
  const meta = document.querySelector('meta[name="_csrf"]');
  return meta ? meta.getAttribute('content') : null;
}
