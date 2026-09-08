// H-Derma — header scroll state + mobile off-canvas nav + scroll animations
document.addEventListener('DOMContentLoaded', function () {
  var header = document.getElementById('header');
  var menuBtn = document.getElementById('menuBtn');
  var closeBtn = document.getElementById('closeBtn');
  var gnbM = document.getElementById('gnbM');
  var dimd = document.getElementById('dimd');

  function onScroll() {
    if (window.scrollY > 40) header.classList.add('scrolled');
    else header.classList.remove('scrolled');
  }
  window.addEventListener('scroll', onScroll);
  onScroll();

  function openMenu() {
    gnbM.classList.add('on');
    dimd.classList.add('on');
    document.body.style.overflow = 'hidden';
  }
  function closeMenu() {
    gnbM.classList.remove('on');
    dimd.classList.remove('on');
    document.body.style.overflow = '';
  }
  if (menuBtn) menuBtn.addEventListener('click', openMenu);
  if (closeBtn) closeBtn.addEventListener('click', closeMenu);
  if (dimd) dimd.addEventListener('click', closeMenu);

  // Mobile accordion submenus
  var mobileBoxes = document.querySelectorAll('#gnbM .mobileBox');
  mobileBoxes.forEach(function (box) {
    var trigger = box.querySelector('a.hmbig');
    if (!trigger) return;
    trigger.addEventListener('click', function (e) {
      e.preventDefault();
      var isOpen = box.classList.contains('active');
      mobileBoxes.forEach(function (b) { b.classList.remove('active'); });
      if (!isOpen) box.classList.add('active');
    });
  });

  // Small helper: wait two animation frames before adding the class that
  // triggers the transition, so above-the-fold content still visibly fades
  // in instead of snapping straight to its final state.
  function revealNextFrame(el) {
    requestAnimationFrame(function () {
      requestAnimationFrame(function () {
        el.classList.add('in-view');
      });
    });
  }

  var services = document.querySelectorAll('.service');
  if ('IntersectionObserver' in window && services.length) {
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          revealNextFrame(entry.target);
          io.unobserve(entry.target);
        }
      });
    }, { threshold: 0.25 });
    services.forEach(function (el) { io.observe(el); });
  } else {
    services.forEach(function (el) { revealNextFrame(el); });
  }

  // Vision cards + process steps + any .reveal-* element on any page:
  // same one-time reveal-on-scroll pattern, site-wide
  var revealTargets = document.querySelectorAll('.vision-card, .process-track li, .reveal-up, .reveal-scale, .reveal-stagger');
  if ('IntersectionObserver' in window && revealTargets.length) {
    var io2 = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          revealNextFrame(entry.target);
          io2.unobserve(entry.target);
        }
      });
    }, { threshold: 0.2 });
    revealTargets.forEach(function (el) { io2.observe(el); });
  } else {
    revealTargets.forEach(function (el) { revealNextFrame(el); });
  }

  // Process connector (main page): a smooth curve running just under each
  // step card, traced in actual step order (1 → 8) regardless of grid position
  var track = document.getElementById('processTrack');
  var svg = document.getElementById('processConnector');
  var path = document.getElementById('processPath');
  var wrap = track ? track.closest('.process-track-wrap') : null;

  function drawConnector() {
    if (!track || !svg || !path || !wrap) return;
    if (window.innerWidth <= 900) return;

    var wrapRect = wrap.getBoundingClientRect();
    svg.setAttribute('width', wrapRect.width);
    svg.setAttribute('height', wrapRect.height);
    svg.setAttribute('viewBox', '0 0 ' + wrapRect.width + ' ' + wrapRect.height);

    if (!document.getElementById('processGrad')) {
      var defs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
      defs.innerHTML =
        '<linearGradient id="processGrad" x1="0" y1="0" x2="1" y2="1">' +
        '<stop offset="0%" stop-color="#3E6BAE"/>' +
        '<stop offset="100%" stop-color="#3FAE93"/>' +
        '</linearGradient>';
      svg.prepend(defs);
    }

    var items = Array.prototype.slice.call(track.querySelectorAll('li'));
    var pts = items.map(function (li) {
      var r = li.getBoundingClientRect();
      var label = li.querySelector('.step-no');
      var n = label ? parseInt(label.textContent.replace(/\D/g, ''), 10) : 0;
      return {
        n: n,
        x: r.left + r.width / 2 - wrapRect.left,
        y: r.bottom - wrapRect.top + 16
      };
    });
    pts.sort(function (a, b) { return a.n - b.n; });

    var d = 'M ' + pts[0].x.toFixed(1) + ' ' + pts[0].y.toFixed(1) + ' ';
    for (var i = 0; i < pts.length - 1; i++) {
      var a = pts[i], b = pts[i + 1];
      var midX = (a.x + b.x) / 2;
      d += 'C ' + midX.toFixed(1) + ' ' + a.y.toFixed(1) + ', ' +
                  midX.toFixed(1) + ' ' + b.y.toFixed(1) + ', ' +
                  b.x.toFixed(1) + ' ' + b.y.toFixed(1) + ' ';
    }
    path.setAttribute('d', d);

    wrap.querySelectorAll('.process-node').forEach(function (n) { n.remove(); });
    pts.forEach(function (p) {
      var dot = document.createElement('span');
      dot.className = 'process-node';
      dot.style.left = p.x + 'px';
      dot.style.top = p.y + 'px';
      wrap.appendChild(dot);
    });
  }

  if (track && wrap) {
    if ('ResizeObserver' in window) {
      var ro = new ResizeObserver(function () { drawConnector(); });
      ro.observe(wrap);
    } else {
      window.addEventListener('resize', function () {
        clearTimeout(window.__pcTimer);
        window.__pcTimer = setTimeout(drawConnector, 150);
      });
    }
    window.addEventListener('load', drawConnector);
    if (document.fonts && document.fonts.ready) {
      document.fonts.ready.then(drawConnector);
    }
    track.querySelectorAll('li').forEach(function (li) {
      li.addEventListener('transitionend', function (e) {
        if (e.propertyName === 'transform' || e.propertyName === 'opacity') {
          drawConnector();
        }
      });
    });
    setTimeout(drawConnector, 300);
  }

  // Vision page: connect the center circle to each of the 4 cards with a
  // curved line computed from actual positions — stays correct no matter
  // how wide the cards are, how many lines their text wraps to, or which
  // breakpoint layout is active.
  var visionWrap = document.querySelector('.vision-radial-grid');
  var visionSvg = document.getElementById('visionConnector');
  var visionPath = document.getElementById('visionPath');
  var visionCircle = document.querySelector('.vision-radial-center');

  function drawVisionConnector() {
    if (!visionWrap || !visionSvg || !visionPath || !visionCircle) return;
    var items = visionWrap.querySelectorAll('.vision-radial-item');
    if (!items.length) return;

    var wrapRect = visionWrap.getBoundingClientRect();
    visionSvg.setAttribute('width', wrapRect.width);
    visionSvg.setAttribute('height', wrapRect.height);
    visionSvg.setAttribute('viewBox', '0 0 ' + wrapRect.width + ' ' + wrapRect.height);

    if (!document.getElementById('visionGrad')) {
      var vdefs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
      vdefs.innerHTML =
        '<linearGradient id="visionGrad" x1="0" y1="0" x2="1" y2="1">' +
        '<stop offset="0%" stop-color="#3E6BAE"/>' +
        '<stop offset="100%" stop-color="#3FAE93"/>' +
        '</linearGradient>';
      visionSvg.prepend(vdefs);
    }

    var cRect = visionCircle.getBoundingClientRect();
    var cx = cRect.left + cRect.width / 2 - wrapRect.left;
    var cy = cRect.top + cRect.height / 2 - wrapRect.top;
    var cr = cRect.width / 2;

    var d = '';
    items.forEach(function (item) {
      var r = item.getBoundingClientRect();
      var itemCx = r.left + r.width / 2 - wrapRect.left;
      var itemCy = r.top + r.height / 2 - wrapRect.top;
      var dx = itemCx - cx, dy = itemCy - cy;
      var dist = Math.sqrt(dx * dx + dy * dy) || 1;
      var ux = dx / dist, uy = dy / dist;
      var startX = cx + ux * cr, startY = cy + uy * cr;
      var endX = itemCx - ux * (r.width / 2), endY = itemCy - uy * (r.height / 2);
      var midX = (startX + endX) / 2, midY = (startY + endY) / 2 - 26;
      d += 'M ' + startX.toFixed(1) + ' ' + startY.toFixed(1) +
           ' Q ' + midX.toFixed(1) + ' ' + midY.toFixed(1) +
           ' ' + endX.toFixed(1) + ' ' + endY.toFixed(1) + ' ';
    });
    visionPath.setAttribute('d', d);
  }

  if (visionWrap) {
    if ('ResizeObserver' in window) {
      var vro = new ResizeObserver(function () { drawVisionConnector(); });
      vro.observe(visionWrap);
    } else {
      window.addEventListener('resize', function () {
        clearTimeout(window.__vcTimer);
        window.__vcTimer = setTimeout(drawVisionConnector, 150);
      });
    }
    window.addEventListener('load', drawVisionConnector);
    if (document.fonts && document.fonts.ready) {
      document.fonts.ready.then(drawVisionConnector);
    }
    visionWrap.querySelectorAll('.vision-radial-item, .vision-radial-center').forEach(function (el) {
      el.addEventListener('transitionend', function (e) {
        if (e.propertyName === 'transform' || e.propertyName === 'opacity') {
          drawVisionConnector();
        }
      });
    });
    setTimeout(drawVisionConnector, 300);
  }

  // ================= 사이트 공통 팝업 (main.js 하단에 추가) =================
  document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/popups/active")
      .then(function (res) { return res.ok ? res.json() : []; })
      .then(function (popups) {
        popups.forEach(function (popup, index) {
          if (isPopupHiddenToday(popup.id)) return;
          renderPopup(popup, index);
        });
      })
      .catch(function () {});
  });

  function isPopupHiddenToday(id) {
    var hiddenUntil = localStorage.getItem("popup_hide_" + id);
    if (!hiddenUntil) return false;
    return new Date(hiddenUntil) > new Date();
  }

  function renderPopup(popup, index) {
    var box = document.createElement("div");
    box.className = "site-popup";
    box.style.cssText =
      "position:fixed; top:" + popup.posY + "px; left:" + popup.posX + "px; " +
      "width:" + popup.width + "px; z-index:" + (2000 + index) + "; " +
      "background:#fff; border-radius:10px; box-shadow:0 8px 30px rgba(0,0,0,0.18); overflow:hidden;";

    var imageHtml = popup.imageUrl
      ? '<img src="' + popup.imageUrl + '" alt="' + (popup.title || '') + '" style="width:100%; display:block;">'
      : '<div style="padding:40px 20px; text-align:center; color:#999;">' + (popup.title || '팝업') + '</div>';

    var linkOpenTag = popup.linkUrl ? '<a href="' + popup.linkUrl + '" target="_blank">' : '';
    var linkCloseTag = popup.linkUrl ? '</a>' : '';

    box.innerHTML =
      linkOpenTag + imageHtml + linkCloseTag +
      '<div style="display:flex; justify-content:space-between; align-items:center; padding:8px 12px; background:#f5f5f5; font-size:0.82rem;">' +
      '<label style="display:flex; align-items:center; gap:6px; cursor:pointer; margin:0;">' +
      '<input type="checkbox" class="popup-hide-today"> 오늘 하루 보지 않기' +
      '</label>' +
      '<button type="button" class="popup-close-btn" style="border:none; background:none; font-size:1rem; cursor:pointer;">닫기 ✕</button>' +
      '</div>';

    document.body.appendChild(box);

    box.querySelector(".popup-close-btn").addEventListener("click", function () {
      var hideToday = box.querySelector(".popup-hide-today").checked;
      if (hideToday) {
        var tomorrow = new Date();
        tomorrow.setHours(24, 0, 0, 0);
        localStorage.setItem("popup_hide_" + popup.id, tomorrow.toISOString());
      }
      box.remove();
    });
  }

  // ================= 언어 선택 드롭다운 + 구글 번역 전환 =================
  document.addEventListener("DOMContentLoaded", function () {
    var trigger = document.getElementById("langTrigger");
    var list = document.getElementById("langList");

    if (trigger && list) {
      trigger.addEventListener("click", function (e) {
        e.stopPropagation();
        list.classList.toggle("show");
      });
      document.addEventListener("click", function (e) {
        if (!list.contains(e.target) && e.target !== trigger) {
          list.classList.remove("show");
        }
      });
    }

    document.querySelectorAll("#langList li, #langListMobile li").forEach(function (item) {
      item.addEventListener("click", function () {
        changeLanguage(item.dataset.lang);
      });
    });

    // 현재 언어에 맞춰 active 표시
    var match = document.cookie.match(/googtrans=\/ko\/([a-zA-Z\-]+)/);
    var currentLang = match ? match[1] : "ko";
    document.querySelectorAll("#langList li, #langListMobile li").forEach(function (item) {
      item.classList.toggle("active", item.dataset.lang === currentLang);
    });
  });

  function changeLanguage(lang) {
    document.cookie = "googtrans=/ko/" + lang + "; path=/";
    setTimeout(function () {
      location.reload();
    }, 100);
  }
});

