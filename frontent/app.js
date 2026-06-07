const API = "http://localhost:8080/api/tasks";
const PROJECTS_API = "http://localhost:8080/api/projects";

const form = document.getElementById("task-form");
const titleInput = document.getElementById("title");
const descInput = document.getElementById("description");
const list = document.getElementById("task-list");
const status = document.getElementById("status");
const filterButtons = document.querySelectorAll(".filters button");

const projectForm = document.getElementById("project-form");
const projectNameInput = document.getElementById("project-name");
const projectSelect = document.getElementById("project-select");

let currentFilter = "all";
let currentProjectId = null;

function setStatus(message, isError = false) {
    status.textContent = message;
    status.classList.toggle("error", isError);
}

async function loadProjects() {
    try {
        const res = await fetch(PROJECTS_API);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const projects = await res.json();

        projectSelect.innerHTML = "";
        for (const p of projects) {
            const opt = document.createElement("option");
            opt.value = p.id;
            opt.textContent = p.name;
            projectSelect.appendChild(opt);
        }

        if (projects.length > 0) {
            currentProjectId = projects[0].id;
            projectSelect.value = currentProjectId;
        } else {
            currentProjectId = null;
        }
    } catch (err) {
        setStatus(`Failed to load projects: ${err.message}`, true);
    }
}

async function loadTasks() {
    if (!currentProjectId) {
        list.innerHTML = "";
        setStatus("No project selected — create one to get started");
        return;
    }
    try {
        const res = await fetch(`${PROJECTS_API}/${currentProjectId}/tasks`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        let tasks = await res.json();

        if (currentFilter === "pending") tasks = tasks.filter((t) => !t.completed);
        else if (currentFilter === "completed") tasks = tasks.filter((t) => t.completed);

        render(tasks);
        setStatus(`${tasks.length} task${tasks.length === 1 ? "" : "s"}`);
    } catch (err) {
        setStatus(`Failed to load tasks: ${err.message}`, true);
    }
}

function render(tasks) {
    list.innerHTML = "";
    for (const task of tasks) {
        const li = document.createElement("li");
        li.className = "task" + (task.completed ? " completed" : "");

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.checked = task.completed;
        checkbox.disabled = task.completed;
        checkbox.addEventListener("change", () => completeTask(task.id));

        const body = document.createElement("div");
        body.className = "body";
        const title = document.createElement("div");
        title.className = "title";
        title.textContent = task.title;
        body.appendChild(title);
        if (task.description) {
            const desc = document.createElement("div");
            desc.className = "description";
            desc.textContent = task.description;
            body.appendChild(desc);
        }

        const del = document.createElement("button");
        del.className = "delete";
        del.textContent = "x";
        del.addEventListener("click", () => deleteTask(task.id));

        li.append(checkbox, body, del);
        list.appendChild(li);
    }
}

form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const title = titleInput.value.trim();
    if (!title) return;
    if (!currentProjectId) {
        setStatus("Create a project first", true);
        return;
    }
    try {
        const res = await fetch(API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                title,
                description: descInput.value.trim() || null,
                projectId: currentProjectId,
            }),
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        titleInput.value = "";
        descInput.value = "";
        loadTasks();
    } catch (err) {
        setStatus(`Failed to add task: ${err.message}`, true);
    }
});

projectForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = projectNameInput.value.trim();
    if (!name) return;
    try {
        const res = await fetch(PROJECTS_API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name }),
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const created = await res.json();
        projectNameInput.value = "";
        await loadProjects();
        currentProjectId = created.id;
        projectSelect.value = currentProjectId;
        loadTasks();
    } catch (err) {
        setStatus(`Failed to add project: ${err.message}`, true);
    }
});

projectSelect.addEventListener("change", () => {
    currentProjectId = Number(projectSelect.value);
    loadTasks();
});

async function completeTask(id) {
    try {
        const res = await fetch(`${API}/${id}/complete`, { method: "PATCH" });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        loadTasks();
    } catch (err) {
        setStatus(`Failed to complete task: ${err.message}`, true);
    }
}

async function deleteTask(id) {
    try {
        const res = await fetch(`${API}/${id}`, { method: "DELETE" });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        loadTasks();
    } catch (err) {
        setStatus(`Failed to delete task: ${err.message}`, true);
    }
}

filterButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
        filterButtons.forEach((b) => b.classList.remove("active"));
        btn.classList.add("active");
        currentFilter = btn.dataset.filter;
        loadTasks();
    });
});

loadProjects().then(loadTasks);
