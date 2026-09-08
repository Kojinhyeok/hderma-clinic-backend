/**
 * CKEditor 5 공통 설정 (H-Derma)
 * 사용법:
 *   1) HTML에 ckeditor CDN + 이 파일을 로드
 *   2) AdminUtils.createEditorManager("editor", "notice") 사용 (admin-utils.js에 이미 있음)
 */
var CKEditorConfig = (function () {

	// S3 Presigned URL 업로드 어댑터
	function S3UploadAdapter(loader, entityType) {
		this.loader = loader;
		this.entityType = entityType;
	}

	S3UploadAdapter.prototype.upload = function () {
		var entityType = this.entityType;
		return this.loader.file.then(function (file) {
			return new Promise(function (resolve, reject) {
				fetch("/api/files/upload-link", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({
						entityType: entityType,
						entityId: 0,
						fileCategory: "CONTENT",
						originalFilename: file.name,
						mimeType: file.type,
						fileSize: file.size
					})
				})
				.then(function (res) { return res.json(); })
				.then(function (data) {
					return fetch(data.uploadUrl, {
						method: "PUT",
						headers: { "Content-Type": file.type },
						body: file
					}).then(function () {
						resolve({ default: data.viewUrl });
					});
				})
				.catch(function (err) {
					reject(err);
				});
			});
		});
	};

	S3UploadAdapter.prototype.abort = function () {};

	function createEditor(elementId, entityType, initialData) {
		function UploadPlugin(editor) {
			editor.plugins.get("FileRepository").createUploadAdapter = function (loader) {
				return new S3UploadAdapter(loader, entityType);
			};
		}

		return CKEDITOR.ClassicEditor.create(document.getElementById(elementId), {
			extraPlugins: [UploadPlugin],
			toolbar: {
				items: [
					"heading", "fontSize", "|",
					"fontColor", "fontBackgroundColor", "|",
					"bold", "italic", "underline", "strikethrough", "|",
					"alignment", "|",
					"bulletedList", "numberedList", "|",
					"link", "uploadImage", "blockQuote", "insertTable", "|",
					"undo", "redo"
				],
				shouldNotGroupWhenFull: true
			},
			fontSize: {
				options: [9, 11, 13, "default", 17, 19, 21, 27, 35]
			},
			language: "ko",
			image: {
				insert: { type: "auto" },
				toolbar: [
					"imageStyle:block", "imageStyle:inline", "|",
					"imageStyle:alignLeft", "imageStyle:alignCenter", "imageStyle:alignRight", "|",
					"resizeImage:original", "resizeImage:25", "resizeImage:50", "resizeImage:75", "|",
					"imageTextAlternative"
				],
				resizeOptions: [
					{ name: "resizeImage:original", value: null, label: "원본", icon: "original" },
					{ name: "resizeImage:25", value: "25", label: "25%", icon: "small" },
					{ name: "resizeImage:50", value: "50", label: "50%", icon: "medium" },
					{ name: "resizeImage:75", value: "75", label: "75%", icon: "large" }
				],
				resizeUnit: "%"
			},
			placeholder: "본문 내용을 입력하세요",
			removePlugins: [
				"AIAssistant", "CKBox", "CKFinder", "EasyImage",
				"RealTimeCollaborativeComments", "RealTimeCollaborativeTrackChanges",
				"RealTimeCollaborativeRevisionHistory", "PresenceList", "Comments",
				"TrackChanges", "TrackChangesData", "RevisionHistory",
				"Pagination", "WProofreader", "MathType", "SlashCommand",
				"Template", "DocumentOutline", "FormatPainter", "TableOfContents",
				"PasteFromOfficeEnhanced", "CaseChange", "ExportPdf", "ExportWord",
				"ImportWord", "MultiLevelList", "Mention"
			]
		}).then(function (editor) {
			if (initialData) editor.setData(initialData);
			return editor;
		});
	}

	return { createEditor: createEditor };
})();