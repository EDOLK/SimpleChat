// Fetch public rooms on load
async function loadPublicRooms() {
    try {
        const res = await fetch("/api/publicrooms");
        const roomsNames = await res.json();

        const listContainer = document.getElementById("publicRoomsList");
        listContainer.innerHTML = "";

        for (let index = 0; index < roomsNames.length; index++) {
            const roomName = roomsNames[index];

            const div = document.createElement("div");
            div.className = "publicRoomEntry";

            const nameSpan = document.createElement("span");
            nameSpan.textContent = roomName;

            const joinBtn = document.createElement("button");
            joinBtn.textContent = "Join";
            joinBtn.onclick = async () => {
                const res = await fetch(`/api/roomnametoid?name=${encodeURIComponent(roomName)}`);
                if (res.ok) {
                    const data = await res.text();
                    window.location.href = `/room?id=${data}`;
                }
            };

            div.appendChild(nameSpan);
            div.appendChild(joinBtn);
            listContainer.appendChild(div);
        }

    } catch (err) {
        console.error("Failed to load public rooms", err);
    }
}

// Create room
async function createRoom() {
    const roomName = document.getElementById("newRoomName").value.trim();
    const isPublic = document.getElementById("publicToggle").checked;

    if (!roomName) return;

    const res = await fetch('/api/newroom',
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                roomName: roomName,
                roomPublic: isPublic
            })
        }
    );
    if (res.ok) {
        const data = await res.text();
        window.location.href = `/room?id=${data}`;
    }
}

// Join private room
async function joinPrivateRoom() {
    const roomId = document.getElementById("privateRoomId").value.trim();

    if (!roomId) return;

    try {
        const res = await fetch(`/api/checkroom?id=${encodeURIComponent(roomId)}`);
        const exists = await res.json();

        if (exists) {
            window.location.href = `/room?id=${roomId}`;
        } else {
            alert("Room not found");
        }
    } catch (err) {
        console.error("Failed to check room", err);
        alert("Error checking room");
    }
}

document.getElementById("createRoomBtn").addEventListener("click", createRoom);
document.getElementById("joinPrivateBtn").addEventListener("click", joinPrivateRoom);

// Load public rooms at startup
loadPublicRooms();
