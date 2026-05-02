const portalState = {
    pageNum: 1,
    pageSize: 6,
    keyword: "",
    categoryId: null,
    tagId: null
};

const portalElements = {
    siteName: document.getElementById("siteName"),
    siteLogoMark: document.getElementById("siteLogoMark"),
    siteDescription: document.getElementById("siteDescription"),
    siteNotice: document.getElementById("siteNotice"),
    footerInfo: document.getElementById("footerInfo"),
    footerText: document.getElementById("footerText"),
    githubUrl: document.getElementById("githubUrl"),
    giteeUrl: document.getElementById("giteeUrl"),
    articleList: document.getElementById("articleList"),
    categoryList: document.getElementById("categoryList"),
    tagList: document.getElementById("tagList"),
    archiveList: document.getElementById("archiveList"),
    keywordInput: document.getElementById("keywordInput"),
    searchButton: document.getElementById("searchButton"),
    prevPageButton: document.getElementById("prevPageButton"),
    nextPageButton: document.getElementById("nextPageButton"),
    pageInfo: document.getElementById("pageInfo")
};

async function fetchJson(url, options = {}) {
    const response = await fetch(url, options);
    const data = await response.json();
    if (!response.ok || data.code !== 200) {
        throw new Error(data.message || "请求失败");
    }
    return data.data;
}

async function initPortal() {
    bindPortalEvents();
    await Promise.all([
        loadSiteInfo(),
        loadCategories(),
        loadTags(),
        loadArchives()
    ]);
    await loadArticles();
}

function bindPortalEvents() {
    portalElements.searchButton.addEventListener("click", searchArticles);
    portalElements.keywordInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            searchArticles();
        }
    });

    portalElements.prevPageButton.addEventListener("click", async () => {
        if (portalState.pageNum > 1) {
            portalState.pageNum -= 1;
            await loadArticles();
        }
    });

    portalElements.nextPageButton.addEventListener("click", async () => {
        portalState.pageNum += 1;
        await loadArticles();
    });
}

function searchArticles() {
    portalState.keyword = portalElements.keywordInput.value.trim();
    portalState.pageNum = 1;
    loadArticles().catch(renderLoadError);
}

async function loadSiteInfo() {
    const info = await fetchJson("/site/info");
    portalElements.siteName.textContent = info.siteName || "我的博客";
    portalElements.siteDescription.textContent = info.siteDescription || "记录开发、学习与生活";
    portalElements.siteNotice.textContent = info.siteNotice || "欢迎来到我的博客。";
    portalElements.footerInfo.textContent = info.footerInfo || "暂无页脚信息";
    portalElements.footerText.textContent = info.footerInfo || "Blog";
    applySiteLogo(info.siteLogo);
    setOptionalLink(portalElements.githubUrl, info.githubUrl);
    setOptionalLink(portalElements.giteeUrl, info.giteeUrl);
}

function applySiteLogo(url) {
    if (!url) {
        portalElements.siteLogoMark.textContent = "B";
        portalElements.siteLogoMark.style.backgroundImage = "";
        return;
    }
    portalElements.siteLogoMark.textContent = "";
    portalElements.siteLogoMark.style.backgroundImage = `url("${url}")`;
}

function setOptionalLink(element, value) {
    if (value) {
        element.href = value;
        element.style.display = "inline";
    } else {
        element.style.display = "none";
    }
}

async function loadCategories() {
    const categories = await fetchJson("/category/list");
    portalElements.categoryList.innerHTML = "";

    const allChip = createChip("全部", portalState.categoryId == null, async () => {
        portalState.categoryId = null;
        portalState.pageNum = 1;
        await loadArticles();
        await loadCategories();
    });
    portalElements.categoryList.appendChild(allChip);

    categories.forEach((category) => {
        portalElements.categoryList.appendChild(createChip(category.name, portalState.categoryId === category.id, async () => {
            portalState.categoryId = category.id;
            portalState.pageNum = 1;
            await loadArticles();
            await loadCategories();
        }));
    });
}

async function loadTags() {
    const tags = await fetchJson("/tag/list");
    portalElements.tagList.innerHTML = "";

    const allChip = createChip("全部", portalState.tagId == null, async () => {
        portalState.tagId = null;
        portalState.pageNum = 1;
        await loadArticles();
        await loadTags();
    });
    portalElements.tagList.appendChild(allChip);

    tags.forEach((tag) => {
        portalElements.tagList.appendChild(createChip(tag.name, portalState.tagId === tag.id, async () => {
            portalState.tagId = tag.id;
            portalState.pageNum = 1;
            await loadArticles();
            await loadTags();
        }));
    });
}

async function loadArchives() {
    const archives = await fetchJson("/article/archive");
    portalElements.archiveList.innerHTML = archives.length
        ? archives.slice(0, 8).map((item) => `
            <a class="archive-item archive-button" href="/post/${item.id}">
                ${formatDate(item.publishTime)} · ${escapeHtml(item.title)}
            </a>
        `).join("")
        : `<div class="archive-item">暂无归档内容</div>`;
}

async function loadArticles() {
    const query = new URLSearchParams({
        pageNum: String(portalState.pageNum),
        pageSize: String(portalState.pageSize)
    });

    if (portalState.keyword) {
        query.set("keyword", portalState.keyword);
    }
    if (portalState.categoryId != null) {
        query.set("categoryId", String(portalState.categoryId));
    }
    if (portalState.tagId != null) {
        query.set("tagId", String(portalState.tagId));
    }

    const page = await fetchJson(`/article/list?${query.toString()}`);
    renderArticleCards(page.records || []);

    const totalPages = Math.max(1, Math.ceil((page.total || 0) / (page.pageSize || portalState.pageSize)));
    portalElements.pageInfo.textContent = `第 ${page.pageNum} 页 / 共 ${totalPages} 页`;
    portalElements.prevPageButton.disabled = page.pageNum <= 1;
    portalElements.nextPageButton.disabled = page.pageNum >= totalPages;
}

function renderArticleCards(records) {
    portalElements.articleList.innerHTML = "";
    if (!records.length) {
        portalElements.articleList.innerHTML = `
            <div class="article-card">
                <h3>暂无文章</h3>
                <p class="meta-line">可以先去后台发布一篇内容。</p>
            </div>
        `;
        return;
    }

    records.forEach((item) => {
        const card = document.createElement("article");
        card.className = "article-card";
        card.innerHTML = `
            ${item.coverImage ? `<div class="article-cover" style="background-image:url('${escapeAttribute(item.coverImage)}')"></div>` : ""}
            <p class="meta-line">${formatDate(item.publishTime)} · ${escapeHtml(item.categoryName || "未分类")} · ${escapeHtml(item.authorName || "匿名")}</p>
            <h3>${escapeHtml(item.title)}</h3>
            <p>${escapeHtml(item.summary || "暂无摘要")}</p>
            <div class="chip-list">${(item.tags || []).map((tag) => `<span class="chip">${escapeHtml(tag.name)}</span>`).join("")}</div>
        `;
        card.addEventListener("click", () => {
            window.location.href = `/post/${item.id}`;
        });
        portalElements.articleList.appendChild(card);
    });
}

function createChip(text, active, onClick) {
    const chip = document.createElement("button");
    chip.type = "button";
    chip.className = `chip${active ? " active" : ""}`;
    chip.textContent = text;
    chip.addEventListener("click", onClick);
    return chip;
}

function formatDate(value) {
    if (!value) {
        return "未发布";
    }
    return new Date(value).toLocaleDateString("zh-CN");
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function escapeAttribute(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("'", "\\'");
}

function renderLoadError(error) {
    portalElements.articleList.innerHTML = `
        <div class="article-card">
            <h3>加载失败</h3>
            <p class="meta-line">${escapeHtml(error.message)}</p>
        </div>
    `;
}

initPortal().catch(renderLoadError);
