/**
 * 클라이언트 사이드 테이블 페이지네이션
 *
 * @param {Object} options
 *   tableId  - 테이블 요소 ID
 *   perPage  - 페이지당 행 수 (기본 10)
 *   infoId   - 페이지 정보 텍스트 컨테이너 ID
 *   navId    - 페이지네이션 네비 컨테이너 ID (ul)
 *
 * @returns {Object} { refresh(), goToPage(n), getCurrentPage() }
 *
 * Usage:
 *   var pager = initPagination({
 *       tableId: "myTable",
 *       perPage: 10,
 *       infoId: "pageInfo",
 *       navId: "pageNav"
 *   });
 *   // 행 추가/삭제 후:
 *   pager.refresh();
 */
function initPagination(options) {
	var tableEl = document.getElementById(options.tableId);
	if (!tableEl) return null;

	var tbody = tableEl.querySelector("tbody");
	var perPage = options.perPage || 10;
	var infoEl = document.getElementById(options.infoId);
	var navEl = document.getElementById(options.navId);
	var currentPage = 1;

	function getRows() {
		return Array.from(tbody.querySelectorAll("tr:not(.search-hidden)"));
	}

	function render() {
		var rows = getRows();
		var total = rows.length;
		var totalPages = Math.ceil(total / perPage) || 1;

		if (currentPage > totalPages) currentPage = totalPages;
		if (currentPage < 1) currentPage = 1;

		var start = (currentPage - 1) * perPage;
		var end = Math.min(start + perPage, total);

		// 행 표시/숨김
		rows.forEach(function (row, idx) {
			row.style.display = (idx >= start && idx < end) ? "" : "none";
		});

		// 페이지 정보
		if (infoEl) {
			if (total === 0) {
				infoEl.textContent = "데이터가 없습니다.";
			} else {
				infoEl.textContent = "전체 " + total + "건 중 " + (start + 1) + "-" + end + " 표시";
			}
		}

		// 네비게이션
		if (navEl) {
			renderNav(totalPages);
		}
	}

	function renderNav(totalPages) {
		var html = "";

		// 이전
		html += '<li class="page-item' + (currentPage <= 1 ? " disabled" : "") + '">';
		html += '<a class="page-link" href="#" data-page="prev">&laquo;</a></li>';

		// 페이지 번호 (최대 5개 표시)
		var startPage = Math.max(1, currentPage - 2);
		var endPage = Math.min(totalPages, startPage + 4);
		startPage = Math.max(1, endPage - 4);

		if (startPage > 1) {
			html += '<li class="page-item"><a class="page-link" href="#" data-page="1">1</a></li>';
			if (startPage > 2) {
				html += '<li class="page-item disabled"><span class="page-link">&hellip;</span></li>';
			}
		}

		for (var i = startPage; i <= endPage; i++) {
			html += '<li class="page-item' + (i === currentPage ? " active" : "") + '">';
			html += '<a class="page-link" href="#" data-page="' + i + '">' + i + "</a></li>";
		}

		if (endPage < totalPages) {
			if (endPage < totalPages - 1) {
				html += '<li class="page-item disabled"><span class="page-link">&hellip;</span></li>';
			}
			html += '<li class="page-item"><a class="page-link" href="#" data-page="' + totalPages + '">' + totalPages + "</a></li>";
		}

		// 다음
		html += '<li class="page-item' + (currentPage >= totalPages ? " disabled" : "") + '">';
		html += '<a class="page-link" href="#" data-page="next">&raquo;</a></li>';

		navEl.innerHTML = html;

		// 클릭 바인딩
		navEl.querySelectorAll("a[data-page]").forEach(function (a) {
			a.addEventListener("click", function (e) {
				e.preventDefault();
				var page = this.dataset.page;
				if (page === "prev") {
					if (currentPage > 1) currentPage--;
				} else if (page === "next") {
					var tp = Math.ceil(getRows().length / perPage) || 1;
					if (currentPage < tp) currentPage++;
				} else {
					currentPage = parseInt(page);
				}
				render();
			});
		});
	}

	render();

	return {
		refresh: function () { currentPage = 1; render(); },
		goToPage: function (page) { currentPage = page; render(); },
		getCurrentPage: function () { return currentPage; }
	};
}
