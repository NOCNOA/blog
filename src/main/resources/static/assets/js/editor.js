var editorState = {
    token: localStorage.getItem("blog_admin_token") || "",
    articleId: null,
    categories: [],
    tags: [],
    selectedTagIds: new Set(),
    coverUrl: "",
    previewVisible: false
};

var editorElements = {
    editorTitle: document.getElementById("editorTitle"),
    titleInput: document.getElementById("titleInput"),
    summaryInput: document.getElementById("summaryInput"),
    contentInput: document.getElementById("contentInput"),
    previewPanel: document.getElementById("previewPanel"),
    imageFileInput: document.getElementById("imageFileInput"),
    coverFileInput: document.getElementById("coverFileInput"),
    categorySelect: document.getElementById("categorySelect"),
    tagContainer: document.getElementById("tagContainer"),
    tagSearchInput: document.getElementById("tagSearchInput"),
    coverPreview: document.getElementById("coverPreview"),
    coverUrlInput: document.getElementById("coverUrlInput"),
    uploadCoverButton: document.getElementById("uploadCoverButton"),
    clearCoverButton: document.getElementById("clearCoverButton"),
    topSelect: document.getElementById("topSelect"),
    saveDraftButton: document.getElementById("saveDraftButton"),
    publishButton: document.getElementById("publishButton"),
    editorMessage: document.getElementById("editorMessage"),
    editorArea: document.getElementById("editorArea"),
    imageUploadBtn: document.getElementById("imageUploadBtn"),
    imageSizeBtn: document.getElementById("imageSizeBtn"),
    fontSizeSelect: document.getElementById("fontSizeSelect"),
    togglePreviewBtn: document.getElementById("togglePreviewBtn"),
    imageDialog: document.getElementById("imageDialog"),
    imgUrlInput: document.getElementById("imgUrlInput"),
    imgAltInput: document.getElementById("imgAltInput"),
    imgWidthInput: document.getElementById("imgWidthInput"),
    imgHeightInput: document.getElementById("imgHeightInput"),
    imgDialogCancel: document.getElementById("imgDialogCancel"),
    imgDialogInsert: document.getElementById("imgDialogInsert")
};

function getAuthHeaders() {
    return editorState.token ? { Authorization: "Bearer " + editorState.token } : {};
}

function requestJson(url, options) {
    options = options || {};
    options.headers = Object.assign(
        {},
        options.body ? { "Content-Type": "application/json" } : {},
        getAuthHeaders(),
        options.headers || {}
    );
    return fetch(url, options).then(function (response) {
        return response.json().then(function (data) {
            if (!response.ok || data.code !== 200) throw new Error(data.message || "请求失败");
            return data.data;
        });
    });
}

function initEditor() {
    var pathParts = window.location.pathname.split("/");
    var lastPart = pathParts[pathParts.length - 1];
    if (lastPart && lastPart !== "editor") {
        editorState.articleId = Number(lastPart);
    }
    if (!editorState.token) { window.location.href = "/admin-ui"; return; }

    bindEditorEvents();
    bindToolbar();
    bindImageDialog();
    bindImageDragDrop();
    loadSidebarData();
}

function bindEditorEvents() {
    editorElements.saveDraftButton.addEventListener("click", function () { saveArticle(0); });
    editorElements.publishButton.addEventListener("click", function () { saveArticle(1); });
    editorElements.uploadCoverButton.addEventListener("click", function () {
        editorElements.coverFileInput.value = "";
        editorElements.coverFileInput.click();
    });
    editorElements.coverFileInput.addEventListener("change", uploadCoverImage);
    editorElements.clearCoverButton.addEventListener("click", clearCover);
    editorElements.coverUrlInput.addEventListener("input", updateCoverFromUrl);
    editorElements.tagSearchInput.addEventListener("keydown", handleTagSearch);
}

function bindToolbar() {
    var toolbar = document.querySelector(".editor-toolbar");
    toolbar.addEventListener("click", function (e) {
        var btn = e.target.closest("button[data-action]");
        if (!btn) return;
        handleToolbarAction(btn.getAttribute("data-action"));
    });

    editorElements.imageUploadBtn.addEventListener("click", function () {
        editorElements.imageFileInput.value = "";
        editorElements.imageFileInput.click();
    });

    editorElements.imageSizeBtn.addEventListener("click", function () {
        var ta = editorElements.contentInput;
        var text = ta.value;
        var pos = ta.selectionStart;
        var url = "", alt = "", w = "", h = "";

        var imgTag = findImageTagAt(text, pos);
        var mdImg = null;
        if (!imgTag) {
            mdImg = findMarkdownImageAt(text, pos);
        }

        if (imgTag) {
            url = imgTag.src || "";
            alt = imgTag.alt || "";
            w = imgTag.width || "";
            h = imgTag.height || "";
            editorState._editRange = imgTag.range;
        } else if (mdImg) {
            url = mdImg.src || "";
            alt = mdImg.alt || "";
            editorState._editRange = mdImg.range;
        } else {
            editorState._editRange = null;
        }

        editorElements.imgUrlInput.value = url;
        editorElements.imgAltInput.value = alt;
        editorElements.imgWidthInput.value = w;
        editorElements.imgHeightInput.value = h;
        editorElements.imageDialog.classList.remove("hidden");
    });

    editorElements.fontSizeSelect.addEventListener("change", function () {
        var size = editorElements.fontSizeSelect.value;
        if (!size) return;
        var ta = editorElements.contentInput;
        ta.focus();
        var start = ta.selectionStart;
        var end = ta.selectionEnd;
        var text = ta.value;
        var selected = text.substring(start, end) || "文字";
        var tag = '<span style="font-size:' + size + 'px">' + selected + '</span>';
        ta.value = text.substring(0, start) + tag + text.substring(end);
        ta.selectionStart = start + tag.length;
        ta.selectionEnd = start + tag.length;
        editorElements.fontSizeSelect.value = "";
    });

    editorElements.togglePreviewBtn.addEventListener("click", function () {
        editorState.previewVisible = !editorState.previewVisible;
        if (editorState.previewVisible) {
            editorElements.previewPanel.innerHTML = renderMarkdown(editorElements.contentInput.value);
            editorElements.previewPanel.classList.remove("hidden");
            editorElements.contentInput.classList.add("hidden");
            editorElements.togglePreviewBtn.textContent = "编辑";
            editorElements.togglePreviewBtn.classList.add("active");
        } else {
            editorElements.previewPanel.classList.add("hidden");
            editorElements.contentInput.classList.remove("hidden");
            editorElements.togglePreviewBtn.textContent = "预览";
            editorElements.togglePreviewBtn.classList.remove("active");
        }
    });
}

function bindImageDialog() {
    editorElements.imgDialogCancel.addEventListener("click", function () {
        editorElements.imageDialog.classList.add("hidden");
    });
    editorElements.imageDialog.addEventListener("click", function (e) {
        if (e.target === editorElements.imageDialog) {
            editorElements.imageDialog.classList.add("hidden");
        }
    });
    editorElements.imgDialogInsert.addEventListener("click", function () {
        var url = editorElements.imgUrlInput.value.trim();
        if (!url) { showMessage("请输入图片地址", true); return; }
        var alt = editorElements.imgAltInput.value.trim();
        var w = editorElements.imgWidthInput.value.trim();
        var h = editorElements.imgHeightInput.value.trim();
        var style = "";
        if (w) style += "width:" + w + "px;";
        if (h) style += "height:" + h + "px;";
        if (!w && !h) style = "max-width:100%;";
        var tag = '<img src="' + url + '" alt="' + alt + '" style="' + style + '">';
        var ta = editorElements.contentInput;

        if (editorState._editRange) {
            var r = editorState._editRange;
            ta.value = ta.value.substring(0, r.start) + tag + ta.value.substring(r.end);
            ta.selectionStart = r.start + tag.length;
            ta.selectionEnd = r.start + tag.length;
            editorState._editRange = null;
        } else {
            var start = ta.selectionStart;
            ta.value = ta.value.substring(0, start) + tag + ta.value.substring(start);
            ta.selectionStart = start + tag.length;
            ta.selectionEnd = start + tag.length;
        }
        editorElements.imageDialog.classList.add("hidden");
        showMessage("图片已插入");
    });
}

function handleToolbarAction(action) {
    var ta = editorElements.contentInput;
    ta.focus();
    switch (action) {
        case "bold": insertAround(ta, "**", "**", "粗体文本"); break;
        case "italic": insertAround(ta, "*", "*", "斜体文本"); break;
        case "heading": insertAtLineStart(ta, "## ", "标题"); break;
        case "code": insertAround(ta, "`", "`", "代码"); break;
        case "quote": insertAtLineStart(ta, "> ", "引用文本"); break;
        case "ul": insertAtLineStart(ta, "- ", "列表项"); break;
        case "link": insertAround(ta, "[", "](https://)", "链接文本"); break;
    }
}

function insertAround(ta, before, after, placeholder) {
    var start = ta.selectionStart;
    var end = ta.selectionEnd;
    var text = ta.value;
    var selected = text.substring(start, end) || placeholder;
    ta.value = text.substring(0, start) + before + selected + after + text.substring(end);
    ta.selectionStart = start + before.length;
    ta.selectionEnd = start + before.length + selected.length;
}

function insertAtLineStart(ta, prefix, placeholder) {
    var start = ta.selectionStart;
    var end = ta.selectionEnd;
    var text = ta.value;
    var selected = text.substring(start, end) || placeholder;
    var lineStart = text.lastIndexOf("\n", start - 1) + 1;
    ta.value = text.substring(0, lineStart) + prefix + selected + text.substring(end);
    ta.selectionStart = lineStart + prefix.length;
    ta.selectionEnd = lineStart + prefix.length + selected.length;
}

function bindImageDragDrop() {
    var ta = editorElements.contentInput;
    var area = editorElements.editorArea;

    ta.addEventListener("paste", function (e) {
        var items = e.clipboardData && e.clipboardData.items;
        if (!items) return;
        for (var i = 0; i < items.length; i++) {
            if (items[i].type.indexOf("image/") === 0) {
                e.preventDefault();
                uploadAndInsertImage(items[i].getAsFile());
                return;
            }
        }
    });

    area.addEventListener("dragover", function (e) { e.preventDefault(); area.classList.add("drag-over"); });
    area.addEventListener("dragleave", function () { area.classList.remove("drag-over"); });
    area.addEventListener("drop", function (e) {
        e.preventDefault();
        area.classList.remove("drag-over");
        var files = e.dataTransfer && e.dataTransfer.files;
        if (!files) return;
        for (var i = 0; i < files.length; i++) {
            if (files[i].type.indexOf("image/") === 0) { uploadAndInsertImage(files[i]); return; }
        }
    });

    editorElements.imageFileInput.addEventListener("change", function () {
        var file = editorElements.imageFileInput.files[0];
        if (file) uploadAndInsertImage(file);
    });
}

function uploadAndInsertImage(file) {
    var ta = editorElements.contentInput;
    var uid = "upload_" + Date.now() + "_" + Math.random().toString(36).substring(2, 8);
    var placeholder = "![" + uid + "]()";
    var start = ta.selectionStart;
    ta.value = ta.value.substring(0, start) + placeholder + ta.value.substring(start);
    ta.selectionStart = start + placeholder.length;
    ta.selectionEnd = start + placeholder.length;
    showMessage("图片上传中...");

    uploadImageFile(file).then(function (url) {
        var tag = '<img src="' + url + '" alt="' + file.name + '" style="max-width:100%">';
        ta.value = ta.value.replace(placeholder, tag);
        showMessage("图片已插入");
    }).catch(function (e) {
        ta.value = ta.value.replace(placeholder, "");
        showMessage("图片上传失败: " + e.message, true);
    });
}

function renderMarkdown(md) {
    if (!md) return "";
    var blocks = [];
    var html = md.replace(/<([^>]+)>/g, function (match) {
        var idx = blocks.length;
        blocks.push(match);
        return "\x00BLOCK" + idx + "\x00";
    });
    html = escapeHtml(html);
    for (var i = 0; i < blocks.length; i++) {
        html = html.replace("\x00BLOCK" + i + "\x00", blocks[i]);
    }
    html = html.replace(/^### (.+)$/gm, "<h3>$1</h3>");
    html = html.replace(/^## (.+)$/gm, "<h2>$1</h2>");
    html = html.replace(/^# (.+)$/gm, "<h1>$1</h1>");
    html = html.replace(/\*\*(.+?)\*\*/g, "<strong>$1</strong>");
    html = html.replace(/\*(.+?)\*/g, "<em>$1</em>");
    html = html.replace(/~~(.+?)~~/g, "<del>$1</del>");
    html = html.replace(/`([^`]+?)`/g, "<code>$1</code>");
    html = html.replace(/!\[([^\]]*)\]\(([^)]+)\)/g, '<img src="$2" alt="$1">');
    html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank">$1</a>');
    html = html.replace(/^&gt; (.+)$/gm, "<blockquote>$1</blockquote>");
    html = html.replace(/^- (.+)$/gm, "<li>$1</li>");
    html = html.replace(/^(\d+)\. (.+)$/gm, "<li>$2</li>");
    html = html.replace(/\n{2,}/g, "</p><p>");
    html = html.replace(/\n/g, "<br>");
    return "<p>" + html + "</p>";
}

function loadSidebarData() {
    Promise.all([loadCategories(), loadTags()]).then(function () {
        if (editorState.articleId) return loadArticleDetail();
    }).catch(function (error) { showMessage(error.message, true); });
}

function loadCategories() {
    return requestJson("/admin/category/list").then(function (data) {
        editorState.categories = data || [];
        editorElements.categorySelect.innerHTML = editorState.categories.length
            ? editorState.categories.map(function (c) { return '<option value="' + c.id + '">' + escapeHtml(c.name) + '</option>'; }).join("")
            : '<option value="">请先新增分类</option>';
        editorElements.categorySelect.disabled = editorState.categories.length === 0;
    });
}

function loadTags() {
    return requestJson("/admin/tag/list").then(function (data) {
        editorState.tags = data || [];
        renderTagChips();
    });
}

function renderTagChips() {
    editorElements.tagContainer.innerHTML = editorState.tags.map(function (tag) {
        var selected = editorState.selectedTagIds.has(tag.id);
        return '<span class="tag-chip ' + (selected ? "selected" : "") + '" data-id="' + tag.id + '" onclick="toggleTag(' + tag.id + ')">' + escapeHtml(tag.name) + '</span>';
    }).join("");
}

function toggleTag(tagId) {
    if (editorState.selectedTagIds.has(tagId)) { editorState.selectedTagIds.delete(tagId); }
    else { editorState.selectedTagIds.add(tagId); }
    renderTagChips();
}

function handleTagSearch(event) {
    if (event.key !== "Enter") return;
    var input = editorElements.tagSearchInput.value.trim();
    if (!input) return;
    var found = editorState.tags.find(function (t) { return t.name === input; });
    if (found) { editorState.selectedTagIds.add(found.id); renderTagChips(); }
    else { createTagByName(input); }
    editorElements.tagSearchInput.value = "";
}

function createTagByName(name) {
    requestJson("/admin/tag", { method: "POST", body: JSON.stringify({ name: name }) })
    .then(function () { return loadTags(); })
    .then(function () {
        var newTag = editorState.tags.find(function (t) { return t.name === name; });
        if (newTag) { editorState.selectedTagIds.add(newTag.id); renderTagChips(); }
    }).catch(function (error) { showMessage(error.message, true); });
}

function loadArticleDetail() {
    return requestJson("/admin/article/" + editorState.articleId).then(function (detail) {
        editorElements.editorTitle.textContent = "编辑文章 #" + editorState.articleId;
        document.title = "编辑: " + detail.title;
        editorElements.titleInput.value = detail.title || "";
        editorElements.summaryInput.value = detail.summary || "";
        editorElements.topSelect.value = String(detail.isTop ?? 0);
        if (detail.categoryId) editorElements.categorySelect.value = String(detail.categoryId);
        if (detail.tagIdList) { detail.tagIdList.forEach(function (id) { editorState.selectedTagIds.add(id); }); renderTagChips(); }
        if (detail.coverImage) { editorState.coverUrl = detail.coverImage; editorElements.coverUrlInput.value = detail.coverImage; updateCoverPreview(); }
        if (detail.content) editorElements.contentInput.value = detail.content;
    }).catch(function (error) { showMessage(error.message, true); });
}

function saveArticle(status) {
    if (!editorState.categories.length) { showMessage("请先在后台新增分类", true); return; }
    var title = editorElements.titleInput.value.trim();
    if (!title) { showMessage("请输入文章标题", true); return; }
    var content = editorElements.contentInput.value.trim();
    if (!content) { showMessage("请输入文章正文", true); return; }

    var isEdit = Boolean(editorState.articleId);
    showMessage(isEdit ? "保存修改中..." : (status === 0 ? "保存草稿中..." : "发布中..."));

    requestJson("/admin/article", {
        method: isEdit ? "PUT" : "POST",
        body: JSON.stringify({
            id: editorState.articleId || null,
            title: title,
            summary: editorElements.summaryInput.value.trim(),
            content: content,
            coverImage: editorState.coverUrl,
            categoryId: Number(editorElements.categorySelect.value),
            status: status,
            isTop: Number(editorElements.topSelect.value),
            tagIdList: Array.from(editorState.selectedTagIds)
        })
    }).then(function () {
        showMessage(isEdit ? "文章已更新" : (status === 0 ? "草稿已保存" : "文章已发布"));
    }).catch(function (error) { showMessage(error.message, true); });
}

function uploadCoverImage() {
    var file = editorElements.coverFileInput.files[0];
    if (!file) return;
    showMessage("封面上传中...");
    uploadImageFile(file).then(function (url) {
        editorState.coverUrl = url;
        editorElements.coverUrlInput.value = url;
        updateCoverPreview();
        showMessage("封面图已上传");
    }).catch(function (error) { showMessage(error.message, true); });
}

function clearCover() {
    editorState.coverUrl = "";
    editorElements.coverUrlInput.value = "";
    editorElements.coverPreview.innerHTML = "";
    editorElements.coverPreview.classList.remove("has-image");
}

function updateCoverFromUrl() {
    editorState.coverUrl = editorElements.coverUrlInput.value.trim();
    updateCoverPreview();
}

function updateCoverPreview() {
    if (editorState.coverUrl) {
        editorElements.coverPreview.innerHTML = '<img src="' + escapeHtml(editorState.coverUrl) + '" alt="封面预览">';
        editorElements.coverPreview.classList.add("has-image");
    } else {
        editorElements.coverPreview.innerHTML = "";
        editorElements.coverPreview.classList.remove("has-image");
    }
}

function uploadImageFile(file) {
    var formData = new FormData();
    formData.append("file", file);
    return fetch("/admin/file/upload", {
        method: "POST",
        headers: getAuthHeaders(),
        body: formData
    }).then(function (response) {
        return response.json().then(function (data) {
            if (!response.ok || data.code !== 200) throw new Error(data.message || "上传失败");
            return data.data.url;
        });
    });
}

function showMessage(msg, isError) {
    editorElements.editorMessage.textContent = msg;
    editorElements.editorMessage.classList.toggle("error", Boolean(isError));
    if (!isError) { setTimeout(function () { editorElements.editorMessage.textContent = ""; }, 3000); }
}

function findImageTagAt(text, pos) {
    var re = /<img\s+[^>]*>/gi;
    var m;
    while ((m = re.exec(text)) !== null) {
        var start = m.index;
        var end = start + m[0].length;
        if (pos >= start && pos <= end) {
            var tag = m[0];
            var srcMatch = tag.match(/src=["']([^"']+)["']/i);
            var altMatch = tag.match(/alt=["']([^"']*)["']/i);
            var styleMatch = tag.match(/style=["']([^"']*)["']/i);
            var widthMatch = tag.match(/style=["'][^"']*width:\s*(\d+)px/i);
            var heightMatch = tag.match(/style=["'][^"']*height:\s*(\d+)px/i);
            return {
                src: srcMatch ? srcMatch[1] : "",
                alt: altMatch ? altMatch[1] : "",
                width: widthMatch ? widthMatch[1] : "",
                height: heightMatch ? heightMatch[1] : "",
                range: { start: start, end: end }
            };
        }
    }
    return null;
}

function findMarkdownImageAt(text, pos) {
    var re = /!\[([^\]]*)\]\(([^)]+)\)/g;
    var m;
    while ((m = re.exec(text)) !== null) {
        var start = m.index;
        var end = start + m[0].length;
        if (pos >= start && pos <= end) {
            return {
                alt: m[1],
                src: m[2],
                width: "",
                height: "",
                range: { start: start, end: end }
            };
        }
    }
    return null;
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

window.toggleTag = toggleTag;

initEditor();
