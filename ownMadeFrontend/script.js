// Load tasks from a button to a ul, items should be as a li
const loadTaskButton = document.getElementById("load-tasks");

async function loadTasks() {
    try {
        const response = await fetch("http://localhost:8080/api/tasks");
        const data = await response.json();
        console.log(data);

        const list = document.getElementById("task-list");
        list.innerHTML = "";

        data.forEach(task => {
            const li = document.createElement("li");
            li.innerHTML = `
                <strong>Title: </strong>${task.title}<br>
                <strong>Description: </strong>${task.description}<br>
                <strong>Completed: </strong>${task.completed}<br>
            `;
            li.id = "task";

            const button = document.createElement("button");
            button.textContent = "Delete Task";
            button.addEventListener("click", async () => {
                await fetch("http://localhost:8080/api/tasks/" + task.id, {
                    method: "DELETE"
                })
                await loadTasks();
            })

            li.appendChild(button);
            list.appendChild(li);
        });
    } catch (error) {
        console.log(error);
    }
}

loadTaskButton.addEventListener("click", loadTasks);


const addTaskButton = document.getElementById("add-task");

async function addTask() {
    try {
        let title = document.getElementById("task-title").value;
        let description = document.getElementById("task-description").value;

        await fetch("http://localhost:8080/api/tasks", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                title,
                description,
                projectId: 1
            })
        })

        await loadTasks();
    } catch (error) {
        console.log(error);
    }
}

addTaskButton.addEventListener("click", addTask);
