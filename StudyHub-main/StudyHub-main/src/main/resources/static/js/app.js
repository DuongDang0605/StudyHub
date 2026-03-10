// Biến toàn cục để lưu lại thông tin trước khi call AJAX
let pendingAjaxUrl = '';
let pendingAjaxData = {};

/**
 * Hiển thị Toast thông báo
 * @param {string} message - Nội dung thông báo
 * @param {string} type - 'success' hoặc 'error'
 */
function showToast(message, type) {
  const toastEl = document.getElementById('globalToast');
  const toastMessage = document.getElementById('globalToastMessage');

  toastMessage.innerHTML = type === 'success'
      ? `<i class="bi bi-check-circle-fill me-2"></i>${message}`
      : `<i class="bi bi-x-circle-fill me-2"></i>${message}`;

  // Đổi màu nền dựa theo type
  toastEl.classList.remove('bg-success', 'bg-danger');
  toastEl.classList.add(type === 'success' ? 'bg-success' : 'bg-danger');

  const toast = new bootstrap.Toast(toastEl);
  toast.show();
}

/**
 * Gọi hàm này từ HTML khi bấm nút Xóa/Khóa
 */
function confirmAction(message, url, data) {
  // 1. Gắn câu hỏi vào Modal
  document.getElementById('globalConfirmMessage').innerText = message;

  // 2. Lưu lại url và data chuẩn bị cho AJAX
  pendingAjaxUrl = url;
  pendingAjaxData = data;

  // 3. Mở pop-up lên
  const modal = new bootstrap.Modal(document.getElementById('globalConfirmModal'));
  modal.show();
}

// Lắng nghe sự kiện click vào nút "Yes, proceed" trong Modal
document.getElementById('btnGlobalConfirm').addEventListener('click', function() {
  // 1. Tắt pop-up
  const modalEl = document.getElementById('globalConfirmModal');
  bootstrap.Modal.getInstance(modalEl).hide();

  // 2. Thực thi AJAX (Sử dụng Fetch API của trình duyệt)
  fetch(pendingAjaxUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    // Chuyển object thành chuỗi x-www-form-urlencoded (vd: status=INACTIVE)
    body: new URLSearchParams(pendingAjaxData)
  })
      .then(response => {
        if(response.ok) {
          showToast("Action completed successfully!", "success");
          // Reload lại giao diện sau 1 giây để cập nhật bảng mới nhất
          setTimeout(() => window.location.reload(), 1000);
        } else {
          showToast("Failed to perform action. Server error.", "error");
        }
      })
      .catch(error => {
        console.error("AJAX Error:", error);
        showToast("Network error occurred.", "error");
      });
});