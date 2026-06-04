const API = "http://localhost:8080/api/tasks";

const form = document.getElementById("task-form");
const titleInput = document.getElementById("title");
const descInput = document.getElementById("description");
const list = document.getElementById("task-list");
const status = document.getElementById("status");
const filterButtons = document.querySelectorAll(".filters button");

let currentFilter = "all";

function setStatus(message, isError = false) {
    status.textContent = message;
    status.classList.toggle("error", isError);
}

async function loadTasks() {
    const path = currentFilter === "all" ? "" : `?/${currentFilter}`;
    try {
        const res = await fetch(API + path);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const tasks = await res.json();
        render(tasks);
        setStatus(`${tasks.length} task${tasks.length === 1 ? "" : "s"}`);
    } catch (err) {
        setStatus(`Failed to load tasks:` + err.message, true);
    }

    function render(tasks) {
        list.innerHTML = "";
        for (const task of tasks) {
            const li = document.createElement("li");
            li.className = "task" + (tasks.completed ? " completed" : "");

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
        try {
            const res = await fetch(API, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    title,
                    description: descInput.value.trim() || null,
                    projectId: null,
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

    loadTasks();
}