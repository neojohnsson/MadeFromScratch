/*
Method   URL                            What it does                          Frontend
GET      /api/tasks                     Returns all tasks                     DONE
GET      /api/tasks/{id}                Returns one task
GET      /api/tasks/completed           Returns all completed tasks
GET      /api/tasks/pending             Returns all pending (incomplete) tasks
GET      /api/tasks/search?title=...    Returns tasks matching a title
POST     /api/tasks                     Creates a task                        DONE
PATCH    /api/tasks/{id}                Updates a task (partial)
PATCH    /api/tasks/{id}/complete       Marks a task as completed
DELETE   /api/tasks/{id}                Deletes a task                        DONE

Method   URL                            What it does                          Frontend
GET      /api/projects                  Returns all projects                  DONE
GET      /api/projects/{id}             Returns one project                   DONE
GET      /api/projects/{id}/tasks       Returns all tasks in a project
POST     /api/projects                  Creates a project                     DONE
DELETE   /api/projects/{id}             Deletes a project                     DONE
 */

// Run loadTasks and loadProjects on load
window.addEventListener("load", () => {
    loadTasks();
    loadProjects();
    selectProject();
})

// Run all the functions below
async function loadFunctions() {
    await loadProjects();
    await loadTasks();
    await selectProject();

    document.getElementById("task-name").value = "";
    document.getElementById("task-description").value = "";
    document.getElementById("project-name").value = "";
}

async function loadTasks() {
    try {
        const response = await fetch("http://localhost:8080/api/tasks");
        const data = await response.json();
        console.log(data);

        const list = document.getElementById("task-list");
        list.innerHTML = "";

        for (const task of data) {
            const li = document.createElement("li");
            if (task.completed) {
                li.classList.add("completed");
            }
            const response = await fetch(`http://localhost:8080/api/projects/${task.projectId}`);
            const project = await response.json();
            li.innerHTML = `
                <strong>Title: </strong>${task.title}<br>
                <strong>Description: </strong>${task.description}<br>
                <strong>Completed: </strong>${task.completed}<br>
                <strong>Project: </strong>${project.name}<br>
            `;
            li.classList.add("task");

            const checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.checked = task.completed;
            checkbox.addEventListener("change", async () => {
                const toggleResponse = await fetch(`http://localhost:8080/api/tasks/${task.id}/toggle`, {
                    method: "PATCH"
                })
                const updatedTask = await toggleResponse.json();

                if (updatedTask.completed) {
                    li.classList.add("completed");
                } else {
                    li.classList.remove("completed");
                }
            })

            const button = document.createElement("button");
            button.textContent = "Delete Task";
            button.addEventListener("click", async () => {
                await fetch("http://localhost:8080/api/tasks/" + task.id, {
                    method: "DELETE"
                })
                li.remove();
            })

            li.appendChild(checkbox);
            li.appendChild(button);
            list.appendChild(li);
        }
    } catch (error) {
        console.log(error);
    }
}

const addTaskButton = document.getElementById("add-task");

async function addTask() {
    try {
        let title = document.getElementById("task-name").value;
        let description = document.getElementById("task-description").value;
        let projectId = document.getElementById("select-project").value;

        await fetch("http://localhost:8080/api/tasks", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                title,
                description,
                projectId: Number(projectId)
            })
        })

        await loadFunctions();
    } catch (error) {
        console.log(error);
    }
}

addTaskButton.addEventListener("click", addTask);

const addProjectButton = document.getElementById("add-project");

async function addProject() {
    try {
        let name = document.getElementById("project-name").value;

        await fetch("http://localhost:8080/api/projects", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name
            })
        })

        await loadFunctions();
    } catch (error) {
        console.log(error);
    }
}

addProjectButton.addEventListener("click", addProject);

async function loadProjects() {
    try {
        const response = await fetch("http://localhost:8080/api/projects");
        const data = await response.json();
        console.log(data);

        const list = document.getElementById("project-list");
        list.innerHTML = "";

        for (const project of data) {
            const li = document.createElement("li");
            li.innerHTML = `
                <strong>Name: </strong>${project.name}<br>
            `;
            li.className = "project";

            const button = document.createElement("button");
            button.textContent = "Delete Project";
            button.addEventListener("click", async () => {
                await fetch("http://localhost:8080/api/projects/" + project.id, {
                    method: "DELETE"
                })
                li.remove();
                await selectProject();
            })

            li.appendChild(button);
            list.appendChild(li);
        }
    } catch (error) {
        console.log(error);
    }
}

async function selectProject() {
    try {
        let response = await fetch("http://localhost:8080/api/projects");
        let projects = await response.json();

        let options = "<option>Select Project</option>";

        for (const project of projects) {
            options += `<option value="${project.id}">${project.name}</option>`
        }

        document.getElementById("select-project").innerHTML = options;
    } catch (error) {
        console.log(error);
    }
}