const adminState = {
    token: localStorage.getItem("blog_admin_token") || "",
    userInfo: JSON.parse(localStorage.getItem("blog_admin_user") || "null"),
    categories: [],
    tags: [],
    editingArticleId: null,
    articleFilters: {
        keyword: "",
        categoryId: "",
        status: ""
    }
};

const adminElements = {
    loginPanel: document.getElementById("loginPanel"),
    dashboardPanel: document.getElementById("dashboardPanel"),
    usernameInput: document.getElementById("usernameInput"),
    passwordInput: document.getElementById("passwordInput"),
    loginButton: document.getElementById("loginButton"),
    loginMessage: document.getElementById("loginMessage"),
    welcomeText: document.getElementById("welcomeText"),
    refreshButton: document.getElementById("refreshButton"),
    logoutButton: document.getElementById("logoutButton"),
    articleCount: document.getElementById("articleCount"),
    publishedCount: document.getElementById("publishedCount"),
    draftCount: document.getElementById("draftCount"),
    categoryCount: document.getElementById("categoryCount"),
    tagCount: document.getElementById("tagCount"),
    siteNameInput: document.getElementById("siteNameInput"),
    siteDescriptionInput: document.getElementById("siteDescriptionInput"),
    siteNoticeInput: document.getElementById("siteNoticeInput"),
    footerInfoInput: document.getElementById("footerInfoInput"),
    siteLogoInput: document.getElementById("siteLogoInput"),
    avatarInput: document.getElementById("avatarInput"),
    githubInput: document.getElementById("githubInput"),
    giteeInput: document.getElementById("giteeInput"),
    saveSiteButton: document.getElementById("saveSiteButton"),
    siteMessage: document.getElementById("siteMessage"),
    profileUsernameInput: document.getElementById("profileUsernameInput"),
    profileNicknameInput: document.getElementById("profileNicknameInput"),
    profileEmailInput: document.getElementById("profileEmailInput"),
    profileAvatarInput: document.getElementById("profileAvatarInput"),
    saveProfileButton: document.getElementById("saveProfileButton"),
    profileMessage: document.getElementById("profileMessage"),
    oldPasswordInput: document.getElementById("oldPasswordInput"),
    newPasswordInput: document.getElementById("newPasswordInput"),
    savePasswordButton: document.getElementById("savePasswordButton"),
    passwordMessage: document.getElementById("passwordMessage"),
    articleFormTitle: document.getElementById("articleFormTitle"),
    articleTitleInput: document.getElementById("articleTitleInput"),
    articleSummaryInput: document.getElementById("articleSummaryInput"),
    articleCategorySelect: document.getElementById("articleCategorySelect"),
    articleStatusSelect: document.getElementById("articleStatusSelect"),
    articleTopSelect: document.getElementById("articleTopSelect"),
    articleCoverInput: document.getElementById("articleCoverInput"),
    articleTagsInput: document.getElementById("articleTagsInput"),
    articleContentInput: document.getElementById("articleContentInput"),
    saveArticleButton: document.getElementById("saveArticleButton"),
    resetArticleButton: document.getElementById("resetArticleButton"),
    articleMessage: document.getElementById("articleMessage"),
    uploadFileInput: document.getElementById("uploadFileInput"),
    uploadButton: document.getElementById("uploadButton"),
    uploadResultInput: document.getElementById("uploadResultInput"),
    fillCoverButton: document.getElementById("fillCoverButton"),
    fillLogoButton: document.getElementById("fillLogoButton"),
    fillAvatarButton: document.getElementById("fillAvatarButton"),
    fillProfileAvatarButton: document.getElementById("fillProfileAvatarButton"),
    uploadMessage: document.getElementById("uploadMessage"),
    categoryNameInput: document.getElementById("categoryNameInput"),
    categorySortInput: document.getElementById("categorySortInput"),
    categoryDescriptionInput: document.getElementById("categoryDescriptionInput"),
    saveCategoryButton: document.getElementById("saveCategoryButton"),
    categoryMessage: document.getElementById("categoryMessage"),
    tagNameInput: document.getElementById("tagNameInput"),
    saveTagButton: document.getElementById("saveTagButton"),
    tagMessage: document.getElementById("tagMessage"),
    articleKeywordFilterInput: document.getElementById("articleKeywordFilterInput"),
    articleCategoryFilterSelect: document.getElementById("articleCategoryFilterSelect"),
    articleStatusFilterSelect: document.getElementById("articleStatusFilterSelect"),
    articleSearchButton: document.getElementById("articleSearchButton"),
    articleFilterResetButton: document.getElementById("articleFilterResetButton"),
    articleTable: document.getElementById("articleTable"),
    categoryTable: document.getElementById("categoryTable"),
    tagTable: document.getElementById("tagTable")
};

function getAuthHeaders() {
    return adminState.token ? { Authorization: `Bearer ${adminState.token}` } : {};
}

async function requestJson(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            ...(options.body ? { "Content-Type": "application/json" } : {}),
            ...getAuthHeaders(),
            ...(options.headers || {})
        }
    });

    const data = await response.json();
    if (!response.ok || data.code !== 200) {
        throw new Error(data.message || "请求失败");
    }
    return data.data;
}

function bindAdminEvents() {
    adminElements.loginButton.addEventListener("click", login);
    adminElements.passwordInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            login();
        }
    });
    adminElements.logoutButton.addEventListener("click", logout);
    adminElements.refreshButton.addEventListener("click", loadDashboardData);
    adminElements.saveSiteButton.addEventListener("click", saveSiteConfig);
    adminElements.saveProfileButton.addEventListener("click", saveProfile);
    adminElements.savePasswordButton.addEventListener("click", updatePassword);
    adminElements.saveCategoryButton.addEventListener("click", createCategory);
    adminElements.saveTagButton.addEventListener("click", createTag);
    adminElements.saveArticleButton.addEventListener("click", saveArticle);
    adminElements.resetArticleButton.addEventListener("click", resetArticleForm);
    adminElements.uploadButton.addEventListener("click", uploadImage);
    adminElements.fillCoverButton.addEventListener("click", () => fillUploadedUrl(adminElements.articleCoverInput));
    adminElements.fillLogoButton.addEventListener("click", () => fillUploadedUrl(adminElements.siteLogoInput));
    adminElements.fillAvatarButton.addEventListener("click", () => fillUploadedUrl(adminElements.avatarInput));
    adminElements.fillProfileAvatarButton.addEventListener("click", () => fillUploadedUrl(adminElements.profileAvatarInput));
    adminElements.articleSearchButton.addEventListener("click", searchArticles);
    adminElements.articleFilterResetButton.addEventListener("click", resetArticleFilters);
    adminElements.articleKeywordFilterInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            searchArticles();
        }
    });
}

async function login() {
    try {
        adminElements.loginMessage.textContent = "登录中...";
        const data = await requestJson("/admin/auth/login", {
            method: "POST",
            body: JSON.stringify({
                username: adminElements.usernameInput.value.trim(),
                password: adminElements.passwordInput.value
            })
        });
        adminState.token = data.token;
        adminState.userInfo = data.userInfo;
        localStorage.setItem("blog_admin_token", adminState.token);
        localStorage.setItem("blog_admin_user", JSON.stringify(adminState.userInfo));
        adminElements.loginMessage.textContent = "登录成功";
        showDashboard();
        await loadDashboardData();
    } catch (error) {
        adminElements.loginMessage.textContent = error.message;
    }
}

async function logout() {
    try {
        await requestJson("/admin/auth/logout", { method: "POST" });
    } catch (error) {
        console.warn(error.message);
    }

    adminState.token = "";
    adminState.userInfo = null;
    localStorage.removeItem("blog_admin_token");
    localStorage.removeItem("blog_admin_user");
    adminElements.dashboardPanel.classList.add("hidden");
    adminElements.loginPanel.classList.remove("hidden");
    adminElements.loginMessage.textContent = "";
}

function showDashboard() {
    adminElements.loginPanel.classList.add("hidden");
    adminElements.dashboardPanel.classList.remove("hidden");
    if (adminState.userInfo) {
        const displayName = adminState.userInfo.nickname || adminState.userInfo.username || "管理员";
        adminElements.welcomeText.textContent = `欢迎回来，${displayName}`;
    } else {
        adminElements.welcomeText.textContent = "欢迎回来";
    }
}

async function loadDashboardData() {
    await Promise.all([
        loadStatistics(),
        loadSiteConfig(),
        loadProfile(),
        loadCategories(),
        loadTags(),
        loadArticles()
    ]);
}

async function loadStatistics() {
    const data = await requestJson("/admin/dashboard/statistics");
    adminElements.articleCount.textContent = data.articleCount ?? 0;
    adminElements.publishedCount.textContent = data.publishedArticleCount ?? 0;
    adminElements.draftCount.textContent = data.draftArticleCount ?? 0;
    adminElements.categoryCount.textContent = data.categoryCount ?? 0;
    adminElements.tagCount.textContent = data.tagCount ?? 0;
}

async function loadSiteConfig() {
    const data = await requestJson("/admin/site/config");
    adminElements.siteNameInput.value = data.siteName || "";
    adminElements.siteDescriptionInput.value = data.siteDescription || "";
    adminElements.siteNoticeInput.value = data.siteNotice || "";
    adminElements.footerInfoInput.value = data.footerInfo || "";
    adminElements.siteLogoInput.value = data.siteLogo || "";
    adminElements.avatarInput.value = data.avatar || "";
    adminElements.githubInput.value = data.githubUrl || "";
    adminElements.giteeInput.value = data.giteeUrl || "";
}

async function saveSiteConfig() {
    try {
        adminElements.siteMessage.textContent = "保存中...";
        await requestJson("/admin/site/config", {
            method: "PUT",
            body: JSON.stringify({
                siteName: adminElements.siteNameInput.value.trim(),
                siteDescription: adminElements.siteDescriptionInput.value.trim(),
                siteNotice: adminElements.siteNoticeInput.value.trim(),
                footerInfo: adminElements.footerInfoInput.value.trim(),
                siteLogo: adminElements.siteLogoInput.value.trim(),
                avatar: adminElements.avatarInput.value.trim(),
                githubUrl: adminElements.githubInput.value.trim(),
                giteeUrl: adminElements.giteeInput.value.trim()
            })
        });
        adminElements.siteMessage.textContent = "站点配置已保存";
    } catch (error) {
        adminElements.siteMessage.textContent = error.message;
    }
}

async function loadProfile() {
    const profile = await requestJson("/admin/auth/profile");
    adminElements.profileUsernameInput.value = profile.username || "";
    adminElements.profileNicknameInput.value = profile.nickname || "";
    adminElements.profileEmailInput.value = profile.email || "";
    adminElements.profileAvatarInput.value = profile.avatar || "";
    if (adminState.userInfo) {
        adminState.userInfo.nickname = profile.nickname || adminState.userInfo.nickname;
        localStorage.setItem("blog_admin_user", JSON.stringify(adminState.userInfo));
        showDashboard();
    }
}

async function saveProfile() {
    try {
        adminElements.profileMessage.textContent = "保存中...";
        await requestJson("/admin/auth/profile", {
            method: "PUT",
            body: JSON.stringify({
                nickname: adminElements.profileNicknameInput.value.trim(),
                email: adminElements.profileEmailInput.value.trim(),
                avatar: adminElements.profileAvatarInput.value.trim()
            })
        });
        adminElements.profileMessage.textContent = "个人资料已更新";
        await loadProfile();
    } catch (error) {
        adminElements.profileMessage.textContent = error.message;
    }
}

async function updatePassword() {
    try {
        adminElements.passwordMessage.textContent = "更新中...";
        await requestJson("/admin/auth/password", {
            method: "PUT",
            body: JSON.stringify({
                oldPassword: adminElements.oldPasswordInput.value,
                newPassword: adminElements.newPasswordInput.value
            })
        });
        adminElements.oldPasswordInput.value = "";
        adminElements.newPasswordInput.value = "";
        adminElements.passwordMessage.textContent = "密码修改成功";
    } catch (error) {
        adminElements.passwordMessage.textContent = error.message;
    }
}

function searchArticles() {
    adminState.articleFilters.keyword = adminElements.articleKeywordFilterInput.value.trim();
    adminState.articleFilters.categoryId = adminElements.articleCategoryFilterSelect.value;
    adminState.articleFilters.status = adminElements.articleStatusFilterSelect.value;
    loadArticles().catch((error) => {
        adminElements.articleMessage.textContent = error.message;
    });
}

function resetArticleFilters() {
    adminState.articleFilters.keyword = "";
    adminState.articleFilters.categoryId = "";
    adminState.articleFilters.status = "";
    adminElements.articleKeywordFilterInput.value = "";
    adminElements.articleCategoryFilterSelect.value = "";
    adminElements.articleStatusFilterSelect.value = "";
    loadArticles().catch((error) => {
        adminElements.articleMessage.textContent = error.message;
    });
}

async function loadArticles() {
    const query = new URLSearchParams({
        pageNum: "1",
        pageSize: "20"
    });
    if (adminState.articleFilters.keyword) {
        query.set("keyword", adminState.articleFilters.keyword);
    }
    if (adminState.articleFilters.categoryId) {
        query.set("categoryId", adminState.articleFilters.categoryId);
    }
    if (adminState.articleFilters.status) {
        query.set("status", adminState.articleFilters.status);
    }

    const data = await requestJson(`/admin/article/list?${query.toString()}`);
    const records = data.records || [];
    if (!records.length) {
        adminElements.articleTable.innerHTML = `
            <div class="table-row empty-state">
                <strong>还没有符合条件的文章</strong>
                <span class="hint">你可以先发布一篇文章，或者调整筛选条件。</span>
            </div>
        `;
        return;
    }

    adminElements.articleTable.innerHTML = records.map((item) => `
        <div class="table-row">
            <div class="row-summary">
                <strong>${escapeHtml(item.title)}</strong>
                <span class="status-badge status-${item.status}">${formatStatus(item.status)}</span>
            </div>
            <span class="hint">${escapeHtml(item.categoryName || "未分类")} · ${escapeHtml(item.authorName || "匿名")} · ${formatDate(item.publishTime)} · 阅读 ${item.viewCount ?? 0}</span>
            <span class="hint">${escapeHtml(item.summary || "暂无摘要")}</span>
            <div class="tag-inline">${(item.tags || []).map((tag) => `<span>${escapeHtml(tag.name)}</span>`).join("")}</div>
            <div class="row-actions">
                <a href="/admin-ui/editor/${item.id}" class="mini-btn primary">编辑</a>
                <button class="mini-btn primary" onclick="toggleArticleStatus(${item.id}, ${item.status})">${item.status === 1 ? "下线" : "发布"}</button>
                <button class="mini-btn danger" onclick="deleteArticle(${item.id})">删除</button>
            </div>
        </div>
    `).join("");
}

async function loadCategories() {
    const data = await requestJson("/admin/category/list");
    adminState.categories = data || [];
    adminElements.categoryTable.innerHTML = adminState.categories.length
        ? adminState.categories.map((item) => `
            <div class="table-row compact">
                <div>
                    <strong>${escapeHtml(item.name)}</strong>
                    <div class="hint">${escapeHtml(item.description || "暂无描述")}</div>
                </div>
                <div class="row-actions">
                    <span class="hint">排序 ${item.sort ?? 0}</span>
                    <button class="mini-btn danger" onclick="deleteCategory(${item.id})">删除</button>
                </div>
            </div>
        `).join("")
        : `<div class="table-row empty-state"><strong>还没有分类</strong><span class="hint">先新增一个分类，文章发布会更顺手。</span></div>`;

    renderCategoryOptions();
}

async function loadTags() {
    const data = await requestJson("/admin/tag/list");
    adminState.tags = data || [];
    adminElements.tagTable.innerHTML = adminState.tags.length
        ? adminState.tags.map((item) => `
            <div class="table-row compact">
                <strong>${escapeHtml(item.name)}</strong>
                <div class="row-actions">
                    <span class="hint">#${item.id}</span>
                    <button class="mini-btn danger" onclick="deleteTag(${item.id})">删除</button>
                </div>
            </div>
        `).join("")
        : `<div class="table-row empty-state"><strong>还没有标签</strong><span class="hint">新增几个标签，前台筛选会更丰富。</span></div>`;
}

function renderCategoryOptions() {
    const categoryOptions = adminState.categories.length
        ? adminState.categories.map((item) => `<option value="${item.id}">${escapeHtml(item.name)}</option>`).join("")
        : `<option value="">请先新增分类</option>`;

    adminElements.articleCategorySelect.innerHTML = categoryOptions;
    adminElements.articleCategorySelect.disabled = adminState.categories.length === 0;

    adminElements.articleCategoryFilterSelect.innerHTML = `
        <option value="">全部分类</option>
        ${adminState.categories.map((item) => `<option value="${item.id}">${escapeHtml(item.name)}</option>`).join("")}
    `;

    if (adminState.articleFilters.categoryId) {
        adminElements.articleCategoryFilterSelect.value = adminState.articleFilters.categoryId;
    }
}

async function createCategory() {
    try {
        adminElements.categoryMessage.textContent = "创建中...";
        await requestJson("/admin/category", {
            method: "POST",
            body: JSON.stringify({
                name: adminElements.categoryNameInput.value.trim(),
                description: adminElements.categoryDescriptionInput.value.trim(),
                sort: Number(adminElements.categorySortInput.value || 0)
            })
        });
        adminElements.categoryNameInput.value = "";
        adminElements.categoryDescriptionInput.value = "";
        adminElements.categorySortInput.value = "1";
        adminElements.categoryMessage.textContent = "分类创建成功";
        await loadCategories();
        await loadStatistics();
    } catch (error) {
        adminElements.categoryMessage.textContent = error.message;
    }
}

async function createTag() {
    try {
        adminElements.tagMessage.textContent = "创建中...";
        await requestJson("/admin/tag", {
            method: "POST",
            body: JSON.stringify({
                name: adminElements.tagNameInput.value.trim()
            })
        });
        adminElements.tagNameInput.value = "";
        adminElements.tagMessage.textContent = "标签创建成功";
        await loadTags();
        await loadStatistics();
    } catch (error) {
        adminElements.tagMessage.textContent = error.message;
    }
}

async function saveArticle() {
    if (!adminState.categories.length) {
        adminElements.articleMessage.textContent = "请先新增分类，再发布文章。";
        return;
    }

    try {
        const isEdit = Boolean(adminState.editingArticleId);
        adminElements.articleMessage.textContent = isEdit ? "保存修改中..." : "发布中...";
        await requestJson("/admin/article", {
            method: isEdit ? "PUT" : "POST",
            body: JSON.stringify({
                id: adminState.editingArticleId,
                title: adminElements.articleTitleInput.value.trim(),
                summary: adminElements.articleSummaryInput.value.trim(),
                content: adminElements.articleContentInput.value.trim(),
                coverImage: adminElements.articleCoverInput.value.trim(),
                categoryId: Number(adminElements.articleCategorySelect.value),
                status: Number(adminElements.articleStatusSelect.value),
                isTop: Number(adminElements.articleTopSelect.value),
                tagIdList: resolveTagIds(adminElements.articleTagsInput.value)
            })
        });
        clearArticleForm();
        adminElements.articleMessage.textContent = isEdit ? "文章修改成功" : "文章发布成功";
        await loadArticles();
        await loadStatistics();
    } catch (error) {
        adminElements.articleMessage.textContent = error.message;
    }
}

async function editArticle(articleId) {
    try {
        const detail = await requestJson(`/admin/article/${articleId}`);
        adminState.editingArticleId = articleId;
        adminElements.articleFormTitle.textContent = `编辑文章 #${articleId}`;
        adminElements.articleTitleInput.value = detail.title || "";
        adminElements.articleSummaryInput.value = detail.summary || "";
        adminElements.articleContentInput.value = detail.content || "";
        adminElements.articleCoverInput.value = detail.coverImage || "";
        adminElements.articleCategorySelect.value = String(detail.categoryId || "");
        adminElements.articleStatusSelect.value = String(detail.status ?? 1);
        adminElements.articleTopSelect.value = String(detail.isTop ?? 0);
        adminElements.articleTagsInput.value = (detail.tags || []).map((tag) => tag.name).join(",");
        adminElements.articleMessage.textContent = "文章内容已载入，可以直接修改后保存。";
        window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (error) {
        adminElements.articleMessage.textContent = error.message;
    }
}

async function deleteArticle(articleId) {
    if (!window.confirm("确认删除这篇文章吗？")) {
        return;
    }
    try {
        await requestJson(`/admin/article/${articleId}`, { method: "DELETE" });
        await loadArticles();
        await loadStatistics();
        if (adminState.editingArticleId === articleId) {
            clearArticleForm();
        }
        adminElements.articleMessage.textContent = "文章已删除";
    } catch (error) {
        adminElements.articleMessage.textContent = error.message;
    }
}

async function toggleArticleStatus(articleId, currentStatus) {
    try {
        const nextStatus = currentStatus === 1 ? 2 : 1;
        await requestJson("/admin/article/status", {
            method: "PUT",
            body: JSON.stringify({
                id: articleId,
                status: nextStatus
            })
        });
        await loadArticles();
        await loadStatistics();
        adminElements.articleMessage.textContent = "文章状态已更新";
    } catch (error) {
        adminElements.articleMessage.textContent = error.message;
    }
}

function resolveTagIds(rawValue) {
    const names = rawValue
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean);
    return adminState.tags
        .filter((tag) => names.includes(tag.name))
        .map((tag) => tag.id);
}

function clearArticleForm() {
    adminState.editingArticleId = null;
    adminElements.articleFormTitle.textContent = "发布文章";
    adminElements.articleTitleInput.value = "";
    adminElements.articleSummaryInput.value = "";
    adminElements.articleCoverInput.value = "";
    adminElements.articleTagsInput.value = "";
    adminElements.articleContentInput.value = "";
    adminElements.articleStatusSelect.value = "1";
    adminElements.articleTopSelect.value = "0";
    if (adminState.categories.length) {
        adminElements.articleCategorySelect.value = String(adminState.categories[0].id);
    }
}

function resetArticleForm() {
    clearArticleForm();
    adminElements.articleMessage.textContent = "文章表单已清空";
}

async function deleteCategory(categoryId) {
    if (!window.confirm("确认删除这个分类吗？")) {
        return;
    }
    try {
        await requestJson(`/admin/category/${categoryId}`, { method: "DELETE" });
        await loadCategories();
        await loadStatistics();
        await loadArticles();
        adminElements.categoryMessage.textContent = "分类已删除";
    } catch (error) {
        adminElements.categoryMessage.textContent = error.message;
    }
}

async function deleteTag(tagId) {
    if (!window.confirm("确认删除这个标签吗？")) {
        return;
    }
    try {
        await requestJson(`/admin/tag/${tagId}`, { method: "DELETE" });
        await loadTags();
        await loadStatistics();
        await loadArticles();
        adminElements.tagMessage.textContent = "标签已删除";
    } catch (error) {
        adminElements.tagMessage.textContent = error.message;
    }
}

async function uploadImage() {
    const file = adminElements.uploadFileInput.files[0];
    if (!file) {
        adminElements.uploadMessage.textContent = "请先选择一张图片。";
        return;
    }

    try {
        adminElements.uploadMessage.textContent = "上传中...";
        const formData = new FormData();
        formData.append("file", file);
        const response = await fetch("/admin/file/upload", {
            method: "POST",
            headers: {
                ...getAuthHeaders()
            },
            body: formData
        });
        const data = await response.json();
        if (!response.ok || data.code !== 200) {
            throw new Error(data.message || "上传失败");
        }
        adminElements.uploadResultInput.value = data.data.url || "";
        adminElements.uploadMessage.textContent = "图片上传成功";
    } catch (error) {
        adminElements.uploadMessage.textContent = error.message;
    }
}

function fillUploadedUrl(targetInput) {
    if (!adminElements.uploadResultInput.value) {
        adminElements.uploadMessage.textContent = "请先完成图片上传。";
        return;
    }
    targetInput.value = adminElements.uploadResultInput.value;
    adminElements.uploadMessage.textContent = "地址已填入对应输入框";
}

function formatStatus(status) {
    if (status === 1) {
        return "已发布";
    }
    if (status === 2) {
        return "已下线";
    }
    return "草稿";
}

function formatDate(value) {
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

async function initAdmin() {
    bindAdminEvents();
    if (adminState.token) {
        try {
            await requestJson("/admin/auth/profile");
            showDashboard();
            await loadDashboardData();
            return;
        } catch (error) {
            await logout();
        }
    }
}

initAdmin();

window.editArticle = editArticle;
window.deleteArticle = deleteArticle;
window.toggleArticleStatus = toggleArticleStatus;
window.deleteCategory = deleteCategory;
window.deleteTag = deleteTag;
