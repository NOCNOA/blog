const detailState = {
    articleId: Number(document.body.dataset.articleId || 0)
};

const detailElements = {
    siteName: document.getElementById("siteName"),
    siteLogoMark: document.getElementById("siteLogoMark"),
    siteNotice: document.getElementById("siteNotice"),
    footerInfo: document.getElementById("footerInfo"),
    footerText: document.getElementById("footerText"),
    githubUrl: document.getElementById("githubUrl"),
    giteeUrl: document.getElementById("giteeUrl"),
    detailMeta: document.getElementById("detailMeta"),
    detailTitle: document.getElementById("detailTitle"),
    detailSummary: document.getElementById("detailSummary"),
    detailTags: document.getElementById("detailTags"),
    detailCover: document.getElementById("detailCover"),
    detailContent: document.getElementById("detailContent"),
    moreArticleList: document.getElementById("moreArticleList")
};

async function fetchJson(url, options = {}) {
    const response = await fetch(url, options);
    const data = await response.json();
    if (!response.ok || data.code !== 200) {
        throw new Error(data.message || "请求失败");
    }
    return data.data;
}

async function initDetailPage() {
    if (!detailState.articleId) {
        throw new Error("文章编号无效");
    }

    await Promise.all([
        loadSiteInfo(),
        loadArticleDetail(),
        loadMoreArticles()
    ]);
}

async function loadSiteInfo() {
    const info = await fetchJson("/site/info");
    detailElements.siteName.textContent = info.siteName || "我的博客";
    detailElements.siteNotice.textContent = info.siteNotice || "欢迎来到我的博客。";
    detailElements.footerInfo.textContent = info.footerInfo || "暂无页脚信息";
    detailElements.footerText.textContent = info.footerInfo || "Blog";
    applySiteLogo(info.siteLogo);
    setOptionalLink(detailElements.githubUrl, info.githubUrl);
    setOptionalLink(detailElements.giteeUrl, info.giteeUrl);
}

async function loadArticleDetail() {
    const detail = await fetchJson(`/article/${detailState.articleId}`);
    document.title = `${detail.title || "文章详情"} - 博客`;
    detailElements.detailMeta.textContent = `${formatDateTime(detail.publishTime)} · ${detail.categoryName || "未分类"} · ${detail.authorName || "匿名作者"} · 阅读 ${detail.viewCount ?? 0}`;
    detailElements.detailTitle.textContent = detail.title || "文章详情";
    detailElements.detailSummary.textContent = detail.summary || "";
    detailElements.detailTags.innerHTML = (detail.tags || [])
        .map((tag) => `<span class="chip">${escapeHtml(tag.name)}</span>`)
        .join("");
    renderCover(detail.coverImage);
    detailElements.detailContent.innerHTML = renderMarkdown(detail.content || "");
}

async function loadMoreArticles() {
    const page = await fetchJson("/article/list?pageNum=1&pageSize=6");
    const records = (page.records || []).filter((item) => item.id !== detailState.articleId).slice(0, 5);
    detailElements.moreArticleList.innerHTML = records.length
        ? records.map((item) => `
            <a class="archive-item archive-button" href="/post/${item.id}">
                ${formatDate(item.publishTime)} · ${escapeHtml(item.title)}
            </a>
        `).join("")
        : `<div class="archive-item">暂时没有更多文章</div>`;
}

function renderCover(coverImage) {
    if (!coverImage) {
        detailElements.detailCover.classList.add("hidden");
        detailElements.detailCover.style.backgroundImage = "";
        return;
    }
    detailElements.detailCover.classList.remove("hidden");
    detailElements.detailCover.style.backgroundImage = `url("${coverImage}")`;
}

function applySiteLogo(url) {
    if (!url) {
        detailElements.siteLogoMark.textContent = "B";
        detailElements.siteLogoMark.style.backgroundImage = "";
        return;
    }
    detailElements.siteLogoMark.textContent = "";
    detailElements.siteLogoMark.style.backgroundImage = `url("${url}")`;
}

function setOptionalLink(element, value) {
    if (value) {
        element.href = value;
        element.style.display = "inline";
    } else {
        element.style.display = "none";
    }
}

function formatDate(value) {
    if (!value) {
        return "未发布";
    }
    return new Date(value).toLocaleDateString("zh-CN");
}

function formatDateTime(value) {
    if (!value) {
        return "未发布";
    }
    return new Date(value).toLocaleString("zh-CN", { hour12: false });
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function renderDetailError(error) {
    document.title = "文章加载失败";
    detailElements.detailMeta.textContent = "页面加载失败";
    detailElements.detailTitle.textContent = "文章不存在或暂时不可用";
    detailElements.detailSummary.textContent = "";
    detailElements.detailTags.innerHTML = "";
    detailElements.detailContent.innerHTML = "<p>" + escapeHtml(error.message) + "</p>";
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

initDetailPage().catch(renderDetailError);
