/* Automação JS - extensões específicas do motor de automação */
document.addEventListener('DOMContentLoaded', function() {
  // Auto-refresh status badges every 30s
  const statusBadges = document.querySelectorAll('[data-auto-refresh]');
  if (statusBadges.length > 0) {
    setInterval(function() { window.location.reload(); }, 30000);
  }
});
