
export const Userbox = {
    init : function(args) {
        initialize_userbox(args);
    }
}

async function initialize_userbox(args) {
    const linkId = 'userboxcss';
    if (!document.getElementById(linkId)) {
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.type = 'text/css';
        link.href = 'userbox_widget.css'
        link.id = linkId
        document.head.appendChild(link);
    }

    const body = document.body;

    const userBoxDiv = document.createElement("div")
    userBoxDiv.id = "userBox"

    const userBoxContDiv = document.createElement("div");
    userBoxContDiv.id = "userBoxContent";
    userBoxDiv.appendChild(userBoxContDiv);

    body.appendChild(userBoxDiv);

    const authModalDiv = document.createElement("div")
    authModalDiv.id = "authModal"
    authModalDiv.classList.add("hidden")

    const authModalContDiv = document.createElement("div");
    authModalContDiv.id = "authModalContent";
    authModalDiv.appendChild(authModalContDiv);

    body.appendChild(authModalDiv);

    const userBoxContent = document.getElementById("userBoxContent");
    const authModal = document.getElementById("authModal");
    const authModalContent = document.getElementById("authModalContent");

    async function checkUser() {
        const res = await fetch("/api/user/check");
        const text = await res.text();

        if (!res.ok) {
            if (args.doLoginRegister) {
                userBoxContent.innerHTML = `
                    <button id="loginBtn">Login</button>
                    <button id="registerBtn">Register</button>
                    `;
                document.getElementById("loginBtn").onclick = () =>
                    openForm("login");
                document.getElementById("registerBtn").onclick = () =>
                    openForm("register");
            } else {
                userBoxContent.innerHTML = `<strong>Logged out</strong>`
            }
        } else {
            userBoxContent.textContent = "Logged in as: ";
            const textElement = document.createElement("strong");
            textElement.textContent = text
            userBoxContent.appendChild(textElement)
            const logoutButton = document.createElement("Button");
            logoutButton.id = "logoutBtn"
            logoutButton.textContent = "Logout"
            userBoxContent.appendChild(logoutButton)

            document.getElementById("logoutBtn").onclick = async () => {
                await fetch("/api/user/logout", {
                    method: "POST"
                });
                authModal.classList.add("hidden");
                checkUser()
            }
        }
    }

    function openForm(type) {
        authModal.classList.remove("hidden");

        authModalContent.innerHTML = `
            <h3>${type === "login" ? "Login" : "Register"}</h3>
            <input id="authUsername" type="text" placeholder="Username" />
            <input id="authPassword" type="password" placeholder="Password" />
            <button id="submitAuth">${type}</button>
            <button id="closeAuth">Close</button>
            `;

        document.getElementById("closeAuth").onclick = () =>
            authModal.classList.add("hidden");

        document.getElementById("submitAuth").onclick = async () => {
            const username = document.getElementById("authUsername").value;
            const password = document.getElementById("authPassword").value;

            const endpoint =
                type === "login" ? "/api/user/login" : "/api/user/register";

            const res = await fetch(endpoint, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            if (!res.ok) {
                alert(type === "login" ? "Login failed" : "Registration failed");
                return;
            }

            if (type != "login") {
                await fetch("/api/user/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        username: username,
                        password: password
                    })
                });
            }

            authModal.classList.add("hidden");

            await checkUser();
        };
    }

    checkUser();
}

