import { Client } from "https://ga.jspm.io/npm:@stomp/stompjs@7.0.0/esm6/index.js";

function getRoomId() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

var client = null;
const roomId = getRoomId();

const messageContainer = document.getElementById('messageContainer');
const contentInput = document.getElementById("contentInput");
const sendBtn = document.getElementById("sendBtn");
const scrollBtn = document.getElementById("scrollDownBtn");
const backBtn = document.getElementById("backBtn");

function isAtBottom() {
    return messageContainer.scrollHeight - messageContainer.scrollTop - messageContainer.clientHeight < 5;
}

function updateScrollButton() {
    if (isAtBottom()) {
        scrollBtn.style.display = "none";
    } else {
        scrollBtn.style.display = "block";
    }
}

async function initialLoad() {
    if (!roomId) return;
    await loadAllMessages(roomId);
    client = new Client({
        brokerURL: "/ws",
        onConnect: () => {
            client.subscribe('/topic/chat/' + roomId, msg => {
                loadMessage(JSON.parse(msg.body));
            })
        }
    })
    await client.activate()
}

async function loadAllMessages(roomId){

    try {
        const res = await fetch(`/api/rooms/${roomId}/messages`);
        if (res.ok) {
            const messages = await res.json();
            for (let index = 0; index < messages.length; index++) {
                loadMessage(messages[index]);
            }
        }
    } catch (err) {
        console.error('Error loading messages', err);
    }

}

function loadMessage(message) {
    const shouldAutoScroll = isAtBottom();
    const username = message.username;
    const content = message.content;

    const wrapper = document.createElement("div");
    wrapper.className = "message-box";

    const userEl = document.createElement("div");
    userEl.className = "message-username";
    userEl.textContent = username || "Anonymous";

    const contentEl = document.createElement("div");
    contentEl.className = "message-content";

    const contentList = urlify(content)

    for (let index = 0; index < contentList.length; index++) {
        contentEl.appendChild(contentList[index]);
    }

    wrapper.appendChild(userEl);
    wrapper.appendChild(contentEl);
    messageContainer.appendChild(wrapper);

    if (shouldAutoScroll) {
        messageContainer.scrollTop = messageContainer.scrollHeight;
    }

    updateScrollButton();
}

function urlify(text) {
    var urlRegex = /(https?:\/\/[^\s]+)/g;
    var lastEndIndex = 0;
    const list = [];
    let match;
    while ((match = urlRegex.exec(text)) !== null) {
        const startIndex = match.index;
        const endIndex = startIndex + match[0].length;
        const prev_text = text.substring(lastEndIndex, startIndex);
        if (prev_text != "") {
            list.push(document.createTextNode(prev_text));
        }
        var link_text = text.substring(startIndex, endIndex);
        var link = document.createElement("a");
        link.href = link_text;
        link.textContent = link_text;
        link.target = '_blank';
        list.push(link)
        lastEndIndex = endIndex;
    }
    const end_text = text.substring(lastEndIndex, text.length)
    if (end_text != "") {
        list.push(document.createTextNode(end_text));
    }
    return list;
}

async function sendMessage() {
    const content = contentInput.value.trim();
    if (!roomId || !content || !client) return;

    client.publish({
        destination: '/app/chat/sendmessage',
        body: JSON.stringify({
            message: content,
            roomId: roomId
        })
    });

    contentInput.value = "";
}

contentInput.addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
        sendBtn.click();
    }
});

sendBtn.onclick = sendMessage;

messageContainer.addEventListener("scroll", updateScrollButton);

scrollBtn.onclick = () => {
    messageContainer.scrollTop = messageContainer.scrollHeight;
    updateScrollButton();
}

backBtn.onclick = () => {
    window.location.href = `/index.html`;
}

window.onload = initialLoad;
