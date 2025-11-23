// Fetch public rooms on load
async function loadPublicRooms() {
    try {
        const res = await fetch("/api/rooms");
        const rooms = await res.json();

        const listContainer = document.getElementById("publicRoomsList");
        listContainer.innerHTML = "";

        for (let index = 0; index < rooms.length; index++) {
            const room = rooms[index];

            const div = document.createElement("div");
            div.className = "publicRoomEntry";

            const nameSpan = document.createElement("span");
            nameSpan.textContent = room.name;

            const joinBtn = document.createElement("button");
            joinBtn.textContent = "Join";
            joinBtn.onclick = async () => {
                window.location.href = `/room?id=${room.roomId}`;
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

    const res = await fetch('/api/rooms',
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
        const room = await res.json();
        window.location.href = `/room?id=${room.roomId}`;
    }
}

// Join private room
async function joinPrivateRoom() {
    const roomId = document.getElementById("privateRoomId").value.trim();

    if (!roomId) return;

    try {

        const res = await fetch(`/api/rooms/${encodeURIComponent(roomId)}`,
            {
                method: 'HEAD',
            }
        );

        if (res.ok) {
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
