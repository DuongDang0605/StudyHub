// =============================================
//  StudyHub - app.js
// =============================================

document.addEventListener('DOMContentLoaded', () => {

  // ----- Sidebar toggle (mobile) -----
  const toggle  = document.getElementById('sidebarToggle');
  const sidebar = document.getElementById('sidebar');
  if (toggle && sidebar) {
    toggle.addEventListener('click', () => sidebar.classList.toggle('open'));
    document.addEventListener('click', e => {
      if (sidebar.classList.contains('open') && !sidebar.contains(e.target) && e.target !== toggle) {
        sidebar.classList.remove('open');
      }
    });
  }

  // ----- Auto-init Bootstrap toasts -----
  document.querySelectorAll('.toast').forEach(el => {
    new bootstrap.Toast(el, { delay: 4000 }).show();
  });

  // ----- Active nav highlight -----
  const path = window.location.pathname;
  document.querySelectorAll('.nav-item[data-href]').forEach(el => {
    if (path.startsWith(el.dataset.href)) el.classList.add('active');
  });

  // ----- Confirm delete -----
  document.querySelectorAll('[data-confirm]').forEach(el => {
    el.addEventListener('click', e => {
      if (!confirm(el.dataset.confirm || 'Are you sure?')) e.preventDefault();
    });
  });

  // ----- Password toggle -----
  document.querySelectorAll('.pwd-toggle').forEach(btn => {
    btn.addEventListener('click', () => {
      const inp = document.querySelector(btn.dataset.target);
      const ico = btn.querySelector('i');
      if (!inp) return;
      inp.type = inp.type === 'password' ? 'text' : 'password';
      ico.className = inp.type === 'password' ? 'bi bi-eye' : 'bi bi-eye-slash';
    });
  });

  // ----- Image preview on file choose -----
  document.querySelectorAll('.img-pick').forEach(inp => {
    inp.addEventListener('change', () => {
      const preview = document.querySelector(inp.dataset.preview);
      if (preview && inp.files[0]) {
        preview.src = URL.createObjectURL(inp.files[0]);
      }
    });
  });

  // ----- Table row clickable -----
  document.querySelectorAll('tr[data-href]').forEach(row => {
    row.style.cursor = 'pointer';
    row.addEventListener('click', () => window.location.href = row.dataset.href);
  });

});
