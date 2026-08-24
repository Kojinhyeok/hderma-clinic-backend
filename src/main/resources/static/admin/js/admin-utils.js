/**
 * 관리자 페이지 공통 유틸리티
 * 사용법: <script src="../js/admin-utils.js"></script>
 *
 * AdminUtils.formatFileSize(bytes)       - 파일 크기 포맷
 * AdminUtils.formatDate(dateStr)         - 날짜 포맷 (yyyy-mm-dd)
 * AdminUtils.createEditorManager(id, entityType) - CKEditor 래퍼
 * AdminUtils.validateTitleAndContent(editorMgr)  - 제목+본문 유효성 검사
 * AdminUtils.bindTitleErrorClear()       - 제목 입력 시 에러 초기화
 * AdminUtils.createThumbnailManager(entityType)  - 썸네일 상태/이벤트 관리
 * AdminUtils.createAttachmentManager()   - 첨부파일 상태/이벤트 관리
 */
var AdminUtils = (function () {

	// ===== 유틸리티 =====

	function formatFileSize(bytes) {
		if (bytes === 0) return "0 B";
		var k = 1024;
		var sizes = ["B", "KB", "MB", "GB"];
		var i = Math.floor(Math.log(bytes) / Math.log(k));
		return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + " " + sizes[i];
	}

	function formatDate(dateStr) {
		if (!dateStr) return "-";
		return dateStr.substring(0, 10);
	}

	// ===== CKEditor 초기화 래퍼 =====
	// editorMgr = AdminUtils.createEditorManager("editor", "HUMAN_NEWS")
	// editorMgr.init(initialData) → Promise<editor>
	// editorMgr.getData() → string
	function createEditorManager(elementId, entityType) {
		var instance = null;

		function init(initialData) {
			if (instance) {
				instance.setData(initialData || "");
				return Promise.resolve(instance);
			}
			return CKEditorConfig.createEditor(elementId, entityType, initialData)
				.then(function (editor) {
					instance = editor;
					editor.model.document.on("change:data", function () {
						document.getElementById("editorError").style.display = "none";
					});
					return editor;
				});
		}

		function getData() {
			return instance ? instance.getData() : "";
		}

		return { init: init, getData: getData };
	}

	// ===== 유효성 검사 (제목 + 본문) =====
	function validateTitleAndContent(editorManager) {
		var valid = true;
		var titleInput = document.getElementById("inputTitle");
		var content = editorManager.getData().trim();

		if (!titleInput.value.trim()) {
			titleInput.classList.add("is-invalid");
			valid = false;
		} else {
			titleInput.classList.remove("is-invalid");
		}

		if (!content) {
			document.getElementById("editorError").style.display = "block";
			valid = false;
		} else {
			document.getElementById("editorError").style.display = "none";
		}

		return valid;
	}

	// ===== 제목 입력 시 에러 초기화 =====
	function bindTitleErrorClear() {
		document.getElementById("inputTitle").addEventListener("input", function () {
			this.classList.remove("is-invalid");
		});
	}

	// ===== 썸네일 매니저 =====
	// human-news, human-story 등 썸네일 이미지가 있는 페이지용
	// 필요 요소: #inputThumbnail, #thumbPreview, #thumbPreviewWrap, #btnRemoveThumb
	function createThumbnailManager(entityType) {
		var newFile = null;
		var removed = false;
		var currentFileId = null;

		function setCurrentFileId(fileId) {
			currentFileId = fileId;
		}

		function getCurrentFileId() {
			return currentFileId;
		}

		// 파일 선택 시 미리보기
		function onFileSelect() {
			var input = document.getElementById("inputThumbnail");
			var file = input.files[0];
			if (file && file.type.startsWith("image/")) {
				newFile = file;
				var reader = new FileReader();
				reader.onload = function (e) {
					document.getElementById("thumbPreview").src = e.target.result;
					document.getElementById("thumbPreviewWrap").style.display = "";
				};
				reader.readAsDataURL(file);
			}
		}

		// 썸네일 삭제
		function onRemove() {
			document.getElementById("thumbPreviewWrap").style.display = "none";
			document.getElementById("thumbPreview").src = "";
			document.getElementById("inputThumbnail").value = "";
			newFile = null;
			removed = true;
		}

		// 수정 모드 진입 시 기존 썸네일 표시
		function showPreview(url) {
			if (url) {
				document.getElementById("thumbPreview").src = url;
				document.getElementById("thumbPreviewWrap").style.display = "";
			} else {
				document.getElementById("thumbPreviewWrap").style.display = "none";
			}
		}

		// 상태 초기화 (수정 모드 진입 시)
		function reset() {
			removed = false;
			newFile = null;
			document.getElementById("inputThumbnail").value = "";
		}

		// 이벤트 바인딩
		function bindEvents() {
			document.getElementById("inputThumbnail").addEventListener("change", onFileSelect);
			document.getElementById("btnRemoveThumb").addEventListener("click", onRemove);
		}

		// Presigned URL 업로드 (Promise<fileId|null>)
		function upload() {
			if (newFile) {
				return CKEditorConfig.uploadThumbnail(newFile, entityType);
			}
			return Promise.resolve(null);
		}

		// 저장 요청 body에 썸네일 필드 적용
		function applyToBody(body, newFileId, isUpdate) {
			if (isUpdate) {
				if (newFileId) {
					body.newThumbnailFileId = newFileId;
					if (currentFileId) {
						body.removeThumbnailFileId = currentFileId;
					}
				} else if (removed && currentFileId) {
					body.removeThumbnailFileId = currentFileId;
				}
			} else {
				if (newFileId) {
					body.thumbnailFileId = newFileId;
				}
			}
		}

		return {
			setCurrentFileId: setCurrentFileId,
			getCurrentFileId: getCurrentFileId,
			showPreview: showPreview,
			reset: reset,
			bindEvents: bindEvents,
			upload: upload,
			applyToBody: applyToBody
		};
	}

	// ===== 첨부파일 매니저 =====
	// trial-notification, trial-files 등 첨부파일이 있는 페이지용
	// 필요 요소: #attachmentList, #inputAttachment, #btnAddAttachment, #viewAttachments
	function createAttachmentManager() {
		var pendingFiles = [];
		var existingFiles = [];

		// 보기 모드 렌더링
		function renderView(attachments) {
			var container = document.getElementById("viewAttachments");
			if (!attachments || attachments.length === 0) {
				container.innerHTML = '<p class="text-muted mb-0">첨부파일이 없습니다.</p>';
				return;
			}
			var html = "";
			attachments.forEach(function (att) {
				var downloadUrl = att.downloadUrl || ("/api/files/" + att.id + "/view");
				html += '<div class="attachment-item">';
				html += '  <span class="att-icon"><i data-feather="paperclip" style="width:16px;height:16px;"></i></span>';
				html += '  <a href="' + downloadUrl + '" class="att-name" target="_blank" download="' + (att.name || '') + '">' + att.name + '</a>';
				html += '  <span class="att-size">' + formatFileSize(att.size) + '</span>';
				html += '  <span class="att-actions">';
				html += '    <a class="btn btn-sm btn-outline-primary" href="' + downloadUrl + '" target="_blank" download="' + (att.name || '') + '">';
				html += '      <i data-feather="download" style="width:12px;height:12px;"></i>';
				html += '    </a>';
				html += '  </span>';
				html += '</div>';
			});
			container.innerHTML = html;
			if (window.feather) feather.replace();
		}

		// 편집 모드 렌더링
		function renderEdit() {
			var container = document.getElementById("attachmentList");
			var html = "";

			existingFiles.forEach(function (att, idx) {
				html += '<div class="attachment-item">';
				html += '  <span class="att-icon"><i data-feather="paperclip" style="width:16px;height:16px;"></i></span>';
				html += '  <span class="att-name">' + att.name + '</span>';
				html += '  <span class="att-size">' + formatFileSize(att.size) + '</span>';
				html += '  <span class="att-actions">';
				html += '    <button type="button" class="btn btn-sm btn-outline-danger btn-remove-existing" data-index="' + idx + '" title="삭제">&times;</button>';
				html += '  </span>';
				html += '</div>';
			});

			pendingFiles.forEach(function (file, idx) {
				html += '<div class="attachment-item">';
				html += '  <span class="att-icon"><i data-feather="file-plus" style="width:16px;height:16px;color:#3b7ddd;"></i></span>';
				html += '  <span class="att-name">' + file.name + ' <span class="badge bg-primary">신규</span></span>';
				html += '  <span class="att-size">' + formatFileSize(file.size) + '</span>';
				html += '  <span class="att-actions">';
				html += '    <button type="button" class="btn btn-sm btn-outline-danger btn-remove-pending" data-index="' + idx + '" title="삭제">&times;</button>';
				html += '  </span>';
				html += '</div>';
			});

			if (!existingFiles.length && !pendingFiles.length) {
				html = '<p class="text-muted small mb-0">첨부파일이 없습니다. 파일을 선택하고 [추가] 버튼을 눌러주세요.</p>';
			}

			container.innerHTML = html;
			if (window.feather) feather.replace();

			container.querySelectorAll(".btn-remove-existing").forEach(function (btn) {
				btn.addEventListener("click", function () {
					existingFiles.splice(parseInt(this.dataset.index), 1);
					renderEdit();
				});
			});

			container.querySelectorAll(".btn-remove-pending").forEach(function (btn) {
				btn.addEventListener("click", function () {
					pendingFiles.splice(parseInt(this.dataset.index), 1);
					renderEdit();
				});
			});
		}

		// 추가 버튼 이벤트 바인딩
		function bindAddButton() {
			document.getElementById("btnAddAttachment").addEventListener("click", function () {
				var input = document.getElementById("inputAttachment");
				var files = input.files;
				if (!files.length) {
					alert("파일을 먼저 선택해주세요.");
					return;
				}
				for (var i = 0; i < files.length; i++) {
					pendingFiles.push(files[i]);
				}
				input.value = "";
				renderEdit();
			});
		}

		// 기존 첨부파일 로드 + 초기화
		function reset(existingAttachments) {
			existingFiles = (existingAttachments || []).map(function (att) {
				return { id: att.id, name: att.name, size: att.size };
			});
			pendingFiles = [];
			renderEdit();
		}

		function getPendingFiles() {
			return pendingFiles;
		}

		function getExistingFiles() {
			return existingFiles;
		}

		return {
			renderView: renderView,
			renderEdit: renderEdit,
			bindAddButton: bindAddButton,
			reset: reset,
			getPendingFiles: getPendingFiles,
			getExistingFiles: getExistingFiles
		};
	}

	return {
		formatFileSize: formatFileSize,
		formatDate: formatDate,
		createEditorManager: createEditorManager,
		validateTitleAndContent: validateTitleAndContent,
		bindTitleErrorClear: bindTitleErrorClear,
		createThumbnailManager: createThumbnailManager,
		createAttachmentManager: createAttachmentManager
	};

})();
