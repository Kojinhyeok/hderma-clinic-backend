/* 헤더 로그인 상태 반영: 로그인 → 내정보/신청내역/로그아웃, 비로그인 → 기존 로그인/회원가입 유지 */
(function () {
    function run() {
      fetch("/api/auth/me", { credentials: "same-origin" })
        .then(function (res) { return res.ok ? res.json() : null; })
        .then(function (me) {
          if (!me) return;
          var html =
            '<a href="/auth/mypage">내정보</a>' +
            '<a href="/auth/mypage?tab=applications">신청내역</a>' +
            '<a href="#" class="js-logout">로그아웃</a>';
          document.querySelectorAll(".auth-links-top, .mUtil").forEach(function (el) { el.innerHTML = html; });
          document.querySelectorAll(".js-logout").forEach(function (a) {
            a.addEventListener("click", function (e) {
              e.preventDefault();
              fetch("/api/auth/logout", { method: "POST", credentials: "same-origin" })
                .finally(function () { window.location.href = "/"; });
            });
          });
        })
        .catch(function () {});
    }
    if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", run);
    else run();
  })();