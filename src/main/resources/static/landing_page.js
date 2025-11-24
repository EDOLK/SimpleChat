
const roomDivs = new Map();

async function loadRooms(){
    fetch("/api/rooms")
        .then(resToJson)
        .then((rooms) => populateRoomContainer(rooms, true, false, document.getElementById("publicRoomsList"), document.getElementsByClassName("middleColumn")[0]))
        .catch(logRoomLoadError);
    fetch("/api/rooms?own=true&private=true&public=true")
        .then(resToJson)
        .then((rooms) => populateRoomContainer(rooms, true, true, document.getElementById("userRoomsList"), document.getElementsByClassName("rightColumn")[0]))
        .catch(logRoomLoadError);
}

async function resToJson(res){
    if (res.ok) {
        return res.json();
    }
    return [];
}

async function logRoomLoadError(error){
    console.error("Failed to load rooms", error);
}

async function populateRoomContainer(rooms, join, del, container, outerContainer){
    container.innerHTML = "";

    if (rooms.length <= 0) {
        outerContainer.style.display = 'none';
    }

    for (let index = 0; index < rooms.length; index++) {
        const room = rooms[index];

        container.appendChild(createRoomEntry({
            room: room,
            join: join,
            del: del
        }))
    }
}

function createRoomEntry({ room = null, join = true, del = false} = {}){

    if (!room) {
        return document.createElement("div");
    }

    const div = document.createElement("div");
    div.className = "publicRoomEntry";

    const nameSpan = document.createElement("span");
    nameSpan.textContent = room.name;

    const btnDiv = document.createElement("div")
    btnDiv.style.cssText = "display: flex; flex-direction: row; justify-content: flex-end; gap: 5px;"

    div.appendChild(nameSpan);

    if (join) {

        const joinBtn = document.createElement("button");
        joinBtn.textContent = "Join";
        joinBtn.onclick = async () => {
            const res = await fetch("/api/user/refresh", {
                method: 'HEAD'
            });
            if (res.ok) {
                window.location.href = `/room?id=${room.roomId}`;
            }
        };

        btnDiv.appendChild(joinBtn);
    }

    if (del) {

        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "❌";
        deleteBtn.onclick = async () => {
            const res = await fetch(`api/rooms/` + room.roomId,
                {
                    method: 'DELETE',
                }
            )
            if (res.ok) {
                roomDivs.get(room.roomId).forEach((div) => {
                    div.style.display = 'none'
                })
            }
        };

        btnDiv.appendChild(deleteBtn);
    }

    div.appendChild(btnDiv);

    if (roomDivs.has(room.roomId)) {
        roomDivs.get(room.roomId).push(div);
    } else {
        roomDivs.set(room.roomId, [div])
    }

    return div;

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

        const resUser = await fetch(`/api/user/refresh`,
            {
                method: 'HEAD',
            }
        );

        if (res.ok && resUser.ok) {
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

// Load rooms at startup
loadRooms();
